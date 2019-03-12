package com.api.market.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.market.dao.CartDao;
import com.api.market.entity.Cart;
import com.api.market.exception.ErrorTecnicoException;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	CartDao cartRepository;

	@Override
	public Cart savedOrder(Cart request) throws ErrorTecnicoException {
		return cartRepository.save(request);
	}

}
