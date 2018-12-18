/**
 * 
 */
package com.ihsinformatics.gfatmnotifications.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.service.TestUtil;
import com.ihsinformatics.util.RegexUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class ValidationUtilTest extends TestUtil {

	private static Patient harry;
	private static Patient fast;
	private static Location ihk;

	@BeforeClass
	public static void initialize() throws Exception {
		harry = Context.getPatientByIdentifierOrGeneratedId(null, 7, dbUtil);
		fast = Context.getPatientByIdentifierOrGeneratedId(null, 2601, dbUtil);
		ihk = Context.getLocationByName("IHK-KHI", dbUtil);
	}

	@Test
	public void shouldValidateSingleNotNullCondition() {
		String conditions = "{\"entity\":\"encounter\",\"property\":referral_site,\"validate\":\"NOTNULL\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("FAST-Referral Form"), true, dbUtil);
		assertTrue(ValidationUtil.validateConditions(conditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void shouldValidateSingleEqualsValueCondition() {
		String conditions = "{\"entity\":\"encounter\",\"property\":referral_site,\"validate\":\"EQUALS\",\"value\":\"IHK-KHI\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("FAST-Referral Form"), true, dbUtil);
		assertTrue(ValidationUtil.validateConditions(conditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void shouldValidateSingleNotEqualsValueCondition() {
		String conditions = "{\"entity\":\"encounter\",\"property\":referral_site,\"validate\":\"NOTEQUALS\",\"value\":\"Other\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("FAST-Referral Form"), true, dbUtil);
		assertTrue(ValidationUtil.validateConditions(conditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void shouldValidateANDConditions() {
		String conditions = "{\"entity\":\"encounter\",\"property\":\"patient_source\",\"validate\":\"LIST\",\"value\":\"1648,164239,124068\"}"
				+ "AND" + "{\"entity\":\"encounter\",\"property\":\"primary_contact\",\"validate\":\"NOTNULL\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("Patient Information"), true, dbUtil);
		assertTrue(ValidationUtil.validateConditions(conditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void shouldValidateORConditions() {
		String conditions = "{\"entity\":\"encounter\",\"property\":\"address_provided\",\"validate\":\"EQUALS\",\"value\":\"1065\"}"
				+ "OR"
				+ "{\"entity\":\"encounter\",\"property\":\"patient_source\",\"validate\":\"EQUALS\",\"value\":\"1648\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("Patient Information"), true, dbUtil);
		assertTrue(ValidationUtil.validateConditions(conditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void shouldValidateCombinedConditions() {
		String conditions = "{\"entity\":\"encounter\",\"property\":\"patient_source\",\"validate\":\"EQUALS\",\"value\":\"1648\"}"
				+ "OR"
				+ "{\"entity\":\"encounter\",\"property\":\"address_provided\",\"validate\":\"EQUALS\",\"value\":\"1065\"}"
				+ "AND" + "{\"entity\":\"encounter\",\"property\":\"primary_contact\",\"validate\":\"EXISTS\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("Patient Information"), true, dbUtil);
		assertTrue(ValidationUtil.validateConditions(conditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void shouldValidateStopCondition() {
		String stopConditions = "{\"entity\":\"encounter\",\"encounter\":\"FAST-End of Followup\",\"property\":\"treatment_initiated_referralsite\",\"validate\":\"EQUALS\",\"value\":\"1065\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("FAST-Treatment Followup"), true, dbUtil);
		assertTrue(ValidationUtil.validateConditions(stopConditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void shouldValidateStopConditionWithQuery() {
		String query = "select username from users";
		String stopConditions = "{\"entity\":\"Patient\",\"property\":\"treatmentSupporter\",\"validate\":\"QUERY\",\"value\":\"" + query + "\"}";
		Encounter encounter = Context.getEncounterByPatientIdentifier(fast.getPatientIdentifier(),
				Context.getEncounterTypeId("FAST-Treatment Followup"), true, dbUtil);
		fast.setTreatmentSupporter("owais.hussain");
		assertTrue(ValidationUtil.validateConditions(stopConditions, fast, ihk, encounter, dbUtil));
	}

	@Test
	public void testIsValidPatientId() {
		String patientId = "ABCD3-1";
		assertTrue(patientId + " should be validated!", ValidationUtil.isValidPatientId(patientId));
	}

	@Test
	public void testInvalidPatientId() {
		String patientId = "111111";
		assertFalse(patientId + " should not be validated!", ValidationUtil.isValidPatientId(patientId));
	}

	@Test
	public void testIsValidLocationId() {
		String locationId = "IHK-KHI";
		assertTrue(locationId + " should be validated!", ValidationUtil.isValidLocationId(locationId));
		locationId = "Indus Hospital";
		assertFalse(locationId + " should not be validated!", ValidationUtil.isValidLocationId(locationId));
	}

	@Test
	public void testInvalidLocationId() {
		String locationId = "Indus Hospital";
		assertFalse(locationId + " should not be validated!", ValidationUtil.isValidLocationId(locationId));
	}

	@Test
	public void testIsValidContactNumber() {
		String[] invalidContacts = { "12345", "03333333333", "03219876543", "03001234567" };
		for (String contact : invalidContacts) {
			assertFalse(contact + " should not be validated!", ValidationUtil.isValidContactNumber(contact));
		}
		String[] validContacts = { "03452345345", "03335689552", "03195549110", "03001122346" };
		for (String contact : validContacts) {
			assertTrue(contact + " should be validated!", ValidationUtil.isValidContactNumber(contact));
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateRegex(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateRegex() {
		assertTrue(ValidationUtil.validateRegex(RegexUtil.EMAIL, "alpha@beta.gamma"));
		assertTrue(ValidationUtil.validateRegex(RegexUtil.SQL_DATE, "2018-11-25"));
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateRange(java.lang.String, java.lang.Double)}.
	 * 
	 * @throws InvalidPropertiesFormatException
	 */
	@Test(expected = InvalidPropertiesFormatException.class)
	public void shouldThrowExceptionOnValidateRange() throws InvalidPropertiesFormatException {
		ValidationUtil.validateRange("1,2,3,4,X,10", 0D);
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateRange(java.lang.String, java.lang.Double)}.
	 * 
	 * @throws InvalidPropertiesFormatException
	 */
	@Test
	public void testValidateRange() throws InvalidPropertiesFormatException {
		Double[] validValues = { 0D, 1D, 3D, 5D, 7D };
		Double[] invalidValues = { -1D, 11D };
		String range = "0-3,5,7";
		for (Double value : validValues) {
			assertTrue(value + " should be validated!", ValidationUtil.validateRange(range, value));
		}
		for (Double value : invalidValues) {
			assertFalse(value + " should not be validated!", ValidationUtil.validateRange(range, value));
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateList(java.lang.String, java.lang.String)}.
	 * 
	 * @throws InvalidPropertiesFormatException
	 */
	@Test
	public void testValidateList() throws InvalidPropertiesFormatException {
		String[] validValues = { "Alpha", "Beta", "Gamma" };
		String[] invalidValues = { "Bravo", "Charlie" };
		String list = "Alpha,Beta,Gamma,Delta,Epsilon,Zeta";
		for (String value : validValues) {
			assertTrue(value + " should be validated!", ValidationUtil.validateList(list, value));
		}
		for (String value : invalidValues) {
			assertFalse(value + " should not be validated!", ValidationUtil.validateList(list, value));
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateInQuery(java.lang.String, java.lang.String)}.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testValidateQuery() throws SQLException {
		String query = "select system_id from users where username = 'daemon'";
		assertTrue(ValidationUtil.validateInQuery(query, "daemon"));
		assertFalse(ValidationUtil.validateInQuery(query, null));
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#getEntityPropertyValue(java.lang.Object, java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetPatientPropertyValueByPropertyName() throws Exception {
		String value = ValidationUtil.getEntityPropertyValue(harry, "givenName");
		assertEquals("Harry", value);
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#getEntityPropertyValue(java.lang.Object, java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetLocationPropertyValueByMethodName() throws Exception {
		String value = ValidationUtil.getEntityPropertyValue(ihk, "getLocationName");
		assertEquals("IHK-KHI", value);
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#variableMatchesWithConcept(java.lang.String, com.ihsinformatics.gfatmnotifications.common.model.Observation)}.
	 */
	@Test
	public void shouldMatchIntegerVariableMatchesWithConcept() {
		String variable = "5096";
		Observation obs = new Observation();
		obs.setConceptId(5096);
		obs.setConceptName("RETURN VISIT DATE");
		obs.setConceptShortName("return_visit_date");
		assertTrue(ValidationUtil.variableMatchesWithConcept(variable, obs));
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#variableMatchesWithConcept(java.lang.String, com.ihsinformatics.gfatmnotifications.common.model.Observation)}.
	 */
	@Test
	public void shouldMatchVariableNameMatchesWithConcept() {
		String variable = "RETURN VISIT DATE";
		Observation obs = new Observation();
		obs.setConceptId(5096);
		obs.setConceptName("RETURN VISIT DATE");
		obs.setConceptShortName("return_visit_date");
		assertTrue(ValidationUtil.variableMatchesWithConcept(variable, obs));
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#variableMatchesWithConcept(java.lang.String, com.ihsinformatics.gfatmnotifications.common.model.Observation)}.
	 */
	@Test
	public void shouldMatchVariableShorrNameMatchesWithConcept() {
		String variable = "return_visit_date";
		Observation obs = new Observation();
		obs.setConceptId(5096);
		obs.setConceptName("RETURN VISIT DATE");
		obs.setConceptShortName("return_visit_date");
		assertTrue(ValidationUtil.variableMatchesWithConcept(variable, obs));
	}
}
