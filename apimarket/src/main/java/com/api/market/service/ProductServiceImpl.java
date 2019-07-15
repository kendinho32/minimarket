package com.api.market.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class ProductServiceImpl implements ProductsService {
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	private UtilFile utilFile;
	
	@Autowired
	private CategoriesService categorieService;
	
	@Override
	public ApiResponse createProduct(ProductRequest request)  throws ErrorTecnicoException{
		ApiResponse response = new ApiResponse(false, null);
		Categories categories = categorieService.loadCategorieById(request.getCategorieId());
		
		Products product = new Products();
		if(request.getId() != null) {
			// se esta actualizando el producto
			product.setId(request.getId());
		}
		
		product.setCategorie(categories);
		product.setDescription(request.getDescription());
		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setQuantity(request.getQuantity());
		product.setOutstanding(request.isOutstanding());
		product.setStatus(request.isStatus());
		
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
				if(i < 10) {
					String[] array = element[0].split(";");
					Products product = new Products();
					// Se busca la categoria
					Categories categories = categorieService.loadCategorieByName(array[5]);
					product.setSku(array[0]);
					product.setCategorie(categories);
					product.setDescription(array[2]);
					product.setName(array[1]);
					product.setPrice((long) Integer.parseInt(array[3]));
					product.setQuantity(Float.valueOf(array[4]));
					product.setOutstanding(false);
					product.setStatus(true);
					
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

	@SuppressWarnings("unchecked")
	@Override
	public void savedAll(List<?> products) {
		productDao.saveAll((Iterable<Products>) products);
	}

	@Override
	public List<?> getAllProducts() {
		return (List<?>) productDao.findAll();
	}
	
	@Override
	public List<?> getAllProductsRecommended() {
		return (List<?>) productDao.findAllProductsByOutstanding(true);
	}

	@Override
	public List<?> getProductsByCategorie(Categories categorie) {
		return productDao.findAllProductsByCategorie(categorie);
	}

	@Override
	public List<?> getProductByName(String name) {
		return productDao.findProductsByName(name);
	}

	@Override
	public Optional<Products> getProductById(Long id) throws ErrorTecnicoException {
		return productDao.findById(id);
	}

	@Override
	public ApiResponse updateProduct(Products product) throws ErrorTecnicoException {
		ApiResponse response = new ApiResponse(false, null);
		productDao.save(product);
		response.setMessage("Producto actualizado con exito");
		response.setSuccess(true);
		
		return response;
	}

	@Override
	public List<?> getAllProductsByStatus(boolean status) {
		return productDao.findAllProductsByStatus(status);
	}
}
