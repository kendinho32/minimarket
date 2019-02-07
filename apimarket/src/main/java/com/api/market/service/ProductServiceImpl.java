package com.api.market.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.market.dao.ProductDao;
import com.api.market.entity.Categories;
import com.api.market.entity.Products;

@Service
public class ProductServiceImpl implements ProductsService {
	
	@Autowired
	ProductDao productDao;

	@SuppressWarnings("unchecked")
	@Override
	public void savedAll(List<?> products) {
		productDao.saveAll((Iterable<Products>) products);
	}

	@Override
	public List<?> getAllProducts() {
		return (List<?>) productDao.findAll();
	}

	@Override
	public List<?> getProductsByCategorie(Categories categorie) {
		return productDao.findAllProductsByCategorie(categorie);
	}

}
