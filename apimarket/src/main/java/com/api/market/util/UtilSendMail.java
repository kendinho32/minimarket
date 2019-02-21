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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api.market.payload.ContactRequest;

@Component
public class UtilSendMail {
	
	private static final Logger logger = LogManager.getLogger(UtilSendMail.class);
	
	private static final String AUTH_MAIL = "true";
	
	@Value("${gmail.smtp.host}")
	private String gmailSMTPHost;
	
	@Value("${gmail.smtp.port}")
	private String gmailSMTPPort;
	
	@Value("${gmail.user.account}")
	private String gmailAccount;
	
	@Value("${gmail.user.password}")
	private String gmailPassword;
	
	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	public boolean sendEmail(ContactRequest request){
		logger.info("Se ejecuta el metodo para el envio de correo");
		boolean response = false;
		String destinations = new StringBuilder().append(gmailAccount).append(";").append(request.getEmail()).toString();
		
		Properties props = propertiesServerMail();
		final String[] emailDestinations = destinations.split(";");
 
		Session session = setSessionServerMail(props, gmailAccount, gmailPassword);
		
		try {
			 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(gmailAccount));
			
			for (String emailDestination : emailDestinations) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestination));
			}
 
			message.setSubject(new StringBuilder().append("Formulario Contacto - ").append(request.getSubject()).toString());
 
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(new StringBuilder().append("<strong>").append(request.getMessage()).append("</strong>").toString(),
					"text/html; charset=utf-8");
			
			Multipart multipart = new MimeMultipart();
			
			//Setting email text message
			multipart.addBodyPart(messageBodyPart);
 
			//set the attachments to the email
	        message.setContent(multipart);
 
	        Transport transport = session.getTransport("smtp");
	        transport.connect("smtp.gmail.com", gmailAccount, gmailPassword);
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
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

	private Properties propertiesServerMail() {
		Properties props = new Properties();
		props.put("mail.smtp.host", gmailSMTPHost);
		props.put("mail.smtp.user", gmailAccount);
	    props.put("mail.smtp.clave", gmailPassword);
		props.put("mail.smtp.auth", AUTH_MAIL);
		props.put("mail.smtp.starttls.enable", AUTH_MAIL); //Para conectar de manera segura al servidor SMTP
		props.put("mail.smtp.port", gmailSMTPPort); //El puerto SMTP seguro de Google
		return props;
	}

}
