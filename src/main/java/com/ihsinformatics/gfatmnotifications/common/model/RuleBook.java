/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.util.NotificationType;
import com.ihsinformatics.gfatmnotifications.common.util.SheetsServiceUtil;
import com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class RuleBook {

	private final Integer typeColumn = Context.getIntegerProperty("rule.type.column");
	private final Integer encounterColumn = Context.getIntegerProperty("rule.encounter.column");
	private final Integer conditionsColumn = Context.getIntegerProperty("rule.conditions.column");
	private final Integer sendToColumn = Context.getIntegerProperty("rule.send_to.column");
	private final Integer scheduleDateColumn = Context.getIntegerProperty("rule.schedule_date.column");
	private final Integer plusMinusColumn = Context.getIntegerProperty("rule.plus_minus.column");
	private final Integer plusMinusUnitColumn = Context.getIntegerProperty("rule.unit.column");
	private final Integer messageCodeColumn = Context.getIntegerProperty("rule.message_code.column");
	private final Integer stopConditionColumn = Context.getIntegerProperty("rule.stop_condition.column");
	private final Integer fetchDurationColumn = Context.getIntegerProperty("rule.fetch_duration.column");
	private final Integer databaseConnectionNameColumn = Context
			.getIntegerProperty("rule.database_connection_name.column");
	private final Integer recordOnlyColumn = Context.getIntegerProperty("rule.record_only.column");
	private List<Rule> rules;
	private Map<String, String> messages;
	private Set<String> blacklistedPatients;
	private Set<String> blacklistedLocations;
	private Set<String> blacklistedUsers;
	
public RuleBook(String googleSheetId) throws IOException, GeneralSecurityException{
		
        Sheets sheetsService = SheetsServiceUtil.getSheetsService();

		String rulesRange = "Rules!A1:Z1000";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(googleSheetId, rulesRange)
                .execute();
        List<List<Object>> values = response.getValues();
        setRules(new ArrayList<Rule>());
        for (int i = 1; i < response.size(); i++) {
        	Rule rule = new Rule();
        	rule.setType(NotificationType.valueOf(String.valueOf(values.get(i).get(typeColumn))));
			rule.setEncounterType(String.valueOf(values.get(i).get(encounterColumn)));
			rule.setConditions(String.valueOf(values.get(i).get(conditionsColumn)));
			rule.setSendTo(String.valueOf(values.get(i).get(sendToColumn)));
			rule.setScheduleDate(String.valueOf(values.get(i).get(scheduleDateColumn)));
			rule.setPlusMinus(Double.parseDouble(String.valueOf(values.get(i).get(plusMinusColumn))));
			rule.setPlusMinusUnit(String.valueOf(values.get(i).get(plusMinusUnitColumn)));
			rule.setMessageCode(String.valueOf(values.get(i).get(messageCodeColumn)));
			try {
				rule.setFetchDuration(String.valueOf(values.get(i).get(fetchDurationColumn)));
				rule.setStopConditions(String.valueOf(values.get(i).get(stopConditionColumn)));
				rule.setDatabaseConnectionName(String.valueOf(values.get(i).get(databaseConnectionNameColumn)));
				rule.setRecordOnly(String.valueOf(values.get(i).get(recordOnlyColumn)));
			} catch (Exception e) {
			}
			rules.add(rule);
        }
        
        String messagesRange = "Messages!A1:Z1000";
        response = sheetsService.spreadsheets().values()
                .get(googleSheetId, messagesRange)
                .execute();
        values = response.getValues();
		setMessages(new HashMap<String, String>());
		for (int i = 1; i < response.size(); i++) {
			getMessages().put(String.valueOf(values.get(i).get(0)), String.valueOf(values.get(i).get(1)));
		}
		
		String blacklistRange = "Blacklist!A1:Z1000";
        response = sheetsService.spreadsheets().values()
                .get(googleSheetId, blacklistRange)
                .execute();
        values = response.getValues();
		setBlacklistedPatient(new HashSet<String>());
		setBlacklistedLocations(new HashSet<String>());
		setBlacklistedUsers(new HashSet<String>());

		for (int i = 1; i < response.size(); i++) {
			try {
				getBlacklistedPatient().add(String.valueOf(values.get(i).get(0)));
				getBlacklistedLocations().add(String.valueOf(values.get(i).get(1)));
				getBlacklistedUsers().add(String.valueOf(values.get(i).get(2)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public RuleBook(File ruleBookFile) throws IOException {
		FileInputStream fis = new FileInputStream(ruleBookFile);
		// Read Excel document
		Workbook workbook = new XSSFWorkbook(fis);
		// Fetch sheet
		Sheet sheet = workbook.getSheet("Rules");
		setRules(new ArrayList<Rule>());
		for (Row row : sheet) {
			// Skip the header row
			if (row.getRowNum() == 0) {
				continue;
			}
			Rule rule = new Rule();
			rule.setType(NotificationType.valueOf(row.getCell(typeColumn).getStringCellValue()));
			rule.setEncounterType(row.getCell(encounterColumn).getStringCellValue());
			rule.setConditions(row.getCell(conditionsColumn).getStringCellValue());
			rule.setSendTo(row.getCell(sendToColumn).getStringCellValue());
			rule.setScheduleDate(row.getCell(scheduleDateColumn).getStringCellValue());
			rule.setPlusMinus(row.getCell(plusMinusColumn).getNumericCellValue());
			rule.setPlusMinusUnit(row.getCell(plusMinusUnitColumn).getStringCellValue());
			rule.setMessageCode(row.getCell(messageCodeColumn).getStringCellValue());
			try {
				rule.setFetchDuration(row.getCell(fetchDurationColumn).getStringCellValue());
				rule.setStopConditions(row.getCell(stopConditionColumn).getStringCellValue());
				rule.setDatabaseConnectionName(row.getCell(databaseConnectionNameColumn).getStringCellValue());
				rule.setRecordOnly(row.getCell(recordOnlyColumn).getStringCellValue());
			} catch (Exception e) {
			}
			rules.add(rule);
		}

		Sheet messageSheet = workbook.getSheet("Messages");
		setMessages(new HashMap<String, String>());
		for (Row row : messageSheet) {
			// Skip the header row
			if (row.getRowNum() == 0) {
				continue;
			}
			getMessages().put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
		}

		Sheet blacklistSheet = workbook.getSheet("Blacklist");
		setBlacklistedPatient(new HashSet<String>());
		setBlacklistedLocations(new HashSet<String>());
		setBlacklistedUsers(new HashSet<String>());

		for (Row row : blacklistSheet) {
			// Skip the header row
			if (row.getRowNum() == 0) {
				continue;
			}
			try {
				getBlacklistedPatient().add(row.getCell(0).getStringCellValue());
				getBlacklistedLocations().add(row.getCell(1).getStringCellValue());
				getBlacklistedUsers().add(row.getCell(2).getStringCellValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		workbook.close();
	}

	/**
	 * @return the rules
	 */
	public List<Rule> getRules() {
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public List<Rule> getCallRules() {
		List<Rule> callRules = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.getType() == NotificationType.CALL) {
				callRules.add(rule);
			}
		}
		return callRules;
	}

	public List<Rule> getEmailRules() {
		List<Rule> emailRules = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.getType() == NotificationType.EMAIL) {
				emailRules.add(rule);
			}
		}
		return emailRules;
	}

	public List<Rule> getSmsRules() {
		List<Rule> smsRules = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.getType() == NotificationType.SMS) {
				try {
					ValidationUtil.validateRuleSyntax(rule);
					smsRules.add(rule);					
				} catch(Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		return smsRules;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	public Set<String> getBlacklistedPatient() {
		return blacklistedPatients;
	}

	public void setBlacklistedPatient(Set<String> blacklistedPatient) {
		this.blacklistedPatients = blacklistedPatient;
	}

	public Set<String> getBlacklistedLocations() {
		return blacklistedLocations;
	}

	public void setBlacklistedLocations(Set<String> blacklistedLocations) {
		this.blacklistedLocations = blacklistedLocations;
	}

	public Set<String> getBlacklistedUsers() {
		return blacklistedUsers;
	}

	public void setBlacklistedUsers(Set<String> blacklistedUsers) {
		this.blacklistedUsers = blacklistedUsers;
	}
}
