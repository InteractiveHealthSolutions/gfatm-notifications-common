/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.DirectoryIteratorException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.joda.time.DateTime;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.Relationship;
import com.ihsinformatics.gfatmnotifications.common.model.RuleBook;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.gfatmnotifications.common.util.DateDeserializer;
import com.ihsinformatics.gfatmnotifications.common.util.DateSerializer;
import com.ihsinformatics.gfatmnotifications.common.util.Decision;
import com.ihsinformatics.gfatmnotifications.common.util.FormattedMessageParser;
import com.ihsinformatics.gfatmnotifications.common.util.ValidationUtil;
import com.ihsinformatics.util.ClassLoaderUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author shujaat.ali@ihsinformatics.com, owais.hussain@ihsinformatics.com
 *
 */
public class Context {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	public static final String PROP_FILE_NAME = "gfatm-notifications.properties";
	public static final String PROJECT_NAME = "Aao-TB-Mitao Notifications";
	private static final String RULE_BOOK_FILE = "rules/RuleBook.xlsx";

	private static Properties props;
	private static DatabaseUtil dbOpenmrsUtil;
	private static DatabaseUtil dbDwUtil;
	private static GsonBuilder builder;

	// Collection of files in the rules directory
	private static RuleBook ruleBook;

	private static List<User> users;
	private static List<Location> locations;
	private static List<String> userRoles;
	private static List<Patient> patients;
	private static Map<Integer, String> encounterTypes;
	private static Map<Integer, String[]> relationshipTypes;

	private Context() {
	}

	/**
	 * See overloaded method Context.initialize(boolean, boolean)
	 * 
	 * @throws IOException
	 */
	public static void initialize() throws IOException {
		initialize(true, true, true);
	}

	/**
	 * This method reloads metadata only if the list objects are null. In order to
	 * explicitly load metadata, call respective load...() methods
	 * 
	 * @param initPatientData
	 * @param initRuleBook
	 * @throws IOException
	 */
	public static void initialize(boolean initMetadata, boolean initPatientData, boolean initRuleBook)
			throws IOException {
		if (props == null) {
			log.info("Reading properties...");
			readProperties(PROP_FILE_NAME);
		}
		if (getProps() == null) {
			log.severe("Unable to read properties file.");
			System.exit(-1);
		}
		createOpenmrsDbConnection();
		createWarehouseDbConnection();
		DateTime start = new DateTime();
		if (initMetadata) {
			log.info("Loading metadata...");
			if (encounterTypes == null) {
				loadEncounterTypes(Context.getOpenmrsDb());
			}
			if (relationshipTypes == null) {
				loadRelationshipTypes(Context.getOpenmrsDb());
			}
			if (users == null) {
				loadUsers(Context.getOpenmrsDb());
			}
			if (locations == null) {
				loadLocations(Context.getOpenmrsDb());
			}
		}
		if (initPatientData && patients == null) {
			log.info("Loading patient data...");
			loadPatients(Context.getOpenmrsDb());
		}
		if (initRuleBook && ruleBook == null) {
			log.info("Loading rule book...");
			loadRuleBook();
			log.info("It took me: " + new DateTime().minus(start.getMillis()).getMillis()
					+ " milliseconds to load data and rules.");
		}
		log.info("Initialization complete.");
	}

	/**
	 * Reads OpenMRS connection from properties file
	 */
	private static void createOpenmrsDbConnection() {
		String url = getProps().getProperty("openmrs.connection.url", "jdbc:mysql://localhost:3306");
		String dbName = getProps().getProperty("openmrs.connection.database", "gfatm_dw");
		String driverName = getProps().getProperty("openmrs.connection.driver_class", "com.mysql.jdbc.Driver");
		String userName = getProps().getProperty("openmrs.connection.username", "root");
		String password = getProps().getProperty("openmrs.connection.password");
		DatabaseUtil openmrsDb = new DatabaseUtil(url, dbName, driverName, userName, password);
		setOpenmrsDb(openmrsDb);
	}

	/**
	 * Reads Data warehouse connection from properties file
	 */
	private static void createWarehouseDbConnection() {
		String url = getProps().getProperty("dwh.connection.url", "jdbc:mysql://localhost:3306");
		String dbName = getProps().getProperty("dwh.connection.database", "gfatm_dw");
		String driverName = getProps().getProperty("dwh.connection.driver_class", "com.mysql.jdbc.Driver");
		String userName = getProps().getProperty("dwh.connection.username", "root");
		String password = getProps().getProperty("dwh.connection.password");
		DatabaseUtil warehouseDb = new DatabaseUtil(url, dbName, driverName, userName, password);
		setDwDb(warehouseDb);
	}

	/**
	 * Read properties from PROP_FILE
	 * 
	 * @throws IOException
	 */
	public static void readProperties(String fileName) throws IOException {
		InputStream inputStream = ClassLoaderUtil.getResourceAsStream(fileName, Context.class);
		if (inputStream != null) {
			props = new Properties();
			props.load(inputStream);
		}
	}

	/**
	 * @return the props
	 */
	public static Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public static void setProps(Properties props) {
		Context.props = props;
	}

