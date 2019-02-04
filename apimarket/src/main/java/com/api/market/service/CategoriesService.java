package com.api.market.service;

import java.util.List;

import com.api.market.entity.Categories;

public interface CategoriesService {
	
	public Categories loadCategorieById(Long id);
	public Categories loadCategorieByName(String name);
	public List<?> getAllCategories();

}
