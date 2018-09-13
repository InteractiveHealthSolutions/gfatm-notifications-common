/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.Context;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class FormattedMessageParserTest {

	private FormattedMessageParser parser;
	private String message = "Assalamu alaekum janab {patient.fullName}. Aao TB Mitao ki team ap ko yaad dilana chahti hai ke ap ko {encounter[PET-Treatment Initiation].observations[RETURN VISIT DATE].valueDatetime} ke din {encounter.encounterLocation} pe Doctor ke paas moainay aur dawa hasil karne ke liyey ana hai. Is mutaliq mazeed maloomat ke liyey helpline 080011982 pe rabta karain";

	@BeforeClass
	public static void initialize() throws Exception {
		Context.initialize();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		parser = new FormattedMessageParser(Decision.FAIL);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#parseFormattedMessage(java.lang.String, java.lang.Object[])}.
	 */
	@Test
	public void testParseFormattedMessage() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#tokenizeMessage(java.lang.String)}.
	 */
	@Test
	public void testTokenizeMessage() {
		String str = "One a token, [two] a token; {three[a token]}; (four) [a (token): five] a token.";
		List<String> tokens = parser.tokenizeMessage(str);
		assertTrue(tokens.contains("One a token, "));
		assertTrue(tokens.contains("two"));
		assertTrue(tokens.contains("three"));
		assertTrue(tokens.contains(": five"));
		StringBuilder merged = new StringBuilder();
		for (String token : tokens) {
			merged.append(token);
		}
		assertTrue(merged.toString().equals("One a token, two a token; threea token; four a token: five a token."));
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#areParenthesesBalanced(java.lang.String)}.
	 */
	@Test
	public void testAreParenthesesBalanced() {
		assertTrue(parser.areParenthesesBalanced(""));
		assertTrue(parser.areParenthesesBalanced("{}"));
		assertTrue(parser.areParenthesesBalanced("[]"));
		assertTrue(parser.areParenthesesBalanced("()"));
		assertTrue(parser.areParenthesesBalanced("{[()]}"));
		assertTrue(parser.areParenthesesBalanced("xxx{[(yyy)]}zzz"));
		assertTrue(parser.areParenthesesBalanced("abc{def[ghi(jkl)mno]pqrs}tuv"));
		assertTrue(parser.areParenthesesBalanced(message));
		for (String s : Arrays.asList("{", "[", "(", "}", "]", ")")) {
			assertFalse(parser.areParenthesesBalanced(s));
		}
		assertFalse(parser.areParenthesesBalanced("11{55[66(5)44}33"));
		assertFalse(parser.areParenthesesBalanced(message + "]"));
	}
}
