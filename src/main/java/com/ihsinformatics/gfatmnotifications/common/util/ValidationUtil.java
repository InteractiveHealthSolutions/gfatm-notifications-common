/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONObject;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.Rule;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.JsonUtil;
import com.ihsinformatics.util.RegexUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class ValidationUtil {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	public static final String PATIENT_ID_REGEX = "[0-9A-Za-z]{5}\\-[0-9]";
	public static final String LOCATION_ID_REGEX = "[A-Z\\-]+";
	public static final String USERNAME_REGEX = "[a-z]+\\.[a-z]+";
	public static final String VALIDATE_STRING = "validate";
	public static final String ENTITY_STRING = "entity";
	public static final String PROPERTY_STRING = "property";
	public static final String VALUE_STRING = "value";

	public static final String EQUALS_STRING = "EQUALS";
	public static final String NOTEQUALS_STRING = "NOTEQUALS";
	public static final String RANGE_STRING = "RANGE";
	public static final String REGEX_STRING = "REGEX";
	public static final String INQUERY_STRING = "QUERY";
	public static final String NOTINQUERY_STRING = "NOTQUERY";
	public static final String LIST_STRING = "LIST";
	public static final String NOTNULL_STRING = "NOTNULL";
	public static final String PRESENT_STRING = "PRESENT";
	public static final String EXISTS_STRING = "EXISTS";

	private ValidationUtil() {
	}

	/**
	 * Checks whether given patient ID matches with the ID scheme or not.
	 * 
	 * @param patientId
	 * @return
	 */
	public static boolean isValidPatientId(String patientId) {
		return patientId.matches(PATIENT_ID_REGEX);
	}

	/**
	 * Checks whether given location ID matches with the ID scheme or not.
	 * 
	 * @param locationId
	 * @return
	 */
	public static boolean isValidLocationId(String locationId) {
		return locationId.matches(LOCATION_ID_REGEX);
	}

	/**
	 * Checks whether given location ID matches with the ID scheme or not.
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isValidUsername(String username) {
		return username.matches(USERNAME_REGEX);
	}

	/**
	 * Checks whether given email address is valid or not.
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidEmailAddress(String email) {
		return RegexUtil.isEmailAddress(email);
	}

	/**
	 * Checks whether given contact number is legitimate or not. Invalid numbers
	 * contain same digits or digits in increasing or decreasing order
	 *
	 * @param number
	 * @return
	 */
	public static boolean isValidContactNumber(String number) {
		if (!RegexUtil.isContactNumber(number)) {
			return false;
		}
		if (number.length() < 9) {
			return false;
		}
		char[] array = number.toCharArray();
		int occurrances = 0;
		// Similarity check
		for (int i = 1; i < array.length; i++) {
			if (array[i] == array[i - 1]) {
				occurrances++;
			} else {
				occurrances = 0;
			}
			if (occurrances >= 5) {
				return false;
			}
		}
		// Series check
		for (int i = 1; i < array.length; i++) {
			if (Math.abs(array[i] - array[i - 1]) == 1) {
				occurrances++;
			} else {
				occurrances = 0;
			}
			if (occurrances >= 5) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Validates a value against given regular expression
	 * 
	 * @param regex
	 * @param value
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static boolean validateRegex(String regex, String value) throws PatternSyntaxException {
		try {
			Pattern.compile(regex);
		} catch (Exception e) {
			throw new PatternSyntaxException("Invalid regular expression provided for validation.", regex, -1);
		}
		return value.matches(regex);
	}

	/**
	 * Validates a value against given range (as a string)
	 * 
	 * @param range
	 * @param value
	 * @return
	 * @throws InvalidPropertiesFormatException
	 */
	public static boolean validateRange(String range, Double value) throws InvalidPropertiesFormatException {
		boolean valid = false;
		if (!range.matches("^[0-9.,-]+")) {
			throw new InvalidPropertiesFormatException(
					"Invalid format provided for validation range. Must be a list of hyphenated or comma-separated tuples of numbers (1-10; 2.2-3.0; 1,3,5; 1-5,7,9).");
		}
		// Break into tuples
		String[] tuples = range.split(",");
		for (String tuple : tuples) {
			if (tuple.contains("-")) {
				String[] parts = tuple.split("-");
				double min = Double.parseDouble(parts[0]);
				double max = Double.parseDouble(parts[1]);
				valid = (value >= min && value <= max);
			} else {
				valid = (Double.compare(value.doubleValue(), Double.parseDouble(tuple)) == 0);
			}
			if (valid) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Validates a value against given list of comma-separated values
	 * 
	 * @param list
	 * @param value
	 * @return
	 * @throws InvalidPropertiesFormatException
	 */
	public static boolean validateList(String list, String value) throws InvalidPropertiesFormatException {
		if (!list.matches("^[A-Za-z0-9,_\\-\\s]+")) {
			throw new InvalidPropertiesFormatException(
					"Invalid format provided for validation list. Must be a comma-separated list of alpha-numeric values (white space, hypen and underscore allowed).");
		}
		if (value == null) {
			return false;
		}
		String[] values = list.split(",");
		for (int i = 0; i < values.length; i++) {
			if (value.trim().equalsIgnoreCase(values[i].trim()))
				return true;
		}
		return false;
	}

	/**
	 * Searches for a value in results against a query. Returns true if found
	 * 
	 * @param query
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public static boolean validateInQuery(String query, String value) throws SQLException {
		Object[][] data = Context.getOpenmrsDb().getTableData(query);
		for (Object[] row : data) {
			for (Object obj : row) {
				if (obj.toString().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Searches for a value in results against a query. Returns false if found
	 * 
	 * @param query
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public static boolean validateNotInQuery(String query, String value) throws SQLException {
		return !validateInQuery(query, value);
	}

	/**
	 * Validates all conditions and stop conditions in given rule against parameter
	 * objects
	 * 
	 * @param rule
	 * @param patient
	 * @param location
	 * @param encounter
	 * @param dbUtil
	 * @return
	 */
	public static boolean validateRule(Rule rule, Patient patient, Location location, Encounter encounter,
			DatabaseUtil dbUtil) {
		boolean conditions = false;
		boolean stopConditions = true;
		if ("".equals(rule.getConditions())) {
			conditions = true;
		} else {
			conditions = validateConditions(rule.getConditions(), patient, location, encounter, dbUtil);
		}
		if (!conditions) {
			return false;
		}
		if ("".equals(rule.getStopConditions())) {
			stopConditions = false;
		} else {
			stopConditions = validateConditions(rule.getStopConditions(), patient, location, encounter, dbUtil);
		}
		return !stopConditions;
	}

	/**
	 * Validates all conditions
	 * 
	 * @param patient
	 * @param location
	 * @param encounter
	 * @param conditions
	 * @param dbUtil
	 * @return
	 */
	public static boolean validateConditions(String conditions, Patient patient, Location location, Encounter encounter,
			DatabaseUtil dbUtil) {
		// Check if the encounter requires to be retrieved
		String orPattern = "(.)+\\}OR\\{(.)+";
		String andPattern = "(.)+\\}AND\\{(.)+";
		if (conditions.matches(orPattern) && conditions.matches(andPattern)) {
			String[] conditionTokens = conditions.split("( )?OR( )?");
			for (String condition : conditionTokens) {
				// First, process all singleton OR conditions
				if (!condition.matches(andPattern)) {
					if (validateSingleCondition(condition, patient, location, encounter, dbUtil)) {
						return true;
					}
				}
			}
			// If we are here, none of the OR conditions were fulfilled
			for (String condition : conditionTokens) {
				// First, process all AND conditions
				if (condition.matches(andPattern)) {
					String[] andConditions = condition.split("( )?AND( )?");
					for (String nestedCondition : andConditions) {
						if (!validateSingleCondition(nestedCondition, patient, location, encounter, dbUtil)) {
							return false;
						}
					}
				}
			}
			return true;
		}
		if (conditions.matches(orPattern)) {
			String[] orConditions = conditions.split("( )?OR( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is true
				if (validateSingleCondition(condition, patient, location, encounter, dbUtil)) {
					return true;
				}
			}
		} else if (conditions.matches(andPattern)) {
			String[] andConditions = conditions.split("( )?AND( )?");
			for (String condition : andConditions) {
				// No need to proceed even if one condition is false
				if (!validateSingleCondition(condition, patient, location, encounter, dbUtil)) {
					return false;
				}
			}
			return true;
		} else {
			return validateSingleCondition(conditions, patient, location, encounter, dbUtil);
		}
		return false;
	}

	/**
	 * This method validates a single condition token
	 * 
	 * @param condition
	 * @param patient
	 * @param location
	 * @param encounter
	 * @param dbUtil
	 * @return
	 */
	public static boolean validateSingleCondition(String condition, Patient patient, Location location,
			Encounter encounter, DatabaseUtil dbUtil) {
		JSONObject jsonObject = JsonUtil.getJSONObject(condition);
		// Prerequisites must be checked
		if (!(jsonObject.has(ENTITY_STRING) && jsonObject.has(PROPERTY_STRING) && jsonObject.has(VALIDATE_STRING))) {
			throw new MissingFormatArgumentException(
					"Condition must contain all required keys: entity, property and validate.");
		}
		// Check if there's another encounter to retrieve
		if (jsonObject.has("encounter")) {
			String encounterName = jsonObject.getString("encounter");
			encounter = Context.getEncounterByPatientIdentifier(patient.getPatientIdentifier(),
					Context.getEncounterTypeId(encounterName), true, dbUtil);
			if (encounter == null) {
				return true;
			}
		}
		// Clear to proceed
		String entity = jsonObject.getString(ENTITY_STRING);
		String validationType = jsonObject.getString(VALIDATE_STRING);
		String property = jsonObject.getString(PROPERTY_STRING);
		String expectedValue = null;
		if (jsonObject.has(VALUE_STRING)) {
			expectedValue = jsonObject.getString(VALUE_STRING);
		}
		String actualValue = null;
		// In case of Encounter, search through observations
		try {
			if (entity.equalsIgnoreCase("encounter")) {
				if (encounter.getObservations() == null) {
					return false;
				}
				for (Observation obs : encounter.getObservations()) {
					// Search for the observation's concept name matching the variable name
					if (variableMatchesWithConcept(property, obs)) {
						actualValue = obs.getValueCoded() == null ? obs.getValue().toString()
								: obs.getValueCoded().toString();
						return validateValue(validationType, expectedValue, actualValue);
					}
				}
			}
			// In case of Patient or Location, search for the defined property
			else if (entity.equals("Patient")) {
				actualValue = getEntityPropertyValue(patient, property);
			} else if (entity.equals("Location")) {
				actualValue = getEntityPropertyValue(location, property);
			}
			return validateValue(validationType, expectedValue, actualValue);
		} catch (InvalidPropertiesFormatException | NoSuchFieldException | IllegalAccessException
				| NoSuchMethodException | InvocationTargetException | SQLException e) {
			log.warning(e.getMessage());
			return false;
		}
	}

	/**
	 * This method checks the type of validation and validates the value passed
	 * 
	 * @param validationType
	 * @param expectedValue
	 * @param actualValue
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws SQLException
	 */
	private static boolean validateValue(String validationType, String expectedValue, String actualValue)
			throws InvalidPropertiesFormatException, SQLException {
		switch (validationType) {
		case EQUALS_STRING:
			return expectedValue.equalsIgnoreCase(actualValue);
		case NOTEQUALS_STRING:
			return !expectedValue.equalsIgnoreCase(actualValue);
		case RANGE_STRING:
			Double valueDouble = Double.parseDouble(actualValue);
			return ValidationUtil.validateRange(expectedValue, valueDouble);
		case REGEX_STRING:
			return ValidationUtil.validateRegex(expectedValue, actualValue);
		case INQUERY_STRING:
			return ValidationUtil.validateInQuery(expectedValue, actualValue);
		case NOTINQUERY_STRING:
			return ValidationUtil.validateNotInQuery(expectedValue, actualValue);
		case LIST_STRING:
			return ValidationUtil.validateList(expectedValue, actualValue);
		case NOTNULL_STRING:
		case PRESENT_STRING:
		case EXISTS_STRING:
			return actualValue != null;
		default:
			throw new InvalidPropertiesFormatException("Unknown validation type: " + validationType);
		}
	}

	/**
	 * Checks the entity type of the object and looks for the value in given
	 * property using Reflection
	 * 
	 * @param object
	 * @param property could be a field name or a method name (strictly camel case)
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public static String getEntityPropertyValue(Object object, String property)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		String actualValue;
		// Is it a field or method?
		if (property.matches("get[A-Z](.)+")) {
			Method method = object.getClass().getDeclaredMethod(property, new Class[] {});
			boolean flag = method.isAccessible();
			method.setAccessible(true);
			Object objectReturned = method.invoke(object, new Object[] {});
			actualValue = objectReturned == null ? null : objectReturned.toString();
			method.setAccessible(flag);
		} else {
			Field field = object.getClass().getDeclaredField(property);
			boolean flag = field.isAccessible();
			field.setAccessible(true);
			actualValue = field.get(object).toString();
			field.setAccessible(flag);
		}
		return actualValue;
	}

	/**
	 * This function only checks whether the variable is an Integer ID, or a concept
	 * name and matches with the concept in the observation
	 * 
	 * @param variable
	 * @param observation
	 * @return
	 */
	public static boolean variableMatchesWithConcept(String variable, Observation observation) {
		// Check if the variable is a concept ID
		if (RegexUtil.isNumeric(variable, false)) {
			return observation.getConceptId().equals(Integer.parseInt(variable));
		} else if (observation.getConceptName() != null && observation.getConceptName().equalsIgnoreCase(variable)) {
			return true;
		} else if (observation.getConceptShortName() != null
				&& observation.getConceptShortName().equalsIgnoreCase(variable)) {
			return true;
		}
		return false;
	}

	/**
	 * This method reads all fields from given rule and checks if the syntax is
	 * valid
	 * 
	 * @param rule
	 * @return
	 */
	public static void validateRuleSyntax(Rule rule) {
		// Encounter type should exist
		if (Context.getEncounterTypeId(rule.getEncounterType()) == null) {
			throw new IllegalArgumentException("Encounter type could not be found for rule: " + rule.toString());
		}

		// Send to patient, facility, user, supervisor, location, or search
		// Entity.property
		List<String> possibilities = Arrays.asList("patient", "facility", "location", "supervisor", "user",
				"search encounter.", "search patient.", "search relationship.");
		boolean flag = false;
		for (String possibility : possibilities) {
			if (rule.getSendTo().toLowerCase().startsWith(possibility)) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			throw new IllegalArgumentException(
					"Value of 'Send to' does not follow the standard for rule: " + rule.toString());
		}

		// Schedule date should point to a date field
		if (rule.getScheduleDate() == null) {
			throw new IllegalArgumentException("Schedule Date is not provided for rule: " + rule.toString());
		}

		// Only hours, days and months are allowed here
		if (!rule.getPlusMinusUnit().toLowerCase().matches("hours|days|months")) {
			throw new IllegalArgumentException(
					"Value provided as unit of plus minus is not permitted in rule: " + rule.toString());
		}

		// Message code should exist
		if (Objects.equals(rule.getMessageCode(), "")) {
			throw new IllegalArgumentException("Message code not provided for rule: " + rule.toString());
		}

		// Fetch duration should either be blank or contain a number followed by unit
		if (!(rule.getFetchDuration().equals("")
				|| rule.getFetchDuration().toLowerCase().matches("^[0-9]+ (hours|days|months)"))) {
			throw new IllegalArgumentException(
					"Fetch duration must either be empty or in form like 2 hours, 3 days, 1 months, etc. Error found in rule: "
							+ rule.toString());
		}
	}
}
