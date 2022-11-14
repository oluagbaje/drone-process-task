/**
 * ClientRepository
 * holds the statement for database connection
 */
package com.musala.drone.service.repository;

import static com.musala.drone.service.utils.LogHandler.logError;
import static com.musala.drone.service.utils.LogHandler.logInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musala.drone.service.dal.RelationalDataSource;
import com.musala.drone.service.services.GatewayServices;
import com.musala.drone.service.utils.LogHandler;

/**
 * @author ADM_AMAGBAJE
 *
 */
public class ClientRepository {

	/**
	 * The methods here holds the statement connection for the the main Database
	 * connector is in the com.musala.drone.service.config package. the
	 * configuration class is used through here.
	 */

	public static final GatewayServices services = new GatewayServices();
	static final String dateFormatUse = "yyyy-MM-dd HH:mm:ss";
	static final String ZerodateFormatUse = "0000-00-00 00:00:00";

	public void addNewDrone(String droneRec) throws SQLException {

		// Create a new record

		String[] addItems = droneRec.split("\\|");
		String droneSerialNumber = addItems[0];
		String droneModel = addItems[1];
		double droneWeightLimit = Double.valueOf(addItems[2]);
		double droneBatteryLevel = Double.valueOf(addItems[3]);
		String droneState = addItems[4];

		String query = "INSERT INTO drone_process_record(drone_serial_number,drone_model,drone_weight_limit,drone_battery_level,drone_state) VALUES( ?, ?, ?, ?, ?)";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, droneSerialNumber);
			pst.setString(2, droneModel);
			pst.setDouble(3, droneWeightLimit);
			pst.setDouble(4, droneBatteryLevel);
			pst.setString(5, droneState);

