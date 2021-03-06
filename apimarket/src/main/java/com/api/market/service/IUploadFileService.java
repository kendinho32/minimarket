package com.api.market.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
	
	public Resource loadImg(String filename) throws MalformedURLException;
	public Resource loadSliderImg(String filename) throws MalformedURLException;
	public String copy(MultipartFile file) throws IOException;
	public String copyImgSlider(MultipartFile file) throws IOException;
	public boolean delete(String filename);
	public boolean deleteImgSlider(String filename);
	public void deleteAll();
	public void init() throws IOException;
}
