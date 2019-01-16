package com.api.market.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Products;

@Repository
public interface ProductDao extends CrudRepository<Products, Long> {

}
