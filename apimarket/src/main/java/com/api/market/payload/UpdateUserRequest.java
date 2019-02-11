package com.api.market.payload;

import javax.validation.constraints.*;

/**
 * Clase que representa y contiene las caracteristicas de los usuarios dentro de 
 * la base de datos y se pueden modificar
 * 
 * Created by knavarro <knavarro@everis.com> on 29/07/18.
 */

public class UpdateUserRequest {
	private Long id;
	
    @NotBlank
    @Size(min = 4, max = 40)
    private String name;

    @NotBlank
    @Size(min = 6, max = 20 , message = "debe ser minimo 6 maximo 20 caracteres")
    private String password;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
