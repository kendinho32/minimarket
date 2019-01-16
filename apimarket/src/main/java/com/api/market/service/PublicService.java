package com.api.market.service;

import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.ProductRequest;

public interface PublicService {
	
	public ApiResponse createProduct(ProductRequest request) throws ErrorTecnicoException;

}
