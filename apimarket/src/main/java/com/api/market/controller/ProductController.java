package com.api.market.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.market.entity.Products;
import com.api.market.entity.Usuario;
import com.api.market.exception.ErrorNegocioException;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.exception.ResourceNotFoundException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ProductRequest;
import com.api.market.service.IUploadFileService;
import com.api.market.service.JWTServiceImpl;
import com.api.market.service.ProductsService;
import com.api.market.service.UserService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins={"http://localhost:4200"})
public class ProductController {
	
	private static final Logger logger = LogManager.getLogger(ProductController.class);
	
	@Autowired
	private ProductsService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
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
	
	/**
	 * Metodo que se encarga de salvar la foto del producto dentro del proyecto
	 * 
	 * @param foto Archivo que representa la foto del producto
	 * @param id   Identificador que representa el registro del producto dentro de la
	 *             base de datos
	 * @return <ApiResponse> Objeto que contiene la respuesta del servicio de exito
	 *         o fracaso
	 * @throws ErrorTecnicoException 
	 */
	@PostMapping("/savePicture/{id}")
	public ResponseEntity<?> savePicture(@Valid @RequestParam("picture") MultipartFile foto, @PathVariable("id") Long id) throws ErrorTecnicoException {
		logger.info("Se invoca el metodo savePicture, para almacenar la foto del producto");
		String name = foto.getOriginalFilename();
		String[] propiedadesNombre = name.replace(".", "-").split("-");
		ApiResponse api = null;

		if (propiedadesNombre[1].equalsIgnoreCase("jpg") || propiedadesNombre[1].equalsIgnoreCase("png")
				|| propiedadesNombre[1].equalsIgnoreCase("jpge")) {
			Products product = productService.getProductById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

			// Elimino la foto anterior
			if (product.getImage() != null) {
				uploadFileService.delete(product.getImage());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				logger.error("Error --> ", e);
			}

			product.setImage(uniqueFilename);
			productService.updateProduct(product);

			logger.info("Picture save successfully");
			api = new ApiResponse(true, "Picture save successfully", product);
		} else {
			logger.info("Extension no valida");
			api = new ApiResponse(false, "Extension no valida");
		}
		return new ResponseEntity<>(api, HttpStatus.OK);
	}
	
	/**
	 * Metodo que se encarga de almacenar un producto dentro de la BD
	 * 
	 * @param request Objeto que contiene las caracteristicas del producto que se
	 * 		desea almacenar en la BD
	 * @return {@link ApiResponse} Objeto que contiene la respuesta del metodo rest
	 */
	@PutMapping("/update-product/{id}")
	public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductRequest request, @PathVariable("id") Long id) throws ErrorNegocioException {
		try {
			Products product = productService.getProductById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
			product.setName(request.getName());
			product.setDescription(request.getDescription());
			product.setPrice(request.getPrice());
			product.setQuantity(request.getQuantity());
			product.setOutstanding(request.isOutstanding());
			product.setStatus(request.isStatus());
			
			ApiResponse response = productService.updateProduct(product);
			response.setData(product);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible actualizar el producto");
            throw new ErrorNegocioException("No es posible actualizar el producto","SOL-0004",et);
        }
	}
	
	@GetMapping("/get-product/{id}/user/{idUser}")
	public ResponseEntity<?> getProductById(@PathVariable("id") Long id, @PathVariable("idUser") Long idUser, HttpServletRequest servletRequest) throws ErrorNegocioException {		
		try {
			Usuario user = userService.getUsuario(idUser);
			
			if (user == null) {
				return new ResponseEntity<>(new ApiResponse(false, "El usuario no existe!"), HttpStatus.BAD_REQUEST);
			}
			String[] header = servletRequest.getHeader(JWTServiceImpl.HEADER_STRING).split(" ");
			
			// Verifico que el token corresponda al usuario
			if(!userService.verificarToken(user, header[1])) {
				return new ResponseEntity<>(new ApiResponse(false, "El token no corresponde al usuario!"), HttpStatus.UNAUTHORIZED);
			}
			
			Products product = productService.getProductById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
			
			if (product == null) {
				return new ResponseEntity<>(new ApiResponse(false, "Producto no encontrado!"), HttpStatus.NOT_FOUND);
			}
			
			return new ResponseEntity<>(new ApiResponse(true, "producto encontrado exitosamente!", product), HttpStatus.OK);
		} catch (ErrorTecnicoException e) {
			logger.error("No es posible encontrar el producto");
            throw new ErrorNegocioException("No es posible encontrar el producto","SOL-0004",e);
		}
	}

}
