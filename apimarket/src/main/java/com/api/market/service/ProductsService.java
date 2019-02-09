package com.api.market.service;

import java.util.List;

import com.api.market.entity.Categories;
import com.api.market.entity.Products;

public interface ProductsService {
	
	public void savedAll(List<?> products);
	public List<?> getAllProducts();
	public List<?> getProductsByCategorie(Categories categorie);
	public Products getProductByName(String name);

}
