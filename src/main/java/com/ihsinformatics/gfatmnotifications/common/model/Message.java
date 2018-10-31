package com.ihsinformatics.gfatmnotifications.common.model;

import java.util.Date;

public class Message {
	private String preparedMessage;
	private String contactNumber;
	private String projectName;
	private Date sendOn;
	
	
	public Message(String preparedMessage, String contactNumber, String projectName, Date sendOn) {
		super();
		this.preparedMessage = preparedMessage;
		this.contactNumber = contactNumber;
		this.projectName = projectName;
		this.sendOn = sendOn;
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
	public Date getSendOn() {
		return sendOn;
	}
	public void setSendOn(Date sendOn) {
		this.sendOn = sendOn;
	}
	
	

}
