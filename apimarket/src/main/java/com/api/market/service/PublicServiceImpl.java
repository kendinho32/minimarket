package com.api.market.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api.market.entity.Categories;
import com.api.market.entity.Products;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ContactRequest;
import com.api.market.util.UtilSendMail;

@Service
public class PublicServiceImpl implements PublicService {
	
	@Autowired
	private CategoriesService categorieService;
	
	@Autowired
	private ProductsService productService;
	
	@Autowired
	private UtilSendMail sendMail;

	@Override
	public List<?> getAllCategories() throws ErrorTecnicoException {
		return categorieService.getAllCategories();
	}

	@Override
	public List<?> getAllProducts() throws ErrorTecnicoException {
		return productService.getAllProducts();
	}
	
	@Override
	public List<?> getAllProductsRecommended() throws ErrorTecnicoException {
		return productService.getAllProductsRecommended();
	}

	@Override
	public List<?> getProductsByCategorie(Long id) throws ErrorTecnicoException {
		Categories categories = categorieService.loadCategorieById(id);
		return productService.getProductsByCategorie(categories);
	}

	@Override
	public List<?> getProductByName(String name) throws ErrorTecnicoException {
		return productService.getProductByName(name);
	}

	@Override
	public List<?> getAllProductsByStatus(boolean status) throws ErrorTecnicoException {
		return productService.getAllProductsByStatus(status);
	}

	@Override
	public boolean sendFormContact(ContactRequest request) throws ErrorTecnicoException {
		return sendMail.sendEmail(request);
	}

}
