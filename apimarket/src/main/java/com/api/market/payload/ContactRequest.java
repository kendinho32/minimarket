package com.api.market.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ContactRequest {
	
	@NotBlank
    @Size(min = 7, max = 40)
    private String name;
	
	@NotBlank
    @Size(min = 6, max = 50)
    private String email;
	
	@NotBlank
    @Size(min = 6, max = 50)
    private String subject;
	
	@NotBlank
    @Size(min = 6, max = 250)
    private String message;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
