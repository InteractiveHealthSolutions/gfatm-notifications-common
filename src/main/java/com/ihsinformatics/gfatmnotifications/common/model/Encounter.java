/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.model;

import java.util.Map;

import org.joda.time.DateTime;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class Encounter {

	Integer encounter_id;
	String encounter_type;
	long encounter_datetime;
	String patientId;
	String identifier;
	String patient_name;
	String patient_contact;
	String encounter_location;
	String location_contact;
	String provider;
	String provider_contact;
	String username;
	long date_created;
	String uuid;
	Map<String, Object> observations;

	public Encounter() {
	}

	public Encounter(Integer encounterId, String encounterType, DateTime encounterDate, String patientId,
			String provider, String location, String uuid) {
		super();
		encounter_id = encounterId;
		encounter_type = encounterType;
		// this.encounterdatetime = encounterDate;
		this.patientId = patientId;
		this.provider = provider;
		encounter_location = location;
		this.uuid = uuid;
	}

	public Integer getEncounterId() {
		return encounter_id;
	}

	public void setEncounterId(Integer encounterId) {
		encounter_id = encounterId;
	}

	public String getEncounterType() {
		return encounter_type;
	}

	public void setEncounterType(String encounterType) {
		encounter_type = encounterType;
	}

	public long getEncounterDate() {
		return encounter_datetime;
	}

	public void setEncounterDate(long encounterDate) {
		encounter_datetime = encounterDate;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patient_name;
	}

	public void setPatientName(String patientName) {
		patient_name = patientName;
	}

	public String getPatientContact() {
		return patient_contact;
	}

	public void setPatientContact(String patientContact) {
		patient_contact = patientContact;
	}

	public String getLocation() {
		return encounter_location;
	}

	public void setLocation(String location) {
		encounter_location = location;
	}

	public String getLocationContact() {
		return location_contact;
	}

	public void setLocationContact(String locationContact) {
		location_contact = locationContact;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderContact() {
		return provider_contact;
	}

	public void setProviderContact(String providerContact) {
		provider_contact = providerContact;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getDateCreated() {
		return date_created;
	}

	public void setDateCreated(long dateCreated) {
		date_created = dateCreated;
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

	public Map<String, Object> getObservations() {
		return observations;
	}

	public void setObservations(Map<String, Object> observations) {
		this.observations = observations;
	}
}