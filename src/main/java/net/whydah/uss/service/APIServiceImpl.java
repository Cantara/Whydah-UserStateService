package net.whydah.uss.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.sso.user.types.UserIdentity;
import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.entity.OldUserEntity;
import net.whydah.uss.repository.AppStateRepository;
import net.whydah.uss.repository.LoginUserStatusRepository;
import net.whydah.uss.repository.OldUserRepository;
import net.whydah.uss.service.module.DetectOldUserModule;
import net.whydah.uss.service.module.DetectOldUserModuleImpl;
import net.whydah.uss.service.module.ImportUserModule;
import net.whydah.uss.service.module.ImportUserModuleImpl;
import net.whydah.uss.service.module.RemoveOldUserModule;
import net.whydah.uss.service.module.RemoveOldUserModuleImpl;
import net.whydah.uss.service.module.WhydahClientModule;
import net.whydah.uss.settings.AppSettings;
import no.cantara.config.ApplicationProperties;

@Service
public class APIServiceImpl extends APIService {
	
	public static final Logger logger = LoggerFactory.getLogger(APIService.class);
	
	public APIServiceImpl() {
	}
	
	public void initialize() {
		try {
			if(!testMode) {
				runImportUserSchedule();
				runDetectOldUserAndNotifySchedule();
				runRemoveOldUserSchedule();
			}
			
		} catch (Exception e) {
			logger.error("Failed to initialize codes");
			e.printStackTrace();
			System.exit(0);	
		} 
	}

	private void runImportUserSchedule() {
		ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(1);
		scheduledExecutorService2.scheduleAtFixedRate(() -> {
			getModuleImportUser().importUsers();
		}, 0, 1, TimeUnit.HOURS);
	}


	private void runDetectOldUserAndNotifySchedule() {
		ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(1);
		scheduledExecutorService2.scheduleAtFixedRate(() -> {
			getModuleDetectOldUser().detectOldUsers();
			getNotifyOldUserModule().checkAndSendNotifications();
		}, 0, 1, TimeUnit.DAYS);
	}
	
	private void runRemoveOldUserSchedule() {
		ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(1);
		scheduledExecutorService2.scheduleAtFixedRate(() -> {
			getModuleRemoveOldUser().checkOldUsersAndRemove();
		}, 0, 1, TimeUnit.DAYS);
	}
	
	
	

	@Override
	public void updateUserLogonTimeFromSTS(List<UserIdentity> useridentities) {
		for(UserIdentity u : useridentities) {
			Optional<OldUserEntity> op_comming_back_found = getRepositoryOldUser().findById(u.getUid()); 
			if(op_comming_back_found.isPresent()) {
				getRepositoryOldUser().delete(op_comming_back_found.get());
				//update app state
				AppStateEntity appstate = getRepositoryAppState().get();
				appstate.setStats_number_of_old_users_comming_back(appstate.getStats_number_of_old_users_comming_back() + 1);
				getRepositoryAppState().update(appstate);
			}
			
			//register login time
			Optional<LoginUserStatusEntity> op_en = getRepositoryLoginUserStatus().findById(u.getUid());
			if(op_en.isPresent()) {
				LoginUserStatusEntity en = op_en.get();
				en.setLastLoginTime(LocalDateTime.now());
				en.setOldLogonDetected(false);
				en = getRepositoryLoginUserStatus().update(en);
			} else {
				LoginUserStatusEntity en = new LoginUserStatusEntity();
				en.setCellPhone(u.getCellPhone());
				en.setEmail(u.getEmail());
				en.setFirstName(u.getFirstName());
				en.setId(u.getUid());
				en.setLastLoginTime(LocalDateTime.now());
				en.setLastName(u.getLastName());
				en.setPersonRef(u.getPersonRef());
				en.setUsername(u.getUsername());
				en.setCreationTime(LocalDateTime.now());
				getRepositoryLoginUserStatus().insert(en);
			}
		}
	}

	@Override
	public void setTestMode(boolean test) {
		this.testMode = test;
		
	}

	@Override
	public long getNumberOfRecentLogins() {
		return getRepositoryLoginUserStatus().getNumberOfRecentLogins(LocalDateTime.now().minusDays(AppSettings.RECENT_LOGON_PERIOD_IN_DAY));
	}

	
}
