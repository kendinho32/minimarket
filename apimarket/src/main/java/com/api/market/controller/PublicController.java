package com.api.market.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.market.service.CategoriesService;

@RestController
@RequestMapping("/api/auth")
public class PublicController {
	
	private static final Logger logger = LogManager.getLogger(PublicController.class);

	
	@Autowired
	private CategoriesService catService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping("/prueba")
	public String prueba() {
		logger.info("Se invoca al servicio Prueba para probar el API REST");
		catService.loadCategorieById(1L);

		return "Prueba con exito";
	}

}
