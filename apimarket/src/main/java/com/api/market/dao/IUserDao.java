package com.api.market.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Usuario;

@Repository
public interface IUserDao extends CrudRepository<Usuario, Long> {

	public Usuario findByEmail(String email);
	
	public Usuario findByEmailAndPassword(String email, String password);
	
	public Optional<Usuario> findById(Long id);
}
