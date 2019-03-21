package com.api.market.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio implementado para abarcar todas las funciones de la carga de
 * imagenes para cada usuario de la aplicacion
 * 
 * @author Kendall Navarro <knavarro@everis.com>
 *
 */
@Service
public class UploadFileServiceImpl implements IUploadFileService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final static String UPLOADS_FOLDER = "uploads";
	private final static String UPLOADS_SLIDER_FOLDER = "sliders";

	/**
	 * Metodo que se encarga de cargar una imagen a traves del nombre recibido
	 * 
	 */
	@Override
	public Resource loadImg(String filename) throws MalformedURLException {
		Path pathFoto = getPathImg(filename);
		log.info("pathFoto: " + pathFoto);

		Resource recurso = new UrlResource(pathFoto.toUri());

		if (!recurso.exists() || !recurso.isReadable()) {
			throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
		}
		
		return recurso;
	}

	/**
	 * Metodo que se encarga de copiar una imagen en el directorio especificado
	 * dentro del proyecto
	 */
	@Override
	public String copy(MultipartFile file) throws IOException {
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = getPathImg(uniqueFilename);

		log.info("rootPath: " + rootPath);

		Files.copy(file.getInputStream(), rootPath);

		return uniqueFilename;
	}
	
	/**
	 * Metodo que se encarga de copiar una imagen en el directorio especificado
	 * dentro del proyecto
	 */
	@Override
	public String copyImgSlider(MultipartFile file) throws IOException {
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = getPathSliderImg(uniqueFilename);

		log.info("rootPath: " + rootPath);

		Files.copy(file.getInputStream(), rootPath);

		return uniqueFilename;
	}

	/**
	 * Metodo que se encarga de eliminar una imagen del directorio, evitando que
	 * queden imagenes basura dentro del proyecto
	 * 
	 */
	@Override
	public boolean delete(String filename) {
		if (filename != null && filename.length() > 0) {
			Path rootPath = getPathImg(filename);
			File archivo = rootPath.toFile();

			if (archivo.exists() && archivo.canRead()) {
				if (archivo.delete()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Metodo que se encarga de eliminar una imagen del directorio, evitando que
	 * queden imagenes basura dentro del proyecto
	 * 
	 */
	@Override
	public boolean deleteImgSlider(String filename) {
		if (filename != null && filename.length() > 0) {
			Path rootPath = getPathSliderImg(filename);
			File archivo = rootPath.toFile();

			if (archivo.exists() && archivo.canRead()) {
				if (archivo.delete()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Metodo que retorna el Path absoluto de la carpeta de las imagenes
	 * 
	 * @param filename String que especifica el nombre de la imagen a buscar
	 * @return
	 */
	public Path getPathImg(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}
	
	/**
	 * Metodo que retorna el Path absoluto de la carpeta de las imagenes
	 * 
	 * @param filename String que especifica el nombre de la imagen a buscar
	 * @return
	 */
	public Path getPathSliderImg(String filename) {
		return Paths.get(UPLOADS_SLIDER_FOLDER).resolve(filename).toAbsolutePath();
	}

	/**
	 * Metodo que se encarga de eliminar todo lo que se encuentra dentro de la
	 * carpeta Uploads
	 * 
	 */
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
	}

	/**
	 * Metodo que se encarga de crear el directorio cada vez que se inicia la
	 * aplicacion
	 */
	@Override
	public void init() throws IOException {
		Files.createDirectory(Paths.get(UPLOADS_SLIDER_FOLDER));
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));
	}

}
