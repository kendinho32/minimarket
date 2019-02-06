package com.api.market.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.market.dao.ProductDao;
import com.api.market.entity.Categories;
import com.api.market.entity.Products;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ProductRequest;
import com.api.market.util.UtilFile;

@Service
public class PublicServiceImpl implements PublicService {
	
	@Autowired
	private CategoriesService categorieService;
	
	@Autowired
	private ProductsService productService;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private UtilFile utilFile;

	@Override
	public ApiResponse createProduct(ProductRequest request)  throws ErrorTecnicoException{
		ApiResponse response = new ApiResponse(false, null);
		Categories categories = categorieService.loadCategorieById(request.getCategorieId());
		
		Products product = new Products();
		product.setCategorie(categories);
		product.setDescription(request.getDescription());
		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setQuantity(request.getQuantity());
		
		productDao.save(product);
		response.setMessage("Producto almacenado con exito");
		response.setSuccess(true);
		
		return response;
	}

	@Override
	public ApiResponse dumpData(MultipartFile file) throws ErrorTecnicoException {
		ApiResponse response = new ApiResponse(false, "No se pudo almacenar la data del archivo");
		List<Products> listProducts = new ArrayList<>();
		List<String[]> dataProcess = utilFile.processCSV(file);
		int i = 0;
		
		if(!dataProcess.isEmpty()) {
			for(String[] element : dataProcess) {
				if(i < 100) {
					Products product = new Products();
					// Se busca la categoria
					Categories categories = categorieService.loadCategorieByName(element[9]);
					product.setCategorie(categories);
					product.setDescription(element[2]);
					product.setName(element[2]);
					product.setPrice((long) Integer.parseInt(element[5]));
					product.setQuantity(Float.valueOf(element[3]));
					
					listProducts.add(product);
				} else {
					break;
				}	
				i++;
			}
			productDao.saveAll(listProducts);
			response = new ApiResponse(true, "Data almacenada exitosamente...!");
		}
		
		return response;
	}

	@Override
	public List<?> getAllCategories() throws ErrorTecnicoException {
		return categorieService.getAllCategories();
	}

	@Override
	public List<?> getAllProducts() throws ErrorTecnicoException {
		return productService.getAllProducts();
	}

}
