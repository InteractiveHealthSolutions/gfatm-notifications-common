/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.model;

import java.util.List;

import org.joda.time.DateTime;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class Encounter {

	private Integer encounterId;
	private String encounterType;
	private long encounterDatetime;
	private String patientId;
	private String identifier;
	private String patientName;
	private String patientContact;
	private String encounterLocation;
	private String locationContact;
	private String provider;
	private String providerContact;
	private String username;
	private long dateCreated;
	private String uuid;
	private List<Observation> observations;

	public Encounter() {
	}

	public Encounter(Integer encounterId, String encounterType, DateTime encounterDate, String patientId,
			String provider, String location, String uuid) {
		super();
		this.encounterId = encounterId;
		this.encounterType = encounterType;
		this.encounterDatetime = encounterDate.toDate().getTime();
		this.patientId = patientId;
		this.provider = provider;
		this.encounterLocation = location;
		this.uuid = uuid;
	}

	public Encounter(Integer encounterId, String encounterType, long encounterDatetime, String patientId,
			String identifier, String patientName, String patientContact, String encounterLocation,
			String locationContact, String provider, String providerContact, String username, long dateCreated,
			String uuid, List<Observation> observations) {
		super();
		this.encounterId = encounterId;
		this.encounterType = encounterType;
		this.encounterDatetime = encounterDatetime;
		this.patientId = patientId;
		this.identifier = identifier;
		this.patientName = patientName;
		this.patientContact = patientContact;
		this.encounterLocation = encounterLocation;
		this.locationContact = locationContact;
		this.provider = provider;
		this.providerContact = providerContact;
		this.username = username;
		this.dateCreated = dateCreated;
		this.uuid = uuid;
		this.observations = observations;
	}

	public Integer getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}

	public String getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	public long getEncounterDate() {
		return encounterDatetime;
	}

	public void setEncounterDate(long encounterDate) {
		encounterDatetime = encounterDate;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientContact() {
		return patientContact;
	}

	public void setPatientContact(String patientContact) {
		this.patientContact = patientContact;
	}

	public String getLocation() {
		return encounterLocation;
	}

	public void setLocation(String location) {
		encounterLocation = location;
	}

	public String getLocationContact() {
		return locationContact;
	}

	public void setLocationContact(String locationContact) {
		this.locationContact = locationContact;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderContact() {
		return providerContact;
	}

	public void setProviderContact(String providerContact) {
		this.providerContact = providerContact;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<Observation> getObservations() {
		return observations;
	}

	public void setObservations(List<Observation> observations) {
		this.observations = observations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder obsStr = new StringBuilder();
		if (observations != null) {
			obsStr.append("\r\n");
			for (Observation observation : observations) {
				obsStr.append(observation.getConceptShortName());
				obsStr.append("=");
				obsStr.append(observation.getValue());
				obsStr.append("\r\n");
			}
		}
		return encounterId + ", " + encounterType + ", " + encounterDatetime + ", " + patientId + ", " + identifier
				+ ", " + patientName + ", " + encounterLocation + ", " + provider + ", " + username + ", " + uuid + obsStr.toString();
	}
}