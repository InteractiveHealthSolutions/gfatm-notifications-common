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
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
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
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.JsonUtil;
import com.ihsinformatics.util.RegexUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class ValidationUtil {
	public static final String PATIENT_ID_REGEX = "[0-9A-Za-z]{5}\\-[0-9]";
	public static final String LOCATION_ID_REGEX = "[A-Z\\-]+";
	public static final String USERNAME_REGEX = "[a-z]+\\.[a-z]+";
	private static final Logger log = Logger.getLogger(Class.class.getName());

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
		String[] values = list.split(",");
		for (int i = 0; i < values.length; i++) {
			if (value.trim().equalsIgnoreCase(values[i].trim()))
				return true;
		}
		return false;
	}

	/**
	 * Validates a value against given query
	 * 
	 * @param query
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public static boolean validateQuery(String query, String value) throws SQLException {
		Object[][] data = Context.getOpenmrsDb().getTableData(query);
		for (Object[] row : data) {
			for (Object obj : row) {
				if (Objects.equals(obj, value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * MOAV (mother of all validations). This method first checks if the input value
	 * is of give dataType (String, Double, etc.), then matches regex. The regex
	 * must be in format: LHS=RHS. If LHS is "REGEX", then RHS is expected to be a
	 * valid regular expression to match value with; If LHS is "LIST", then RHS
	 * should be a comma-separated list of strings to lookup value in; If LHS is
	 * "RANGE", then RHS should be a set of range parts, like
	 * 1-10,2.2,3.2,5.5,17.1-18.9, etc. in which, the value will be checked; If LHS
	 * is "QUERY", then RHS is expected to be a SQL to lookup the value in database
	 * 
	 * @param regex
	 * @param dataType
	 * @param value
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws PatternSyntaxException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static boolean validateData(String regex, String dataType, String value)
			throws InvalidPropertiesFormatException, PatternSyntaxException, ClassNotFoundException, SQLException {
		boolean isValidDataType = false;
		boolean isValidValue = false;
		dataType = dataType.toLowerCase();
		DataType dataTypeEnum = DataType.getDataTypeByAlias(dataType);
		// Validate according to given data type
		switch (dataTypeEnum) {
		case BOOLEAN:
			isValidDataType = value.matches("Y|N|y|n|true|false|True|False|TRUE|FALSE|0|1");
			break;
		case CHARACTER:
			isValidDataType = value.length() == 1;
			break;
		case DATE:
			isValidDataType = value.matches(RegexUtil.SQL_DATE);
			break;
		case DATETIME:
			isValidDataType = value.matches(RegexUtil.SQL_DATETIME);
			break;
		case FLOAT:
			isValidDataType = value.matches(RegexUtil.DECIMAL);
			break;
		case INTEGER:
			isValidDataType = value.matches(RegexUtil.INTEGER);
			break;
		case STRING:
			isValidDataType = true;
			break;
		case TIME:
			isValidDataType = value.matches(RegexUtil.SQL_TIME);
			break;
		default:
			break;
		}
		// Check if validation regex is provided
		if (regex == null) {
			isValidValue = true;
		} else {
			String[] parts = regex.split("=");
			if (parts.length != 2) {
				throw new InvalidPropertiesFormatException(
						"Invalid value provided for validation regex. Must be in format LHS=RHS");
			}
			String type = parts[0];
			String validatorStr = parts[1];
			// Validate regular expression
			if (type.equalsIgnoreCase("regex")) {
				isValidValue = validateRegex(validatorStr, value);
			}
			// Validate range
			else if (type.equalsIgnoreCase("range")) {
				try {
					double num = Double.parseDouble(value);
					isValidValue = validateRange(validatorStr, num);
				} catch (NumberFormatException e) {
					isValidValue = false;
				}
			}
			// Validate comma-separated list
			else if (type.equalsIgnoreCase("list")) {
				isValidValue = validateList(validatorStr, value);
			}
			// Validate using query
			else if (type.equalsIgnoreCase("query")) {
				isValidValue = validateQuery(validatorStr, value);
			}
			// Validate matching single value
			if (type.equalsIgnoreCase("value")) {
				isValidValue = validatorStr.equalsIgnoreCase(value);
			}
		}
		return (isValidDataType && isValidValue);
	}

	public static boolean validateStopConditions(Patient patient, Location location, Encounter encounter, Rule rule,
			DatabaseUtil dbUtil) {
		if (rule.getStopCondition() == null || rule.getStopCondition().isEmpty()) {
			return false;
		}
		String conditions = rule.getStopCondition();
		String orPattern = "(.)+OR(.)+";
		String andPattern = "(.)+AND(.)+";
		if (conditions.matches(orPattern) && conditions.matches(andPattern)) {
			String[] orConditions = conditions.split("( )?OR( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is true
				if (condition.matches(andPattern)) {

					String[] andConditions = condition.split("( )?AND( )?");
					for (String nestedCondition : andConditions) {
						// No need to proceed even if one condition is false
						if (!validateSingleStopCondition(nestedCondition, patient, location, dbUtil)) {
							return false;
						}
					}
					return true;
				} else {
					return validateSingleStopCondition(condition, patient, location, dbUtil);
				}
			}
		}
		if (conditions.matches(orPattern)) {
			String[] orConditions = conditions.split("( )?OR( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is true

				if (validateSingleStopCondition(condition, patient, location, dbUtil)) {
					return true;
				}
			}
		} else if (conditions.matches(andPattern)) {
			String[] andConditions = conditions.split("( )?AND( )?");
			for (String condition : andConditions) {
				// No need to proceed even if one condition is false
				if (!validateSingleStopCondition(condition, patient, location, dbUtil)) {
					return false;
				}
			}
			return true;
		} else {
			return validateSingleStopCondition(conditions, patient, location, dbUtil);
		}
		return false;
	}

	public static boolean validateSingleStopCondition(String condition, Patient patient, Location location,
			DatabaseUtil dbUtil) {
		boolean result = false;
		JSONObject jsonObject = JsonUtil.getJSONObject(condition);
		if (jsonObject.has("entity") && jsonObject.has("property") && jsonObject.has("validate")) {
			String entity = jsonObject.getString("entity");
			String validationType = jsonObject.getString("validate");
			String property = jsonObject.getString("property");
			String expectedValue = jsonObject.getString("value");
			String actualValue = null;
			String encounter = jsonObject.getString("encounter");
			Encounter baseEncounter = null;
			if (encounter != null && !(encounter.isEmpty())) {
				try {
					baseEncounter = Context.getEncounterByPatientIdentifier(patient.getPatientIdentifier(),
							Context.getEncounterTypeId(encounter), dbUtil);
					baseEncounter.setObservations(Context.getEncounterObservations(baseEncounter, dbUtil));
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			try {
				// In case of Encounter, search through observations
				if (entity.equals("Encounter")) {
					// Search for the observation's concept name matching the variable name
					Observation target = null;
					for (Observation observation : baseEncounter.getObservations()) {
						if (variableMatchesWithConcept(property, observation)) {
							target = observation;
							break;
						}
					}
					if (target == null) {
						return result;
					}
					if (validationType.equalsIgnoreCase("List")) {
						actualValue = target.getValueCoded().toString();
					} else {
						actualValue = target.getValue().toString();
					}
				}
				// In case of Patient or Location, search for the defined property
				else if (entity.equals("Patient")) {
					actualValue = getEntityPropertyValue(patient, property);
				} else if (entity.equals("Location")) {
					actualValue = getEntityPropertyValue(location, property);

				}
				if (validationType.equals("VALUE")) {
					return actualValue.equalsIgnoreCase(expectedValue);
				} else if (validationType.equalsIgnoreCase("NOTEQUALS")) {
					return !actualValue.equalsIgnoreCase(expectedValue);
				} else if (validationType.equalsIgnoreCase("RANGE")) {
					Double valueDouble = Double.parseDouble(actualValue);
					return ValidationUtil.validateRange(expectedValue, valueDouble);
				} else if (validationType.equalsIgnoreCase("REGEX")) {
					return ValidationUtil.validateRegex(expectedValue, actualValue);
				} else if (validationType.equalsIgnoreCase("QUERY")) {
					return ValidationUtil.validateQuery(expectedValue, actualValue);
				} else if (validationType.equalsIgnoreCase("NOTNULL") || validationType.equalsIgnoreCase("PRESENT")
						|| validationType.equalsIgnoreCase("EXISTS")) {
					if (actualValue != null) {
						return true;
					}
					return false;
				} else if (validationType.equalsIgnoreCase("LIST")) {
					return ValidationUtil.validateList(expectedValue, actualValue);

				}

			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			// log.severe("Condition must contain all four required keys: entity, validate
			// and value");
			return false;
		} else if (jsonObject.has("entity") && jsonObject.has("encounter") && jsonObject.has("validate")
				&& jsonObject.has("after")) {
			String entity = jsonObject.getString("entity");
			String validationType = jsonObject.getString("validate");
			String encounter = jsonObject.getString("encounter");
			String afterEncounterType = jsonObject.getString("after");
			Encounter baseEncounter = null;
			if (encounter != null || (!encounter.isEmpty())) {
				try {
					baseEncounter = Context.getEncounterByPatientIdentifier(patient.getPatientIdentifier(),
							Context.getEncounterTypeId(encounter), dbUtil);
					baseEncounter.setObservations(Context.getEncounterObservations(baseEncounter, dbUtil));
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				// Check the type of validation and call respective method
				if (validationType.equalsIgnoreCase("Encounter")) {
					Encounter afterEncounter = Context.getEncounterByPatientIdentifier(patient.getPatientIdentifier(),
							Context.getEncounterTypeId(afterEncounterType), dbUtil);
					if (baseEncounter.getEncounterDate() > afterEncounter.getEncounterDate()) {
						return true;
					}
				}
				// log.severe("Condition must contain all four required keys: entity, validate,
				// after and encounter ");
				return false;
			}
		}

		return false;

	}

	/**
	 * At present, this function can only validate all OR's or all AND's, not their
	 * combinations
	 * 
	 * @param patient
	 * @param location
	 * @param encounter
	 * @param rule
	 * 
	 * @return
	 */
	public static boolean validateConditions(Patient patient, Location location, Encounter encounter, Rule rule) {
		String conditions = rule.getConditions().trim();
		String orPattern = "(.)+OR(.)+";
		String andPattern = "(.)+AND(.)+";
		if (conditions.matches(orPattern) && conditions.matches(andPattern)) {
			String[] orConditions = conditions.split("( )?OR( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is true
				if (condition.matches(andPattern)) {

					String[] andConditions = conditions.split("( )?AND( )?");
					for (String nestedCondition : andConditions) {
						// No need to proceed even if one condition is false
						if (!validateSingleCondition(nestedCondition, patient, location, encounter,
								encounter.getObservations())) {
							return false;
						}
					}
					return true;
				} else {
					return validateSingleCondition(conditions, patient, location, encounter,
							encounter.getObservations());
				}
			}
			return false;
		}
		if (conditions.matches(orPattern)) {
			String[] orConditions = conditions.split("( )?OR( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is true
				if (validateSingleCondition(condition, patient, location, encounter, encounter.getObservations())) {
					return true;
				}
			}
		} else if (conditions.matches(andPattern)) {
			String[] orConditions = conditions.split("( )?AND( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is false
				if (!validateSingleCondition(condition, patient, location, encounter, encounter.getObservations())) {
					return false;
				}
			}
			return true;
		} else {
			return validateSingleCondition(conditions, patient, location, encounter, encounter.getObservations());
		}
		return false;
	}

	private static boolean validateSingleCondition(String condition, Patient patient, Location location,
			Encounter encounter, List<Observation> observations) {
		boolean result = false;
		try {

			JSONObject jsonObject = JsonUtil.getJSONObject(condition.trim());
			if (!(jsonObject.has("entity") && jsonObject.has("property") && jsonObject.has("validate")
			/* && jsonObject.has("value") */)) {
				log.info(jsonObject.toString());
				log.severe("Condition must contain all four required keys: entity, validate and value");
				return false;
			}
			String entity = jsonObject.getString("entity");
			String property = jsonObject.getString("property");
			String validationType = jsonObject.getString("validate");
			String expectedValue = null;
			if (!(validationType.equalsIgnoreCase("NOTNULL") || validationType.equalsIgnoreCase("PRESENT")
					|| validationType.equalsIgnoreCase("EXISTS"))) {

				expectedValue = jsonObject.getString("value");
			}
			String actualValue = null;

			// In case of Encounter, search through observations
			if (entity.equalsIgnoreCase("Encounter")) {
				// Search for the observation's concept name matching the variable name
				Observation target = null;
				for (Observation observation : observations) {
					if (variableMatchesWithConcept(property, observation)) {
						target = observation;
						break;
					}
				}
				if (target == null) {
					return result;
				}
				if (validationType.equalsIgnoreCase("List")) {
					actualValue = target.getValueCoded().toString();
				} else {
					actualValue = target.getValue().toString();
				}
			}
			// In case of Patient or Location, search for the defined property
			else if (entity.equalsIgnoreCase("Patient")) {
				actualValue = getEntityPropertyValue(patient, property);
			} else if (entity.equalsIgnoreCase("Location")) {
				actualValue = getEntityPropertyValue(location, property);
			}
			// Check the type of validation and call respective method
			if (validationType.equalsIgnoreCase("VALUE")) {
				return actualValue.equalsIgnoreCase(expectedValue);
			} else if (validationType.equalsIgnoreCase("RANGE")) {
				Double valueDouble = Double.parseDouble(actualValue);
				return ValidationUtil.validateRange(expectedValue, valueDouble);
			} else if (validationType.equalsIgnoreCase("REGEX")) {
				return ValidationUtil.validateRegex(expectedValue, actualValue);
			} else if (validationType.equalsIgnoreCase("QUERY")) {
				return ValidationUtil.validateQuery(expectedValue, actualValue);
			} else if (validationType.equalsIgnoreCase("NOTNULL") || validationType.equalsIgnoreCase("PRESENT")
					|| validationType.equalsIgnoreCase("EXISTS")) {
				if (actualValue != null) {
					return true;
				}
				return false;
			} else if (validationType.equalsIgnoreCase("List")) {
				return ValidationUtil.validateList(expectedValue, actualValue);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace(); // org.json.JSONException
		} catch (org.json.JSONException e) {
			System.out.println("Condition :: " + condition);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Checks the entity type of the object and looks for the value in given
	 * property using Reflection
	 * 
	 * @param object
	 * @param property
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static String getEntityPropertyValue(Object object, String property)
			throws NoSuchFieldException, IllegalAccessException {
		String actualValue;
		Field field;
		if (object instanceof Patient) {
			field = Patient.class.getDeclaredField(property);
		} else if (object instanceof Location) {
			field = Location.class.getDeclaredField(property);
		} else if (object instanceof Encounter) {
			field = Encounter.class.getDeclaredField(property);
		} else if (object instanceof User) {
			field = User.class.getDeclaredField(property);
		}
		field = Patient.class.getDeclaredField(property);
		boolean flag = field.isAccessible();
		field.setAccessible(true);
		actualValue = field.get(object).toString();
		field.setAccessible(flag);
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

}
