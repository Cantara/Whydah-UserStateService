package net.whydah.uss.service.module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.Unirest;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.session.WhydahApplicationSession2;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.util.WhydahUtil2;
import net.whydah.uss.MainApplication;
import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.model.UASUserIdentity;
import net.whydah.uss.model.UASUserQueryResult;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;
import no.cantara.config.ApplicationProperties;

public class ImportUserModuleImpl implements ImportUserModule {

	public static final Logger logger = LoggerFactory.getLogger(ImportUserModuleImpl.class);

	private APIService api_service;

	public ImportUserModuleImpl(APIService service) {
		this.api_service = service;
	}

	public void importUsers() {

		AppStateEntity app_state_en = api_service.getRepositoryAppState().get();
		int current_page = app_state_en.getImportuser_page_index();
		Long total_users_imported = api_service.getRepositoryLoginUserStatus().count();
		doImport(current_page, Math.toIntExact(total_users_imported));

	}

	private void doImport(int page, int newUserCount) {
		try {

			UASUserQueryResult query_result = api_service.getModuleWhydahClient().fetchUsers(page);

			boolean shouldStop = query_result.getTotalItems() < query_result.getPageSize() * page;

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
				newUserCount = newUserCount + new_users.size();
				api_service.getRepositoryLoginUserStatus().insertAll(new_users);
				
				AppStateEntity app_state_en = api_service.getRepositoryAppState().get();
				app_state_en.setImportuser_page_index(page);
				app_state_en.setStats_total_users_imported(newUserCount);
				api_service.getRepositoryAppState().update(app_state_en);
			}

			if (!shouldStop) {
				try {
					Thread.sleep(AppSettings.TIME_WAIT_FOR_USER_FETCH_WITH_PAGINATION_FROM_UAS_IN_MILLISECONDS);
				} catch (InterruptedException e) {
				}

				doImport(++page, newUserCount);
				
				
				
			} else {

				AppStateEntity app_state_en = api_service.getRepositoryAppState().get();
				app_state_en.setImportuser_page_index(page);
				app_state_en.setStats_total_users_imported(Math.toIntExact(api_service.getRepositoryLoginUserStatus().count()));
				api_service.getRepositoryAppState().update(app_state_en);
				
			}
		} catch (Exception ex) {
			logger.error("unexpected error", ex);
		}

	}

}
