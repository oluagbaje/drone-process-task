/**
 * initial main entry point into the Application
 */
package com.musala.drone.service.main;



import static com.musala.drone.service.utils.LogHandler.*;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import com.musala.drone.service.async.SmppSessionMonitor;
import com.musala.drone.service.router.DroneRoute;
import com.musala.drone.service.services.GatewayServices;

/**
 * @author ADM_AMAGBAJE
 *
 */
public class DroneMainApp {

	/**
	 * This is the main class the execute the application. here apache camel class
	 * is used apache camel is a leading open source api used. The Camel context is
	 * described by the CamelContext interface and is autoconfigured by default The
	 * Camel context activates the routing rules at startup by loading all the
	 * resources required for their execution.
	 */
	public DroneMainApp() {
	      throw new UnsupportedOperationException();
	}

	public static void main(String[] args) {

		DroneRoute routeBuilder = new DroneRoute();
		CamelContext ctx = new DefaultCamelContext();
		routeBuilder.setNotificationProducerTemplate(ctx.createProducerTemplate());
		GatewayServices services = new GatewayServices();

		SmppSessionMonitor sessionMonitor = new SmppSessionMonitor();

		services.initializeGateway();
		sessionMonitor.init();

		



		try {
			ctx.addRoutes(routeBuilder);
			ctx.start();
		} catch (Exception e) {
			logError("Unable to initiate camel routes", e);
		}

	}

}
