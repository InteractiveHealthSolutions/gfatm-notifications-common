/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.util;

/**
 * Enumeration to represent common Data Types and their possible aliases
 * 
 * @author owais.hussain@ihsinformatics.com
 *
 */
public enum DataType {

	STRING(new String[] { "string", "text" }), DATE(new String[] { "date" }), DATETIME(
			new String[] { "datetime", "timestamp" }), TIME(new String[] { "time" }), INTEGER(
					new String[] { "int", "byte", "integer", "long", "number" }), CHARACTER(
							new String[] { "char", "character", "digit", "letter", "symbol", "sign" }), FLOAT(
									new String[] { "float", "double", "decimal" }), BOOLEAN(
											new String[] { "boolean", "binary", "bit" }), UNKNOWN(new String[] { "" });

	private String[] aliases;

	private DataType(String[] aliases) {
		this.aliases = aliases;
	}

	/**
	 * Return all listed aliases against a DataType. For example, aliases for
	 * STRING can be 'string' and 'text'
	 * 
	 * @return
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Searches for all aliases of the DataType and returns true if any one
	 * matches
	 * 
	 * @param alias
	 * @return
	 */
	public boolean match(String alias) {
		for (String a : aliases) {
			if (a.equalsIgnoreCase(alias)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Searches all DataType enums for the given alias and returns the matching
	 * DataType. If no results are found, UNKNOWN type is returned.
	 * 
	 * @param alias
	 * @return
	 */
	public static DataType getDataTypeByAlias(String alias) {
		for (DataType dataType : DataType.values()) {
			for (String a : dataType.aliases) {
				if (a.equalsIgnoreCase(alias)) {
					return dataType;
				}
			}
		}
		return UNKNOWN;
	}

	/**
	 * Return default representation of data type
	 */
	@Override
	public String toString() {
		return this.aliases[0];
	}
}
