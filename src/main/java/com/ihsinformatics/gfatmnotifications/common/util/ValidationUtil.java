/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.util;

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

}
