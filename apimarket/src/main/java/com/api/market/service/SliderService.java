package com.api.market.service;

import java.util.List;
import java.util.Optional;

import com.api.market.entity.Slider;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;

public interface SliderService {
	
	public Slider savedSlider(Slider request) throws ErrorTecnicoException;
	public List<?> getAllSliders() throws ErrorTecnicoException;
	public Optional<Slider> getSliderById(Long id) throws ErrorTecnicoException;
	public ApiResponse updateSlider(Slider slider) throws ErrorTecnicoException;

}
