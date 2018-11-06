/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hpsf.Date;
import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.BaseEntity;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.util.ClassLoaderUtil;
import com.ihsinformatics.util.CommandType;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class FormattedMessageParser {
	
	
	
	public static void main(String[] args) {
	    String message ="[encounter.encounterDatetime.day]  {patient.getFullName}, aap ko yaad karana chahtain hain kay ap ko {encounter[encounterType=Childhood TB-Treatment Initiation].observations[concept=RETURN VISIT DATE].valueDatetime}, barooz [day of week in urdu], {patient.getHealthCenter} pe doctor ke paas moainey aur adwiyaat hasil karne ke liyey tashreef lana hai. Agar is kay mutaliq ap kuch poochna chahain tou AaoTBMitao helpline 080011982 pe rabta karain.";
	  	Patient patient = new Patient();
	  	 patient.setLastName("bac");
	  	 
	  	 User  user = new User();
	  	 user.setLastName("LAST ANAME");
	  	 
	  	 Encounter  encounter = new Encounter();
	  	 encounter.setEncounterDate(new DateTime().getMillis());
	  	 encounter.setDateCreated(new DateTime().getMillis());
	  	 
	  	 user.setLastName("LAST ANAME");
	  	  
		FormattedMessageParser  parser  =  new FormattedMessageParser(null);
		try {
			parser.parseFormattedMessage(message, patient,user,encounter);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		
	}
	

	private Decision onNullDecision;

	public FormattedMessageParser(Decision onNull) {
		this.setOnNullDecision(onNull);
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
		// Tokenize the message
		List<String> tokens = tokenizeMessage(message);
		StringBuilder output = new StringBuilder();
		for (String token : tokens) {
			// Detect the entity.property tokens
			if (isEntityValuePair(token)) {
				String[] pair = token.split("\\.",2);
				String entityName = pair[0];
				String propertyName = pair[1];
				
				// Match the key with objects passed
				try {
					// Precaution! Turn first character into capital
					entityName = String.valueOf(entityName.charAt(0)).toUpperCase()
							+ entityName.substring(1, entityName.length());
					Object object = getMatchingClassObject(entityName, objects);
					output.append(getPropertyValue(object, propertyName));
				} catch (Exception e) {
					//TODO : if any entity is not available or found then throw Exception 
					// discuss about this issue also
					e.printStackTrace();
				}
			} else if (isEntityValuePairWithCondition(token)){
				String[] pair = token.split("\\.");
				String entityName = pair[0];
				String propertyName = pair[1];
				String condition = pair[2];
				try {
					entityName = String.valueOf(entityName.charAt(0)).toUpperCase()
							+ entityName.substring(1, entityName.length());
					Object object = getMatchingClassObject(entityName, objects);
					String resultPropertyVal = getPropertyValue(object, propertyName).toString();
					 if (condition.equals("day")) {
						  String nameOfDay = "";
						 	if (resultPropertyVal.isEmpty() || resultPropertyVal.equals("") || resultPropertyVal == null) {
	 		
						 	/*	for (Observation observation :objects.getClass(). ) {
							 			if (ValidationUtil.variableMatchesWithConcept(propertyName, observation)) {
							 			break;
							 			}
						 		}*/
						 		  nameOfDay = getDayName(Long.parseLong(resultPropertyVal));
							}else{
								 nameOfDay = getDayName(Long.parseLong(resultPropertyVal));
							}
						  output.append(nameOfDay);
					}else{
						  output.append(resultPropertyVal);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else {
				output.append(token);
			}
		}
		// Detect SQL queries and replace result inside the text
		String result = parseSqlQueries(output.toString());
		return result;
	}
	
	public String getDayName(long timestamp){
		DateTime date=new DateTime(timestamp);
		System.out.println("Week Days :" + DaysInUrdu.valueOf(date.dayOfWeek().getAsText(Locale.getDefault()).toUpperCase()));
		return DaysInUrdu.valueOf(date.dayOfWeek().getAsText(Locale.getDefault()).toUpperCase()).toString();
	}

	/**
	 * This piece of intelligent code detects all SQL queries quoted within $ sign
	 * inside the given parameter text and replaces them with query results. This
	 * method does not throw any exception, if a query fails or retrieves no result,
	 * then a placeholder <MISSING TEXT> is replaced with the query instead
	 * 
	 * @param text
	 * @return
	 */
	public String parseSqlQueries(String text) {
		List<String> queries = new ArrayList<String>();
		Pattern p = Pattern.compile("\\$(.*?)\\$");
		Matcher m = p.matcher(text.toString());
		// First, detect all queries
		while (m.find()) {
			queries.add(m.group(1));
		}
		// Set a placeholder for results
		text = m.replaceAll("<RESULT>");
		// Execute queries and replace the first occurrence of placeholder with result
		for (String query : queries) {
			Object result = Context.getOpenmrsDb().runCommand(CommandType.SELECT, query);
			if (result == null) {
				result = "<MISSING TEXT>";
			}
			text = text.replaceFirst("<RESULT>", result.toString());
		}
		return text;
	}

	/**
	 * Searches for class by name and returns the first object which belongs to
	 * found class. Returns null if no object is instance of found class
	 * 
	 * @param className
	 * @param objects
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Object getMatchingClassObject(String className, Object[] objects) throws ClassNotFoundException {
		Class<?> clazz = ClassLoaderUtil.loadClass(className, BaseEntity.class.getPackage().getName(), this.getClass());
		for (Object object : objects) {
			if (clazz.isInstance(object)) {
				return object;
			}
		}
		return null;
	}

	/**
	 * Searches for field or method in given object and returns the corresponding
	 * value of that field in given object
	 * 
	 * @param object
	 * @param fieldOrMethod
	 * @return
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public Object getPropertyValue(Object object, String fieldOrMethod)
			throws SecurityException, IllegalArgumentException, ReflectiveOperationException {
		Object value = null;
		try {
			Field field = object.getClass().getDeclaredField(fieldOrMethod);
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			value = field.get(object);
			field.setAccessible(accessible);
		} catch (NoSuchFieldException e) {
			// In case the field doesn't exist, try to find method
			Class<?> params[] = new Class[0];
			Method method = object.getClass().getDeclaredMethod(fieldOrMethod, params);
			boolean accessible = method.isAccessible();
			method.setAccessible(true);
			value = method.invoke(object, new Object[] {});
			method.setAccessible(accessible);
		}
		return value;
	}

	/**
	 * Returns true of the parameter token is in entity.value format (e.g.
	 * patient.lastName)
	 * 
	 * @param token
	 * @return
	 */
	public boolean isEntityValuePair(String token) {
		return token.matches("^[\\w]+\\.[\\w]+$");
	}
	public boolean isEntityValuePairWithCondition (String token) {
		return token.matches("^[\\w]+\\.[\\w]+\\.[\\w]+$");
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
			if (isTokenizerParenthesis(c)) {
				tokens.add(token.toString());
				token = new StringBuilder();
			} else {
				token.append(c);
			}
		}
		tokens.add(token.toString());
		return tokens;
	}

	private boolean isTokenizerParenthesis(char c) {
		return c == '{' || c == '[' || c == ']' || c == '}';
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
