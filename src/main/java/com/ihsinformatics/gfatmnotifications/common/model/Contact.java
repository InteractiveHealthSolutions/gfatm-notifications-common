package com.ihsinformatics.gfatmnotifications.common.model;

public class Contact {

	private String emailAdress;
	private Integer locationId;
	private String locationName;
	private String primaryContact;
	private String secondaryContact;

	/**
	 * @return the emailAdress
	 */
	public String getEmailAdress() {
		return emailAdress;
	}

	/**
	 * @param emailAdress the emailAdress to set
	 */
	public void setEmailAdress(String emailAdress) {
		this.emailAdress = emailAdress;
	}

	/**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the primaryContact
	 */
	public String getPrimaryContact() {
		return primaryContact;
	}

	/**
	 * @param primaryContact the primaryContact to set
	 */
	public void setPrimaryContact(String primaryContact) {
		this.primaryContact = primaryContact;
	}

	/**
	 * @return the secondaryContact
	 */
	public String getSecondaryContact() {
		return secondaryContact;
	}

	/**
	 * @param secondaryContact the secondaryContact to set
	 */
	public void setSecondaryContact(String secondaryContact) {
		this.secondaryContact = secondaryContact;
	}

	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * @param locationName the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

}
