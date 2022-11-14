/**
 * 
 */
package com.musala.drone.service.config;

import static com.musala.drone.service.utils.LogHandler.logError;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


import org.json.JSONObject;

/**
 * @author ADM_AMAGBAJE
 *
 */
public class Configuration {

	/**
	 * This is the connection source for the service_config.json file
	 * The service_config.json hold all setup parameters which are referred to
	 * throughout the app. the file is setup in json format and can be amended
	 * as the users of the app requires.
	 * 
	 */
	
	private Configuration() {
		
	}
	


	private static JSONObject serviceConfig;

	static {
		try {
			String configFileName = System.getenv("SERVICE_CONFIG");
			if (configFileName == null) {
				configFileName = "service_config.json";
			}
			byte[] bytes = Files.readAllBytes(Paths.get(configFileName));
			String fileText = new String(bytes, StandardCharsets.UTF_8);
			serviceConfig = new JSONObject(fileText);

		} catch (Exception e) {
			logError("Unable to load configuration",e);
                        System.exit(1);
		}
	}

	public static JSONObject getServiceConfig() {

		return serviceConfig;
	}



}
