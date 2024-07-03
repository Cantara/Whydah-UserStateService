package net.whydah.uss.service.module;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.entity.OldUserEntity;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;

public class NotifyOldUserModuleImpl implements NotifyOldUserModule {

	public static final Logger logger = LoggerFactory.getLogger(ImportUserModuleImpl.class);

	private APIService api_service;

	public NotifyOldUserModuleImpl(APIService service) {
		this.api_service = service;

	}

	@Override
	public void checkAndSendNotifications() {
		List<OldUserEntity> not_notified_users = api_service.getRepositoryOldUser().findOldUsersForNotifyingOperation();
		int sentCount = 0;
		for (OldUserEntity user : not_notified_users) {
			try {
				boolean sent = false;

				if (AppSettings.MAIL_TEMPLATE != null && AppSettings.MAIL_TEMPLATE.length() > 4) {
					sent = api_service.getModuleWhydahClient().sendScheduledMail(user.getEmail(), user.getUsername(),
							user.getFirstName(), user.getLastName(), 0);
				} else {
					String template = "<html>" + "<head></head>" + "<body>" + "    <p>${msg}</p>" + "</body>" + ""
							+ "</html>";
					template = template.replace("${msg}", AppSettings.MAIL_DEFAULT_MESSAGE)
							.replace("${firstname}", user.getFirstName()).replace("${lastname}", user.getLastName())
							.replace("${username}", user.getUsername());
					sent = api_service.getModuleWhydahClient().sendScheduledMailWithAMessage(user.getEmail(), template, System.currentTimeMillis() + 30000);
				}

				if (sent) {
					user.setNotified(true);
					api_service.getRepositoryOldUser().update(user);
					sentCount ++;
				}
				
			} catch (Exception ex) {
				logger.error("unexpected error", ex);
			}	

		}
		
		AppStateEntity en = api_service.getRepositoryAppState().get();
		en.setStats_number_of_mails_sent(sentCount + en.getStats_number_of_mails_sent());
		api_service.getRepositoryAppState().update(en);
		
	}

}
