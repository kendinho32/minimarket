package com.api.market.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginRequest {
	
	@NotBlank
    @Size(min = 4, max = 40)
    private String email;
	
	@NotBlank
    @Size(min = 6, max = 20)
    private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
