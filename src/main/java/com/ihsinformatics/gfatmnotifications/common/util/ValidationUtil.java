/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.util;

import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.util.RegexUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class ValidationUtil {

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
		if (!range.matches("^[0-9.,-]+")) {
			throw new InvalidPropertiesFormatException(
					"Invalid format provided for validation range. Must be a list of hyphenated or comma-separated tuples of numbers (1-10; 2.2-3.0; 1,3,5; 1-5,7,9).");
		}
		// If there are multiple tuples, then recurse
		if (range.contains(",")) {
			String[] parts = range.split(",");
			return validateRange(parts[0], value) || validateRange(parts[1], value);
		}
		// Otherwise, it's just two numbers and a hyphen
		else {
			if (range.contains("-")) {
				String[] parts = range.split("-");
				double min = Double.parseDouble(parts[0]);
				double max = Double.parseDouble(parts[1]);
				return (value >= min && value <= max);
			}
			// In case of a standalone value, just match primitive type
			else {
				return (Double.compare(value.doubleValue(), Double.parseDouble(range)) == 0);
			}
		}
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
	
		// TODO commited for test only
		/*	if (!list.matches("^[A-Za-z0-9,_\\-\\s]+")) {
			throw new InvalidPropertiesFormatException(
					"Invalid format provided for validation list. Must be a comma-separated list of alpha-numeric values (white space, hypen and underscore allowed).");
		}*/
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
		Object[][] data = Context.getLocalDb().getTableData(query);
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
}
