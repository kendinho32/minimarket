package com.api.market.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

@Component
public class UtilFile {

	private static final Logger logger = LogManager.getLogger(UtilFile.class);

	/**
	 * Metodo que se encarga de procesar la informacion del archivo recibido de tipo
	 * CSV el cual es dividido en una lista por cada linea del archivo eliminando la
	 * cabecera
	 * 
	 * @param csvfile File que se envia por post para ser procesado
	 * 
	 * @return Lista de String con la data procesada del archivo
	 */
	public List<String[]> processCSV(MultipartFile csvfile) {
		logger.info("Se ejecuta el metodo para procesar el archivo CSV");
		CSVReader csvReader = null;
		List<String[]> elements = null;

		try {
			if (csvfile != null) {
				csvReader = new CSVReader(new InputStreamReader(csvfile.getInputStream()));
			}

			elements = csvReader.readAll();
			
			if (elements.size() > 1) {
				elements.remove(0); // remuevo la cabecera
			}
		} catch (Exception e) {
			logger.error("Error " + e);
		} finally {
			try {
				if (csvReader != null) {
					csvReader.close();
				}
			} catch (IOException e) {
				logger.error("Error " + e);
			}
		}
		return elements;
	}

}