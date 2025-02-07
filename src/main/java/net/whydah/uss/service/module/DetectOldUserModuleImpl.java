package net.whydah.uss.service.module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.uss.MainApplication;
import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.entity.DeletedUserEntity;
import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.entity.OldUserEntity;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;

public class DetectOldUserModuleImpl implements DetectOldUserModule {

	public static final Logger logger = LoggerFactory.getLogger(DetectOldUserModuleImpl.class);

	private APIService api_service;
	
	public DetectOldUserModuleImpl(APIService service) {
		this.api_service = service;
	}
	
	@Override
	public List<OldUserEntity> detectOldUsers() {
		List<LoginUserStatusEntity> detected_old_users = 
				api_service.getRepositoryLoginUserStatus().
				detectOldUsers(LocalDateTime.now().minusMonths(AppSettings.THRESHOLD_FOR_OLD_USER_DETECTION_IN_MONTH));
		
		//import
		List<OldUserEntity> oldUsers = new ArrayList<OldUserEntity>();
		
		for(LoginUserStatusEntity x : detected_old_users) {
			x.setOldLogonDetected(true);
			OldUserEntity old_user = new OldUserEntity();
			old_user.setCellPhone(x.getCellPhone());
			old_user.setEmail(x.getEmail());
			old_user.setFirstName(x.getFirstName());
			old_user.setId(x.getId());
			old_user.setLastLoginTime(x.getLastLoginTime());
			old_user.setLastName(x.getLastName());
			old_user.setNotified(false);
			old_user.setPersonRef(x.getPersonRef());
			old_user.setUsername(x.getUsername());
			oldUsers.add(old_user);
		}
		
		
		api_service.getRepositoryLoginUserStatus().updateAll(detected_old_users);
		
		oldUsers = api_service.getRepositoryOldUser().updateAll(oldUsers);
		
		//update app state
		AppStateEntity en = api_service.getRepositoryAppState().get();
		en.setStats_number_of_old_users_detected(oldUsers.size() + en.getStats_number_of_old_users_detected());
		en.setLast_updated(LocalDateTime.now());
		api_service.getRepositoryAppState().update(en);

		
		return oldUsers;
	}
	
	

}
