/**
 * The GatewayService class is used as the main class where
 * all the component service for the smpp library resides
 */
package com.musala.drone.service.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.musala.drone.service.repository.ClientRepository;

import static com.musala.drone.service.crypto.CryptoService.ENCRYPTED_INDICATOR;
import static com.musala.drone.service.utils.LogHandler.logError;
import static com.musala.drone.service.utils.LogHandler.logInfo;

/**
 * @author ADM_AMAGBAJE
 *
 */
@Service
public class GatewayServices {

	/**
	 * In this class we carried out the session creation Session binding session
	 * segmenting etc. there is a connection here with the service_config.json file
	 * In the GatewayServices class all the various setup for the SMSC which are in
	 * the service_config.json is called here and the session maintained here also
	 *
	 */

	public int timeToCharge;// in seconds

	public static final Map<String, JSONObject> sessionsmapActive = new ConcurrentHashMap<>();
	public static final Map<String, String> mobileNetworkGatewayMaP = new ConcurrentHashMap<>();
	public static final Map<String, Integer> droneBatteryChargeProcessMaP = new ConcurrentHashMap<>();
	public static final Map<String, Double> droneBatteryChargeStateMaP = new ConcurrentHashMap<>();

	ClientRepository repository = new ClientRepository();

	public void initializeGateway() {
		try {
			System.out.println("in the initial stage");
			repository.droneBatterychargingProcess();

			droneBatteryChargeStateMaP.forEach((seriaNum, battLevel) -> {
				System.out.println("in estimste "+seriaNum+"  "+ battLevel);
				estimateBatteryChargeLevel(seriaNum, battLevel);
			});

			droneBatteryChargeProcessMaP.forEach((seriaNums, timeToCharge) -> {
				System.out.println("inside battery charge process");
				String comments = "";
				switch (timeToCharge) {
				case 10:
					chargeProcess(seriaNums, timeToCharge);
					comments = "battery successfully charged after " + timeToCharge + "s";
					System.out.println("battery successfully charged after "+ timeToCharge + "s");
					break;
				case 15:
					chargeProcess(seriaNums, timeToCharge);
					comments = "battery successfully charged after " + timeToCharge + "s";
					System.out.println("battery successfully charged after "+ timeToCharge + "s");
					break;
				case 20:
					chargeProcess(seriaNums, timeToCharge);
					comments = "battery successfully charged after " + timeToCharge + "s";
					System.out.println("battery successfully charged after "+ timeToCharge + "s");
					break;
				default:
					comments = "battery is okay";
				}
			});
		} catch (Exception e) {
			logError("Failed connect and bind to host", e);
		}

	}

	public String estimateBatteryChargeLevel(String seriaNum, double battLevel) {
System.out.println("in estimate   ");
		if (battLevel >= 0.50) {
			timeToCharge = 10;
		}

		if (battLevel < 0.50 && battLevel >= 0.37) {
			timeToCharge = 15;
		}

		if (battLevel < 0.370 && battLevel >= 0.25) {
			timeToCharge = 20;
		}
		if (battLevel < 0.25) {
			timeToCharge = 30;
		}
		System.out.println("in estimate   "+seriaNum+"     "+ timeToCharge);
		droneBatteryChargeProcessMaP.put(seriaNum, timeToCharge);

		return seriaNum;
	}

	public String chargeProcess(String seriaNum, int chargeTime) {
		/**
		 * update battery charge process audit log before
		 */
		repository.updateDronesAuditLogforBatteryProcess(seriaNum, chargeTime, "before");
		try {
			/**
			 * actual battery charge process function here
			 */
			TimeUnit.SECONDS.sleep(chargeTime);
		
				repository.chargeDroneBattery(seriaNum);
			
		} catch (SQLException  |InterruptedException e) {
			logError("Error occured with charging process", e);
		}

		/**
		 * update battery charge process audit log before
		 */
		repository.updateDronesAuditLogforBatteryProcess(seriaNum, chargeTime, "after");
		return seriaNum;
	}

}
