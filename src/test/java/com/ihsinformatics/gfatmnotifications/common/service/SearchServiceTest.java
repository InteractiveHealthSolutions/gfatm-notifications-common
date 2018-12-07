/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.service;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.BaseEntity;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SearchServiceTest extends TestUtil {

	private static SearchService service;
	private static Patient harry;

	@BeforeClass
	public static void initialize() throws Exception {
		service = new SearchService(dbUtil);
		harry = Context.getPatientByIdentifierOrGeneratedId(null, 7, dbUtil);
	}

	@Test
	public void shouldSearchContactFromRule() {
	}

	@Test
	public void testGetPrimaryContactFromEntity() {
	}

	@Test
	public void shouldSearchLocationEntityFromEncounter() {
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
