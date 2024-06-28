package net.whydah.uss.service.module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.entity.OldUserEntity;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;

public class RemoveOldUserModuleImpl implements RemoveOldUserModule {

	public static final Logger logger = LoggerFactory.getLogger(RemoveOldUserModuleImpl.class);
	
	private APIService api_service;

	public RemoveOldUserModuleImpl(APIService service) {
		this.api_service = service;
	}

	@Override
	public void checkOldUsersAndRemove() {
		
		List<OldUserEntity> deleted_users = api_service.getRepositoryOldUser().findOldUsersForDeleteOperation(
				LocalDateTime.now().minusMonths(AppSettings.THRESHOLD_FOR_OLD_USER_REMOVAL_IN_MONTH));

		List<String> affected_uids = new ArrayList<String>();
		// remove from whydah
		for (OldUserEntity user : deleted_users) {
			try {
				//skip this whydah operation in test mode
				if(api_service.isTestMode() || api_service.getModuleWhydahClient().removeUser(user.getId())) {
					affected_uids.add(user.getId());
				}
			} catch (Exception ex) {
				logger.error("unexpected error", ex);
			}
			try {
				Thread.sleep(AppSettings.TIME_WAIT_FOR_USER_DELETE_IN_MILLISECONDS);
			} catch (InterruptedException e) {
			}
		}

		if(affected_uids.size()>0) {
		
			api_service.getRepositoryLoginUserStatus().deleteAllById(affected_uids);

			api_service.getRepositoryOldUser().deleteAllById(affected_uids);

			// update app state
			AppStateEntity en = api_service.getRepositoryAppState().get();
			en.setStats_number_of_old_users_removed(en.getStats_number_of_old_users_removed() + affected_uids.size());
			api_service.getRepositoryAppState().update(en);

		}

	}

}
