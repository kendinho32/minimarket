package com.api.market.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.market.dao.SliderDao;
import com.api.market.entity.Slider;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.payload.ApiResponse;

@Service
public class SliderServiceImpl implements SliderService {
	
	@Autowired
	SliderDao sliderRepository;

	@Override
	public Slider savedSlider(Slider request) throws ErrorTecnicoException {
		return sliderRepository.save(request);
	}
	
	@Override
	public List<?> getAllSliders()throws ErrorTecnicoException {
		return (List<?>) sliderRepository.findAll();
	}
	
	@Override
	public Optional<Slider> getSliderById(Long id) throws ErrorTecnicoException {
		return sliderRepository.findById(id);
	}
	
	@Override
	public ApiResponse updateSlider(Slider slider) throws ErrorTecnicoException {
		ApiResponse response = new ApiResponse(false, null);
		sliderRepository.save(slider);
		response.setMessage("Slider actualizado con exito");
		response.setSuccess(true);
		
		return response;
	}

}
