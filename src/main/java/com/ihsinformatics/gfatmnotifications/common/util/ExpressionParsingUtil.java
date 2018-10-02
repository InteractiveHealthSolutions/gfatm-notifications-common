/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.util;

import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class ExpressionParsingUtil {

	public static boolean parseBooleanExpression(String expression) {
		Constant T = new Constant("T = 1");
		Constant F = new Constant("F = 0");
		Expression e = new Expression("T && (F || (F && T))", T, F);
		System.out.println(e.getExpressionString() + " = " + e.calculate());
		return true;
	}

}
