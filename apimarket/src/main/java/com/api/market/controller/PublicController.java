package com.api.market.controller;

import java.io.IOException;
import java.net.MalformedURLException;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

import com.api.market.entity.Cart;
import com.api.market.entity.Products;
import com.api.market.entity.ProductsCart;
import com.api.market.entity.Usuario;
import com.api.market.exception.ErrorNegocioException;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.exception.ResourceNotFoundException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ContactRequest;
import com.api.market.payload.LoginRequest;
import com.api.market.payload.SignUpRequest;
import com.api.market.service.CartService;
import com.api.market.service.CategoriesService;
import com.api.market.service.IUploadFileService;
import com.api.market.service.JWTService;
import com.api.market.service.JpaUserDetailsService;
import com.api.market.service.ProductsCartService;
import com.api.market.service.ProductsService;
import com.api.market.service.PublicService;
import com.api.market.service.UserService;
import com.api.market.util.UtilSendMail;
import com.sendgrid.*;

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
	
	@Autowired
	private JpaUserDetailsService userDetailsService;
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductsService productService;
	
	@Autowired
	private ProductsCartService productCartService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private UtilSendMail sendMail;
	
	@GetMapping("/prueba")
	public String prueba() {
		logger.info("Se invoca al servicio Prueba para probar el API REST");
		catService.loadCategorieById(1L);

		return "Prueba con exito";
	}
	
	/**
	 * Metodo que se encarga de registrar un usuario dentro de la BD con todas sus 
	 * caracteristicas
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest request) throws IOException {
		
		if (userDetailsService.verificarEmail(request.getEmail())) {
			return new ResponseEntity(new ApiResponse(false, "El correo electronico ya se encuentra registrado!"), HttpStatus.BAD_REQUEST);
		}
		
		Usuario user = userService.generateAndSaveUser(request);
		ApiResponse response = new ApiResponse(true, "usuario registrado exitosamente!", user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Metodo que se encarga de autenticar las credenciales de un usuario
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/loginUser")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) throws IOException {
		Usuario user = userDetailsService.verificarUser(request.getEmail(), request.getPassword());

		if (user == null) {
			return new ResponseEntity(new ApiResponse(false, "Usuario o contraseña inválidos!"), HttpStatus.UNAUTHORIZED);
		}
		
		user.setToken(jwtService.createTokenByUser(user));
		user = userService.updateUser(user);
		
		return new ResponseEntity<>(new ApiResponse(true, "usuario logeado exitosamente!", user), HttpStatus.OK);
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
	
	/**
	 * Metodo que se encarga de recuperar todos los productos registrados dentro de la BD
	 * 
	 * @return {@link ApiResponse} Objeto que contiene la respuesta de la ejecucion del metodo
	 * @throws ErrorNegocioException
	 */
	@GetMapping("/get-all-products-recommended")
	public ResponseEntity<?> getAllProductsRecommended() throws ErrorNegocioException {
		try {
			List<?> listProducts = publicService.getAllProductsRecommended();
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
			List<?> products = publicService.getProductByName(name);
			ApiResponse response = new ApiResponse(true, "Producto recuperado exitosamente...!", products);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar el producto de la BD");
            throw new ErrorNegocioException("No es posible recuperar el producto de la BD","SOL-0004",et);
        }
	}
	
	/**
	 * Metodo que se encarga de traer la imagen guardada para el producto
	 * 
	 * @param filename
	 * @return
	 */
	@GetMapping("/get-picture-product/{filename:.+}")
	public ResponseEntity<Resource> getPicture(@PathVariable String filename) {
		Resource recurso = null;

		try {
			recurso = uploadFileService.loadImg(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
	
	@PostMapping("/send-contact")
	public ResponseEntity<?> sendContact(@RequestBody ContactRequest request) throws ErrorNegocioException {
		ApiResponse response = new ApiResponse();
		try {
			boolean result = publicService.sendFormContact(request);
			if(result) {
				response.setSuccess(true);
				response.setMessage("Formulario de contacto enviado satisfactoriamente...!");
			} else {
				response.setSuccess(false);
				response.setMessage("Error al enviar el correo de  contacto...!");
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar el producto de la BD");
            throw new ErrorNegocioException("No es posible recuperar el producto de la BD","SOL-0004",et);
        }
	}
	
	@GetMapping("/get-product/{id}")
	public ResponseEntity<?> getProductById(@PathVariable("id") Long id) throws ErrorNegocioException {		
		try {
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
	
	/**
	 * Metodo que se encarga de almacenar un producto dentro de la BD
	 * 
	 * @param request Objeto que contiene las caracteristicas del producto que se
	 * 		desea almacenar en la BD
	 * @return {@link ApiResponse} Objeto que contiene la respuesta del metodo rest
	 */
	@PostMapping("/update-product")
	public ResponseEntity<?> updateProduct(@Valid @RequestBody Products request) throws ErrorNegocioException {
		try {
			ApiResponse response = productService.updateProduct(request);
			response.setData(request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible actualizar el producto");
            throw new ErrorNegocioException("No es posible actualizar el producto","SOL-0004",et);
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
	
	@SuppressWarnings("unchecked")
	@PostMapping("/sendOrder/{idUsuario}")
	public ResponseEntity<?> sendOrder(@RequestBody Cart request, @PathVariable("idUsuario") Long idUsuario) throws ErrorNegocioException {
		ApiResponse response = new ApiResponse();
		try {
			request.setProducts((List<ProductsCart>) productCartService.savedAllProducts(request.getProducts()));
			// busco el usuario que crea la orden
			Usuario user = userService.getUsuario(idUsuario);
			request.setUsuario(user);
			Cart cart = cartService.savedOrder(request);
			
			// se envia el correo a la tienda
			sendMail.sendOrderStore(request);
			// se envia el correo al usuario
			sendMail.sendOrderUser(request);
			
			response.setSuccess(true);
			response.setMessage("Orden ejecutada exitosamente...!");
			response.setData(cart);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible almacenar la orden");
            throw new ErrorNegocioException("No es posible almacenar la orden","SOL-0004",et);
        }
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/get-orders-user/{id}")
	public ResponseEntity<?> getOrdersByUser(@PathVariable("id") Long id) throws ErrorNegocioException {
		ApiResponse response = new ApiResponse();
		try {
			Usuario user = userService.getUsuario(id);
			List<Cart> carts = (List<Cart>) cartService.getOrdersByUser(user);			
			response.setSuccess(true);
			response.setMessage("Ordenes recuperadas exitosamente...!");
			response.setData(carts);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar las ordenes");
            throw new ErrorNegocioException("No es posible recuperar las ordenes","SOL-0004",et);
        }
	}
	
	@GetMapping("/testSendMail")
	public ResponseEntity<?> testSendMail() throws ErrorNegocioException, IOException {
		ApiResponse response = new ApiResponse();
		Email from = new Email("dulceregalokye@gmail.com");
	    String subject = "Hello World from the SendGrid Java Library!";
	    Email to = new Email("kendinho22@gmail.com");
	    Content content = new Content("text/plain", "Hello, Email!");
	    Mail mail = new Mail(from, subject, to, content);

	    SendGrid sg = new SendGrid("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");
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response res = sg.api(request);
	      response.setData(res);
	    } catch(IOException et) {
            logger.error("No es posible recuperar las ordenes");
            throw new ErrorNegocioException("No es posible recuperar las ordenes","SOL-0004",et);
        }
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
