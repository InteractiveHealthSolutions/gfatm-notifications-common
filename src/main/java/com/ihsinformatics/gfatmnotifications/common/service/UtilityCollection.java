package com.ihsinformatics.gfatmnotifications.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ihsinformatics.gfatmnotifications.common.model.ChilhoodFact;
import com.ihsinformatics.gfatmnotifications.common.model.Contact;
import com.ihsinformatics.gfatmnotifications.common.model.FastFact;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.PatientScheduled;
import com.ihsinformatics.gfatmnotifications.common.model.PetFact;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.util.DatabaseUtil;

public class UtilityCollection {

	private static UtilityCollection instance = null;
	// static variable should be remove...
	private List<User> users;
	private List<Location> locations;
	private List<Patient> patients;
	private List<String> userRoles;
	private ArrayList<FastFact> factFast;
	private ArrayList<ChilhoodFact> factChildhood;
	private ArrayList<PetFact> factPet;
	private ArrayList<PatientScheduled> patientScheduledsList;
	/* private static List<Person> */
	private Map<Integer, String> encounterTypes;
	private List<Contact> emailList;
	// Email Jobs Instance
	private GfatmDatabaseUtil warehouseInstance;
	private Properties props;
	private DatabaseUtil warehouseDb;
	private DatabaseUtil localDb;

	public static String minutes;
	public static String seconds;
	public static String hours;

	private UtilityCollection() {
	}

	// static method to create instance of Singleton class
	public static UtilityCollection getInstance() {
		if (instance == null) {
			synchronized (UtilityCollection.class) {
				if (instance == null) {
					instance = new UtilityCollection();
				}
			}
		}
		return instance;
	}

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
		this.users = users;
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
		this.locations = locations;
	}

	/**
	 * @return the patients
	 */
	public List<Patient> getPatients() {
		return patients;
	}

	/**
	 * @param patients the patients to set
	 */
	public void setPatients(List<Patient> patients) {
		this.patients = patients;
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
		this.userRoles = userRoles;
	}

	/**
	 * @return the factFast
	 */
	public ArrayList<FastFact> getFactFast() {
		return factFast;
	}

	/**
	 * @param factFast the factFast to set
	 */
	public void setFactFast(ArrayList<FastFact> factFast) {
		this.factFast = factFast;
	}

	/**
	 * @return the factChildhood
	 */
	public ArrayList<ChilhoodFact> getFactChildhood() {
		return factChildhood;
	}

	/**
	 * @param factChildhood the factChildhood to set
	 */
	public void setFactChildhood(ArrayList<ChilhoodFact> factChildhood) {
		this.factChildhood = factChildhood;
	}

	/**
	 * @return the factPet
	 */
	public ArrayList<PetFact> getFactPet() {
		return factPet;
	}

	/**
	 * @param factPet the factPet to set
	 */
	public void setFactPet(ArrayList<PetFact> factPet) {
		this.factPet = factPet;
	}

	/**
	 * @return the patientScheduledsList
	 */
	public ArrayList<PatientScheduled> getPatientScheduledsList() {
		return patientScheduledsList;
	}

	/**
	 * @param patientScheduledsList the patientScheduledsList to set
	 */
	public void setPatientScheduledsList(ArrayList<PatientScheduled> patientScheduledsList) {
		this.patientScheduledsList = patientScheduledsList;
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
		this.encounterTypes = encounterTypes;
	}

	/**
	 * @return the emailList
	 */
	public List<Contact> getEmailList() {
		return emailList;
	}

	/**
	 * @param emailList the emailList to set
	 */
	public void setEmailList(List<Contact> emailList) {
		this.emailList = emailList;
	}

	/**
	 * @return the warehouseInstance
	 */
	public GfatmDatabaseUtil getWarehouseInstance() {
		return warehouseInstance;
	}

	/**
	 * @param warehouseInstance the warehouseInstance to set
	 */
	public void setWarehouseInstance(GfatmDatabaseUtil warehouseInstance) {
		this.warehouseInstance = warehouseInstance;
	}

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * @return the warehouseDb
	 */
	public DatabaseUtil getWarehouseDb() {
		return warehouseDb;
	}

	/**
	 * @param warehouseDb the warehouseDb to set
	 */
	public void setWarehouseDb(DatabaseUtil warehouseDb) {
		this.warehouseDb = warehouseDb;
	}

	/**
	 * @return the localDb
	 */
	public DatabaseUtil getLocalDb() {
		return localDb;
	}

	/**
	 * @param localDb the localDb to set
	 */
	public void setLocalDb(DatabaseUtil localDb) {
		this.localDb = localDb;
	}

}
