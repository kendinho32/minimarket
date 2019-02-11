package com.api.market;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.api.market.service.IUploadFileService;

@SpringBootApplication
public class ApimarketApplication implements CommandLineRunner {
	
	private static final Logger logger = LogManager.getLogger(ApimarketApplication.class);
	
	@Autowired
	IUploadFileService uploadFileService;

	public static void main(String[] args) {
		SpringApplication.run(ApimarketApplication.class, args);
	}
	
	/**
	 * Metodo que se ejecuta cuando se inicia el contexto de la aplicacion
	 * 
	 * @author kendall Navarro <knavarro@everis.com>
	 */
	@Override
	public void run(String... args) throws Exception {
		try {
			uploadFileService.init(); // Inicializa la carpeta Upload
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}

}

