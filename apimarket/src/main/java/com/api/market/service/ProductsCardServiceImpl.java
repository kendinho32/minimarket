package com.api.market.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.market.dao.ProductCartDao;
import com.api.market.entity.ProductsCart;
import com.api.market.exception.ErrorTecnicoException;

@Service
public class ProductsCardServiceImpl implements ProductsCartService {
	
	@Autowired
	ProductCartDao productCartDao;

	@Override
	public List<?> savedAllProducts(List<ProductsCart> list) throws ErrorTecnicoException {
		return (List<?>) productCartDao.saveAll(list);
	}

}
