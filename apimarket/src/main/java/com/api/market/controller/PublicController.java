package com.api.market.controller;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.market.entity.Products;
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
	 * Metodo que se encarga de procesar un archivo Excel para poblar la BD
	 * 
	 * @param file Archivo que contiene las caracteristicas de los productos
	 * @return {@link ApiResponse} Objeto que contiene la respuesta del metodo rest
	 * @throws ErrorNegocioException 
	 */
	@SuppressWarnings("resource")
	@PostMapping("/process-file")
	public ResponseEntity<?> listFile(@RequestParam("inventario") MultipartFile file) throws ErrorNegocioException {
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			Sheet firstSheet = workbook.getSheetAt(0);
	        Iterator<?> iterator = firstSheet.iterator();
	        
	        DataFormatter formatter = new DataFormatter();
	        while (iterator.hasNext()) {
	            Row nextRow = (Row) iterator.next();
	            Iterator<?> cellIterator = nextRow.cellIterator();
	            while(cellIterator.hasNext()) {
	                Cell cell = (Cell) cellIterator.next();
	                String contenidoCelda = formatter.formatCellValue(cell);
	                System.out.println("celda: " + contenidoCelda);
	            }   
	        }
		} catch (Exception e) {
			logger.error("No es posible leer el archivo");
			throw new ErrorNegocioException("No es posible guardar el producto","SOL-0004",e);
        }
		return new ResponseEntity<>(new ApiResponse(), HttpStatus.OK);
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
			ApiResponse response = publicService.dumpData(file);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible guardar la data en la BD");
            throw new ErrorNegocioException("No es posible guardar el producto","SOL-0004",et);
        }
	}
	
	/**
	 * Metodo que se encarga de recuperar todas las categorias almacenadas dentro de la BD
	 * 
	 * @return {@link ApiResponse} Objeto que contiene la respuesta de la ejecucion del metodo
	 * @throws ErrorNegocioException
	 */
	@GetMapping("/get-all-categories")
	public ResponseEntity<?> getAllCategories() throws ErrorNegocioException {
		try {
			List<?> listCategories = publicService.getAllCategories();
			ApiResponse response = new ApiResponse(true, "Categorias recuperadas exitosamente...!", listCategories);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar la lista de categorias de la BD");
            throw new ErrorNegocioException("No es posible recuperar la lista de categorias de la BD","SOL-0004",et);
        }
	}
	
	/**
	 * Metodo que se encarga de recuperar todos los productos registrados dentro de la BD
	 * 
	 * @return {@link ApiResponse} Objeto que contiene la respuesta de la ejecucion del metodo
	 * @throws ErrorNegocioException
	 */
	@GetMapping("/get-all-products")
	public ResponseEntity<?> getAllProducts() throws ErrorNegocioException {
		try {
			List<?> listProducts = publicService.getAllProducts();
			ApiResponse response = new ApiResponse(true, "Productos recuperados exitosamente...!", listProducts);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar la lista de categorias de la BD");
            throw new ErrorNegocioException("No es posible recuperar la lista de categorias de la BD","SOL-0004",et);
        }
	}
	
	@GetMapping("/get-products-by-categorie/{idCategorie}")
	public ResponseEntity<?> getAllProductsByCategorie(@PathVariable(value = "idCategorie") Long id) throws ErrorNegocioException {
		try {
			List<?> listProducts = publicService.getProductsByCategorie(id);
			ApiResponse response = new ApiResponse(true, "Productos recuperados exitosamente...!", listProducts);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar la lista de productos de la BD");
            throw new ErrorNegocioException("No es posible recuperar la lista de productos de la BD","SOL-0004",et);
        }
	}
	
	@GetMapping("/get-product-by-name/{name}")
	public ResponseEntity<?> getProductByName(@PathVariable(value = "name") String name) throws ErrorNegocioException {
		try {
			Products products = publicService.getProductByName(name);
			ApiResponse response = new ApiResponse(true, "Producto recuperado exitosamente...!", products);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar el producto de la BD");
            throw new ErrorNegocioException("No es posible recuperar el producto de la BD","SOL-0004",et);
        }
	}

}
