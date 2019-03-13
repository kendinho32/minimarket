package com.api.market.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.market.dao.CartDao;
import com.api.market.entity.Cart;
import com.api.market.entity.Usuario;
import com.api.market.exception.ErrorTecnicoException;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	CartDao cartRepository;

	@Override
	public Cart savedOrder(Cart request) throws ErrorTecnicoException {
		return cartRepository.save(request);
	}

	@Override
	public List<?> getOrdersByUser(Usuario usuario) throws ErrorTecnicoException {
		return cartRepository.findAllCartsByUsuario(usuario);
	}

}
