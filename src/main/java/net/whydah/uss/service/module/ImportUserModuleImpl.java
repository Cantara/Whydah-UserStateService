package net.whydah.uss.service.module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.model.UASUserIdentity;
import net.whydah.uss.model.UASUserQueryResult;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;

public class ImportUserModuleImpl implements ImportUserModule {

	public static final Logger logger = LoggerFactory.getLogger(ImportUserModuleImpl.class);

	private APIService api_service;

	public ImportUserModuleImpl(APIService service) {
		this.api_service = service;
	}
	
	private boolean isRunning = false;

	public void importUsers() {

		if(!isRunning) {
			isRunning = true;
			AppStateEntity app_state_en = api_service.getRepositoryAppState().get();
			int current_page = app_state_en.getImportuser_page_index();
			doImport(current_page);
			isRunning = false;
		}
	
	}

	private void doImport(int page) {
		try {

			UASUserQueryResult query_result = api_service.getModuleWhydahClient().fetchUsers(page);
			
			if(query_result==null) {
				return;
			}

			int maxPageCount = query_result.getTotalItems() / query_result.getPageSize();
			
			boolean shouldStop =  page > maxPageCount; // query_result.getTotalItems() < query_result.getPageSize() * page;

			List<LoginUserStatusEntity> new_users = new ArrayList<LoginUserStatusEntity>();
			for (UASUserIdentity x : query_result.getResult()) {
				if (!api_service.getRepositoryLoginUserStatus().findById(x.getUid()).isPresent()) {
					// create new user
					LoginUserStatusEntity user = new LoginUserStatusEntity();
					user.setId(x.getUid());
					user.setCellPhone(x.getCellPhone());
					user.setEmail(x.getEmail());
					user.setFirstName(x.getFirstName());
					user.setLastLoginTime(LocalDateTime.now().minusMonths(3));
					user.setLastName(x.getLastName());
					user.setPersonRef(x.getPersonRef());
					user.setUsername(x.getUsername());
					user.setCreationTime(LocalDateTime.now());
					user.setOldLogonDetected(false);
					new_users.add(user);
				}
			}

			if (new_users.size() > 0) {
				
				api_service.getRepositoryLoginUserStatus().insertAll(new_users);
				
				AppStateEntity app_state_en = api_service.getRepositoryAppState().get();
				app_state_en.setImportuser_page_index(page);
				app_state_en.setStats_total_users_imported(Math.toIntExact(api_service.getRepositoryLoginUserStatus().count()));
				app_state_en.setLast_updated(LocalDateTime.now());
				api_service.getRepositoryAppState().update(app_state_en);
			}

			if (!shouldStop) {
				try {
					Thread.sleep(AppSettings.TIME_WAIT_FOR_USER_FETCH_WITH_PAGINATION_FROM_UAS_IN_MILLISECONDS);
				} catch (InterruptedException e) {
				}

				doImport(++page);
				
			} else {

				AppStateEntity app_state_en = api_service.getRepositoryAppState().get();
				app_state_en.setImportuser_page_index(page);
				app_state_en.setStats_total_users_imported(Math.toIntExact(api_service.getRepositoryLoginUserStatus().count()));
				app_state_en.setLast_updated(LocalDateTime.now());
				api_service.getRepositoryAppState().update(app_state_en);
				
			}
		} catch (Exception ex) {
			logger.error("unexpected error", ex);
		}

	}

}
