package net.whydah.uss.util;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.cantara.config.ApplicationProperties;


public class MailSender {
	private static final Logger log = LoggerFactory.getLogger(MailSender.class);

	private String smtpUsername;
	private String smtpPassword;
	private String smtpHost;
	private String smtpPort;
	private String smtpSenderEmail;
	

	public MailSender(String smtpHost, String smtpport, String smtpsenderemail, String smtpusername, String smtppassword) {
		this.smtpUsername = smtpusername;
		this.smtpPassword = smtppassword;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpport;
		this.smtpSenderEmail = smtpsenderemail;

		log.info("email.smtp.host:" + smtpHost);
		log.info("email.smtp.port:" + smtpPort);
		
	}

	public boolean send(String recipients, String subject, String body) throws Exception {
		log.debug("Sending email to recipients={}, subject={}, body={} via smtphost={}, smtpPort={}", recipients, subject, body, smtpHost, smtpPort);
		log.info("If you are looking at the previous log statement and no mail get sent: try https://accounts.google.com/DisplayUnlockCaptcha");

		Properties smtpProperties = new Properties();
		smtpProperties.put("mail.transport.protocol", "smtp");
		smtpProperties.put("mail.smtp.port", smtpPort); 
		smtpProperties.put("mail.smtp.starttls.enable", "true");
		smtpProperties.put("mail.smtp.auth", "true");
		smtpProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		Session session = Session.getDefaultInstance(smtpProperties);
		session.setDebug(true);



		Message message = new MimeMessage(session);

		message.setFrom(new javax.mail.internet.InternetAddress(this.smtpSenderEmail));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
		message.setSubject(subject);
		message.setContent(body, "text/html");
		Transport transport = session.getTransport();

		try
		{
			log.info("Sending mail to {} ... ", recipients);

			// Connect to Amazon SES using the SMTP username and password you specified above.
			transport.connect(smtpHost, smtpUsername, smtpPassword);
			// Send the email.
			transport.sendMessage(message, message.getAllRecipients());
			
			log.info("Sent sucessfully to {}", recipients);
			return true;
		}
		catch (Exception ex) {
			log.error("unexected error", ex);
			return false;
		}
		finally
		{
			// Close and terminate the connection.
			transport.close();
		}

		
	}

}