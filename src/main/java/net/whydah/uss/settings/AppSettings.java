package net.whydah.uss.settings;

import net.whydah.uss.MainApplication;

public interface AppSettings extends HibernateJdbcSettings, MailSettings, WhydahSettings {

	int THRESHOLD_FOR_OLD_USER_DETECTION_IN_MONTH = Integer.valueOf(MainApplication.instance.config().get("app.threshold_for_detecting_old_users_in_month", "3"));
	int THRESHOLD_FOR_OLD_USER_REMOVAL_IN_MONTH = THRESHOLD_FOR_OLD_USER_DETECTION_IN_MONTH + 1;
	int RECENT_LOGON_PERIOD_IN_DAY = Integer.valueOf(MainApplication.instance.config().get("app.recent_logon_period_in_day"));
	String MY_URI = MainApplication.instance.config().get("app.uri");
	String ACCESS_TOKEN =  MainApplication.instance.config().get("app.accessToken");
	int ERROR_LEVEL = 0;
	int TIME_WAIT_FOR_USER_FETCH_WITH_PAGINATION_FROM_UAS_IN_MILLISECONDS = 5000;
	int TIME_WAIT_FOR_USER_DELETE_IN_MILLISECONDS = 5000;
	
}
