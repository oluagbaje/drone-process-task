/**
 * Basic Authentication
 */
package com.musala.drone.service.authenticate;

import static com.musala.drone.service.crypto.CryptoService.ENCRYPTED_INDICATOR;
import static com.musala.drone.service.utils.LogHandler.logError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.musala.drone.service.config.Configuration;
import com.musala.drone.service.crypto.JasyptUtils;
import com.musala.drone.service.model.Responses;
import com.musala.drone.service.services.GatewayServices;

/**
 * @author ADM_AMAGBAJE
 *
 */
public class AuthenticateUsers {

	/**
	 * This class is used for Authentication method. The Basic Authentication
	 * process is carried out here Validation for the payload is also carried out
	 * here
	 */


	JSONObject apiAuthConfig = Configuration.getServiceConfig().getJSONObject("AuthSecurity");
	String headerPrefix = apiAuthConfig.getString("headerPref");
	List<String> languageCodeSet = new ArrayList<>();
	Map<String, String> languageMessengerMap = new ConcurrentHashMap<>();

	private String password;

	public AuthenticateUsers() {

		password = (String) apiAuthConfig.get("password");
		if (password.startsWith(ENCRYPTED_INDICATOR)) {
			password = JasyptUtils.decrypt(password.replaceFirst(ENCRYPTED_INDICATOR, ""));
		}

	}
	
	public void setMessenger() {
		try {
			JSONObject jsonObject = Configuration.getServiceConfig();

			JSONObject langMap = jsonObject.getJSONObject("servicetypeInfo");

			langMap.keySet().forEach((String coCode) -> {
				JSONArray jsonArray = (JSONArray) langMap.get(coCode);
				languageCodeSet.add(coCode);

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject messagertype = (JSONObject) jsonArray.get(i);

					languageMessengerMap.put(coCode, messagertype.toString());

				}

			});

		} catch (Exception e) {
			logError("Failed setting options for otp", e);

		}

		
		
	}

	GatewayServices gs = new GatewayServices();

	public Responses securedAuthenticateUser(String authHeader) {

		if (authHeader != null && authHeader.contains(headerPrefix)) {

			String authToken = authHeader;
			authToken = authToken.replaceFirst(headerPrefix, "");
			byte[] userpas = java.util.Base64.getDecoder().decode(authToken.trim());
			String decodedString = new String(userpas);
			StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
			String usernames = tokenizer.nextToken();
			String passwords = tokenizer.nextToken();

			String userName = (String) apiAuthConfig.get("userName");

			if (userName.equalsIgnoreCase(usernames) && password.equalsIgnoreCase(passwords)) {
				Responses errorResponse = new Responses();
				errorResponse.setCode("00");
				return errorResponse;
			}

		}

		Responses errorResponse = new Responses();
		errorResponse.setCode("05");

		return errorResponse;
	}

	/***
	 * At this end, we read the json file to get the contract type/consent Type that
	 * the customer should be consenting to based on the consentType and the
	 * customerId the return type should a json file with the actual
	 * consent.contract and customer details
	 */

	public String getConsentType(String coreString,String custLang, String customerDetails) {
		String outparam = "";
		JSONArray fileObj = Configuration.getServiceConfig().getJSONArray(coreString);
		JSONArray coreString1 = new JSONArray();
		int size = fileObj.length();

		for (int i = 0; i < size; i++) {
			JSONObject terms = new JSONObject();
			JSONObject contract = fileObj.getJSONObject(i);
			String ids = (String) contract.get("id");
			String header = (String) contract.get("header");
			String content = (String) contract.get("content");
			terms.put("id", ids);
			terms.put("header", header);
			terms.put("content", content);

			coreString1.put(terms);
		}

		String[] tempStr = customerDetails.split("\\|");
		String cusFirstname = tempStr[0];
		String cusLastname = tempStr[1];
		String cusPhoneNumber = tempStr[2];
		String cusEmail = tempStr[3];

		JSONObject mainObj = new JSONObject();
		JSONObject main = new JSONObject();
		main.put("firstName", cusFirstname);
		main.put("lastName", cusLastname);
		main.put("phoneNumber", cusPhoneNumber);
		main.put("email", cusEmail);
		main.put("code", "00");
		
		setMessenger();
		String outStr = languageMessengerMap.get(custLang);
		JSONObject jsonnew = new JSONObject(outStr);
		String messengerInfo = jsonnew.getString(coreString);

		try {
			mainObj.put("data", coreString1);
			mainObj.put("people", main);
			mainObj.put("pageHeader",messengerInfo );
		} catch (JSONException e) {
			logError("Error occured", e);
		}

		outparam = mainObj.toString();
		return outparam;
	}
	
	public String getConsentType(String coreString, String customerDetails) {
		String outparam = "";
		JSONArray fileObj = Configuration.getServiceConfig().getJSONArray(coreString);
		JSONArray coreString1 = new JSONArray();
		int size = fileObj.length();

		for (int i = 0; i < size; i++) {
			JSONObject terms = new JSONObject();
			JSONObject contract = fileObj.getJSONObject(i);
			String ids = (String) contract.get("id");
			String header = (String) contract.get("header");
			String content = (String) contract.get("content");
			terms.put("id", ids);
			terms.put("header", header);
			terms.put("content", content);

			coreString1.put(terms);
		}

		String[] tempStr = customerDetails.split("\\|");
		String cusFirstname = tempStr[0];
		String cusLastname = tempStr[1];
		String cusPhoneNumber = tempStr[2];
		String cusEmail = tempStr[3];

		JSONObject mainObj = new JSONObject();
		JSONObject main = new JSONObject();
		main.put("firstName", cusFirstname);
		main.put("lastName", cusLastname);
		main.put("phoneNumber", cusPhoneNumber);
		main.put("email", cusEmail);
		main.put("code", "00");

		try {
			mainObj.put("data", coreString1);
			mainObj.put("people", main);
		} catch (JSONException e) {
			logError("Error occured", e);
		}

		outparam = mainObj.toString();
		return outparam;
	}

}
