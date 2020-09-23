package com.ihsinformatics.gfatmnotifications.common.model;

@Deprecated
public class Contact {

	private Integer personId;
	private Integer locationId;
	private String locationName;
	private String primaryContact;
	private String secondaryContact;
	private String emailAdress;

	public Contact() {
	}

	public Contact(Integer personId, Integer locationId, String locationName, String primaryContact,
			String secondaryContact, String emailAdress) {
		super();
		this.personId = personId;
		this.locationId = locationId;
		this.locationName = locationName;
		this.primaryContact = primaryContact;
		this.secondaryContact = secondaryContact;
		this.emailAdress = emailAdress;
	}

	/**
	 * @return the personId
	 */
	public Integer getPersonId() {
		return personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(Integer personId) {
		this.personId = personId;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return personId + ", " + locationId + ", " + locationName + ", " + primaryContact + ", " + secondaryContact
				+ ", " + emailAdress;
	}
}
