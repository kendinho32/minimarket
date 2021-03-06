package com.api.market.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Categories;
import com.api.market.entity.Products;

@Repository
public interface ProductDao extends CrudRepository<Products, Long> {
	
	public List<?> findAllProductsByCategorie(Categories categorie);
	public List<?> findAllProductsByStatus(boolean status);
	
	@Query("select p from Products p where p.name like %?1%")
	public List<?> findProductsByName(String name);
	public List<?> findAllProductsByOutstanding(boolean outstanding);

}
