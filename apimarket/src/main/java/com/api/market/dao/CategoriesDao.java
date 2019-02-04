package com.api.market.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Categories;

@Repository
public interface CategoriesDao extends CrudRepository<Categories, Long> {
	
	public Categories findByName(String name);

}
