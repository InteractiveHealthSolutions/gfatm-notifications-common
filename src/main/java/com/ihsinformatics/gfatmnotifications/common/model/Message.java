package com.ihsinformatics.gfatmnotifications.common.model;

import java.util.Date;

public class Message {
	private String preparedMessage;
	private String contactNumber;
	private String projectName;
	private String sendOn;
	private String encounterType;
	private String patientName;
	private String contactType;
	private String scheduleDate;
	private String location;
	
	
	public Message(String preparedMessage, String contactNumber, String projectName, String sendOn) {
		super();
		this.preparedMessage = preparedMessage;
		this.contactNumber = contactNumber;
		this.projectName = projectName;
		this.sendOn = sendOn;
	}
	
	
	public Message(String contactNumber, String sendOn, String encounterType, String patientName, String contactType,
			String scheduleDate, String location) {
		super();
		this.contactNumber = contactNumber;
		this.sendOn = sendOn;
		this.encounterType = encounterType;
		this.patientName = patientName;
		this.contactType = contactType;
		this.scheduleDate = scheduleDate;
		this.location = location;
	}


	public String getPreparedMessage() {
		return preparedMessage;
	}
	public void setPreparedMessage(String preparedMessage) {
		this.preparedMessage = preparedMessage;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getSendOn() {
		return sendOn;
	}
	public void setSendOn(String sendOn) {
		this.sendOn = sendOn;
	}


	public String getEncounterType() {
		return encounterType;
	}


	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}


	public String getPatientName() {
		return patientName;
	}


	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}


	public String getContactType() {
		return contactType;
	}


	public void setContactType(String contactType) {
		this.contactType = contactType;
	}


	public String getScheduleDate() {
		return scheduleDate;
	}


	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}
	
	

}
