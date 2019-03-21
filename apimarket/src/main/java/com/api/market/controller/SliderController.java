package com.api.market.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.market.entity.Slider;
import com.api.market.exception.ErrorNegocioException;
import com.api.market.exception.ErrorTecnicoException;
import com.api.market.exception.ResourceNotFoundException;
import com.api.market.payload.ApiResponse;
import com.api.market.service.IUploadFileService;
import com.api.market.service.SliderService;

@RestController
@RequestMapping("/api/slider")
public class SliderController {

	private static final Logger logger = LogManager.getLogger(SliderController.class);
	
	@Autowired
	private SliderService sliderService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@GetMapping("/get-all-sliders")
	public ResponseEntity<?> getAllSliders() throws ErrorNegocioException {
		try {
			List<?> list = sliderService.getAllSliders();
			ApiResponse response = new ApiResponse(true, "Sliders recuperados exitosamente...!", list);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible recuperar los sliders de la BD");
            throw new ErrorNegocioException("No es posible recuperar los sliders de la BD","SOL-0004",et);
        }
	}
	
	@PostMapping("/save-slider")
	public ResponseEntity<?> saveSlider(@Valid @RequestBody Slider request) throws ErrorNegocioException {
		try {
			Slider aux = sliderService.savedSlider(request);
			ApiResponse response = new ApiResponse(true, "Slider guardado exitosamente...!", aux);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(ErrorTecnicoException et) {
            logger.error("No es posible guardar el slider");
            throw new ErrorNegocioException("No es posible guardar el slider","SOL-0004",et);
        }
	}
	
	@PostMapping("/saveImageSlider/{id}/tipo/{tipo}")
	public ResponseEntity<?> saveImageSlider(@Valid @RequestParam("picture") MultipartFile foto, @PathVariable("id") Long id,
			@PathVariable("tipo") Long tipo) throws ErrorTecnicoException {
		logger.info("Se invoca el metodo saveImageSlider, para almacenar la foto principal del slider");
		String name = foto.getOriginalFilename();
		String[] propiedadesNombre = name.replace(".", "-").split("-");
		ApiResponse api = null;

		if (propiedadesNombre[1].equalsIgnoreCase("jpg") || propiedadesNombre[1].equalsIgnoreCase("png")
				|| propiedadesNombre[1].equalsIgnoreCase("jpge")) {
			Slider slider = sliderService.getSliderById(id).orElseThrow(() -> new ResourceNotFoundException("Slider", "id", id));

			// Elimino la foto anterior
			if (slider.getImage() != null) {
				uploadFileService.deleteImgSlider(slider.getImage());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copyImgSlider(foto);
			} catch (IOException e) {
				logger.error("Error --> ", e);
			}

			if(tipo == 1) {
				slider.setImage(uniqueFilename);
			} else {
				slider.setImagePrice(uniqueFilename);
			}
			
			sliderService.updateSlider(slider);

			logger.info("Picture save successfully");
			api = new ApiResponse(true, "Picture save successfully", slider);
		} else {
			logger.info("Extension no valida");
			api = new ApiResponse(false, "Extension no valida");
		}
		return new ResponseEntity<>(api, HttpStatus.OK);
	}
}
