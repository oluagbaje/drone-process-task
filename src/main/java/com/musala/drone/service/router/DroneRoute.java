package com.musala.drone.service.router;

import static com.musala.drone.service.utils.APIErrorHandler.handleValidationError;
import static com.musala.drone.service.utils.LogHandler.logError;
import static com.musala.drone.service.utils.LogHandler.logInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.model.rest.RestBindingMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.musala.drone.service.authenticate.AuthenticateUsers;
import com.musala.drone.service.config.Configuration;
import com.musala.drone.service.model.Responses;
import com.musala.drone.service.repository.ClientRepository;

public class DroneRoute extends RouteBuilder {

	/**
	 * The flow and logic of an integration is specified here The RouteBuilder is a
	 * base class which is derived from to create routing rules using the DSL. This
	 * where the system are coordinated. A Camel route is where the integration flow
	 * is defined. For example to integrate two systems then a Camel route can be
	 * coded to specify how these systems are integrated
	 */

	AuthenticateUsers authenticate = new AuthenticateUsers();

	ClientRepository repository = new ClientRepository();

	JSONObject resiliencyConfig = Configuration.getServiceConfig().getJSONObject("resiciliency");

	final String processingRequestQueue = resiliencyConfig.getString("processingRequestQueue");
	final String pendingRequestQueue = resiliencyConfig.getString("pendingRequestQueue");
	final String loadingRequestQueue = resiliencyConfig.getString("loadingRequestQueue");
	final String loadedRequestQueue = resiliencyConfig.getString("loadedRequestQueue");
	final String deliveringProcessQueue = resiliencyConfig.getString("deliveringProcessQueue");
	final String deliveredProcessQueue = resiliencyConfig.getString("deliveredProcessQueue");
	final String returningProcessQueue = resiliencyConfig.getString("returningProcessQueue");
	final String failedRequestQueue = resiliencyConfig.getString("failedRequestQueue");
	final String ProcessedQueue = resiliencyConfig.getString("ProcessedQueue");
	final int submitRetryCount = resiliencyConfig.getInt("smscSubmitRetryCount");

	final JSONObject pendingRequestRetry = resiliencyConfig.getJSONObject("pendingRequestRetry");

	JSONObject apiServiceConfig = Configuration.getServiceConfig().getJSONObject("apiService");
	String serviceHost = apiServiceConfig.getString("serviceHost");
	int servicePort = apiServiceConfig.getInt("servicePort");
	String serviceRestConfigComponent = apiServiceConfig.getString("serviceRestConfigComponent");

	JSONObject jsonObject = Configuration.getServiceConfig();
	String jsonvalidator = "json-validator:";

	ProducerTemplate notificationProducerTemplate;

	public void setNotificationProducerTemplate(ProducerTemplate notificationProducerTemplate) {
		this.notificationProducerTemplate = notificationProducerTemplate;
	}

