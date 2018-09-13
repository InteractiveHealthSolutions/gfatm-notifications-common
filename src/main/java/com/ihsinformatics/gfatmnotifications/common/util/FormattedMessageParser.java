/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.User;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class FormattedMessageParser {

	private String dateFormat;
	private Decision onNullDecision;

	public FormattedMessageParser(Decision onNull) {
		this.setOnNullDecision(onNull);
		dateFormat = Context.getProps().getProperty("display.date.format");
	}

	public Decision getOnNullDecision() {
		return onNullDecision;
	}

	public void setOnNullDecision(Decision onNullDecision) {
		this.onNullDecision = onNullDecision;
	}

	public String parseFormattedMessage(String message, Object... objects) throws ParseException {
		if (message == null) {
			throw new ParseException("Parse what? Null!", -1);
		}
		if (!areParenthesesBalanced(message)) {
			throw new ParseException("Parantheses are not balanced in the message.", 0);
		}
		Patient patient = null;
		Location location = null;
		Encounter encounter = null;
		User user = null;
		// Detect the entities that have been passed
		for (Object object : objects) {
			if (object instanceof Patient) {
				patient = (Patient) object;
			} else if (object instanceof Location) {
				location = (Location) object;
			} else if (object instanceof Encounter) {
				encounter = (Encounter) object;
			} else if (object instanceof Encounter) {
				user = (User) object;
			}
		}

		// Tokenize the message
		List<String> tokens = tokenizeMessage(message);

		for (String token : tokens) {

		}

		// Assalamu alaekum janab {patient.fullName}. Aao TB Mitao ki team ap ko yaad
		// dilana chahti hai ke ap ko {encounter[PET-Treatment
		// Initiation].observations[RETURN VISIT DATE].valueDatetime} ke din
		// {encounter.encounterLocation} pe Doctor ke paas moainay aur dawa hasil karne
		// ke liyey ana hai. Is mutaliq mazeed maloomat ke liyey helpline 080011982 pe
		// rabta karain. Shukriya
		return null;
	}

	/**
	 * Detect opening and closing parenthesis and tokenize the given string
	 * 
	 * @param string
	 * @return
	 */
	public List<String> tokenizeMessage(String string) {
		List<String> tokens = new ArrayList<String>();
		char[] charArray = string.toCharArray();
		StringBuilder token = new StringBuilder();
		for (char c : charArray) {
			if (isOpeningParenthesis(c) || isClosingParenthesis(c)) {
				tokens.add(token.toString());
				token = new StringBuilder();
			} else {
				token.append(c);
			}
		}
		tokens.add(token.toString());
		return tokens;
	}

	private boolean isOpeningParenthesis(char c) {
		return c == '{' || c == '[' || c == '(';
	}

	private boolean isClosingParenthesis(char c) {
		return c == ')' || c == ']' || c == '}';
	}

	/**
	 * This function checks whether the number of parentheses are balanced (equally
	 * opened and closed) in given message or not
	 * 
	 * @param message
	 * @return
	 */
	public boolean areParenthesesBalanced(String message) {
		Map<Character, Character> parentheses = new HashMap<Character, Character>();
		parentheses.put('[', ']');
		parentheses.put('{', '}');
		parentheses.put('(', ')');
		// Leave nothing except parentheses in the message
		String clean = message.replaceAll("[^\\Q{[()]}\\E]", "");
		// Odd number would always result in false
		if ((clean.length() % 2) != 0) {
			return false;
		}
		Stack<Character> stack = new Stack<Character>();
		for (int i = 0; i < clean.length(); i++) {
			if (parentheses.containsKey(clean.charAt(i))) {
				stack.push(clean.charAt(i));
			} else if (stack.empty() || (clean.charAt(i) != parentheses.get(stack.pop()))) {
				return false;
			}
		}
		return true;
	}

}
