package com.api.market.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.api.market.entity.Categories;
import com.api.market.entity.Products;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ProductRequest;

public interface ProductsService {
	
	public ApiResponse createProduct(ProductRequest request) throws ErrorTecnicoException;
	public ApiResponse updateProduct(Products product) throws ErrorTecnicoException;
	public ApiResponse dumpData(MultipartFile file) throws ErrorTecnicoException;
	public void savedAll(List<?> products);
	public List<?> getAllProducts();
	public List<?> getAllProductsRecommended();
	public List<?> getAllProductsByStatus(boolean status);
	public List<?> getProductsByCategorie(Categories categorie);
	public Products getProductByName(String name);
	public Optional<Products> getProductById(Long id) throws ErrorTecnicoException;

}