	@Override
	public void configure() throws Exception {
		restConfiguration().component(serviceRestConfigComponent).host(serviceHost).port(servicePort).enableCORS(true)
				.corsAllowCredentials(true).corsHeaderProperty("Access-Control-Allow-Origin", "*")
				.corsHeaderProperty("Access-Control-Allow-Headers", "*")
				.corsHeaderProperty("Access-Control-Allow-Credentials", "true")
				.corsHeaderProperty("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT")
				.bindingMode(RestBindingMode.auto);

		/***
		 * { "droneSerialNumber":"AKT1000SMG", "droneModel":"LightWeight",
		 * "droneWeightLimit":"124.0", "droneBatteryLevel":".98", "droneState":"IDLE" }
		 * 
		 *
		 */

		rest().post("/v1.0/app/register").type(String.class).route().process((Exchange exchange) -> {
			Responses respond = new Responses();
			try {
				String regRequest = exchange.getMessage().getBody(String.class);

				notificationProducerTemplate.requestBody(
						jsonvalidator + Paths.get("validationSchema/registration.json").toUri(), regRequest);

				JSONObject regRequestJson = new JSONObject(regRequest);

				String droneSerialNumber = regRequestJson.getString("droneSerialNumber");
				String droneModel = regRequestJson.getString("droneModel");
				Double droneWeightLimit = regRequestJson.getDouble("droneWeightLimit");
				Double droneBatteryLevel = regRequestJson.getDouble("droneBatteryLevel");
				String droneState = regRequestJson.getString("droneState");
				System.out.println(droneBatteryLevel);

				/**
				 *
				 * Basic Authentication
				 */
				String userpass1 = (exchange.getIn().getHeader("Authorization", String.class));
				respond = authenticate.securedAuthenticateUser(userpass1);

				if (respond.getCode().equalsIgnoreCase("05")) {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "18");
					errorMessage.put("message", "Authentication Error");

					exchange.getMessage().setBody(errorMessage.toString());

				}
				if (respond.getCode().equalsIgnoreCase("00")) {

					/**
					 * register a drone At this point the drone is registered
					 */

					String droneDetails = droneSerialNumber + "|" + droneModel + "|" + droneWeightLimit + "|"
							+ droneBatteryLevel + "|" + droneState;
					System.out.println(droneDetails);
					repository.addNewDrone(droneDetails);

					/**
					 * Date currentTime = new Date(); currentTime.toString(); DateFormat formatter =
					 * new SimpleDateFormat(dateFormatUse); dateTimeOtpVerified =
					 * formatter.format(currentTime);
					 **/

					JSONObject apiResponse = new JSONObject();
					apiResponse.put("message", "Operation OK");
					apiResponse.put("code", "00");
					apiResponse.put("droneSerialNumber", droneSerialNumber);
					exchange.getMessage().setBody(apiResponse.toString());

				}

			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}

			}

		});

		rest().get("/v1.0/app/getListOfDrones").type(String.class).route().process((Exchange exchange) -> {
			Responses respond = new Responses();
			try {

				/**
				 *
				 * Basic Authentication
				 */
				String userpass1 = (exchange.getIn().getHeader("Authorization", String.class));
				respond = authenticate.securedAuthenticateUser(userpass1);

				if (respond.getCode().equalsIgnoreCase("05")) {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "18");
					errorMessage.put("message", "Authentication Error");

					exchange.getMessage().setBody(errorMessage.toString());

				}
				if (respond.getCode().equalsIgnoreCase("00")) {

					/**
					 * List available Drone
					 */
					String state = "IDLE";
					String apiResponse = repository.returnAvailableDrone(state);
					exchange.getMessage().setBody(apiResponse);

				}

			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}

			}

		});

		rest().get("/v1.0/app/loadMedications").type(String.class).route().process((Exchange exchange) -> {
			Responses respond = new Responses();
			try {

				/**
				 *
				 * Basic Authentication
				 */
				String userpass1 = (exchange.getIn().getHeader("Authorization", String.class));
				respond = authenticate.securedAuthenticateUser(userpass1);

				if (respond.getCode().equalsIgnoreCase("05")) {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "18");
					errorMessage.put("message", "Authentication Error");
					exchange.getMessage().setBody(errorMessage.toString());

				}
				if (respond.getCode().equalsIgnoreCase("00")) {

					/**
					 * loading medications
					 */
					Path medicationList = Paths.get("medicsFolder/medications.csv");
					String filepath = medicationList.toString();

					repository.addMedications(filepath);
					JSONObject apiResponse = new JSONObject();
					apiResponse.put("message", "medication successfully uploaded and updated");
					apiResponse.put("code", "00");

					exchange.getMessage().setBody(apiResponse.toString());

				}

			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}

			}

		});

		/**
		 * the process to load is communicated to the Drone after be select from the
		 * list
		 */

		rest().post("/v1.0/app/drone/load").type(String.class).route().process((Exchange exchange) -> {
			try {

				String loadRequest = exchange.getMessage().getBody(String.class);

				notificationProducerTemplate
						.requestBody(jsonvalidator + Paths.get("validationSchema/loadDrone.json").toUri(), loadRequest);

				JSONObject loadRequestJson = new JSONObject(loadRequest);

				String droneSerialNumber = loadRequestJson.getString("droneSerialNumber").trim();
				String medCode = loadRequestJson.getString("medCode").trim();
				String sourceLocation = loadRequestJson.getString("sourceLocation");
				String destinationLocation = loadRequestJson.getString("destinationLocation");

				/**
				 * confirm if drone is IDLE for Loading confirm battery level > 0.25
				 */
				String responsesOut = repository.dronesStateAndBatteryConfirmation(droneSerialNumber);
				if (responsesOut.equals("KO")) {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "02");
					errorMessage.put("message", "battery or Drone state error");
					errorMessage.put("droneSerialNumber", droneSerialNumber);
					exchange.getMessage().setBody(errorMessage.toString());
				} else {

					/**
					 * 
					 * update state of the Drone from IDLE to LOADING in history table and Drone
					 * table
					 */
					String newState = "LOADING";
					String resOut = repository.dronesStateChangeUpdate(droneSerialNumber, newState, medCode);
					System.out.println(resOut);
					String fileName = droneSerialNumber + "|" + resOut;
					JSONObject mainObj = new JSONObject();
					JSONObject obj = new JSONObject();
					mainObj.put("serialNumber", droneSerialNumber);
					mainObj.put("fileName", fileName);
					obj.put(droneSerialNumber, mainObj);

					notificationProducerTemplate.asyncRequestBody("file:" + loadingRequestQueue, obj.toString());
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "00");
					errorMessage.put("message", "Drone state successfully updated");
					errorMessage.put("droneSerialNumber", droneSerialNumber);
					exchange.getMessage().setBody(errorMessage.toString());
				}

			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}
			}

		});

		// from("direct:stashNotificationRequestloading").to("file:" +
		// loadingRequestQueue);

		rest().post("/v1.0/app/drone/loaded").type(String.class).route().process((Exchange exchange) -> {
			String loadRequest = exchange.getMessage().getBody(String.class);

			try {
				notificationProducerTemplate
						.requestBody(jsonvalidator + Paths.get("validationSchema/loadDrone.json").toUri(), loadRequest);
				JSONObject loadRequestJson = new JSONObject(loadRequest);

				String droneSerialNumber = loadRequestJson.getString("droneSerialNumber");
				String medCode = loadRequestJson.getString("medCode").trim();

				/**
				 * 
				 * update state of the Drone from IDLE to LOADING in history table and Drone
				 * table
				 */
				String newState = "LOADED";
				/**
				 * read loadingRequestQueue and get files relating to the drone
				 */
				String processFile = ReadFilesList(droneSerialNumber, loadingRequestQueue);
				String[] itemz = processFile.split("\\*");
				String processFileId = itemz[0];
				System.out.println("the file id  " + processFileId);
				String processFileName = itemz[1];
				repository.updatePerviousDroneStateProcessEndDate(processFileId);
				String resOut = repository.dronesStateChangeUpdate(droneSerialNumber, newState, medCode);
				String fileName = droneSerialNumber + "|" + resOut;
				System.out.println(fileName);
				JSONObject mainObj = new JSONObject();
				JSONObject obj = new JSONObject();
				mainObj.put("serialNumber", droneSerialNumber);
				mainObj.put("fileName", fileName);
				obj.put(droneSerialNumber, mainObj);
				notificationProducerTemplate.asyncRequestBody("file:" + loadedRequestQueue, obj.toString());
				JSONObject errorMessage = new JSONObject();
				errorMessage.put("code", "00");
				errorMessage.put("message", "Drone state successfully updated");
				errorMessage.put("droneSerialNumber", droneSerialNumber);
				exchange.getMessage().setBody(errorMessage.toString());

				MaintainFiles(processFileName, loadingRequestQueue);
			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}
			}

		});

		rest().post("/v1.0/app/drone/delivering").type(String.class).route().process((Exchange exchange) -> {
			String loadRequest = exchange.getMessage().getBody(String.class);

			try {
				notificationProducerTemplate
						.requestBody(jsonvalidator + Paths.get("validationSchema/loadDrone.json").toUri(), loadRequest);
				JSONObject loadRequestJson = new JSONObject(loadRequest);

				String droneSerialNumber = loadRequestJson.getString("droneSerialNumber");
				String medCode = loadRequestJson.getString("medCode").trim();

				/**
				 * update state of the Drone from IDLE to LOADING in history table and Drone
				 * table
				 */
				String newState = "DELIVERING";
				/**
				 * read loadingRequestQueue and get files relating to the drone
				 */
				String processFile = ReadFilesList(droneSerialNumber, loadedRequestQueue);
				String[] itemz = processFile.split("\\*");
				String processFileId = itemz[0];
				System.out.println("the file id  " + processFileId);
				String processFileName = itemz[1];
				repository.updatePerviousDroneStateProcessEndDate(processFileId);
				String resOut = repository.dronesStateChangeUpdate(droneSerialNumber, newState, medCode);
				String fileName = droneSerialNumber + "|" + resOut;
				System.out.println(fileName);
				JSONObject mainObj = new JSONObject();
				JSONObject obj = new JSONObject();
				mainObj.put("serialNumber", droneSerialNumber);
				mainObj.put("fileName", fileName);
				obj.put(droneSerialNumber, mainObj);
				notificationProducerTemplate.asyncRequestBody("file:" + deliveringProcessQueue, obj.toString());
				MaintainFiles(processFileName, loadedRequestQueue);
				JSONObject errorMessage = new JSONObject();
				errorMessage.put("code", "00");
				errorMessage.put("message", "Drone state successfully updated");
				errorMessage.put("droneSerialNumber", droneSerialNumber);
				exchange.getMessage().setBody(errorMessage.toString());
			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}
			}

		});

		rest().post("/v1.0/app/drone/delivered").type(String.class).route().process((Exchange exchange) -> {
			String loadRequest = exchange.getMessage().getBody(String.class);
			try {

				notificationProducerTemplate
						.requestBody(jsonvalidator + Paths.get("validationSchema/loadDrone.json").toUri(), loadRequest);
				JSONObject loadRequestJson = new JSONObject(loadRequest);

				String droneSerialNumber = loadRequestJson.getString("droneSerialNumber");
				String medCode = loadRequestJson.getString("medCode");
				String sourceLocation = loadRequestJson.getString("sourceLocation");
				String destinationLocation = loadRequestJson.getString("destinationLocation");

				/**
				 * 
				 * update state of the Drone from IDLE to LOADING in history table and Drone
				 * table
				 */
				String newState = "DELIVERED";
				/**
				 * read loadingRequestQueue and get files relating to the drone
				 */
				String processFile = ReadFilesList(droneSerialNumber, deliveringProcessQueue);

				String[] itemz = processFile.split("\\*");
				String processFileId = itemz[0];
				System.out.println("the file id  " + processFileId);
				String processFileName = itemz[1];
				repository.updatePerviousDroneStateProcessEndDate(processFileId);
				String resOut = repository.dronesStateChangeUpdate(droneSerialNumber, newState, medCode);
				String fileName = droneSerialNumber + "|" + resOut;
				System.out.println(fileName);
				JSONObject mainObj = new JSONObject();
				JSONObject obj = new JSONObject();
				mainObj.put("serialNumber", droneSerialNumber);
				mainObj.put("fileName", fileName);
				obj.put(droneSerialNumber, mainObj);

				notificationProducerTemplate.asyncRequestBody("file:" + deliveredProcessQueue, obj.toString());
				MaintainFiles(processFileName, deliveringProcessQueue);

			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}
			}

		});

		rest().post("/v1.0/app/drone/returning").type(String.class).route().process((Exchange exchange) -> {
			String loadRequest = exchange.getMessage().getBody(String.class);
			try {
				notificationProducerTemplate
						.requestBody(jsonvalidator + Paths.get("validationSchema/loadDrone.json").toUri(), loadRequest);
				JSONObject loadRequestJson = new JSONObject(loadRequest);

				String droneSerialNumber = loadRequestJson.getString("droneSerialNumber");
				String medCode = loadRequestJson.getString("medCode");

				/**
				 * 
				 * update state of the Drone from IDLE to LOADING in history table and Drone
				 * table
				 */
				String newState = "RETURNING";
				/**
				 * read loadingRequestQueue and get files relating to the drone
				 */
				String processFile = ReadFilesList(droneSerialNumber, deliveredProcessQueue);

				String[] itemz = processFile.split("\\*");
				String processFileId = itemz[0];
				System.out.println("the file id  " + processFileId);
				String processFileName = itemz[1];
				repository.updatePerviousDroneStateProcessEndDate(processFileId);
				String resOut = repository.dronesStateChangeUpdate(droneSerialNumber, newState, medCode);
				String fileName = droneSerialNumber + "|" + resOut;
				System.out.println(fileName);
				JSONObject mainObj = new JSONObject();
				JSONObject obj = new JSONObject();
				mainObj.put("serialNumber", droneSerialNumber);
				mainObj.put("fileName", fileName);
				obj.put(droneSerialNumber, mainObj);

				notificationProducerTemplate.asyncRequestBody("file:" + returningProcessQueue, obj.toString());
				MaintainFiles(processFileName, deliveredProcessQueue);
			} catch (Exception e) {
				if (e.getCause() instanceof JsonValidationException) {
					handleValidationError(((JsonValidationException) e.getCause()).getErrors(), exchange);
				} else {
					logError("errorMessage", e);
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					JSONObject errorMessage = new JSONObject();
					errorMessage.put("code", "96");
					errorMessage.put("message", "System Malfunction");
					exchange.getMessage().setBody(errorMessage.toString());
				}
			}

		});

		

		from("file:" + returningProcessQueue).process((Exchange exchange) -> {
			String notificationRequest = exchange.getIn().getBody(String.class);

			logInfo(notificationRequest);
			JSONObject request = new JSONObject(notificationRequest);
			notificationProducerTemplate.asyncRequestBody("file:" + ProcessedQueue, request.toString());

		});

	}

	private static String ReadFilesList(String serialNumber, String filepaths) {
		String loadingrequestId = "";
		String infilename = "";
		String fileinfo = "";
		List<String> listfiles = new ArrayList<>();
		try {

			String filePathStore = Paths.get(filepaths).toString();
			File file = new File(filePathStore);
			File[] files = file.listFiles();
			for (File f : files) {

				listfiles.add(f.getName());
				infilename = f.getName();

				/**
				 * read file path
				 */

				fileinfo = ReadFiles(serialNumber, filepaths, infilename);

				String[] itemz = fileinfo.split("//|");
				String inserial = itemz[0];
				if (serialNumber.equals(inserial)) {
					loadingrequestId = itemz[2];
					break;
				}

			}

		} catch (JSONException e) {
			// LogError("Error occured"+"ReadFilesList ", e);
		}

		return fileinfo + "*" + infilename;
	}

	protected static void MaintainFiles(String filename, String filepath) {

		try {
			String filePathStore = Paths.get(filepath + "/" + filename).toString();
			Path path = Paths.get(filePathStore);
			try {
				File f = new File(filePathStore);
				if (f.exists() && !f.isDirectory()) {
					Files.delete(path);

				}
			} catch (Exception e) {

				// LogError("Error occured"+"checking files path ", e);
			}
		} catch (JSONException e) {
			// LogError("Error occured"+"MaintainFiles ", e);
		}

	}

	static String manageFiles(String StrObj, String saveFilename, String filepath, String filename) {
		String[] otpSet = filename.split("\\|");
		String inNum = otpSet[0];
		String otpToken = otpSet[1];
		String mssg = "";

		try {
			String filePathStore = Paths.get(filepath + "/" + filename).toString();
			Path path = Paths.get(filePathStore + saveFilename);
			Path p = Files.createFile(path);

			try (PrintWriter out = new PrintWriter(new FileWriter(filePathStore + saveFilename))) {
				out.write(StrObj);
			} catch (Exception e) {
				// LogError("Error occured"+"writing out files ", e);
			}
		} catch (Exception e) {

			// LogError("Error occured"+"manageFiles ", e);
		}
		return mssg;
	}

	private static String ReadFiles(String serialNumber, String filepath, String infilename) {
		String fileInfo = "";

		String filePathStore = Paths.get(filepath).toString();
		String configFileName = filePathStore + "/" + infilename;
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(Paths.get(configFileName));

			String fileText = new String(bytes, StandardCharsets.UTF_8);
			JSONObject serviceConfig = new JSONObject(fileText);
			JSONObject queufile = serviceConfig.getJSONObject(serialNumber);
			fileInfo = queufile.getString("fileName");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// LogError("Error occured"+"Read files errors", e);
		}

		return fileInfo;
	}

}