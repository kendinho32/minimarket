package com.api.market.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ProductRequest;

public interface PublicService {
	
	public ApiResponse createProduct(ProductRequest request) throws ErrorTecnicoException;
	public ApiResponse dumpData(MultipartFile file) throws ErrorTecnicoException;
	public List<?> getAllCategories() throws ErrorTecnicoException;
	public List<?> getAllProducts() throws ErrorTecnicoException;

}
