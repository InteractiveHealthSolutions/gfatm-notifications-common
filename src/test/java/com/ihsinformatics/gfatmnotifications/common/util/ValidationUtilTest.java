/**
 * 
 */
package com.ihsinformatics.gfatmnotifications.common.util;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.service.TestUtil;
import com.ihsinformatics.util.RegexUtil;

import junit.framework.Assert;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class ValidationUtilTest extends TestUtil {

	@BeforeClass
	public static void initialize() throws Exception {
	}

	@Test
	public void testIsValidPatientId() {
		String patientId = "ABCD3-1";
		Assert.assertTrue(patientId + " should be validated!", ValidationUtil.isValidPatientId(patientId));
	}

	@Test
	public void testInvalidPatientId() {
		String patientId = "111111";
		Assert.assertFalse(patientId + " should not be validated!", ValidationUtil.isValidPatientId(patientId));
	}

	@Test
	public void testIsValidLocationId() {
		String locationId = "IHK-KHI";
		Assert.assertTrue(locationId + " should be validated!", ValidationUtil.isValidLocationId(locationId));
		locationId = "Indus Hospital";
		Assert.assertFalse(locationId + " should not be validated!", ValidationUtil.isValidLocationId(locationId));
	}

	@Test
	public void testInvalidLocationId() {
		String locationId = "Indus Hospital";
		Assert.assertFalse(locationId + " should not be validated!", ValidationUtil.isValidLocationId(locationId));
	}

	@Test
	public void testIsValidContactNumber() {
		String[] invalidContacts = { "12345", "03333333333", "03219876543", "03001234567" };
		for (String contact : invalidContacts) {
			Assert.assertFalse(contact + " should not be validated!", ValidationUtil.isValidContactNumber(contact));
		}
		String[] validContacts = { "03452345345", "03335689552", "03195549110", "03001122346" };
		for (String contact : validContacts) {
			Assert.assertTrue(contact + " should be validated!", ValidationUtil.isValidContactNumber(contact));
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateRegex(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateRegex() {
		Assert.assertTrue(ValidationUtil.validateRegex(RegexUtil.EMAIL, "alpha@beta.gamma"));
		Assert.assertTrue(ValidationUtil.validateRegex(RegexUtil.SQL_DATE, "2018-11-25"));
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
			Assert.assertTrue(value + " should be validated!", ValidationUtil.validateRange(range, value));
		}
		for (Double value : invalidValues) {
			Assert.assertFalse(value + " should not be validated!", ValidationUtil.validateRange(range, value));
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
			Assert.assertTrue(value + " should be validated!", ValidationUtil.validateList(list, value));
		}
		for (String value : invalidValues) {
			Assert.assertFalse(value + " should not be validated!", ValidationUtil.validateList(list, value));
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateQuery(java.lang.String, java.lang.String)}.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testValidateQuery() throws SQLException {
		String query = "select system_id from users where username = 'daemon'";
		Assert.assertTrue(ValidationUtil.validateQuery(query, "daemon"));
		Assert.assertFalse(ValidationUtil.validateQuery(query, null));
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateStopConditions(com.ihsinformatics.gfatmnotifications.common.model.Patient, com.ihsinformatics.gfatmnotifications.common.model.Location, com.ihsinformatics.gfatmnotifications.common.model.Encounter, com.ihsinformatics.gfatmnotifications.common.model.Rule, com.ihsinformatics.util.DatabaseUtil)}.
	 */
	@Test
	@Ignore
	public void testValidateANDStopConditions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateStopConditions(com.ihsinformatics.gfatmnotifications.common.model.Patient, com.ihsinformatics.gfatmnotifications.common.model.Location, com.ihsinformatics.gfatmnotifications.common.model.Encounter, com.ihsinformatics.gfatmnotifications.common.model.Rule, com.ihsinformatics.util.DatabaseUtil)}.
	 */
	@Test
	@Ignore
	public void testValidateORStopConditions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateSingleStopCondition(java.lang.String, com.ihsinformatics.gfatmnotifications.common.model.Patient, com.ihsinformatics.gfatmnotifications.common.model.Location, com.ihsinformatics.util.DatabaseUtil)}.
	 */
	@Test
	@Ignore
	public void testValidateSingleStopCondition() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#validateConditions(com.ihsinformatics.gfatmnotifications.common.model.Patient, com.ihsinformatics.gfatmnotifications.common.model.Location, com.ihsinformatics.gfatmnotifications.common.model.Encounter, com.ihsinformatics.gfatmnotifications.common.model.Rule)}.
	 */
	@Test
	@Ignore
	public void testValidateConditions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#getEntityPropertyValue(java.lang.Object, java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testGetEntityPropertyValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil#variableMatchesWithConcept(java.lang.String, com.ihsinformatics.gfatmnotifications.common.model.Observation)}.
	 */
	@Test
	@Ignore
	public void testVariableMatchesWithConcept() {
		fail("Not yet implemented");
	}

}