			pst.executeUpdate();

		} catch (SQLException ex) {

			LogHandler.logError(ex.getMessage(), ex);
		}

	}

	public void addMedications(String filepath) throws SQLException {

		// Create a new record

		String line = "";
		String splitBy = ",";

		String query = "INSERT INTO medication_tbl(med_code,med_name,med_weight,med_image) VALUES( ?, ?, ?, ?) ON CONFLICT DO NOTHING";
		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(query)) {

			BufferedReader br = new BufferedReader(new FileReader(filepath));
			while ((line = br.readLine()) != null) // returns a Boolean value
			{
				String[] data = line.split(splitBy);
				// use comma as separator
				String medCode = data[0];
				String medName = data[1];
				int medWeight = Integer.valueOf(data[2]);
				String medImage = data[3];
				System.out.println(medCode);

				pst.setString(1, medCode.trim());
				pst.setString(2, medName.trim());
				pst.setInt(3, medWeight);
				pst.setString(4, medImage.trim());

				pst.executeUpdate();

			}
		} catch (IOException | SQLException e) {
			LogHandler.logError(e.getMessage(), e);
		}

	}

	public String dronesStateAndBatteryConfirmation(String droneSerialNumber) {
		String ready = "KO";

		String sqlupdate = "select * from drone_process_record where drone_serial_number =? ";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(sqlupdate)) {

			con.setAutoCommit(false);
			messageTypeSQLStatement.setString(1, droneSerialNumber);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			while (rs.next()) {
				double drone_battery_level = rs.getDouble("drone_battery_level");
				String droneState = rs.getString("drone_state");

				if (drone_battery_level > 0.25 && droneState.equals("IDLE")) {
					ready = "OK";

				}
			}

			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting drone_process_record error", e);
		}

		return ready;
	}

	public String selectMedicationByCode(String medcode) {
		String medStringOut = "";

		String sqlupdate = "select * from medication_tbl where med_code =? ";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(sqlupdate)) {

			con.setAutoCommit(false);
			messageTypeSQLStatement.setString(1, medcode);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			while (rs.next()) {
				String medName = rs.getString("med_Name").trim();
				double medWeight = rs.getDouble("med_weight");
				String medImage = rs.getString("med_Image").trim();

				medStringOut = medcode + "|" + medName + "|" + medWeight + "|" + medImage;
			}

			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting medication record error", e);
		}

		return medStringOut;
	}

	public String addDroneActivityHistory(String HistRec, String medCode) throws SQLException {
		int autoId = 0;
		String outStr = "";
		// Create a new record
		String medStrOut = selectMedicationByCode(medCode);
		System.out.println(medStrOut);
		Date currentTime = new Date();
		currentTime.toString();
		DateFormat formatter = new SimpleDateFormat(dateFormatUse);

		String[] addItems = HistRec.split("\\|");
		String droneSerialNumber = addItems[0];
		String droneModel = addItems[1];
		double droneWeightLimit = Double.valueOf(addItems[2]);
		double droneBatteryLevel = Double.valueOf(addItems[3]);
		String droneState = addItems[4];
		String processStartTime = formatter.format(currentTime);
		String processEndTime = ZerodateFormatUse;
		String[] medItems = medStrOut.split("\\|");
		medCode = medItems[0];
		String medName = medItems[1];
		double medWeight = Double.valueOf(medItems[2]);
		String medImage = medItems[3];

		String query = "INSERT INTO drone_process_hist(drone_serial_number,drone_model,drone_weight_limit,drone_battery_level,drone_state,med_code,med_name,med_weight,med_image,process_start_time,process_end_time) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pst = null;
		try {

			Connection con = RelationalDataSource.getConnection();
			pst = con.prepareStatement(query, pst.RETURN_GENERATED_KEYS);

			pst.setString(1, droneSerialNumber);
			pst.setString(2, droneModel);
			pst.setDouble(3, droneWeightLimit);
			pst.setDouble(4, droneBatteryLevel);
			pst.setString(5, droneState);
			pst.setString(6, medCode);
			pst.setString(7, medName);
			pst.setDouble(8, medWeight);
			pst.setString(9, medImage);
			pst.setString(10, processStartTime);
			pst.setString(11, processEndTime);

			int affectedRows = pst.executeUpdate();

		} catch (SQLException ex) {

			LogHandler.logError(ex.getMessage(), ex);
		}

		try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				autoId = generatedKeys.getInt(1);

				outStr = processStartTime + "|" + autoId;
			} else {
				throw new SQLException("Creating user failed, no ID obtained.");

			}
		}
		return outStr;
	}

	public String updatePerviousDroneStateProcessEndDate(String processFileName) {

		Date currentTime = new Date();
		currentTime.toString();
		DateFormat formatter = new SimpleDateFormat(dateFormatUse);

		String[] deliveryRepo = processFileName.split("\\|");
		String serialNumber = deliveryRepo[0];
		String previousStartDate = deliveryRepo[1];
		String previouProcessId = deliveryRepo[2];
		String processEndDate = formatter.format(currentTime);

		///
		String supdate = "UPDATE drone_process_hist " + "SET process_end_time = ? " + "WHERE hist_item_id = ?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(supdate)) {
			pst.setString(1, processEndDate);
			pst.setString(2, previouProcessId);

			pst.executeUpdate();

		} catch (SQLException ex) {

			logError(ex.getMessage(), ex);
		}

		return "";
	}

	public String dronesStateChangeUpdate(String droneSerialNumber, String newState, String medCode) {
		String outStr = "";

		String sqlupdate = "select * from drone_process_record where drone_serial_number =? ";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(sqlupdate)) {

			con.setAutoCommit(false);
			messageTypeSQLStatement.setString(1, droneSerialNumber);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			while (rs.next()) {
				String droneState = rs.getString("drone_state");
				String serialnumber = rs.getString("drone_serial_number").trim();
				String drone_model = rs.getString("drone_model").trim();
				double drone_weight_limit = rs.getDouble("drone_weight_limit");
				double drone_battery_level = rs.getDouble("drone_battery_level");
				String drone_state = rs.getString("drone_state");
				System.out.println(droneState);
				String HistRec = serialnumber + "|" + drone_model + "|" + drone_weight_limit + "|" + drone_battery_level
						+ "|" + newState;
				System.out.println(HistRec);
				updateDroneRecords(droneSerialNumber, newState);
				outStr = addDroneActivityHistory(HistRec, medCode);
			}

			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting drone_process_record error", e);
		}

		return outStr;
	}

	public void updateDroneRecords(String droneSerialNumber, String newState) throws SQLException {

		String supdate = "UPDATE drone_process_record " + "SET drone_state = ? " + "WHERE drone_serial_number = ?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(supdate)) {
			pst.setString(1, newState);
			pst.setString(2, droneSerialNumber);
			pst.executeUpdate();

		} catch (SQLException ex) {

			logError(ex.getMessage(), ex);
		}

	}
	
	public void chargeDroneBattery(String droneSerialNumber) throws SQLException {
final double fullyCharged = 0.99;
		String supdate = "UPDATE drone_process_record " + "SET drone_battery_level = ? " + "WHERE drone_serial_number = ?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(supdate)) {
			pst.setDouble(1, fullyCharged);
			pst.setString(2, droneSerialNumber);
			pst.executeUpdate();

		} catch (SQLException ex) {

			logError(ex.getMessage(), ex);
		}

	}

	public String dronesLowBatterySelect() {
		String outRes = "";

		String sqlupdate = "select * from drone_process_record where drone_battery_level < 0.25";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(sqlupdate)) {

			con.setAutoCommit(false);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			List<String> serialNumber = new ArrayList<String>();

			while (rs.next()) {
				serialNumber.add(rs.getString("drone_serial_number").trim());
			}
			outRes = serialNumber.toString();
			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting drone_process_record error", e);
		}

		return outRes;
	}

	public String droneBatterychargingProcess() {
		String outRes = "";
		final String IDLE = "IDLE";
		

		String sqlupdate = "select * from drone_process_record where drone_state =?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(sqlupdate)) {

			con.setAutoCommit(false);
			messageTypeSQLStatement.setString(1, IDLE);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			while (rs.next()) {
				
				double batLevel = rs.getDouble("drone_battery_level");
				if(batLevel <= 0.50) {
					String seriaNum = rs.getString("drone_serial_number").trim();
					
					GatewayServices.droneBatteryChargeStateMaP.put(seriaNum, batLevel);
				}
				
				
			}

			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting drone_process_record error", e);
		}

		return outRes;
	}

	public String updateDronesAuditLogforBatteryProcess(String serialNumber, int chargeDuration, String type) {
		String outRes = "";

		String sqlupdate = "select * from drone_process_record where drone_serial_number =?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(sqlupdate)) {

			con.setAutoCommit(false);
			messageTypeSQLStatement.setString(1, serialNumber);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			while (rs.next()) {
				String droneSerialNumber = rs.getString("drone_serial_number").trim();
				double battLevel = rs.getDouble("drone_battery_level");
				String droneState = rs.getString("drone_state");

				String repoString = droneSerialNumber + "|" + battLevel + "|" + chargeDuration + "|" + droneState;
				updateBatteryAuditlogBefore(repoString, type);

			}

			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting drone_process_record error", e);
		}

		return outRes;
	}

	public void updateDroneHistoryRecordWithEndTime(String processEndTime, int autoId) throws SQLException {

		///
		String supdate = "UPDATE drone_process_hist " + "SET process_end_time = ? " + "WHERE autoId = ?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(supdate)) {
			pst.setString(1, processEndTime);
			pst.setInt(8, autoId);
			pst.executeUpdate();

		} catch (SQLException ex) {

			logError(ex.getMessage(), ex);
		}

	}

	public void updateBatteryAuditlogBefore(String reportString, String type) throws SQLException {
		final double maxChargeLevel = 0.98;
		Date currentTime = new Date();
		currentTime.toString();
		DateFormat formatter = new SimpleDateFormat(dateFormatUse);

		String droneSerialNumber = "";
		double droneBatteryLevelBeforecharge = 0.0;
		double droneBatteryLevelAftercharge = 0.0;
		String chargeStartTime = "";
		String chargeEndTime = "";
		int chargeDuration = 0;
		String droneState = "";

		if (type.equals("before")) {
			String[] deliveryRepo = reportString.split("\\|");
			droneSerialNumber = deliveryRepo[0];
			droneBatteryLevelBeforecharge = Double.valueOf(deliveryRepo[1]);
			droneBatteryLevelAftercharge = Double.valueOf(deliveryRepo[1]);
			chargeStartTime = formatter.format(currentTime);
			chargeEndTime = ZerodateFormatUse;
			chargeDuration = Integer.valueOf(deliveryRepo[2]);
			droneState = deliveryRepo[3];
		}

		if (type.equals("after")) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, -chargeDuration);
			String[] deliveryRepo = reportString.split("\\|");
			droneSerialNumber = deliveryRepo[0];
			droneBatteryLevelBeforecharge = Double.valueOf(deliveryRepo[1]);
			droneBatteryLevelAftercharge = maxChargeLevel;
			chargeStartTime = formatter.format(cal.getTime());
			chargeEndTime = formatter.format(currentTime);
			chargeDuration = Integer.valueOf(deliveryRepo[2]);
			droneState = deliveryRepo[3];
		}

		String query = "INSERT INTO drone_battery_Battery_charge_log(drone_serial_number,drone_batterylevel_before_charge,drone_batterylevel_after_charge,charge_start_time,charge_end_time,charge_duration,drone_state) VALUES( ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, droneSerialNumber);
			pst.setDouble(2, droneBatteryLevelBeforecharge);
			pst.setDouble(3, droneBatteryLevelAftercharge);
			pst.setString(4, chargeStartTime);
			pst.setString(5, chargeEndTime);
			pst.setInt(6, chargeDuration);
			pst.setString(7, droneState);

			pst.executeUpdate();

		} catch (SQLException ex) {

			LogHandler.logError(ex.getMessage(), ex);
		}

	}

	public String returnAvailableDrone(String state) {
		String outRes = "";

		String sqlupdate = "select * from drone_process_record where drone_state=?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(sqlupdate)) {

			con.setAutoCommit(false);
			messageTypeSQLStatement.setString(1, state);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			JSONObject droneAvailObj = new JSONObject();
			JSONArray arrdroneAvail = new JSONArray();

			while (rs.next()) {

				String serialnumber = rs.getString("drone_serial_number").trim();
				String drone_model = rs.getString("drone_model");
				double drone_weight_limit = rs.getDouble("drone_weight_limit");
				double drone_battery_level = rs.getDouble("drone_battery_level");
				String drone_state = rs.getString("drone_state");
				droneAvailObj.put("droneSerialNumber", serialnumber);
				droneAvailObj.put("drone_model", drone_model);
				droneAvailObj.put("drone_weight_limit", drone_weight_limit);
				droneAvailObj.put("drone_battery_level", drone_battery_level);
				droneAvailObj.put("drone_state", drone_state);
				arrdroneAvail.put(droneAvailObj);

			}
			JSONObject listsHeader = new JSONObject();
			listsHeader.put("AvailableDrones", arrdroneAvail);

			outRes = listsHeader.toString();
			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting customer_consent_record error", e);
		}

		return outRes;
	}

	public void updateDeliveryReport(String reportString) throws SQLException {

		// Create a new SmsDeliveryReport
		String[] deliveryRepo = reportString.split("\\|");
		String messgId = deliveryRepo[0];
		String statusSent = deliveryRepo[1];

		String submittedSent = deliveryRepo[5];
		String deliveredSent = deliveryRepo[6];
		String errorCode = deliveryRepo[7];
		String errorMessage = deliveryRepo[8];
		String dateSent = deliveryRepo[9];
		String doneDateSent = deliveryRepo[10];

		///
		String supdate = "UPDATE SmsDeliveryReport "
				+ "SET StatusSent = ? , SubmittedSent = ? , DeliveredSent = ? , Error_code = ?, Error_message = ?, DateSent = ?, DoneDateSent = ?"
				+ "WHERE messg_Id = ?";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(supdate)) {
			pst.setString(1, statusSent);
			pst.setString(2, submittedSent);
			pst.setString(3, deliveredSent);
			pst.setString(4, errorCode);
			pst.setString(5, errorMessage);
			pst.setString(6, dateSent);
			pst.setString(7, doneDateSent);
			pst.setString(8, messgId);
			pst.executeUpdate();

		} catch (SQLException ex) {

			logError(ex.getMessage(), ex);
		}

	}

	public void addNewCustomerInital2(String customerConsRec) throws SQLException {

		// Create a new SmsDeliveryReport
		String[] addItems = customerConsRec.split("\\|");
		String consentItemId = addItems[0];
		String customerPhone = addItems[1];
		String customerConsent = addItems[2];
		String contractType = addItems[3];
		String otpMessageSent = addItems[4];
		String otpTokenSent = addItems[5];
		String dateTimeOtpSent = addItems[6];
		String dateTimeOtpVerified = addItems[7];
		int noos = Integer.parseInt(addItems[8]);
		int numberOfOtpSent = noos;
		boolean otpVerified = false;

		String query = "INSERT INTO consent_process_record(consent_item_id,customer_phone,customer_consent,contract_type,otp_message_sent,otp_token_sent,date_time_otp_sent,date_time_otp_verified,number_of_otp_sent,otp_verified) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, consentItemId);
			pst.setString(2, customerPhone);
			pst.setString(3, customerConsent);
			pst.setString(4, contractType);
			pst.setString(5, otpMessageSent);
			pst.setString(6, otpTokenSent);
			pst.setString(7, dateTimeOtpSent);
			pst.setString(8, dateTimeOtpVerified);
			pst.setInt(9, numberOfOtpSent);
			pst.setBoolean(10, otpVerified);
			pst.executeUpdate();

		} catch (SQLException ex) {

			LogHandler.logError(ex.getMessage(), ex);
		}

	}

	public void addNewCustomerInital(String consentItemId, String customerPhone, String contractType,
			String otpTokenSent) throws SQLException {

		// Create a new SmsDeliveryReport

		String customerConsent = "";
		String otpMessageSent = "";
		String dateTimeOtpSent = "";
		String dateTimeOtpVerified = "";
		int numberOfOtpSent = 1;
		boolean otpVerified = false;
		Date currentTime = new Date();
		dateTimeOtpSent = currentTime.toString();

		String query = "INSERT INTO consent_process_record(consent_item_id,customer_phone,customer_consent,contract_type,otp_message_sent,otp_token_sent,date_time_otp_sent,date_time_otp_verified,number_of_otp_sent,otp_verified) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, consentItemId);
			pst.setString(2, customerPhone);
			pst.setString(3, customerConsent);
			pst.setString(4, contractType);
			pst.setString(5, otpMessageSent);
			pst.setString(6, otpTokenSent);
			pst.setString(7, dateTimeOtpSent);
			pst.setString(8, dateTimeOtpVerified);
			pst.setInt(9, numberOfOtpSent);
			pst.setBoolean(10, otpVerified);
			pst.executeUpdate();

		} catch (SQLException ex) {

			LogHandler.logError(ex.getMessage(), ex);
		}

	}

	public void addNewCustomer(String customerInfo) throws SQLException {

		// Create a new SmsDeliveryReport
		String[] addItems = customerInfo.split("\\|");
		String consentId = addItems[0];
		String customerPhone = "";
		String customerConsent = addItems[1];
		String otpSent = addItems[2];
		String otpToken = addItems[3];
		String dateTimeOtpSent = "";
		String dateTimeOtpVerified = "";
		int numberOfOtpSent = 0;
		boolean otpVerified = false;

		String query = "INSERT INTO consentprocess(consentId,customerPhone,customerConsent,otpSent,otpToken,dateTimeOtpSent,dateTimeOtpVerified,numberOfOtpSent,OtpVerified) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, consentId);
			pst.setString(2, customerPhone);
			pst.setString(3, customerConsent);
			pst.setString(4, otpSent);
			pst.setString(5, otpToken);
			pst.setString(6, dateTimeOtpSent);
			pst.setString(7, dateTimeOtpVerified);
			pst.setInt(8, numberOfOtpSent);
			pst.setBoolean(9, otpVerified);
			pst.executeUpdate();

		} catch (SQLException ex) {

			LogHandler.logError(ex.getMessage(), ex);
		}

	}

	public boolean stylest(String inb) {
		boolean outb = false;
		if (inb.equalsIgnoreCase("true")) {
			outb = true;
		}

		return outb;
	}

	public void updateCustomerConsent(String consentStr) {

		String[] items = consentStr.split("\\|");
		String consentItemId = items[0];
		String customerPhone = items[1];
		String customerConsent = items[2];
		String contractType = items[3];
		String otpMessageSent = items[4];
		String otpTokenSent = items[5];
		String dateTimeOtpSent = items[6];
		String dateTimeOtpVerified = items[7];
		int noos = Integer.parseInt(items[8]);
		int numberOfOtpSent = noos;
		boolean veri = stylest(items[9]);
		boolean otpVerified = veri;

		String sqlUpdate = "select insert_customer_consent_report(?,?,?,?,?,?,?,?,?,?);";

		try (Connection connection = RelationalDataSource.getConnection();
				PreparedStatement messageUpdateSQLStatement = connection.prepareStatement(sqlUpdate)) {

			messageUpdateSQLStatement.setString(1, consentItemId);
			messageUpdateSQLStatement.setString(2, customerPhone);
			messageUpdateSQLStatement.setString(3, customerConsent);
			messageUpdateSQLStatement.setString(4, contractType);
			messageUpdateSQLStatement.setString(5, otpMessageSent);
			messageUpdateSQLStatement.setString(6, otpTokenSent);
			messageUpdateSQLStatement.setString(7, dateTimeOtpSent);
			messageUpdateSQLStatement.setString(8, dateTimeOtpVerified);
			messageUpdateSQLStatement.setInt(9, numberOfOtpSent);
			messageUpdateSQLStatement.setBoolean(10, otpVerified);

			messageUpdateSQLStatement.execute();

		} catch (SQLException e) {
			logError(e.getMessage(), e);
		}

	}

	public String returnCapitalCharge(String servId) {
		String msgId = "";

		String supdate = "select charge_capital,charge_id from get_capital_charge(?);";

		try (Connection con = RelationalDataSource.getConnection();
				PreparedStatement messageTypeSQLStatement = con.prepareStatement(supdate)) {

			con.setAutoCommit(false);
			messageTypeSQLStatement.setString(1, servId);
			ResultSet rs = messageTypeSQLStatement.executeQuery();

			if (rs.next()) {

				msgId = rs.getString("charge_capital");
				return msgId;
			}
			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			logError("getting charge_capital from consent_insurance_charge_parameter error", e);
		}

		return msgId;

	}

}
