package com.musala.drone.service.model;

import java.math.BigInteger;

public class Drone {

	public Drone() {
		super();

	}

	private String droneSerialNumber;
	private String droneModel;
	private double droneWeightLimit;
	private double droneBatteryLevel;
	private String droneState;
	public Drone(String droneSerialNumber, String droneModel, double droneWeightLimit, double droneBatteryLevel,
			String droneState) {
		super();
		this.droneSerialNumber = droneSerialNumber;
		this.droneModel = droneModel;
		this.droneWeightLimit = droneWeightLimit;
		this.droneBatteryLevel = droneBatteryLevel;
		this.droneState = droneState;
	}
	public String getDroneSerialNumber() {
		return droneSerialNumber;
	}
	public void setDroneSerialNumber(String droneSerialNumber) {
		this.droneSerialNumber = droneSerialNumber;
	}
	public String getDroneModel() {
		return droneModel;
	}
	public void setDroneModel(String droneModel) {
		this.droneModel = droneModel;
	}
	public double getDroneWeightLimit() {
		return droneWeightLimit;
	}
	public void setDroneWeightLimit(double droneWeightLimit) {
		this.droneWeightLimit = droneWeightLimit;
	}
	public double getDroneBatteryLevel() {
		return droneBatteryLevel;
	}
	public void setDroneBatteryLevel(double droneBatteryLevel) {
		this.droneBatteryLevel = droneBatteryLevel;
	}
	public String getDroneState() {
		return droneState;
	}
	public void setDroneState(String droneState) {
		this.droneState = droneState;
	}
	
	
	
	
	
	


}
