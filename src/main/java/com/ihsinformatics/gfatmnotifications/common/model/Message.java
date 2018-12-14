/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.model;

/**
 * @author muhammad.ahmed@ishinformatics.com
 *
 */
public class Message {
	private String preparedMessage;
	private String contact;
	private String preparedOn;
	private String sendOn;
	private String encounterType;
	private String recipient;
	private Rule rule;

	public Message(String preparedMessage, String contactNumber, String encounterType, String preparedOn, String sendOn,
			String recipient, Rule rule) {
		super();
		this.setPreparedMessage(preparedMessage);
		this.setContact(contactNumber);
		this.setPreparedOn(preparedOn);
		this.setEncounterType(encounterType);
		this.setSendOn(sendOn);
		this.setRecipient(recipient);
		this.setRule(rule);
	}

	/**
	 * @return the preparedMessage
	 */
	public String getPreparedMessage() {
		return preparedMessage;
	}

	/**
	 * @param preparedMessage the preparedMessage to set
	 */
	public void setPreparedMessage(String preparedMessage) {
		this.preparedMessage = preparedMessage;
	}

	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * @param contact the contact number or email to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * @return the preparedOn
	 */
	public String getPreparedOn() {
		return preparedOn;
	}

	/**
	 * @param preparedOn the preparedOn to set
	 */
	public void setPreparedOn(String preparedOn) {
		this.preparedOn = preparedOn;
	}

	/**
	 * @return the sendOn
	 */
	public String getSendOn() {
		return sendOn;
	}

	/**
	 * @param sendOn the sendOn to set
	 */
	public void setSendOn(String sendOn) {
		this.sendOn = sendOn;
	}

	/**
	 * @return the encounterType
	 */
	public String getEncounterType() {
		return encounterType;
	}

	/**
	 * @param encounterType the encounterType to set
	 */
	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	/**
	 * @return the recipient
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * @param recipient the recipient to set
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}
}
