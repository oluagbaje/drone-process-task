/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musala.drone.service.model;

/**
 *
 * @author Eboda
 */
public class SMSGatewayOperationError extends Exception {
    
   private static final boolean CANRETRYLATER = true;
    
    public SMSGatewayOperationError(String message, Throwable cause) {
		super(message, cause);
		 
		
		
    }
    
    

    public boolean isCanRetryLater() {
        return CANRETRYLATER;
    }

   /** public void setCanRetryLater(boolean canRetryLater) {
        this.canRetryLater = canRetryLater;
    }**/
    
    

    
    
    
}