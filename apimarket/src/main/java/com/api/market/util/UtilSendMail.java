package com.api.market.util;

import java.io.IOException;
import static j2html.TagCreator.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api.market.entity.Cart;
import com.api.market.payload.ContactRequest;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@Component
public class UtilSendMail {
	
	private static final Logger logger = LogManager.getLogger(UtilSendMail.class);
	
	@Value("${gmail.smtp.host}")
	private String gmailSMTPHost;
	
	@Value("${gmail.smtp.port}")
	private String gmailSMTPPort;
	
	@Value("${gmail.user.account}")
	private String gmailAccount;
	
	@Value("${gmail.user.password}")
	private String gmailPassword;
	
	@Value("${api.key.send.mail}")
	private String apiKeySendMail;
	
	@Value("${api.url.from.img}")
	private String urlFromImg;
	
	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	public boolean sendEmail(ContactRequest request){
		logger.info("Se ejecuta el metodo para el envio de correo");
		Email from = new Email("dulceregalokye@gmail.com");
	    String subject = new StringBuilder().append("Formulario Contacto - ").append(request.getSubject()).toString();
	    Email to = new Email(new StringBuilder().append(gmailAccount).append(";").append(request.getEmail()).toString());
		
		boolean response = false;		
		try {
			Content content = new Content("text/html", new StringBuilder().append("<strong>").append(request.getMessage()).append("</strong>").toString());
			Mail mail = new Mail(from, subject, to, content);
			
			SendGrid sg = new SendGrid(apiKeySendMail);
		    Request req = new Request();
			
		    req.setMethod(Method.POST);
		    req.setEndpoint("mail/send");
		    req.setBody(mail.build());
		    Response res = sg.api(req);
		    
		    if(res.getStatusCode() == 202) {
		    	response = true;
		    }
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return response;
	}
	
	public boolean sendOrderStore(Cart cart){
		logger.info("Se ejecuta el metodo para el envio de correo para la tienda");
		Email from = new Email(gmailAccount);
	    String subject = new StringBuilder().append("Orden de Compra Realizada").toString();
		boolean response = false;
		Email to = new Email(gmailAccount);
					
		StringBuilder html = new StringBuilder();
		StringBuilder msj = new StringBuilder();
		msj.append("Se ha realizado una nueva orden por el usuario: " + cart.getUsuario().getName() + ". ")
		   .append("A continuación te mostramos un detalle de la compra realizada: ");

			try {
				html(
				        header(
				                title("Orden de compra realizada")
				        ),
				        body(
				        		h1("Orden de Compra realizada"),
				                img().withSrc("https://www.baffler.mx/images/baff%20siteartboard%2039.png?crc=62544678"),
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
				        		p("Correo del usuario: " + cart.getUsuario().getEmail()).with(
			                			br()
			                	),
				        		p("Télefono de contacto: " + cart.getUsuario().getPhones().get(0).getContrycode() + cart.getUsuario().getPhones().get(0).getCitycode() + cart.getUsuario().getPhones().get(0).getNumber()).with(
			                			br()
			                	),
				                div(attrs("#employees"),
				                h2("Productos Ordenados: "),
				                table().with(
				                		each(filter(cart.getProducts(), product -> product != null), product ->
				                        	tr().with(
				                        			td().with(
				                        				img().withSrc(new StringBuilder().append(urlFromImg).append(product.getImage()).toString())
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
					                        ),
				                        	h1("Tipo de Despacho: " + cart.getTipo()).with(
							        				br()
							        		),
							        		p("Dirección Seleccionada: " + cart.getDireccion() != null ? new StringBuilder().append(cart.getDireccion().getComuna()).append(" ").append(cart.getDireccion().getCalle()).append(" ").append(cart.getDireccion().getNro()).toString() : "").with(
						                			br()
						                	)	                   
				                        )				                        
				                    )
				        ) // close body
				).render(html);
				Content content = new Content("text/html", html.toString());
				Mail mail = new Mail(from, subject, to, content);
				
				SendGrid sg = new SendGrid(apiKeySendMail);
			    Request req = new Request();
				
			    req.setMethod(Method.POST);
			    req.setEndpoint("mail/send");
			    req.setBody(mail.build());
			    Response res = sg.api(req);
			    
			    if(res.getStatusCode() == 202) {
			    	response = true;
			    }
			} catch (IOException e) {
				logger.error("Error --> ", e);
			}
		
		return response;
	}
	
	public boolean sendOrderUser(Cart cart){
		logger.info("Se ejecuta el metodo para el envio de correo para el usuario");
		Email from = new Email(gmailAccount);
	    String subject = new StringBuilder().append("Orden de Compra Realizada").toString();
		boolean response = false;
		Email to = new Email(cart.getUsuario().getEmail());
		
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
				                        				img().withSrc(new StringBuilder().append(urlFromImg).append(product.getImage()).toString())
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
		                        	h1("Tipo de Despacho: " + cart.getTipo()).with(
					        				br()
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
					                ),
				                	div(attrs("#resumen3"),
					                		p("Tambien puedes cancelar utilizando el codigo QR de Mercado Pago"),
						                	p().with(
						                			img().withSrc(new StringBuilder().append(urlFromImg).append("qrMercadoPago.png").toString())
						                	)
						                )
				        ) // close body
				).render(html);
				
				Content content = new Content("text/html", html.toString());
				Mail mail = new Mail(from, subject, to, content);
				
				SendGrid sg = new SendGrid(apiKeySendMail);
			    Request req = new Request();
				
			    req.setMethod(Method.POST);
			    req.setEndpoint("mail/send");
			    req.setBody(mail.build());
			    Response res = sg.api(req);
			    
			    if(res.getStatusCode() == 202) {
			    	response = true;
			    }
			} catch (IOException e) {
				logger.error("Error --> ", e);
			}		
		return response;
	}

}
