/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.BaseEntity;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.Relationship;
import com.ihsinformatics.gfatmnotifications.common.model.Rule;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil;
import com.ihsinformatics.util.DatabaseUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SearchService {

	private DatabaseUtil dbUtil;

	public SearchService(DatabaseUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	/**
	 * Search for contact using details in Rule object. The rules should be like:
	 * Search Encounter.referral_site, Relationship.index, Relationship.doctor, etc.
	 * 
	 * @param patient
	 * @param encounter
	 * @param rule
	 * @return
	 */
	public String searchContactFromRule(Patient patient, Encounter encounter, Rule rule) {
		String exceptionMessage = rule.getSendTo()
				+ " is not in correct search format. Please specify it like: Search Encounter.referral_site, Search Relationship.index, Search Relationship.doctor, etc.";
		if (rule.getSendTo().toLowerCase().startsWith("search")) {
			String[] parts = rule.getSendTo().split(" ");
			if (parts.length < 2) {
				throw new IllegalArgumentException(exceptionMessage);
			}
			String[] keyValue = parts[1].split("\\.");
			String key = keyValue[0];
			String value = keyValue[1];
			if (keyValue.length < 2) {
				throw new IllegalArgumentException(exceptionMessage);
			}
			BaseEntity entity = null;
			// If the search term is a relationship, then search in DB
			if (key.equalsIgnoreCase("relationship")) {
				entity = searchEntityFromRelationship(patient, value);
			} else if (key.equalsIgnoreCase("encounter")) {
				entity = searchEntityFromEncounter(encounter, value);
			} else if (key.equalsIgnoreCase("patient")) {
				entity = searchEntityFromPatient(patient, value);
			}
			return getPrimaryContactFromEntity(entity);
		} else {
			throw new IllegalArgumentException(exceptionMessage);
		}
	}

	/**
	 * Detects the right object from entity (Patient, Location or User) and returns
	 * its primary contact
	 * 
	 * @param entity
	 * @return
	 */
	public String getPrimaryContactFromEntity(BaseEntity entity) {
		String contact = null;
		if (entity instanceof Patient) {
			contact = ((Patient) entity).getPrimaryContact();
		} else if (entity instanceof Location) {
			contact = ((Location) entity).getPrimaryContact();
		} else if (entity instanceof User) {
			contact = ((User) entity).getPrimaryContact();
		}
		return contact;
	}

	/**
	 * Search for patient, location or user from the encounter. The method first
	 * fetches the observation which matches the variable, then gets the observation
	 * value. If this value is a PatientID, then the contact is searched against
	 * that patient. Otherwise, the contact is searched against in locations
	 * 
	 * @param encounter
	 * @param variable
	 * @return
	 */
	public BaseEntity searchEntityFromEncounter(Encounter encounter, String variable) {
		for (Observation obs : encounter.getObservations()) {
			if (ValidationUtil.variableMatchesWithConcept(variable, obs)) {
				String value = obs.getValue().toString();
				if (ValidationUtil.isValidPatientId(value)) {
					return Context.getPatientByIdentifierOrGeneratedId(value, null, dbUtil);
				} else if (ValidationUtil.isValidLocationId(value)) {
					return Context.getLocationByName(value, dbUtil);
				} else if (ValidationUtil.isValidUsername(value)) {
					return Context.getUserByUsername(value, dbUtil);
				}
			}
		}
		return null;
	}

	/**
	 * Searches for the first relationship found for the patient against given
	 * relationship name
	 * 
	 * @param patient
	 * @param relationshipName
	 * @return
	 */
	public BaseEntity searchEntityFromRelationship(Patient patient, String relationshipName) {
		List<Relationship> relationships = Context.getRelationshipsByPersonId(patient.getPersonId(), dbUtil);
		for (Relationship relationship : relationships) {
			Integer relative = null;
			if (relationship.getPersonA().equals(patient.getPersonId())) {
				relative = relationship.getPersonB();
			} else {
				relative = relationship.getPersonA();
			}
			if (relative != null) {
				if (relationship.getAIsToB().equalsIgnoreCase(relationshipName)
						|| relationship.getBIsToA().equalsIgnoreCase(relationshipName)) {
					return Context.getPatientByIdentifierOrGeneratedId(null, relative, dbUtil);
				}
			}
		}
		return null;
	}

	/**
	 * Searches for the first relationship found for the patient against given
	 * relationship name
	 * 
	 * @param patient
	 * @param attributeName
	 * @return
	 */
	public BaseEntity searchEntityFromPatient(Patient patient, String attributeName) {
		Map<String, String> attributes = Context.getPatientAttributesByGeneratedId(patient.getPersonId(), dbUtil);
		for (Entry<String, String> entry : attributes.entrySet()) {
			if (entry.getKey().equals(attributeName)) {
				String value = entry.getValue();
				if (ValidationUtil.isValidUsername(value)) {
					return Context.getUserByUsername(value, dbUtil);
				} else if (ValidationUtil.isValidLocationId(value)) {
					return Context.getLocationByName(value, dbUtil);
				} else {
					return Context.getPatientByIdentifierOrGeneratedId(value, null, dbUtil);
				}
			}
		}
		return null;
	}
}
