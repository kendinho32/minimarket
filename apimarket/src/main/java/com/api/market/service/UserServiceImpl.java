package com.api.market.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.market.dao.IPhoneDao;
import com.api.market.dao.IUserDao;
import com.api.market.dao.RoleRepository;
import com.api.market.entity.Phone;
import com.api.market.entity.Role;
import com.api.market.entity.RoleName;
import com.api.market.entity.Usuario;
import com.api.market.exception.ResourceNotFoundException;
import com.api.market.payload.SignUpRequest;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	IUserDao userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	IPhoneDao phoneRepository;
	
	@Autowired
	private JWTService jwtService;

	public Usuario getUsuario(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

	@Override
	public boolean verificarToken(Usuario usuario, String token) {
		return usuario.getToken().equalsIgnoreCase(token);
	}

	@Override
	public boolean verificarLastLogin(Usuario usuario) {
		boolean isMayor30 = false;
		int diferencia=(int) ((usuario.getLastLogin().getTime()-usuario.getModified().getTime())/1000);
		int minutos=0;
	
        minutos=(int)Math.floor(diferencia/60);
        diferencia=diferencia-(minutos*60);
        
        if(minutos > 30) {
        	isMayor30 =  true;
        }
		return isMayor30;
	}
	
	@Transactional()
	@Override
	public Usuario generateAndSaveUser(SignUpRequest request) throws IOException {
		// Creating user's account
		Usuario user = new Usuario();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		// se asigna el rol de la cuenta
		Role userRole = roleRepository.findByName(RoleName.valueOf(request.getRole()))
						.orElseThrow(() -> new ResourceNotFoundException("User Role not set.", "Name", request.getRole()));
		user.setRoles(Collections.singleton(userRole));
		
		List<Phone> listPhones = new ArrayList<>();
		setPhones(request, listPhones);
				
		user.setToken(jwtService.createTokenByUser(user));
		user.setPhones(listPhones);
		user.setModified(new Date());
		user.setLastLogin(new Date());
		return userRepository.save(user);
	}
	
	/**
	 * Metodo que se encarga de setear la lista de telefonos con los que se va almacenar
	 * el usuario dentro de la BD
	 * 
	 * @param <SignUpRequest> Objeto que contiene las caracteristicas recibida para 
	 * 			registrar al usuario
	 * @param <listPhones> Lista de telefonos que va almacenar la informacion recibida para el usuario
	 **/
	private void setPhones(SignUpRequest request, List<Phone> listPhones) {
		for(com.api.market.payload.Phone elemntPhone : request.getPhones()) {
			Phone phone = new Phone();
			phone.setNumber(elemntPhone.getNumber());
			phone.setCitycode(elemntPhone.getCitycode());
			phone.setContrycode(elemntPhone.getCountrycode());
			
			listPhones.add(phoneRepository.save(phone));
		}
	}

	@Transactional()
	@Override
	public Usuario updateUser(Usuario usuario) throws IOException {
		return userRepository.save(usuario);
	}

}
