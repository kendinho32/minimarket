package com.api.market.service;

import java.util.List;

import com.api.market.entity.Cart;
import com.api.market.exception.ErrorTecnicoException;

public interface CartService {
	
	public Cart savedOrder(Cart request)  throws ErrorTecnicoException;
	public List<?> getOrdersByUser(Long id) throws ErrorTecnicoException;

}
