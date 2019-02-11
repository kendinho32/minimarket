package com.api.market.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.market.entity.Usuario;
import com.api.market.payload.ApiResponse;
import com.api.market.payload.UpdateUserRequest;
import com.api.market.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	/**
	 * Metodo que se encarga de actualizar un usuario dentro de la base de datos
	 * 
	 * @param signUpRequest Objeto que contiene el objeto JSON con las
	 *                      caracteristicas del usuario que se requiere modificar
	 * @return <ResponseEntity> Entidad que especifica el estado que quedo el
	 *         proceso
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/updateUser")
	public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) throws IOException {
		logger.info("Se invoca el metodo updateUser, para modificar las caracteristicas del usuario");
		Usuario user = userService.getUsuario(updateUserRequest.getId());
		
		if (user == null) {
			return new ResponseEntity(new ApiResponse(false, "El usuario no existe!"), HttpStatus.BAD_REQUEST);
		}

		logger.info("usuario --> " + user.getName());
		user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
		user.setName(updateUserRequest.getName());

		Usuario result = userService.updateUser(user);
		ApiResponse response = new ApiResponse(true, "usuario registrado exitosamente!", result);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
