package net.whydah.uss.settings;

import net.whydah.uss.MainApplication;

public interface WhydahSettings {
	
	String WHYDAH_APPID = MainApplication.instance.config().get("whydah.applicationid");
	String WHYDAH_APPNAME = MainApplication.instance.config().get("whydah.applicationname");
	String WHYDAH_APPSECRET = MainApplication.instance.config().get("whydah.applicationsecret");
	String WHYDAH_UAS_URL = MainApplication.instance.config().get("whydah.useradminservice");
	String WHYDAH_UAS_ADMIN_USERNAME = MainApplication.instance.config().get("whydah.useradminservice.username");
	String WHYDAH_UAS_ADMIN_PASSWORD = MainApplication.instance.config().get("whydah.useradminservice.password");
	String WHYDAH_STS_URL = MainApplication.instance.config().get("whydah.securitytokenservice");
}
