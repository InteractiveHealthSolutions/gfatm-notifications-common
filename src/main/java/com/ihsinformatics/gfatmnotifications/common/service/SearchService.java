/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.service;

import java.util.IllegalFormatException;
import java.util.List;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.Relationship;
import com.ihsinformatics.gfatmnotifications.common.model.Rule;
import com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil;
import com.ihsinformatics.util.DatabaseUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SearchService {

	public SearchService() {
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
	public String searchContactFromRule(Patient patient, Encounter encounter, Rule rule, DatabaseUtil dbUtil)
			throws IllegalFormatException {
		// TODO: Complete and test this
		String exceptionMessage = rule.getSendTo()
				+ " is not in correct search format. Please specify it like: Search Encounter.referral_site, Search Relationship.index, Search Relationship.doctor, etc.";
		if (rule.getSendTo().toLowerCase().startsWith("search")) {
			String[] parts = rule.getSendTo().split(" ");
			if (parts.length < 2) {
				throw new IllegalArgumentException(exceptionMessage);
			}
			String[] keyValue = parts[1].split(".");
			if (keyValue.length < 2) {
				throw new IllegalArgumentException(exceptionMessage);
			}
			// If the search term is a relationship, then search in DB
			if (keyValue[0].equalsIgnoreCase("relationship")) {
				Integer relationshipTypeId = Context.getRelationshipTypeId(parts[0]);
				List<Relationship> relationships = Context.getRelationshipsByPersonId(relationshipTypeId,
						patient.getPersonId(), dbUtil);
				Relationship relationship = relationships.get(0);
				Integer relative = null;
				if (patient.getPersonId().equals(relationship.getPersonA())) {
					relative = relationship.getPersonB();
				} else {
					relative = relationship.getPersonA();
				}
				if (relative != null) {
				}
			} else if (keyValue[0].equalsIgnoreCase("encounter")) {
				for (Observation obs : encounter.getObservations()) {
					if (ValidationUtil.variableMatchesWithConcept(keyValue[1], obs)) {
					}
				}
			}
		} else {
			throw new IllegalArgumentException(exceptionMessage);
		}
		String contactNumber = null;
		return contactNumber;
	}
}
