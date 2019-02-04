package com.api.market.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.market.dao.CategoriesDao;
import com.api.market.entity.Categories;
import com.api.market.exception.ResourceNotFoundException;

@Service
public class CategoriesServiceImpl implements CategoriesService {
	
	@Autowired
	private CategoriesDao categoriesDao;
	
	@Transactional(readOnly=true)
	public Categories loadCategorieById(Long id) {
		Categories categorie = categoriesDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categories", "id", id));
        
        if(categorie == null) {
        	throw new UsernameNotFoundException("categorie: " + id + " no existe en el sistema!");
        }
        
		return categorie;
	}

	@Override
	public Categories loadCategorieByName(String name) {
		Categories categorie = categoriesDao.findByName(name);
        
        if(categorie == null) {
        	throw new UsernameNotFoundException("categorie: " + name + " no existe en el sistema!");
        }
        
		return categorie;
	}

	@Override
	public List<?> getAllCategories() {
		return (List<?>) categoriesDao.findAll();
	}

}
