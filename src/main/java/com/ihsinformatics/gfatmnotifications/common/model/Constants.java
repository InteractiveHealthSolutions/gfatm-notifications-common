/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.model;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public final class Constants {

	// Link to the SMS service API
	public static final String SMS_SERVER_ADDRESS = "http://202.125.133.156/ihs/api/send_sms/";
	// API Key
	public static final String SMS_API_KEY = "aWhzc21zOnVsNjJ6eDM=";
	// Whether or not to use SSL encryption
	public static final boolean SMS_USE_SSL = false;
	// How often to check for new SMS notifications in DB
	public static final int SMS_SCHEDULE_INTERVAL_IN_HOURS = 24;

	// How often to check for new Call notifications in DB
	public static final int CALL_SCHEDULE_INTERVAL_IN_HOURS = 24;

	// How often to check for new Email notifications in DB
	public static final int EMAIL_SCHEDULE_INTERVAL_IN_HOURS = 4;

	// Integer identifiers of FAST types
	// public static final int[] FAST_ENCOUNTER_TYPE_IDS = {17, 18, 19, 20, 21, 22,
	// 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 88, 89, 90};
	public static final int[] FAST_ENCOUNTER_TYPE_IDS = { 29 };

	/*
	 * public static final int[] CHILDHOOD_TB_ENCOUNTER_TYPE_IDS = { 51, 52, 53, 54,
	 * 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73,
	 * 74, 81, 82, 83, 84, 85, 86, 87 };
	 */
	public static final int[] CHILDHOOD_TB_ENCOUNTER_TYPE_IDS = { 67 };

	public static final int[] PET_ENCOUNTER_TYPE_IDS = { 67 };

	/********************** Constant values *********************/

	public static final String PATIENT_REFERRED = "PATIENT REFERRED";
	public static final String PATIENT_TRANSFERRED = "PATIENT TRANSFERRED OUT";

	/***********************/
	/*
	 * public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
	 * "dd-MMM-yyyy"); public static final SimpleDateFormat DATE_FORMATWH = new
	 * SimpleDateFormat( "yyyy-MM-dd");
	 */
	/******************* Extra Variable ***********************/

	public static final int REFERREL_ENCOUNTER_TYPE = 28;
	public static final int END_FOLLOWUP_FORM_ENCOUNTER_TYPE = 32;

	/******************* Program Names ***********************/
	public static final String FAST_PROGRAM = "FAST";
	public static final String CHILDHOOD_PROGRAM = "CHILDHOOD";
	/******************* Connection Name ***********************/
	public static final String WAREHOUSE_CONNECTION = "warehouse";
	public static final String OPENMRS_CONNECTION = "openmrs";
	public static final int NUMBERDAYS = 1;
	/********************* Email Properties ******************/

	// this path used on production time
	public static final String PROP_FILE_NAME = "/src/tbreach-api.properties";
	// Local path
	// public static final String PROP_FILE_NAME = "tbreach-api.properties";

}
