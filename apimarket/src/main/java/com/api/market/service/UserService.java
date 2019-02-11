package com.api.market.service;

import java.io.IOException;

import com.api.market.entity.Usuario;
import com.api.market.payload.SignUpRequest;

public interface UserService {
	
	public Usuario generateAndSaveUser(SignUpRequest request)  throws IOException;
	public Usuario updateUser(Usuario usuario)  throws IOException;
	public Usuario getUsuario(Long id);
	public boolean verificarToken(Usuario usuario, String token);
	public boolean verificarLastLogin(Usuario usuario);

}
