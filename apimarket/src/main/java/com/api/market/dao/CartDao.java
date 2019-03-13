package com.api.market.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Cart;
import com.api.market.entity.Usuario;

@Repository
public interface CartDao extends CrudRepository<Cart, Long> {
	
	public List<?> findAllCartsByUsuario(Usuario usuario);
}
