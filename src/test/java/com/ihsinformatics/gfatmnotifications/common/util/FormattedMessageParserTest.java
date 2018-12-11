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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.gfatmnotifications.common.service.TestUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class FormattedMessageParserTest extends TestUtil {

	private FormattedMessageParser parser;
	private String message = "Assalamu alaekum janab {patient.getFullName}. "
			+ "Aao TB Mitao ki team ap ko yaad dilana chahti hai ke ap ko "
			+ "$select o.value_datetime from obs as o where encounter_id = (select max(encounter_id) from encounter as e where e.encounter_type = 7 and e.voided = 0) and o.value_datetime is not null and o.concept_id = 5096 and o.voided = 0 and o.person_id = {patient.personId}$ "
			+ "ke din {encounter.encounterLocation} pe Doctor ke paas moainay aur dawa hasil karne ke liyey ana hai. "
			+ "Is mutaliq mazeed maloomat ke liyey $select username from users where user_id = 2$ se rabta karain";
	private String queriedText = "The user at userId 2 is $select username from users where user_id = 2$. The location at location_id 1 is $select name from location where location_id = 1$. You-know-who is nickname for $select given_name from person_name where person_id = -999$ ";
	private Patient testPatient;
	private User testUser;
	private Encounter testEncounter;
	private Location testLocation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		parser = new FormattedMessageParser(Decision.FAIL);
		testPatient = new Patient();
		testPatient.setPersonId(2);
		testPatient.setGivenName("Test");
		testPatient.setLastName("Patient");
		testPatient.setGender("M");
		testPatient.setPrimaryContact("03452345345");

		testUser = new User();
		testUser.setUserId(1);
		testUser.setPersonId(1);
		testUser.setUsername("test.user");
		testUser.setGender("M");
		testUser.setGivenName("Test");
		testUser.setLastName("User");
		testUser.setSystemId("1-1");
		testUser.setUserRole("Tester");

		testLocation = new Location();

		testEncounter = new Encounter();
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
		String expected = "Assalamu alaekum janab " + testPatient.getFullName() + ". "
				+ "Aao TB Mitao ki team ap ko yaad dilana chahti hai ke ap ko " + "<MISSING TEXT>" + " ke din "
				+ testLocation.getName() + " pe Doctor ke paas moainay aur dawa hasil karne ke liyey ana hai. "
				+ "Is mutaliq mazeed maloomat ke liyey daemon se rabta karain";
		try {
			String formattedMessage = parser.parseFormattedMessage(message, testPatient, testUser, testEncounter,
					testLocation);
			assertTrue(formattedMessage.equals(expected));
		} catch (ParseException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#getMatchingClassObject(java.lang.String, java.lang.Object[])}.
	 */
	@Test
	public void testGetMatchingClassObject() {
		try {
			Object object = parser.getMatchingClassObject("Patient",
					new Object[] { testPatient, testUser, testEncounter, testLocation });
			assertSame(object, testPatient);
			object = parser.getMatchingClassObject("User", new Object[] { testUser, testEncounter, testLocation });
			assertSame(object, testUser);
			object = parser.getMatchingClassObject("Encounter", new Object[] { testEncounter, testLocation });
			assertSame(object, testEncounter);
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#getPropertyValue(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testGetPropertyValueField() {
		try {
			assertSame(parser.getPropertyValue(testPatient, "givenName"), testPatient.getGivenName());
			assertSame(parser.getPropertyValue(testPatient, "personId"), testPatient.getPersonId());
			assertSame(parser.getPropertyValue(testPatient, "address2"), testPatient.getAddress2());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#getPropertyValue(java.lang.Object, java.lang.String)}.
	 */
	@Test(expected = NoSuchMethodException.class)
	public void shouldThrowExceptionOnGetPropertyValueField()
			throws SecurityException, IllegalArgumentException, ReflectiveOperationException {
		parser.getPropertyValue(testPatient, "nonExistingField");
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#getPropertyValue(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testGetPropertyValueMethod() {
		try {
			String actual = parser.getPropertyValue(testPatient, "getFullName").toString();
			String expected = testPatient.getFullName();
			assertTrue(actual.equals(expected));
			assertNull(parser.getPropertyValue(testPatient, "getBirthplace"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser#getPropertyValue(java.lang.Object, java.lang.String)}.
	 */
	@Test(expected = NoSuchMethodException.class)
	public void shouldThrowExceptionOnGetPropertyValue_Method()
			throws SecurityException, IllegalArgumentException, ReflectiveOperationException {
		parser.getPropertyValue(testPatient, "getNonExistingMethod");
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
		assertTrue(tokens.contains("a (token): five"));
		StringBuilder merged = new StringBuilder();
		for (String token : tokens) {
			merged.append(token);
		}
		assertTrue(merged.toString().equals("One a token, two a token; threea token; (four) a (token): five a token."));
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

	@Test
	public void testParseSqlQueries() {
		String text = parser.parseSqlQueries(queriedText);
		assertFalse(text.contains("$"));
		assertTrue(text.contains("<MISSING TEXT>"));
		assertTrue(text.contains("daemon"));
		assertTrue(text.contains("Unknown Location"));
	}
}
