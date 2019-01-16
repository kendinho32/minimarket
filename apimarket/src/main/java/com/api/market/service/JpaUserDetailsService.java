package com.api.market.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.market.dao.IUserDao;
import com.api.market.entity.Usuario;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{

	@Autowired
	private IUserDao userDao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String email) {
        Usuario user = userDao.findByEmail(email);
        
        if(user == null) {
        	throw new UsernameNotFoundException("email: " + email + " no existe en el sistema!");
        }
        
		return new User(user.getName(), user.getPassword(), false, true, true, true, null);
	}

	@Transactional(readOnly=true)
	public boolean verificarEmail(String email) {
		Usuario user = userDao.findByEmail(email);
		boolean result = false;
		
		if(user != null) {
			result = true;
		}
		return result;
	}	
	
	@Transactional(readOnly=true)
	public Usuario verificarUser(String email, String password) {
		Usuario user = userDao.findByEmail(email);
        
        if(user != null && !passwordEncoder.matches(password, user.getPassword())) {
        	user = null;
        }
		return user;
	}
}
