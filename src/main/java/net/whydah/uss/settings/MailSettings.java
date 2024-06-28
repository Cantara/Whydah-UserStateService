package net.whydah.uss.settings;

import net.whydah.uss.MainApplication;

public interface MailSettings {
	
	String MAIL_TEMPLATE = MainApplication.instance.config().get("email.templateFilename", null);
	String MAIL_SUBJECT = MainApplication.instance.config().get("email.subject");
	String MAIL_DEFAULT_MESSAGE = MainApplication.instance.config().get("email.defaultMessage");  
	String MAIL_SENDER = MainApplication.instance.config().get("email.sender");
	String MAIL_HOST = MainApplication.instance.config().get("email.smtp.host");
	String MAIL_PORT = MainApplication.instance.config().get("email.smtp.port");
	String MAIL_USERNAME = MainApplication.instance.config().get("email.smtp.username");
	String MAIL_PASSWORD = MainApplication.instance.config().get("email.smtp.password");

}
