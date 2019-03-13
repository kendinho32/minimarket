package com.api.market.util;

import java.io.IOException;
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

import static j2html.TagCreator.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api.market.entity.Cart;
import com.api.market.entity.Usuario;
import com.api.market.payload.ContactRequest;
import com.api.market.service.UserService;

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
	
	@Autowired
	private UserService userService;
	
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
	
	public boolean sendOrderStore(Cart cart){
		logger.info("Se ejecuta el metodo para el envio de correo para la tienda");
		boolean response = false;
		String destinations = new StringBuilder().append(gmailAccount).toString();
		
		// busco el usuario que creo la orden de compra
		Usuario user =userService.getUsuario(cart.getIdUsuario());
		
		Properties props = propertiesServerMail();
		final String[] emailDestinations = destinations.split(";");
 
		Session session = setSessionServerMail(props, gmailAccount, gmailPassword);
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(gmailAccount));
			
			for (String emailDestination : emailDestinations) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestination));
			}
 
			message.setSubject(new StringBuilder().append("Orden de Compra Realizada").toString());
 
			BodyPart messageBodyPart = new MimeBodyPart();
			
			StringBuilder html = new StringBuilder();
			StringBuilder msj = new StringBuilder();
			msj.append("Se ha realizado una nueva orden por el usuario: " + user.getName() + ". ")
			.append("A continuación te mostramos un detalle de la compra realizada: ");

			try {
				html(
				        header(
				                title("Orden de compra realizada")
				        ),
				        body(
				        		h1("Orden de Compra realizada"),
				                img().withSrc("https://josefacchin.com/wp-content/uploads/2016/07/Montar-tienda-online-2-610x305.png"),
				                div(attrs("#resumen"),
				                	p().with(
				                			h2(
				                				p(msj.toString())
				                			)
				                		)
				                ),
				                h1("Datos del usuario").with(
				        				br()
				        		),
				        		p("Correo del usuario: " + user.getEmail()).with(
			                			br()
			                	),
				        		p("Télefono de contacto: " + user.getPhones().get(0).getContrycode() + user.getPhones().get(0).getCitycode() + user.getPhones().get(0).getNumber()).with(
			                			br()
			                	),
				                div(attrs("#employees"),
				                h2("Productos Ordenados: "),
				                table().with(
				                		each(filter(cart.getProducts(), product -> product != null), product ->
				                        	tr().with(
				                        			td().with(
				                        				img().withSrc(product.getImage())
					                        	     ),
					                        	     td().with(
					                        	    	h2(product.getName())
					                        	     ),
					                        	     td().with(
					                        	     	p("Precio: " + String.valueOf(product.getPrice().intValue()))
						                        	 ),
					                        	     td().with(
					                        	    	p("Cantidad: " + String.valueOf(product.getQuantitySelect().intValue()))
						                        	 ),
					                        	     td().with(
					                        	     	p("Total: " + String.valueOf(product.getPrice().intValue() * product.getQuantitySelect().intValue()))
						                        	 )
					                        )
					                        ),
				                        	tr().with(
				                        			td().with(),
					                        	    td().with(),
					                        	    td().with(),
					                        	    td().with(),
					                        	    td().with(
					                        	      	p().with(
					                        	      		h2("Total Compra: " + (cart.getTotal().intValue() + cart.getShipping().intValue()))
					                        	      	)
						                        	)
					                        )
				                        )				                        
				                    )
				        ) // close body
				).render(html);
			} catch (IOException e) {
				logger.error("Error --> ", e);
			}
			
			messageBodyPart.setContent(html.toString(),
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
	
	public boolean sendOrderUser(Cart cart){
		logger.info("Se ejecuta el metodo para el envio de correo para la tienda");
		boolean response = false;
		
		// busco el usuario que creo la orden de compra
		Usuario user = userService.getUsuario(cart.getIdUsuario());
		
		String destinations = new StringBuilder().append(user.getEmail()).toString();
		
		Properties props = propertiesServerMail();
		final String[] emailDestinations = destinations.split(";");
 
		Session session = setSessionServerMail(props, gmailAccount, gmailPassword);
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(gmailAccount));
			
			for (String emailDestination : emailDestinations) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestination));
			}
 
			message.setSubject(new StringBuilder().append("Orden de Compra Realizada").toString());
 
			BodyPart messageBodyPart = new MimeBodyPart();
			
			StringBuilder html = new StringBuilder();
			StringBuilder msj = new StringBuilder();
			msj.append("Gracias por tu compra en MiniMarket Dulce Alegria KyE, el mini market que lo pone todo a tu alcance. ")
			.append("A continuación te mostramos un detalle de tu compra, si tienes alguna duda ó algo no esta bien por favor comunicate con nostros inmediatamente.");

			try {
				html(
				        header(
				                title("Orden de compra realizada")
				        ),
				        body(
				                h1("Orden de Compra realizada"),
				                img().withSrc("https://josefacchin.com/wp-content/uploads/2016/07/Montar-tienda-online-2-610x305.png"),
				                div(attrs("#resumen"),
				                	p().with(
				                			h2(
				                				p(msj.toString())
				                			)
				                		)
				                ),
				                div(attrs("#employees"),
				                h2("Productos Ordenados: "),
				                table().with(
				                		each(filter(cart.getProducts(), product -> product != null), product ->
				                        	tr().with(
				                        			td().with(
				                        				img().withSrc(product.getImage())
					                        	     ),
					                        	     td().with(
					                        	    	h2(product.getName())
					                        	     ),
					                        	     td().with(
					                        	     	p("Precio: " + String.valueOf(product.getPrice().intValue()))
						                        	 ),
					                        	     td().with(
					                        	    	p("Cantidad: " + String.valueOf(product.getQuantitySelect().intValue()))
						                        	 ),
					                        	     td().with(
					                        	     	p("Total: " + String.valueOf(product.getPrice().intValue() * product.getQuantitySelect().intValue()))
						                        	 )
					                        )
					                        ),
				                        	tr().with(
				                        			td().with(),
					                        	    td().with(),
					                        	    td().with(),
					                        	    td().with(),
					                        	    td().with(
					                        	      	p().with(
					                        	      		h2("Total Compra: " + (cart.getTotal().intValue() + cart.getShipping().intValue()))
					                        	      	)
						                        	)
					                        )
				                        )				                        
				                    ),
				                	div(attrs("#resumen2"),
				                		p("El metodo de pago seleccionado por ti fue: ").with(h2(cart.getPago())),
					                	p().with(
					                			h2(
					                				p("Si seleccionaste efectivo y quieres cambiar el pago por transferencia puedes hacerlo con los siguientes datos: ")
					                			)
					                	),
					                	p("Banco: Santander").with(
					                			br()
					                	),
					                	p("Tipo de cuenta: Corriente").with(
					                			br()
					                	),
					                	p("Número de cuenta: 0-000-69-98344-8").with(
					                			br()
					                	),
					                	p("Titular: Kendall Navarro").with(
					                			br()
					                	),
					                	p("Correo: dulcealegriakye@gmail.com").with(
					                			br()
					                	),
					                	p("Puedes ver tus compras en la sección mis compras.")
					                )
				        ) // close body
				).render(html);
			} catch (IOException e) {
				logger.error("Error --> ", e);
			}
			
			messageBodyPart.setContent(html.toString(),
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
