package com.api.market.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProductRequest {
	
	private Long id;
	
	@NotBlank
    @Size(min = 4, max = 60)
    private String name;
	
	@NotBlank
    @Size(min = 6, max = 250)
    private String description;

    private Long price;
	
    private Long quantity;
	
    @Size(min = 6, max = 200)
    private String image;
    
    private Long categorieId;
    
    private boolean outstanding;
    
    private boolean status;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getCategorieId() {
		return categorieId;
	}

	public void setCategorieId(Long categorieId) {
		this.categorieId = categorieId;
	}

	public boolean isOutstanding() {
		return outstanding;
	}

	public void setOutstanding(boolean outstanding) {
		this.outstanding = outstanding;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
