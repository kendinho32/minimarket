package com.api.market.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.api.market.service.ProductsService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	
	private static final Logger logger = LogManager.getLogger(ProductController.class);
	
	@Autowired
	private ProductsService productService;
	
	/**
	 * Metodo que se encarga de almacenar un producto dentro de la BD
	 * 
	 * @param request Objeto que contiene las caracteristicas del producto que se
	 * 		desea almacenar en la BD
	 * @return {@link ApiResponse} Objeto que contiene la respuesta del metodo rest
	 */
	@PostMapping("/register-product")
	public ResponseEntity<?> registerProduct(@Valid @RequestBody ProductRequest request) throws ErrorNegocioException {
		try {
			ApiResponse response = productService.createProduct(request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible guardar el producto");
            throw new ErrorNegocioException("No es posible guardar el producto","SOL-0004",et);
        }
	}
	
	/**
	 * Metodo rest que se encarga de volcar la DATA de un file dentro de la BD en la columna
	 * productos
	 * 
	 * @param file File que contiene la lista de productos que se desea guardar dentro de la BD
	 * @return {@link ApiResponse} Objeto que contiene la respuesta de la ejecucion del metodo
	 * @throws ErrorNegocioException
	 */
	@PostMapping("/update-inventario")
	public ResponseEntity<?> updateInventario(@RequestParam("inventario") MultipartFile file) throws ErrorNegocioException {
		try {
			ApiResponse response = productService.dumpData(file);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible guardar la data en la BD");
            throw new ErrorNegocioException("No es posible guardar el producto","SOL-0004",et);
        }
	}

}
