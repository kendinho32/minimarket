package com.api.market.service;

import java.util.List;

import com.api.market.entity.ProductsCart;
import com.api.market.exception.ErrorTecnicoException;

public interface ProductsCartService {
	
	public List<?> savedAllProducts(List<ProductsCart> list) throws ErrorTecnicoException;

}
