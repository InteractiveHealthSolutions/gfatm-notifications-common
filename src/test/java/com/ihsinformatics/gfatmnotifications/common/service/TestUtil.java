/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.service;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.Rule;
import com.ihsinformatics.gfatmnotifications.common.util.NotificationType;
import com.ihsinformatics.util.ClassLoaderUtil;
import com.ihsinformatics.util.CommandType;
import com.ihsinformatics.util.DatabaseUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class TestUtil {

	protected static final String PROP_FILE = "gfatm-notifications-test.properties";
	protected static final String TEST_SCRIPT_FILE = "test_data.sql";
	protected static DatabaseUtil dbUtil;
	protected static Rule treatmentInitiationRule;
	protected static Rule referralRule;

	@BeforeClass
	public static void createTestDb() {
		try {
			URL file = ClassLoaderUtil.getResource(TEST_SCRIPT_FILE, TestUtil.class);
			Context.readProperties(PROP_FILE);
			// Initialize connection only
			Context.initialize(false, false, false);
			dbUtil = Context.getOpenmrsDb();
			ScriptRunner runner = new ScriptRunner(dbUtil.getConnection(), false, true);
			runner.runScript(new BufferedReader(new FileReader(file.getFile())));
			Context.initialize(true, true, false);
			initializeRules();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database initialize script error " + e.getMessage());
		}
	}

	public static void initializeRules() {
		treatmentInitiationRule = new Rule();
		treatmentInitiationRule.setType(NotificationType.SMS);
		treatmentInitiationRule.setConditions("{\"entity\":\"encounter\",\"property\":\"treatment_initiated\",\"validate\":\"VALUE\",\"value\":\"YES\"}AND{\"entity\":\"encounter\",\"property\":\"return_visit_date\",\"validate\":\"NOTNULL\"}");
		treatmentInitiationRule.setEncounterType("Childhood TB-Treatment Initiation");
		treatmentInitiationRule.setFetchDuration("2 months");
		treatmentInitiationRule.setMessageCode("CHTB-1REM");
		treatmentInitiationRule.setPlusMinus(-1D);
		treatmentInitiationRule.setPlusMinusUnit("DAYS");
		treatmentInitiationRule.setRecordOnly("YES");
		treatmentInitiationRule.setScheduleDate("return_visit_date");
		treatmentInitiationRule.setSendTo("PATIENT");
		treatmentInitiationRule.setStopConditions("{\"entity\":\"encounter\",\"encounter\":\"Childhood TB-TB Treatment Followup\",\"validate\":\"encounter\",\"after\":\"Childhood TB-Treatment Initiation\"}OR{\"entity\":\"encounter\",\"encounter\":\"End of Followup\",\"validate\":\"encounter\",\"after\":\"Childhood TB-Treatment Initiation\"}AND{\"entity\":\"encounter\",\"encounter\":\"End of Followup\",\"property\":\"treatment_outcome\",\"validate\":\"LIST\",\"value\":159791,160035,159874,160034,160031,165836,165837,164791,164792,166221,127750,166222,165891,160037,166288,165657}OR{\"entity\":\"encounter\",\"encounter\":\"Referral and Transfer\",\"validate\":\"encounter\",\"after\":\"Childhood TB-Treatment Initiation\"}AND{\"entity\":\"encounter\",\"encounter\":\"Referral and Transfer\",\"property\":\"referral_site\",\"validate\":\"VALUE\",\"value\":\"OTHER\"}OR{\"entity\":\"patient\",\"property\":\"getHealthCenterId\",\"validate\":\"query\",\"value\":\"SELECT location_id FROM location WHERE name not in ( 'SGHNK-KHI','IHK-KHI','GHAURI-CLINIC','ICD-KTR');\"}");

		referralRule = new Rule();
		referralRule.setType(NotificationType.SMS);
		referralRule.setConditions("{\"entity\":\"encounter\",\"property\":referral_site,\"validate\":\"NOTNULL\"}");
		referralRule.setEncounterType("Referral and Transfer");
		referralRule.setFetchDuration("");
		referralRule.setMessageCode("REF-TRANS");
		referralRule.setPlusMinus(0D);
		referralRule.setPlusMinusUnit("HOURS");
		referralRule.setRecordOnly("TRUE");
		referralRule.setScheduleDate("encounterDatetime");
		referralRule.setSendTo("SUPERVISOR");
		referralRule.setStopConditions("{\"entity\":\"encounter\",\"encounter\":\"Referral and Transfer\",\"property\":\"referral_site\",\"validate\":\"VALUE\",\"value\":\"OTHER\"}");

	}

	@Test
	public void testDbConnection() {
		assertNotNull(dbUtil.runCommand(CommandType.SELECT, "select system_id from users where user_id = 1"));
	}
}
