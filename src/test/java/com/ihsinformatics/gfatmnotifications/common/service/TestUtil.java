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
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database initialize script error " + e.getMessage());
		}
	}

	@Test
	public void testDbConnection() {
		assertNotNull(dbUtil.runCommand(CommandType.SELECT, "select system_id from users where user_id = 1"));
	}
}
