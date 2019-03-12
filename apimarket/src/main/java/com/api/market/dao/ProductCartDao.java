package com.api.market.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.ProductsCart;

@Repository
public interface ProductCartDao extends CrudRepository<ProductsCart, Long> {

}
