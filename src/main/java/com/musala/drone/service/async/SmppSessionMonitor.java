/**
 *
 */
package com.musala.drone.service.async;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.musala.drone.service.repository.ClientRepository;
import com.musala.drone.service.services.GatewayServices;
import static com.musala.drone.service.utils.LogHandler.logInfo;

/**
 * @author ADM_AMAGBAJE
 *
 */
public class SmppSessionMonitor {

	public static final GatewayServices services = new GatewayServices();
	ClientRepository repository = new ClientRepository();

	public void init() {
		MonitorTask task = new MonitorTask();
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		executorService.scheduleWithFixedDelay(task, 5, 5, TimeUnit.SECONDS);
		 logInfo("Battery Task processing... ");

	}

	private class MonitorTask implements Runnable {

		@Override
		public void run() {
			repository.droneBatterychargingProcess();

			GatewayServices.droneBatteryChargeStateMaP.forEach((seriaNum, battLevel) -> {
				services.estimateBatteryChargeLevel(seriaNum, battLevel);
			});

			GatewayServices.droneBatteryChargeProcessMaP.forEach((seriaNums, timeToCharge) -> {
				String comments = "";
				switch (timeToCharge) {
				case 10:
					services.chargeProcess(seriaNums, timeToCharge);
					comments = "battery successfully charged after " + timeToCharge + "s";
					break;
				case 15:
					services.chargeProcess(seriaNums, timeToCharge);
					comments = "battery successfully charged after " + timeToCharge + "s";
					break;
				case 20:
					services.chargeProcess(seriaNums, timeToCharge);
					comments = "battery successfully charged after " + timeToCharge + "s";
					break;
				default:
					comments = "battery is okay";
				}
			});

		}
	}
}
