/**
 * 
 */
package com.ihsinformatics.gfatmnotifications.common.service;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.Context;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SearchServiceTest {

	@BeforeClass
	public static void initialize() throws Exception {
		Context.initialize(false, false);
	}

	/**
	 * Test method for {@link com.ihsinformatics.gfatmnotifications.common.service.SearchService#searchContactFromRule(com.ihsinformatics.gfatmnotifications.common.model.Patient, com.ihsinformatics.gfatmnotifications.common.model.Encounter, com.ihsinformatics.gfatmnotifications.common.model.Rule)}.
	 */
	@Test
	public void testSearchContactFromRule() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ihsinformatics.gfatmnotifications.common.service.SearchService#searchEntityFromEncounter(com.ihsinformatics.gfatmnotifications.common.model.Encounter, java.lang.String)}.
	 */
	@Test
	public void testSearchEntityFromEncounter() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ihsinformatics.gfatmnotifications.common.service.SearchService#getContactFromEntity(com.ihsinformatics.gfatmnotifications.common.model.BaseEntity)}.
	 */
	@Test
	public void testGetPrimaryContactFromEntity() {
		fail("Not yet implemented");
	}

}
