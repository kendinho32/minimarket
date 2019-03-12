package com.api.market.service;

import com.api.market.entity.Cart;
import com.api.market.exception.ErrorTecnicoException;

public interface CartService {
	
	public Cart savedOrder(Cart request)  throws ErrorTecnicoException;

}
