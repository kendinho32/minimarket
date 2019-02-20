package com.api.market.service;

import java.util.List;

import com.api.market.entity.Products;
import com.api.market.exception.ErrorTecnicoException;

public interface PublicService {
	
	public List<?> getAllCategories() throws ErrorTecnicoException;
	public List<?> getAllProducts() throws ErrorTecnicoException;
	public List<?> getAllProductsByStatus(boolean status) throws ErrorTecnicoException;
	public List<?> getProductsByCategorie(Long id) throws ErrorTecnicoException;
	public Products getProductByName(String name) throws ErrorTecnicoException;
	public boolean sendFormContact() throws ErrorTecnicoException;

}
