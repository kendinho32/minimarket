package com.api.market.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.market.dao.ProductDao;
import com.api.market.entity.Categories;
import com.api.market.entity.Products;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ProductRequest;

@Service
public class PublicServiceImpl implements PublicService {
	
	@Autowired
	private CategoriesService categorieService;
	
	@Autowired
	private ProductDao productDao;

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

}
