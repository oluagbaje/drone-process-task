/**
 * 
 */
package com.musala.drone.service.model;

/**
 * @author ADM_AMAGBAJE
 *
 */
public class Responses {

	/**
	 * the class is as it refers used to return an object reply that differentiate one response of either 
	 * error or success indicator throughout the app.
	 * code and message are used.
	 */
	


	private String code;
	private String message;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}


}
