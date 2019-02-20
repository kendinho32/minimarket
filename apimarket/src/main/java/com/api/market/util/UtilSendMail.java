package com.api.market.util;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class UtilSendMail {
	
	private static final Logger logger = LogManager.getLogger(UtilSendMail.class);
	
	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	public boolean sendEmail(String toEmail, String subject, String body){
		logger.info("Se ejecuta el metodo para el envio de correo");
		boolean response = false;
		
		Properties props = propertiesServerMail();
		
		final String gmailAccount = "kendinho22@gmail.com";
		final String gmailPassword = "kendinho7";
		final String[] emailDestinations = "kendinho22@gmail.com".split(";");
 
		Session session = setSessionServerMail(props, gmailAccount, gmailPassword);
		
		try {
			 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(gmailAccount));
			
			for (String emailDestination : emailDestinations) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestination));
			}
 
			message.setSubject("Email Subject - Asunto del correo electronico");
 
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Email text Body - Texto o cuerpo del correo electronico");
			
			Multipart multipart = new MimeMultipart();
			
			//Setting email text message
			multipart.addBodyPart(messageBodyPart);
 
			//set the attachments to the email
	        message.setContent(multipart);
 
			Transport.send(message);
			response = true;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return response;
	}

	private static Session setSessionServerMail(Properties props, final String gmailAccount,
			final String gmailPassword) {
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(gmailAccount,gmailPassword);
				}
			});
		return session;
	}

	private static Properties propertiesServerMail() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		return props;
	}

}