	/**
	 * Returns String value of given key in properties file
	 * 
	 * @param key
	 * @return
	 */
	public static String getStringProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * Returns Integer value of given key in properties file
	 * 
	 * @param key
	 * @return
	 */
	public static Integer getIntegerProperty(String key) {
		try {
			return Integer.parseInt(props.getProperty(key));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns Boolean value of given key in properties file
	 * 
	 * @param key
	 * @return
	 */
	public static Boolean getBooleanProperty(String key) {
		try {
			return Boolean.parseBoolean(props.getProperty(key));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns Double value of given key in properties file
	 * 
	 * @param key
	 * @return
	 */
	public static Double getDoubleProperty(String key) {
		try {
			return Double.parseDouble(props.getProperty(key));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns array of String values of given key in properties file, split by the
	 * split character defined. If splitBy is null, the list is split by comma
	 * 
	 * @param key
	 * @param splitBy
	 * @return
	 */
	public static String[] getCommaSeparatedValueProperty(String key, String splitBy) {
		if (splitBy == null) {
			splitBy = ",";
		}
		return props.getProperty(key).split(splitBy);
	}

	/**
	 * @return the localDb
	 */
	public static DatabaseUtil getDwDb() {
		return dbDwUtil;
	}

	/**
	 * @param localDb the localDb to set
	 */
	public static void setDwDb(DatabaseUtil getDwDb) {
		Context.dbDwUtil = getDwDb;
	}

	/**
	 * @return the localDb
	 */
	public static DatabaseUtil getOpenmrsDb() {
		return dbOpenmrsUtil;
	}

	/**
	 * @param localDb the localDb to set
	 */
	public static void setOpenmrsDb(DatabaseUtil openmrsDb) {
		Context.dbOpenmrsUtil = openmrsDb;
	}

	/**
	 * Executes query and converts result set into JSON string
	 *
	 * @param query
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static String queryToJson(String query, DatabaseUtil dbUtil) {
		List<Map<String, Object>> list = null;
		QueryRunner queryRunner = new QueryRunner();
		builder = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer())
				.registerTypeAdapter(Date.class, new DateSerializer()).setPrettyPrinting().serializeNulls();
		try {
			list = queryRunner.query(dbUtil.getConnection(), query, new MapListHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = builder.create().toJson(list);
		return json;
	}

	/**
	 * Converts an object to string, or empty string if obj is null
	 * 
	 * @param obj
	 * @return
	 */
	public static String convertToString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	/**
	 * Loads all readable files with extension xls or xlsx from rules directory into
	 * a Set
	 * 
	 * @throws DirectoryIteratorException
	 * @throws IOException
	 */
	public static void loadRuleBook() throws IOException {
		URL url = ClassLoaderUtil.getResource(RULE_BOOK_FILE, Context.class);
		File ruleBookFile = new File(url.getFile());
		if (ruleBookFile.isDirectory() || !ruleBookFile.canRead()) {
			throw new DirectoryIteratorException(new IOException("Rule file is either a directory or inaccessible."));
		}
		Context.ruleBook = new RuleBook(ruleBookFile);
	}

	/**
	 * @return
	 */
	public static RuleBook getRuleBook() {
		if (ruleBook == null) {
			try {
				Context.loadRuleBook();
			} catch (IOException e) {
				log.severe("Exception while fetching RuleBook " + e.getMessage());
			}
		}
		return ruleBook;
	}

	/**
	 * Fetch all encounter types from DB and store locally
	 */
	public static void loadEncounterTypes(DatabaseUtil dbUtil) {
		encounterTypes = new HashMap<>();
		try {
			StringBuilder query = new StringBuilder(
					"SELECT encounter_type_id as encounterTypeId, name FROM encounter_type");
			Object[][] data = dbUtil.getTableData(query.toString());
			if (data == null) {
				return;
			}
			for (Object[] element : data) {
				encounterTypes.put(Integer.parseInt(element[0].toString()), element[1].toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetch all relationship types from DB and store locally
	 */
	public static void loadRelationshipTypes(DatabaseUtil dbUtil) {
		relationshipTypes = new HashMap<Integer, String[]>();
		try {
			StringBuilder query = new StringBuilder(
					"SELECT relationship_type_id as relationshipTypeId, a_is_to_b, b_is_to_a FROM relationship_type");
			Object[][] data = dbUtil.getTableData(query.toString());
			if (data == null) {
				return;
			}
			for (Object[] element : data) {
				relationshipTypes.put(Integer.parseInt(element[0].toString()),
						new String[] { element[1].toString(), element[2].toString() });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetch all locations from DB and store locally
	 */
	public static void loadLocations(DatabaseUtil dbUtil) {
		setLocations(new ArrayList<Location>());
		StringBuilder query = new StringBuilder();
		query.append(
				"select distinct l.location_id as locationId, l.name, l.parent_location as parentId, l.uuid, (case ifnull(ltfast.location_id, 0) when 0 then 0 else 1 end) as fast, (case ifnull(ltpet.location_id, 0) when 0 then 0 else 1 end) as pet, (case ifnull(ltpmdt.location_id, 0) when 0 then 0 else 1 end) as pmdt, (case ifnull(ltctb.location_id, 0) when 0 then 0 else 1 end) as childhood_tb, (case ifnull(ltcomorb.location_id, 0) when 0 then 0 else 1 end) as comorbidities, ");
		query.append(
				"pcontact.value_reference as primaryContact, pcontact_nm.value_reference as primaryContactName, scontact.value_reference as secondaryContact, scontact_nm.value_reference as secondaryContactName, ");
		query.append(
				"ltype.value_reference as locationType, l.address1, l.address2, l.address3, l.city_village as cityVillage, l.state_province as stateProvince, l.description, l.date_created as dateCreated, st.value_reference as status from location as l ");
		query.append(
				"left outer join location_tag_map as ltfast on ltfast.location_id = l.location_id and ltfast.location_tag_id = 6 ");
		query.append(
				"left outer join location_tag_map as ltpet on ltpet.location_id = l.location_id and ltpet.location_tag_id = 7 ");
		query.append(
				"left outer join location_tag_map as ltpmdt on ltpmdt.location_id = l.location_id and ltpmdt.location_tag_id = 8 ");
		query.append(
				"left outer join location_tag_map as ltctb on ltctb.location_id = l.location_id and ltctb.location_tag_id = 9 ");
		query.append(
				"left outer join location_tag_map as ltcomorb on ltcomorb.location_id = l.location_id and ltcomorb.location_tag_id = 10 ");
		query.append(
				"left outer join location_attribute as pcontact on pcontact.location_id = l.location_id and pcontact.attribute_type_id = 2 and pcontact.voided = 0 ");
		query.append(
				"left outer join location_attribute as pcontact_nm on pcontact_nm.location_id = l.location_id and pcontact_nm.attribute_type_id = 14 and pcontact_nm.voided = 0 ");
		query.append(
				"left outer join location_attribute as scontact on scontact.location_id = l.location_id and scontact.attribute_type_id = 10 and scontact.voided = 0 ");
		query.append(
				"left outer join location_attribute as scontact_nm on scontact_nm.location_id = l.location_id and scontact_nm.attribute_type_id = 15 and scontact_nm.voided = 0 ");
		query.append(
				"left outer join location_attribute as ltype on ltype.location_id = l.location_id and ltype.attribute_type_id = 9 and ltype.voided = 0 ");
		query.append(
				"left outer join location_attribute as st on st.location_id = l.location_id and st.attribute_type_id = 13 and st.voided = 0 ");
		query.append("where l.retired = 0");

		String jsonString = queryToJson(query.toString(), dbUtil);
		Type listType = new TypeToken<List<Location>>() {
		}.getType();
		locations = builder.create().fromJson(jsonString, listType);
	}

	/**
	 * Fetch all users from DB and store locally
	 */
	public static void loadUsers(DatabaseUtil dbUtil) {
		setUsers(new ArrayList<User>());
		StringBuilder query = new StringBuilder();
		query.append(
				"select u.user_id as userId, u.person_id as personId, u.system_id as systemId, u.username, pn.given_name as givenName, pn.family_name as lastName, p.gender, pcontact.value as primaryContact, hd.value as healthDistrict, hc.value as healthCenter, ");
		query.append(
				"lang.value as motherTongue, pa.address1, pa.address2, pa.county_district as district, pa.city_village as cityVillage, pa.country, pa.address3 as landmark, inter.value_reference as intervention, u.date_created as dateCreated, u.uuid,ur.role from users as u ");
		query.append("inner join user_role as ur on ur.user_id = u.user_id ");
		query.append("inner join person as p on p.person_id = u.person_id ");
		query.append("inner join person_name as pn on pn.person_id = p.person_id ");
		query.append("inner join provider as pr on pr.person_id = p.person_id and pr.identifier = u.system_id ");
		query.append(
				"left outer join provider_attribute as inter on inter.provider_id = pr.provider_id and inter.attribute_type_id = 1 and inter.voided = 0 ");
		query.append(
				"left outer join person_attribute as hd on hd.person_id = p.person_id and hd.person_attribute_type_id = 6 and hd.voided = 0 ");
		query.append(
				"left outer join person_attribute as hc on hc.person_id = p.person_id and hc.person_attribute_type_id = 7 and hc.voided = 0 ");
		query.append(
				"left outer join person_attribute as pcontact on pcontact.person_id = p.person_id and pcontact.person_attribute_type_id = 8 and pcontact.voided = 0 ");
		query.append(
				"left outer join person_attribute as lang on lang.person_id = p.person_id and lang.person_attribute_type_id = 18 and lang.voided = 0 ");
		query.append(
				"left outer join person_address as pa on pa.person_id = p.person_id and pa.voided = 0 and pa.preferred = 1 ");
		query.append("where u.retired = 0");
		String jsonString = queryToJson(query.toString(), dbUtil);
		Type listType = new TypeToken<List<User>>() {
		}.getType();
		users = builder.create().fromJson(jsonString, listType);
	}

	/**
	 * Fetch all patients from DB and store locally
	 */
	public static void loadPatients(DatabaseUtil dbUtil) {
		StringBuilder query = new StringBuilder();
		query.append(
				"select pt.patient_id as personId, pn.given_name as givenName, pn.family_name as lastName, p.gender as gender, p.birthdate as birthdate, p.birthdate_estimated as estimated, ");
		query.append(
				"bp.value as birthplace, ms.value as maritalStatus, pcontact.value as primaryContact, hd.value as healthDistrict, hc.value as healthCenter, lang.value as motherTongue, nic.value as nationalID, oin.value as otherIdentificationNumber, tg.value as transgender, pat.value as patientType, pt.creator as creator, pt.date_created as dateCreated,pa.address1, pa.address2, pa.county_district as district, pa.city_village as cityVillage, pa.country, pa.address3 as landmark, pi.identifier as patientIdentifier, pi.location_id as patientIdentifierLocation, pi.uuid, cons.value_coded as consent, p.dead as dead from patient pt ");
		query.append(
				"inner join patient_identifier pi on pi.patient_id = pt.patient_id and pi.identifier_type = 3 and pi.voided = 0 ");
		query.append("inner join person as p on p.person_id = pi.patient_id and p.voided = 0 ");
		query.append("inner join person_name as pn on pn.person_id = p.person_id and pn.preferred = 1 and pn.voided = 0 ");
		query.append(
				"left outer join person_attribute as hd on hd.person_id = p.person_id and hd.person_attribute_type_id = 6 and hd.voided = 0 ");
		query.append(
				"left outer join person_attribute as hc on hc.person_id = p.person_id and hc.person_attribute_type_id = 7 and hc.voided = 0 ");
		query.append(
				"left outer join person_attribute as pcontact on pcontact.person_id = p.person_id and pcontact.person_attribute_type_id = 8 and pcontact.voided = 0 ");
		query.append(
				"left outer join person_attribute as lang on lang.person_id = p.person_id and lang.person_attribute_type_id = 18 and lang.voided = 0 ");
		query.append(
				"left outer join person_attribute as nic on nic.person_id = p.person_id and nic.person_attribute_type_id = 20 and nic.voided = 0 ");
		query.append(
				"left outer join person_attribute as bp on bp.person_id = p.person_id and bp.person_attribute_type_id = 2 and bp.voided = 0 ");
		query.append(
				"left outer join person_attribute as ms on ms.person_id = p.person_id and ms.person_attribute_type_id = 5 and ms.voided = 0 ");
		query.append(
				"left outer join person_attribute as oin on oin.person_id = p.person_id and oin.person_attribute_type_id = 26 and oin.voided = 0 ");
		query.append(
				"left outer join person_attribute as tg on tg.person_id = p.person_id and tg.person_attribute_type_id = 27 and tg.voided = 0 ");
		query.append(
				"left outer join person_attribute as pat on pat.person_id = p.person_id and pat.person_attribute_type_id = 28 and pat.voided = 0 ");
		query.append(
				"left outer join person_address as pa on pa.person_id = p.person_id and pa.voided = 0 and pa.preferred = 1 ");
		query.append(
				"left join obs as cons on pa.person_id = cons.person_id and cons.concept_id = 164700 and cons.voided = 0 ");
		query.append("where pt.voided = 0 ");
		// Exclude patients against whom no encounter was filled in last 100 days
		query.append(
				"and pt.patient_id in (select distinct patient_id from encounter where voided = 0 and datediff(current_date(), encounter_datetime) < 90) ");
		// Exclude patients where last encounter filled was End of follow-up
		query.append(
				"and (select ifnull(encounter_type, 0) from encounter where patient_id = pt.patient_id and voided = 0 order by encounter_datetime desc limit 1) <> 190 ");
		query.append("and ifnull(cons.value_coded, 1065) = 1065 ");
		String jsonString = queryToJson(query.toString(), dbUtil);
		Type listType = new TypeToken<List<Patient>>() {
		}.getType();
		patients = builder.create().fromJson(jsonString, listType);
	}

	/**
	 * Fetch Encounter object by encounter ID
	 *
	 * @param encounterId
	 * @return
	 */
	public static Encounter getEncounter(int encounterId, DatabaseUtil dbUtil) {
		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id as encounterId, et.name as encounterType, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patientName, e.encounter_datetime as encounterDatetime, l.name as encounterLocation, pc.value as patientContact, lc.value_reference as locationContact, pr.identifier as provider, upc.value as providerContact, u.username, e.date_created as dateCreated, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id and pn.preferred = 1 ");
		query.append(
				"inner join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 and pc.voided = 0 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append(
				"left outer join location_attribute as lc on lc.location_id = l.location_id and lc.attribute_type_id = 2 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.encounter_id ");
		query.append(
				"left outer join person_attribute as upc on upc.person_id = pr.person_id and upc.person_attribute_type_id = 8 ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append("where e.encounter_id = " + encounterId);
		query.append(" and e.voided = 0 ");
		String jsonString = queryToJson(query.toString(), dbUtil);
		Type type = new TypeToken<List<Encounter>>() {
		}.getType();
		List<Encounter> encounter = builder.create().fromJson(jsonString, type);
		return encounter.get(0);
	}

	/**
	 * Fetch encounters by date range and type (optional)
	 *
	 * @param from
	 * @param to
	 * @param type
	 * @return
	 */
	public static List<Encounter> getEncounters(DateTime from, DateTime to, Integer type, DatabaseUtil dbUtil) {
		if (from == null || to == null) {
			return null;
		}
		String sqlFrom = DateTimeUtil.toSqlDateTimeString(from.toDate());
		String sqlTo = DateTimeUtil.toSqlDateTimeString(to.toDate());
		StringBuilder filter = new StringBuilder();
		filter.append("where e.voided = 0 and e.date_created between ");
		filter.append("timestamp('" + sqlFrom + "') ");
		filter.append("and ");
		filter.append("timestamp('" + sqlTo + "') ");
		if (type != null) {
			filter.append(" and e.encounter_type = " + type);
		}
		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id as encounterId, et.name as encounterType, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patientName, e.encounter_datetime as encounterDatetime, l.name as encounterLocation, pc.value as patientContact, lc.value_reference as locationContact, pr.identifier as provider, upc.value as providerContact, u.username, e.date_created as dateCreated, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id and pn.preferred = 1 ");
		query.append(
				"inner join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 and pc.voided = 0 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append(
				"left outer join location_attribute as lc on lc.location_id = l.location_id and lc.attribute_type_id = 2 and lc.voided = 0 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.provider_id ");
		query.append(
				"left outer join person_attribute as upc on upc.person_id = pr.person_id and upc.person_attribute_type_id = 8 ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append(filter);
		// Convert query into json sring
		System.out.println(query.toString());
		String jsonString = queryToJson(query.toString(), dbUtil);
		Type listType = new TypeToken<ArrayList<Encounter>>() {
		}.getType();
		List<Encounter> encounters = builder.create().fromJson(jsonString, listType);
		return encounters;
	}

	/**
	 * Overloaded method. Check getEncounterByPatientIdentifier(String, int,
	 * boolean, DatabaseUtil)
	 * 
	 * @param patientIdentifier
	 * @param encounterTypeId
	 * @param dbUtil
	 * @return
	 */
	public static Encounter getEncounterByPatientIdentifier(String patientIdentifier, int encounterTypeId,
			DatabaseUtil dbUtil) {
		return getEncounterByPatientIdentifier(patientIdentifier, encounterTypeId, false, dbUtil);
	}

	/**
	 * Returns latest Encounter by given patient identifier and encounter type ID
	 * 
	 * @param patientIdentifier
	 * @param encounterTypeId
	 * @param attachObs
	 * @param dbUtil
	 * @return
	 */
	public static Encounter getEncounterByPatientIdentifier(String patientIdentifier, int encounterTypeId,
			boolean attachObs, DatabaseUtil dbUtil) {
		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id as encounterId, et.name as encounterType, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patientName, e.encounter_datetime as encounterDatetime, l.name as encounterLocation, pc.value as patientContact, lc.value_reference as locationContact, pr.identifier as provider, upc.value as providerContact, u.username, e.date_created as dateCreated, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id and pn.preferred = 1 ");
		query.append(
				"inner join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 and pc.voided = 0 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append(
				"left outer join location_attribute as lc on lc.location_id = l.location_id and lc.attribute_type_id = 2 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.provider_id ");
		query.append(
				"left outer join person_attribute as upc on upc.person_id = pr.person_id and upc.person_attribute_type_id = 8 ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append("where e.patient_id = (select patient_id from patient_identifier where identifier = '"
				+ patientIdentifier + "' and voided = 0 limit 1)");
		query.append(
				"and e.encounter_type = " + encounterTypeId + " and e.voided = 0 order by e.encounter_datetime desc");
		String jsonString = queryToJson(query.toString(), dbUtil);
		Type type = new TypeToken<List<Encounter>>() {
		}.getType();
		List<Encounter> encounter = builder.create().fromJson(jsonString, type);
		if (encounter.isEmpty()) {
			return null;
		}
		Encounter encounterToReturn = encounter.get(0);
		if (attachObs) {
			encounterToReturn.setObservations(getEncounterObservations(encounterToReturn, dbUtil));
		}
		return encounterToReturn;
	}

	/**
	 * Returns set of observations by encounter
	 * 
	 * @param encounter
	 * @param dbUtil
	 * @return
	 */
	public static List<Observation> getEncounterObservations(Encounter encounter, DatabaseUtil dbUtil) {
		StringBuilder query = new StringBuilder();
		query.append(
				"select o.obs_id as obsId, e.patient_id as patientId, o.concept_id as conceptId, cn.name as conceptName, c.name as conceptShortName, o.encounter_id as encounterId, o.order_id as orderId, o.location_id as locationId, o.value_numeric as valueNumeric, o.value_coded as valueCoded, vn.name as valueCodedName, o.value_datetime as valueDatetime, o.value_text as valueText, o.uuid from obs as o ");
		query.append("inner join encounter as e on e.encounter_id = o.encounter_id ");
		query.append(
				"inner join concept_name as c on c.concept_id = o.concept_id and c.locale = 'en' and c.concept_name_type = 'SHORT' ");
		query.append(
				"inner join concept_name as cn on cn.concept_id = c.concept_id and cn.locale = 'en' and ifnull(cn.concept_name_type, 'FULLY_SPECIFIED') = 'FULLY_SPECIFIED' and cn.locale_preferred = 1 and cn.voided = 0 ");
		query.append(
				"left outer join concept_name as vn on vn.concept_id = o.value_coded and vn.locale = 'en' and vn.concept_name_type = 'FULLY_SPECIFIED' and vn.locale_preferred = 1 and vn.voided = 0 ");
		query.append("where o.voided = 0 and o.encounter_id = " + encounter.getEncounterId());

		String jsonString = queryToJson(query.toString(), dbUtil);
		Type type = new TypeToken<List<Observation>>() {
		}.getType();
		List<Observation> observations = builder.create().fromJson(jsonString, type);
		return observations;
	}

	public static List<Relationship> getRelationshipsByPersonId(Integer personId, DatabaseUtil dbUtil) {
		StringBuilder query = new StringBuilder();
		query.append("select r.person_a, t.a_is_to_b, r.person_b, t.b_is_to_a, r.uuid from relationship as r ");
		query.append("inner join relationship_type as t on t.relationship_type_id = r.relationship ");
		query.append("where r.voided = 0 ");
		query.append("and (r.person_a = " + personId + " or r.person_b = " + personId + ")");
		Object[][] data = dbUtil.getTableData(query.toString());
		List<Relationship> relationships = new ArrayList<>();
		for (Object[] row : data) {
			Integer personA = Integer.parseInt(row[0].toString());
			String aIsToB = row[1].toString();
			Integer personB = Integer.parseInt(row[2].toString());
			String bIsToA = row[3].toString();
			String uuid = row[4].toString();
			Relationship relationship = new Relationship(personA, aIsToB, personB, bIsToA, uuid);
			relationships.add(relationship);
		}
		return relationships;
	}

	public static String[] getUserRolesByUser(User user, DatabaseUtil dbUtil) {
		StringBuilder query = new StringBuilder();
		query.append("select * from user_role ur ");
		query.append("where user_id='" + user.getUserId() + "'");
		String jsonString = queryToJson(query.toString(), dbUtil);
		Type type = new TypeToken<String[]>() {
		}.getType();
		userRoles = builder.create().fromJson(jsonString, type);
		return getUserRoles().toArray(new String[] {});
	}

	public static Location getLocationById(Integer id, DatabaseUtil dbUtil) {
		if (getLocations().isEmpty()) {
			loadLocations(dbUtil);
		}
		for (Location location : getLocations()) {
			if (location.getLocationId().equals(id)) {
				return location;
			}
		}
		return null;
	}

	public static Location getLocationByName(String code, DatabaseUtil dbUtil) {
		if (getLocations().isEmpty()) {
			loadLocations(dbUtil);
		}
		for (Location location : getLocations()) {
			if (location.getName().equals(code)) {
				return location;
			}
		}
		return null;
	}

	public static User getUserById(Integer id, DatabaseUtil dbUtil) {
		if (getUsers().isEmpty()) {
			loadUsers(dbUtil);
		}
		for (User user : getUsers()) {
			if (user.getUserId().equals(id)) {
				return user;
			}
		}
		return null;
	}

	public static User getUserByUsername(String username, DatabaseUtil dbUtil) {
		if (getUsers().isEmpty()) {
			loadUsers(dbUtil);
		}
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Returns Patient object by given patient identifier (first match). If the
	 * patient does not exist in the set in memory, it is fetched from the database
	 * 
	 * @param patientIdentifier
	 * @param dbUtil
	 * @return
	 */
	public static Patient getPatientByIdentifierOrGeneratedId(String patientIdentifier, Integer generatedId,
			DatabaseUtil dbUtil) {
		if (patients == null || getPatients().isEmpty()) {
			loadPatients(dbUtil);
		}
		try {
			for (Patient patient : getPatients()) {
				if (patient == null) {
					continue;
				}
				if (patientIdentifier != null) {
					if (patient.getPatientIdentifier().equalsIgnoreCase(patientIdentifier)) {
						return patient;
					}
				} else {
					if (patient.getPersonId().equals(generatedId)) {
						return patient;
					}
				}
			}
			StringBuilder query = new StringBuilder();
			query.append(
					"select pt.patient_id as personId, pn.given_name as givenName, pn.family_name as lastName, p.gender as gender, p.birthdate as birthdate, p.birthdate_estimated as estimated, ");
			query.append(
					"bp.value as birthplace, ms.value as maritalStatus, pcontact.value as primaryContact, pco.value as primaryContactOwner, scontact.value as secondaryContact, sco.value as secondaryContactOwner, ");
			query.append(
					"hd.value as healthDistrict, hc.value as healthCenter, ethn.value as ethnicity, edu.value as educationLevel, emp.value as employmentStatus, occu.value as occupation, lang.value as motherTongue, ");
			query.append(
					"nic.value as nationalID, cnicO.value as nationalIDOwner, gn.value as guardianName, ts.value as treatmentSupporter, oin.value as otherIdentificationNumber, tg.value as transgender, ");
			query.append(
					"pat.value as patientType, pt.creator as creator, pt.date_created as dateCreated, pa.address1, pa.address2, pa.county_district as district, pa.city_village as cityVillage, pa.country, pa.address3 as landmark, ");
			query.append(
					"pi.identifier as patientIdentifier, pi.uuid, cons.value_coded as consent, p.dead as dead from patient pt ");
			query.append(
					"inner join patient_identifier pi on pi.patient_id =pt.patient_id and pi.identifier_type = 3 and pi.voided = 0 ");
			query.append("inner join person as p on p.person_id = pi.patient_id  and p.voided = 0 ");
			query.append("inner join person_name as pn on pn.person_id = p.person_id  and pn.voided = 0 ");
			query.append(
					"left outer join person_attribute as hd on hd.person_id = p.person_id and hd.person_attribute_type_id = 6 and hd.voided = 0 ");
			query.append(
					"left outer join person_attribute as hc on hc.person_id = p.person_id and hc.person_attribute_type_id = 7 and hc.voided = 0 ");
			query.append(
					"left outer join person_attribute as pcontact on pcontact.person_id = p.person_id and pcontact.person_attribute_type_id = 8 and pcontact.voided = 0 ");
			query.append(
					"left outer join person_attribute as scontact on scontact.person_id = p.person_id and scontact.person_attribute_type_id = 12 and scontact.voided = 0 ");
			query.append(
					"left outer join person_attribute as edu on edu.person_id = p.person_id and edu.person_attribute_type_id = 15 and edu.voided = 0 ");
			query.append(
					"left outer join person_attribute as emp on emp.person_id = p.person_id and emp.person_attribute_type_id = 16 and emp.voided = 0 ");
			query.append(
					"left outer join person_attribute as occu on occu.person_id = p.person_id and occu.person_attribute_type_id = 17 and occu.voided = 0 ");
			query.append(
					"left outer join person_attribute as lang on lang.person_id = p.person_id and lang.person_attribute_type_id = 18 and lang.voided = 0 ");
			query.append(
					"left outer join person_attribute as nic on nic.person_id = p.person_id and nic.person_attribute_type_id = 20 and nic.voided = 0 ");
			query.append(
					"left outer join person_attribute as bp on bp.person_id = p.person_id and bp.person_attribute_type_id = 2 and bp.voided = 0 ");
			query.append(
					"left outer join person_attribute as ms on ms.person_id = p.person_id and ms.person_attribute_type_id = 5 and ms.voided = 0 ");
			query.append(
					"left outer join person_attribute as pco on pco.person_id = p.person_id and pco.person_attribute_type_id = 11 and pco.voided = 0 ");
			query.append(
					"left outer join person_attribute as sco on sco.person_id = p.person_id and sco.person_attribute_type_id = 13 and sco.voided = 0 ");
			query.append(
					"left outer join person_attribute as ethn on ethn.person_id = p.person_id and ethn.person_attribute_type_id = 14 and ethn.voided = 0 ");
			query.append(
					"left outer join person_attribute as cnicO on cnicO.person_id = p.person_id and cnicO.person_attribute_type_id = 21 and cnicO.voided = 0 ");
			query.append(
					"left outer join person_attribute as gn on gn.person_id = p.person_id and gn.person_attribute_type_id = 22 and gn.voided = 0 ");
			query.append(
					"left outer join person_attribute as ts on ts.person_id = p.person_id and ts.person_attribute_type_id = 25 and ts.voided = 0 ");
			query.append(
					"left outer join person_attribute as oin on oin.person_id = p.person_id and oin.person_attribute_type_id = 26 and oin.voided = 0 ");
			query.append(
					"left outer join person_attribute as tg on tg.person_id = p.person_id and tg.person_attribute_type_id = 27 and tg.voided = 0 ");
			query.append(
					"left outer join person_attribute as pat on pat.person_id = p.person_id and pat.person_attribute_type_id = 28 and pat.voided = 0 ");
			query.append(
					"left outer join person_address as pa on pa.person_id = p.person_id and pa.voided = 0 and pa.preferred = 1 ");
			query.append(
					"left join obs as cons on pa.person_id=cons.person_id and cons.concept_id=164700 and cons.voided = 0 ");
			if (patientIdentifier != null) {
				query.append(" where pi.identifier ='" + patientIdentifier + "'");
			} else {
				query.append(" where pt.patient_id ='" + generatedId + "'");
			}
			String jsonString = queryToJson(query.toString(), dbUtil);
			Type listType = new TypeToken<List<Patient>>() {
			}.getType();
			List<Patient> patients = builder.create().fromJson(jsonString, listType);
			if (!patients.isEmpty()) {
				Patient patient = patients.get(0);
				getPatients().add(patient);
				return patient;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the encounterTypes
	 */
	public static Map<Integer, String> getEncounterTypes() {
		return encounterTypes;
	}

	/**
	 * @param encounterTypes the encounterTypes to set
	 */
	public static void setEncounterTypes(Map<Integer, String> encounterTypes) {
		Context.encounterTypes = encounterTypes;
	}

	/**
	 * Returns generated ID of encounter type by name
	 * 
	 * @param encounterType
	 * @return
	 */
	public static Integer getEncounterTypeId(String encounterType) {
		for (Entry<Integer, String> entry : encounterTypes.entrySet()) {
			if (Objects.equals(encounterType, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @return the relationshipTypes
	 */
	public static Map<Integer, String[]> getRelationshipTypes() {
		return relationshipTypes;
	}

	/**
	 * @param relationshipTypes the relationshipTypes to set
	 */
	public static void setRelationshipTypes(Map<Integer, String[]> relationshipTypes) {
		Context.relationshipTypes = relationshipTypes;
	}

	/**
	 * @return the users
	 */
	public static List<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public static void setUsers(List<User> users) {
		Context.users = users;
	}

	/**
	 * @return the locations
	 */
	public static List<Location> getLocations() {
		return locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public static void setLocations(List<Location> locations) {
		Context.locations = locations;
	}

	/**
	 * @return the userRoles
	 */
	public static List<String> getUserRoles() {
		return userRoles;
	}

	/**
	 * @param userRoles the userRoles to set
	 */
	public static void setUserRoles(List<String> userRoles) {
		Context.userRoles = userRoles;
	}

	/**
	 * @return the patients
	 */
	public static List<Patient> getPatients() {
		return patients;
	}

	/**
	 * @param patients the patients to set
	 */
	public static void setPatients(List<Patient> patients) {
		Context.patients = patients;
	}

	public static Date calculateScheduleDate(DateTime referenceDate, Double plusMinus, String plusMinusUnit) {
		Date returnDate = null;
		if (referenceDate == null) {
			return null;
		}
		if (plusMinus == null) {
			DateTime now = new DateTime();
			// Should send next minute
			return referenceDate.withHourOfDay(now.getHourOfDay()).withMinuteOfHour(now.getMinuteOfHour() + 1).toDate();
		}
		if (plusMinusUnit.equalsIgnoreCase("hours")) {
			if (plusMinus < 0) {
				returnDate = referenceDate.minusHours(plusMinus.intValue()).toDate();
			} else {
				returnDate = referenceDate.plusHours(plusMinus.intValue()).toDate();
			}
		} else if (plusMinusUnit.equalsIgnoreCase("days")) {
			if (plusMinus < 0) {
				returnDate = referenceDate.minusDays(plusMinus.intValue()).toDate();
			} else {
				returnDate = referenceDate.plusDays(plusMinus.intValue()).toDate();
			}
		} else if (plusMinusUnit.equalsIgnoreCase("months")) {
			if (plusMinus < 0) {
				returnDate = referenceDate.minusMonths(plusMinus.intValue()).toDate();
			} else {
				returnDate = referenceDate.plusMonths(plusMinus.intValue()).toDate();
			}
		}
		return returnDate;
	}

	public static DateTime getReferenceDate(String variableName, Encounter encounter) {
		DateTime referenceDate = null;
		FormattedMessageParser formattedMessageParser = new FormattedMessageParser(Decision.SKIP);
		try {
			Object object;
			object = formattedMessageParser.getPropertyValue(encounter, variableName);
			if (object != null) {
				if (object instanceof Long) {
					return new DateTime((Long) object);
				} else {
					throw new IllegalArgumentException("Schedule Date Object is not a Date.");
				}
			}
		} catch (SecurityException | IllegalArgumentException | ReflectiveOperationException e) {
			log.severe(e.getMessage());
		}
		Observation target = null;
		for (Observation observation : encounter.getObservations()) {
			if (ValidationUtil.variableMatchesWithConcept(variableName, observation)) {
				target = observation;
				referenceDate = new DateTime(target.getValueDatetime());
				break;
			}
		}
		return referenceDate;
	}

	public static Map<String, String> getPatientAttributesByGeneratedId(Integer personId, DatabaseUtil dbUtil) {
		Map<String, String> map = null;
		StringBuilder query = new StringBuilder(
				"select pt.name, ifnull(l.name, ifnull(cn.name, pa.value)) as value from person_attribute as pa ");
		query.append(
				"inner join person_attribute_type as pt on pt.person_attribute_type_id = pa.person_attribute_type_id and pt.retired = 0 ");
		query.append("left outer join location as l on l.location_id = pa.value and l.retired = 0 ");
		query.append(
				"left outer join concept_name as cn on cn.concept_id = pa.value and cn.voided = 0 and cn.locale = 'en' and locale_preferred = 1 ");
		query.append("where pa.voided = 0 ");
		query.append("and pa.person_id = " + personId);
		Object[][] data = dbUtil.getTableData(query.toString());
		if (data != null) {
			map = new HashMap<>();
			for (Object[] objects : data) {
				String key = objects[0].toString();
				String value = objects[1].toString();
				map.put(key, value);
			}
		}
		return map;
	}
}
