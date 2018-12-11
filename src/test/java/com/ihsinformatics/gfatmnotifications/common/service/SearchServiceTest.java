/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.BaseEntity;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.Rule;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SearchServiceTest extends TestUtil {

	private static SearchService service;
	private static Patient harry;
	private static Patient fast;
	private static Patient mehwish;
	private static Location ihk;

	@BeforeClass
	public static void initialize() throws Exception {
		service = new SearchService(dbUtil);
		harry = Context.getPatientByIdentifierOrGeneratedId(null, 7, dbUtil);
		fast = Context.getPatientByIdentifierOrGeneratedId(null, 2601, dbUtil);
		mehwish = Context.getPatientByIdentifierOrGeneratedId(null, 531, dbUtil);
		ihk = Context.getLocationByName("IHK-KHI", dbUtil);
	}

	@Test
	public void shouldSearchContactFromSearchRelationship() {
		Rule searchRule = new Rule();
		searchRule.setSendTo("Search Relationship.Treatment Supporter");
		Integer encounterTypeId = Context.getEncounterTypeId("FAST-Referral Form");
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(), encounterTypeId, true, dbUtil);
		String contact = service.searchContactFromRule(fast, encounter, searchRule);
		assertNotNull(contact);
	}

	@Test
	public void shouldSearchContactFromSearchEncounter() {
		Rule searchRule = new Rule();
		searchRule.setSendTo("Search Encounter.referral_site");
		Integer encounterTypeId = Context.getEncounterTypeId("FAST-Referral Form");
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(), encounterTypeId, true, dbUtil);
		String contact = service.searchContactFromRule(fast, encounter, searchRule);
		assertNotNull(contact);
	}

	@Test
	public void testGetPrimaryContactFromEntity() {
		String contact = service.getPrimaryContactFromEntity(ihk);
		assertEquals(contact, ihk.getPrimaryContact());
	}

	@Test
	public void shouldSearchLocationEntityFromEncounter() {
		Integer encounterTypeId = Context.getEncounterTypeId("FAST-Referral Form");
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(), encounterTypeId, true, dbUtil);
		BaseEntity entity = service.searchEntityFromEncounter(encounter, "referral_site");
		assertNotNull(entity);
	}

	@Test
	public void shouldSearchUserEntityFromEncounter() {
		Integer encounterTypeId = Context.getEncounterTypeId("PMDT-Treatment Registration");
		Encounter encounter = Context.getEncounterByPatientIdentifier(mehwish.getPatientIdentifier(), encounterTypeId, true, dbUtil);
		BaseEntity entity = service.searchEntityFromEncounter(encounter, "treatment_coordinator_id");
		assertNotNull(entity);
	}

	@Test
	public void shouldSearchEntityFromRelationship() {
		BaseEntity entity = service.searchEntityFromRelationship(harry, "Treatment Supporter");
		assertNotNull(entity);
	}

	@Test
	public void shouldSearchEntityFromPatient() {
		BaseEntity entity = service.searchEntityFromPatient(harry, "Health Center");
		assertNotNull(entity);
	}
}
