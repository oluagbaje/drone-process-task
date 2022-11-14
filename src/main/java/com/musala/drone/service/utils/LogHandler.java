/**
 * 
 */
package com.musala.drone.service.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * @author ADM_AMAGBAJE
 *
 */
public class LogHandler {

	/**
	 * 
	 */
	
private LogHandler(){}
    
    private static final Logger logger = LogManager.getLogger(LogHandler.class);

    public static void logError(String msg, Throwable e) {
        logger.error(msg, e);
    }
    
    public static void logFatal(String msg, Throwable e) {
        logger.fatal(msg, e);
    }
    
    public static void logWarning(String msg, Throwable e) {
        logger.warn(msg, e);
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }
}
