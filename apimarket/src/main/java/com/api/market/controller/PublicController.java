package com.api.market.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.market.exception.ErrorNegocioException;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ProductRequest;
import com.api.market.service.CategoriesService;
import com.api.market.service.PublicService;

@RestController
@RequestMapping("/api/auth")
public class PublicController {
	
	private static final Logger logger = LogManager.getLogger(PublicController.class);

	
	@Autowired
	private CategoriesService catService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private PublicService publicService;
	
	@GetMapping("/prueba")
	public String prueba() {
		logger.info("Se invoca al servicio Prueba para probar el API REST");
		catService.loadCategorieById(1L);

		return "Prueba con exito";
	}
	
	/**
	 * Metodo que se encarga de almacenar un producto dentro de la BD
	 * 
	 * @param request Objeto que contiene las caracteristicas del producto que se
	 * 		desea almacenar en la BD
	 * @return {@link ApiResponse} Objeto que contiene la respuesta del metodo rest
	 */
	@PostMapping("/register-product")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody ProductRequest request) throws ErrorNegocioException {
		try {
			ApiResponse response = publicService.createProduct(request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible guardar el producto");
            throw new ErrorNegocioException("No es posible guardar el producto","SOL-0004",et);
        }
	}
	
	/**
	 * Metodo que se encarga de procesar un archivo para poblar la BD
	 * 
	 * @param file Archivo que contiene las caracteristicas de los productos
	 * @return {@link ApiResponse} Objeto que contiene la respuesta del metodo rest
	 */
	@PostMapping("/process-file")
	public ResponseEntity<?> listFile(@RequestParam("inventario") MultipartFile file) {
		
		return new ResponseEntity<>(new ApiResponse(), HttpStatus.OK);
	}

}
