package com.ihsinformatics.gfatmnotifications.common;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.util.DatabaseUtil;

/**
 * @author shujaat.ali@ihsinformatics.com
 *
 */
public class Constant {

	public static final String PROP_FILE_NAME = "gfatm-notifications.properties";
	private static Properties props;

	private static List<User> users;
	private static List<Location> locations;
	private static List<String> userRoles;
	private static Map<Integer, String> encounterTypes;

	private DatabaseUtil dbUtil;

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		Constant.users = users;
	}

	/**
	 * @return the locations
	 */
	public List<Location> getLocations() {
		return locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<Location> locations) {
		Constant.locations = locations;
	}

	/**
	 * @return the userRoles
	 */
	public List<String> getUserRoles() {
		return userRoles;
	}

	/**
	 * @param userRoles the userRoles to set
	 */
	public void setUserRoles(List<String> userRoles) {
		Constant.userRoles = userRoles;
	}

	/**
	 * @return the encounterTypes
	 */
	public Map<Integer, String> getEncounterTypes() {
		return encounterTypes;
	}

	/**
	 * @param encounterTypes the encounterTypes to set
	 */
	public void setEncounterTypes(Map<Integer, String> encounterTypes) {
		Constant.encounterTypes = encounterTypes;
	}

	/**
	 * @return the props
	 */
	public static Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public static void setProps(Properties props) {
		Constant.props = props;
	}

	/**
	 * @return the localDb
	 */
	public DatabaseUtil getLocalDb() {
		return dbUtil;
	}

	/**
	 * @param localDb the localDb to set
	 */
	public void setLocalDb(DatabaseUtil localDb) {
		this.dbUtil = localDb;
	}
}
