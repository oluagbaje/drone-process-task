/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musala.drone.service.utils;

import com.networknt.schema.ValidationMessage;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.apache.camel.Exchange;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class APIErrorHandler {
	private APIErrorHandler() {
		throw new IllegalStateException("Utility class");
	}

    private static final String ERROR_RESPONSE_TEMPLATE = "{\n"
            + "  \"code\": 21,\n"
            + "  \"message\": \"string\",\n"
            + "  \"errors\": [\n"
            + "    \n"
            + "  ]\n"
            + "}";

    public static void handleBadRequestError(Exchange exchange, String message) {
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        JSONObject errorMessage = new JSONObject();
        errorMessage.put("code", "21");
        errorMessage.put("message", message);
        exchange.getMessage().setBody(errorMessage.toString());
    }

    public static void handleValidationError(Set<ValidationMessage> errors, Exchange exchange) {
        JSONObject errorResponse = new JSONObject(ERROR_RESPONSE_TEMPLATE);
        errorResponse.put("message", "Request validation error  ");
        for (ValidationMessage m : errors) {
            JSONObject error = new JSONObject();
            error.put("type", "InvalidRequestError");
            error.put("source", m.getPath().replace("$", "Body"));
            error.put("message", m.getMessage().replace("$", "Body"));
            errorResponse.getJSONArray("errors").put(error);
        }
        exchange.getIn().setBody(errorResponse.toString());
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    }
}
