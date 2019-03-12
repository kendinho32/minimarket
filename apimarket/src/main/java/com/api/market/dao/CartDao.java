package com.api.market.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Cart;

@Repository
public interface CartDao extends CrudRepository<Cart, Long> {

}
