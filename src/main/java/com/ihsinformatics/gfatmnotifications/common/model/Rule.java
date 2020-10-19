/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.model;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.common.util.NotificationType;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class Rule {

	private NotificationType type;
	private String encounterType;
	private String conditions;
	private String sendTo;
	private String scheduleDate;
	private Double plusMinus;
	private String plusMinusUnit;
	private String messageCode;
	private String stopConditions;
	private String fetchDuration;
	private String fetchSource;
	private String recordOnly;

	public Rule() {
	}

	/**
	 * @param type
	 * @param encounterType
	 * @param conditions
	 * @param sendTo
	 * @param scheduleDate
	 * @param plusMinus
	 * @param plusMinusUnit
	 * @param messageCode
	 * @param stopConditions
	 * @param fetchDuration
	 * @param fetchSource
	 * @param recordOnly
	 */
	public Rule(NotificationType type, String encounterType, String conditions, String sendTo, String scheduleDate,
			Double plusMinus, String plusMinusUnit, String messageCode, String stopConditions, String fetchDuration,
			String fetchSource, String recordOnly) {
		super();
		this.type = type;
		this.encounterType = encounterType;
		this.conditions = conditions;
		this.sendTo = sendTo;
		this.scheduleDate = scheduleDate;
		this.plusMinus = plusMinus;
		this.plusMinusUnit = plusMinusUnit;
		this.messageCode = messageCode;
		this.stopConditions = stopConditions;
		this.fetchDuration = fetchDuration;
		this.fetchSource = fetchSource;
		this.recordOnly = recordOnly;
	}

	public String getDatabaseConnectionName() {
		return fetchSource;
	}

	public void setDatabaseConnectionName(String databaseConnectionName) {
		this.fetchSource = databaseConnectionName;
	}

	/**
	 * @return the type
	 */
	public NotificationType getType() {
		return type;
	}

	/**
	 * @param notificationType the type to set
	 */
	public void setType(NotificationType notificationType) {
		this.type = notificationType;
	}

	/**
	 * @return the encounter
	 */
	public String getEncounterType() {
		return encounterType;
	}

	/**
	 * @param encounterType the encounterType to set
	 */
	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	/**
	 * @return the conditions
	 */
	public String getConditions() {
		return conditions;
	}

	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return the sendTo
	 */
	public String getSendTo() {
		return sendTo;
	}

	/**
	 * @param sendTo the sendTo to set
	 */
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}

	/**
	 * @param scheduleDate the scheduleDate to set
	 */
	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	/**
	 * @return the plusMinus
	 */
	public Double getPlusMinus() {
		return plusMinus;
	}

	/**
	 * @param plusMinus the plusMinus to set
	 */
	public void setPlusMinus(Double plusMinus) {
		this.plusMinus = plusMinus;
	}

	/**
	 * @return the plusMinusUnit
	 */
	public String getPlusMinusUnit() {
		return plusMinusUnit;
	}

	/**
	 * @param plusMinusUnit the plusMinusUnit to set
	 */
	public void setPlusMinusUnit(String plusMinusUnit) {
		this.plusMinusUnit = plusMinusUnit;
	}

	/**
	 * @return the messageCode
	 */
	public String getMessageCode() {
		return messageCode;
	}

	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	/**
	 * @return the stopCondition
	 */
	public String getStopConditions() {
		return stopConditions;
	}

	/**
	 * @param stopConditions the stopCondition to set
	 */
	public void setStopConditions(String stopConditions) {
		this.stopConditions = stopConditions;
	}

	public String getFetchDuration() {
		return fetchDuration;
	}

	public void setFetchDuration(String fetchDuration) {
		this.fetchDuration = fetchDuration;
	}

	public DateTime getFetchDurationDate() {
		if (fetchDuration == null || fetchDuration.isEmpty()) {
			return null;
		}
		Date toDay = new Date();
		DateTime referenceDate = new DateTime();
		DateTime returnDate = null;
		String[] values = fetchDuration.split(" ");
		int duration = Integer.parseInt(values[0].trim());
		Calendar c = Calendar.getInstance();
		c.setTime(toDay);
		if (values[1].equalsIgnoreCase("months")) {
			returnDate = referenceDate.minusMonths(duration).toDateTime();
			c.add(Calendar.MONTH, duration);
		} else if (values[1].equalsIgnoreCase("days")) {
			c.add(Calendar.DATE, duration);
			returnDate = referenceDate.minusDays(duration).toDateTime();
		} else if (values[1].equalsIgnoreCase("hours")) {
			c.add(Calendar.HOUR, duration);
			returnDate = referenceDate.minusHours(duration).toDateTime();
		}
		if(returnDate != null){
		return returnDate
			    .withHourOfDay(0)
			    .withMinuteOfHour(0)
			    .withSecondOfMinute(0);
		}
		return returnDate;
	}

	/**
	 * @return the recordOnly
	 */
	public Boolean getRecordOnly() {
		return recordOnly.equalsIgnoreCase("YES") || recordOnly.equalsIgnoreCase("TRUE");
	}

	/**
	 * @param recordOnly the recordOnly to set
	 */
	public void setRecordOnly(String recordOnly) {
		this.recordOnly = recordOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return type + ", " + encounterType + ", " + sendTo + ", " + scheduleDate + ", " + plusMinus + ", "
				+ plusMinusUnit + ", " + messageCode + ", " + conditions + ", " + stopConditions;
	}
	
	public static Date getZeroTimeDate(Date fecha) {
	    Date res = fecha;
	    Calendar calendar = Calendar.getInstance();

	    calendar.setTime( fecha );
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    res = calendar.getTime();

	    return res;
	}
}
