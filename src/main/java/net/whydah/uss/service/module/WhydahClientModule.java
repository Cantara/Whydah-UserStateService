package net.whydah.uss.service.module;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.session.WhydahApplicationSession2;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.WhydahUtil2;
import net.whydah.uss.MainApplication;
import net.whydah.uss.model.UASUserQueryResult;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;
import net.whydah.uss.util.EntityUtils;
import net.whydah.uss.util.FluentHashMap;
import net.whydah.uss.util.HttpConnectionHelper;
import net.whydah.uss.util.HttpConnectionHelper.Response;
import no.cantara.config.ApplicationProperties;

public class WhydahClientModule {

	public static final Logger logger = LoggerFactory.getLogger(ImportUserModuleImpl.class);


	private final WhydahApplicationSession2 was;
	private final UserCredential adminUserCredential;
	private APIService api_service;
	
	public WhydahClientModule(APIService service) {
		ApplicationProperties properties =  MainApplication.instance.config();
		String appid = properties.get("whydah.applicationid");
		String appName = properties.get("whydah.applicationname");
		String appSecret = properties.get("whydah.applicationsecret");
		String uas_url = properties.get("whydah.useradminservice");
		String sts_url = properties.get("whydah.securitytokenservice");
		String uas_username = properties.get("whydah.useradminservice.username");
		String uas_password = properties.get("whydah.useradminservice.password");

		Objects.requireNonNull(sts_url);
		Objects.requireNonNull(appid);
		Objects.requireNonNull(appName);
		Objects.requireNonNull(appSecret);
		Objects.requireNonNull(uas_url);
		Objects.requireNonNull(uas_username);
		Objects.requireNonNull(uas_password);
		
		this.api_service = service;
		
		ApplicationCredential myApplicationCredential = new ApplicationCredential(appid, appName, appSecret);
		this.adminUserCredential = new UserCredential(uas_username, uas_password);
		this.was = WhydahApplicationSession2.getInstance(sts_url, uas_url, myApplicationCredential);
	}
	
	
	public String getAdminUserTokenId(){
		String userTokenXML = WhydahUtil2.logOnUser(was, this.adminUserCredential);
		if (userTokenXML == null || userTokenXML.length() < 4) {
			logger.error("Error, unable to initialize new user session for admin credential {}", adminUserCredential);
		} else {
			return UserXpathHelper.getUserTokenId(userTokenXML);
		}
		return null;
	}
	
	public UASUserQueryResult fetchUsers(int page) {
		if(was.getActiveApplicationTokenId() == null) {
			logger.error("Apptoken id is null");
			return null;
		}
		if(getAdminUserTokenId() == null) {
			logger.error("Admin usertoken id is null");
			return null;
		}
		try {
//			UASUserQueryResult query_result =
//					Unirest.get(was.getUAS() +  was.getActiveApplicationTokenId() + "/" + getAdminUserTokenId() + "/users/query/" + String.valueOf(page) + "/*")
//					.asObject(UASUserQueryResult.class).getBody();

			String res = HttpConnectionHelper.get(was.getUAS() +  was.getActiveApplicationTokenId() + "/" + getAdminUserTokenId() + "/users/query/" + String.valueOf(page) + "/*")
					.getContent();
			UASUserQueryResult query_result = EntityUtils.mapFromJson(res, UASUserQueryResult.class);
			return query_result;
		} catch(Exception ex) {
			logger.error("unexpected error", ex);
			return null;
		}
	}


	public boolean removeUser(String id) {
//		HttpResponse res = Unirest.delete(was.getUAS() + was.getActiveApplicationTokenId() + "/" + getAdminUserTokenId() + "/user/" + id)
//			.asEmpty();
//		return res.getStatus() == 204;
		
		Response res = HttpConnectionHelper.delete(was.getUAS() + was.getActiveApplicationTokenId() + "/" + getAdminUserTokenId() + "/user/" + id);
		return res.getResponseCode() == 204;
		
	}


	public boolean sendScheduledMail(String email, String username, String firstName, String lastName, long timestamp) throws UnsupportedEncodingException {
//		HttpResponse<String> res = Unirest.post(was.getUAS() + was.getActiveApplicationTokenId() + "/send_scheduled_email")
//				.field("timestamp", timestamp)
//				.field("emailaddress", email)
//				.field("subject", AppSettings.MAIL_SUBJECT)
//				.field("templateName", AppSettings.MAIL_TEMPLATE)
//				.field("templateParams", EntityUtils.object_mapToJsonString(
//						FluentHashMap.map("username", username)
//									.with("firstname", firstName)
//									.with("lastname", lastName)
//						))
//				.asString();
//		logger.debug("sendScheduledMailWithAMessage(username={}, firstName={}, lastName={}, timestamp={}) returns {}", email, firstName, lastName, timestamp, res.getBody());
//		return res.getStatus() == 200;
		Map<String, String> params = new HashMap<String, String>();
		params.put("timestamp", String.valueOf(timestamp));
		params.put("emailaddress", email);
		params.put("subject", AppSettings.MAIL_SUBJECT);
		params.put("templateName", AppSettings.MAIL_TEMPLATE);
		params.put("templateParams", EntityUtils.object_mapToJsonString(
				FluentHashMap.map("username", username)
							.with("firstname", firstName)
							.with("lastname", lastName)
				));
		
		Map<String, String> requestProperties = new HashMap<String, String>();
		requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
		
		String body = HttpConnectionHelper.getURLParams(params); 
		Response res = HttpConnectionHelper.post(
				was.getUAS() + was.getActiveApplicationTokenId() + "/send_scheduled_email",
				null,
				requestProperties, 
				body.getBytes());
		
		logger.debug("sendScheduledMailWithAMessage(username={}, firstName={}, lastName={}, timestamp={}) returns {}", email, firstName, lastName, timestamp, res.getContent());
		return res.getResponseCode() == 200;
	}
	
	public boolean sendScheduledMailWithAMessage(String email, String message, long timestamp) throws UnsupportedEncodingException {
//		HttpResponse<String> res = Unirest.post(was.getUAS() + was.getActiveApplicationTokenId() + "/send_scheduled_email")
//				.contentType("application/x-www-form-urlencoded")
//				.field("timestamp", timestamp)
//				.field("emailaddress", email)
//				.field("subject", AppSettings.MAIL_SUBJECT)
//				.field("emailMessage", message)
//				.asString();
//		logger.debug("sendScheduledMailWithAMessage(email={}, message={}, timestamp={}) returns {}", email, message, timestamp, res.getBody());
//		return res.getStatus() == 200;
		Map<String, String> params = new HashMap<String, String>();
		params.put("timestamp", String.valueOf(timestamp));
		params.put("emailaddress", email);
		params.put("subject", AppSettings.MAIL_SUBJECT);
		params.put("emailMessage", message);
		String body = HttpConnectionHelper.getURLParams(params); 
		Map<String, String> requestProperties = new HashMap<String, String>();
		requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
		Response res = HttpConnectionHelper.post(
				was.getUAS() + was.getActiveApplicationTokenId() + "/send_scheduled_email",
				null,
				requestProperties, 
				body.getBytes());
		
		logger.debug("sendScheduledMailWithAMessage(email={}, message={}, timestamp={}) returns {}", email, message, timestamp, res.getContent());
		return res.getResponseCode() == 200;
		
	}
	
}
