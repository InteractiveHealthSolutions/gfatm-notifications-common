package com.ihsinformatics.gfatmnotifications.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.joda.time.DateTime;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ihsinformatics.gfatmnotifications.common.model.Contact;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.gfatmnotifications.common.util.DateDeserializer;
import com.ihsinformatics.gfatmnotifications.common.util.DateSerializer;
import com.ihsinformatics.util.ClassLoaderUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author shujaat.ali@ihsinformatics.com
 *
 */
public class Context {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	public static final String PROP_FILE_NAME = "gfatm-notifications.properties";
	public static final String PROJECT_NAME = "GFATM-Notifications";

	private static Properties props;
	private static DatabaseUtil dbUtil;
	private static GsonBuilder builder;

	private static List<User> users;
	private static List<Contact> userContacts;
	private static List<Location> locations;
	private static List<String> userRoles;
	private static List<Patient> patients;
	private static Map<Integer, String> encounterTypes;

	static {
		try {
			log.info("Reading properties...");
			readProperties();
			if (getProps() == null) {
				log.severe("Unable to read properties file.");
				System.exit(-1);
			}
			String url = getProps().getProperty("local.connection.url", "jdbc:mysql://localhost:3306");
			String dbName = getProps().getProperty("local.connection.database", "gfatm_dw");
			String driverName = getProps().getProperty("local.connection.driver_class", "com.mysql.jdbc.Driver");
			String userName = getProps().getProperty("local.connection.username", "root");
			String password = getProps().getProperty("local.connection.password");
			DatabaseUtil localDb = new DatabaseUtil(url, dbName, driverName, userName, password);
			setLocalDb(localDb);
			initialize();
		} catch (IOException e) {
			log.severe(e.getMessage());
			System.exit(-1);
		}
	}

	private Context() {
	}

	/**
	 * This method reloads metadata only if the list objects are null. In order to
	 * explicitly load metadata, call respective load...() methods
	 * 
	 * @throws IOException
	 */
	public static void initialize() throws IOException {
		if (encounterTypes == null) {
			loadEncounterTypes();
		}
		if (users == null) {
			loadUsers();
		}
		if (userContacts == null) {
			loadUserContacts();
		}
		if (locations == null) {
			loadLocations();
		}
		if (patients == null) {
			loadPatients();
		}
	}

	/**
	 * Read properties from PROP_FILE
	 * 
	 * @throws IOException
	 */
	public static void readProperties() throws IOException {
		InputStream inputStream = ClassLoaderUtil.getResourceAsStream(PROP_FILE_NAME, Context.class);
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
	 * @return the localDb
	 */
	public static DatabaseUtil getLocalDb() {
		return dbUtil;
	}

	/**
	 * @param localDb the localDb to set
	 */
	public static void setLocalDb(DatabaseUtil localDb) {
		Context.dbUtil = localDb;
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
	public static String queryToJson(String query) {
		List<Map<String, Object>> list = null;
		QueryRunner queryRunner = new QueryRunner();
		builder = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer())
				.registerTypeAdapter(Date.class, new DateSerializer()).setPrettyPrinting().serializeNulls();
		try {
			list = queryRunner.query(Context.getLocalDb().getConnection(), query, new MapListHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = builder.create().toJson(list);
		return json;
	}

	public static String convertToString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	/**
	 * Fetch all encounter types from DB and store locally
	 */
	public static void loadEncounterTypes() {
		encounterTypes = new HashMap<Integer, String>();
		StringBuilder query = new StringBuilder(
				"SELECT encounter_type_id as encounterTypeId, name FROM encounter_type where retired = 0");
		Object[][] data = Context.getLocalDb().getTableData(query.toString());
		if (data == null) {
			return;
		}
		for (Object[] element : data) {
			encounterTypes.put(Integer.parseInt(element[0].toString()), element[1].toString());
		}
	}

	/**
	 * Fetch all locations from DB and store locally
	 */
	public static void loadLocations() {
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

		String jsonString = queryToJson(query.toString());
		Type listType = new TypeToken<List<Location>>() {
		}.getType();
		locations = builder.create().fromJson(jsonString, listType);
	}

	/**
	 * Fetch all users from DB and store locally
	 */
	public static void loadUsers() {
		setUsers(new ArrayList<User>());
		StringBuilder query = new StringBuilder();
		query.append(
				"select u.user_id as userId, u.person_id as personId, u.system_id as systemId, u.username, pn.given_name as givenName, pn.family_name as lastName, p.gender, pcontact.value as primaryContact, scontact.value as secondaryContact, hd.value as healthDistrict, hc.value as healthCenter, ");
		query.append(
				"edu.value as educationLevel, emp.value as employmentStatus, occu.value as occupation, lang.value as motherTongue, nic.value as nationalId, pa.address1, pa.address2, pa.county_district as district, pa.city_village as cityVillage, pa.country, pa.address3 as landmark, inter.value_reference as intervention, u.date_created as dateCreated, u.uuid,ur.role from users as u ");
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
				"left outer join person_address as pa on pa.person_id = p.person_id and pa.voided = 0 and pa.preferred = 1 ");
		query.append("where u.retired = 0");

		String jsonString = queryToJson(query.toString());
		Type listType = new TypeToken<List<User>>() {
		}.getType();
		users = builder.create().fromJson(jsonString, listType);
	}

	/**
	 * Fetch all contacts from DB and store locally
	 */
	public static void loadUserContacts() {
		StringBuilder query = new StringBuilder();
		query.append(
				"select u.user_id, u.person_id as personId, em.value as emailAddress, pc.value as primaryContact, sc.value as secondaryContact, la.location_id as locationId, l.name as locationName from users as u ");
		query.append(
				"left outer join location_attribute as la on la.attribute_type_id = 16 and la.value_reference = u.system_id ");
		query.append("left outer join location as l on l.location_id = la.location_id ");
		query.append(
				"left outer join person_attribute as em on em.person_id = u.person_id and em.person_attribute_type_id = 29 and em.voided = 0 ");
		query.append(
				"left outer join person_attribute as pc on pc.person_id = u.person_id and pc.person_attribute_type_id = 8 and pc.voided = 0 ");
		query.append(
				"left outer join person_attribute as sc on sc.person_id = u.person_id and sc.person_attribute_type_id = 12 and sc.voided = 0 ");
		query.append(
				"having (concat(ifnull(emailAddress, ''), ifnull(primaryContact, ''), ifnull(secondaryContact, ''))) <> ''");
		String jsonString = queryToJson(query.toString());
		Type listType = new TypeToken<List<Contact>>() {
		}.getType();
		userContacts = builder.create().fromJson(jsonString, listType);
	}

	/**
	 * Fetch all patients from DB and store locally
	 */
	public static void loadPatients() {
		StringBuilder query = new StringBuilder();
		query.append(
				"select p.person_id as personId,pn.given_name as givenName,pn.family_name as lastName,p.gender as gender,p.birthdate as birthdate,p.birthdate_estimated as estimated, ");
		query.append(
				"bp.value as birthplace,ms.value as maritalStatus,pcontact.value as primaryContact,pco.value as primaryContactOwner , scontact.value as secondaryContact,sco.value as secondaryContactOwner,");
		query.append(
				"hd.value as healthDistrict, hc.value as healthCenter,ethn.value as ethnicity,edu.value as educationLevel, emp.value as employmentStatus, occu.value as occupation, lang.value as motherTongue,");
		query.append(
				"nic.value as nationalID, cnicO.value as nationalIDOwner,gn.value as guardianName,ts.value as treatmentSupporter,oin.value as otherIdentificationNumber,tg.value as transgender,");
		query.append(
				"pat.value as patientType,pt.creator as creator , pt.date_created as dateCreated,pa.address1, pa.address2, pa.county_district as district, pa.city_village as cityVillage, pa.country, pa.address3 as landmark,");
		query.append("pi.identifier as patientIdentifier,pi.uuid,p.dead as dead from patient pt ");
		query.append(
				"inner join patient_identifier pi on pi.patient_id =pt.patient_id and pi.identifier_type = 3 and pi.voided = 0 ");
		query.append("inner join person as p on p.person_id = pi.patient_id  and p.voided =0 ");
		query.append("inner join person_name as pn on pn.person_id = p.person_id  and pn.voided =0 ");
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
		String jsonString = queryToJson(query.toString());
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
	public static Encounter getEncounter(int encounterId) {
		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id as encounterId, et.name as encounterType, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patientName, e.encounter_datetime as encounterDatetime, l.description as encounterLocation, pc.value as patientContact, lc.value_reference as locationContact, pr.identifier as provider, upc.value as providerContact, u.username, e.date_created as dateCreated, e.uuid from encounter as e ");
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
		String jsonString = queryToJson(query.toString());
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
	public static List<Encounter> getEncounters(DateTime from, DateTime to, Integer type) {
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
		filter.append("and ");
		filter.append("timestampdiff(HOUR, e.date_created, e.encounter_datetime) <= 24 ");
		if (type != null) {
			filter.append(" and e.encounter_type=" + type);
		}
		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id as encounterId, et.name as encounterType, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patientName, e.encounter_datetime as encounterDatetime, l.description as encounterLocation, pc.value as patientContact, lc.value_reference as locationContact, pr.identifier as provider, upc.value as providerContact, u.username, e.date_created as dateCreated, e.uuid from encounter as e ");
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
		query.append(filter);

		// Convert query into json sring
		String jsonString = queryToJson(query.toString());
		Type listType = new TypeToken<ArrayList<Encounter>>() {
		}.getType();
		List<Encounter> encounters = builder.create().fromJson(jsonString, listType);
		return encounters;
	}

	public static Encounter getEncounterByPatientIdentifier(String patientIdentifier, int encounterTypeId) {

		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id as encounterId, et.name as encounterType, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patientName, e.encounter_datetime as encounterDatetime, l.description as encounterLocation, pc.value as patientContact, lc.value_reference as locationContact, pr.identifier as provider, upc.value as providerContact, u.username, e.date_created as dateCreated, e.uuid from encounter as e ");
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
		query.append("where e.patient_id = (select patient_id from patient_identifier where identifier = '"
				+ patientIdentifier + "')");
		query.append("and e.encounter_type = " + encounterTypeId + " and e.voided = 0 ");

		String jsonString = queryToJson(query.toString());
		Type type = new TypeToken<List<Encounter>>() {
		}.getType();
		List<Encounter> encounter = builder.create().fromJson(jsonString, type);
		return encounter.get(0);
	}

	public static Map<String, Object> getEncounterObservations(Encounter encounter) {
		Map<String, Object> observations;
		StringBuilder query = new StringBuilder();
		query.append(
				"select q.name as obs, concat(ifnull(a.name, ''), ifnull(o.value_datetime, ''), ifnull(o.value_text, ''), ifnull(o.value_numeric, '')) as value from obs as o ");
		query.append(
				"left outer join concept_name as q on q.concept_id = o.concept_id and q.locale = 'en' and q.concept_name_type = 'SHORT' and q.voided = 0 ");
		query.append(
				"left outer join concept_name as a on a.concept_id = o.value_coded and a.locale = 'en' and a.locale_preferred = 1 and a.voided = 0 ");
		query.append("where o.voided = 0 and o.encounter_id = " + encounter.getEncounterId());

		Object[][] data = Context.getLocalDb().getTableData(query.toString());
		observations = new HashMap<String, Object>();
		for (Object[] row : data) {
			int k = 0;
			try {
				String observation = convertToString(row[k++]);
				String value = convertToString(row[k++]);
				observations.put(observation, value);
			} catch (Exception ex) {
				log.severe(ex.getMessage());
			}
		}
		return observations;
	}

	public static Location getLocationById(Integer id) {
		if (getLocations().isEmpty()) {
			loadLocations();
		}
		for (Location location : getLocations()) {
			if (location.getLocationId().equals(id)) {
				return location;
			}
		}
		return null;
	}

	public static Location getLocationByName(String code) {
		if (getLocations().isEmpty()) {
			loadLocations();
		}
		for (Location location : getLocations()) {
			if (location.getName().equals(code)) {
				return location;
			}
		}
		return null;
	}

	public static User getUserById(Integer id) {
		if (getUsers().isEmpty()) {
			loadUsers();
		}
		for (User user : getUsers()) {
			if (user.getUserId().equals(id)) {
				return user;
			}
		}
		return null;
	}

	public static User getUserByUsername(String username) {
		if (getUsers().isEmpty()) {
			loadUsers();
		}
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	public static String[] getUserRolesByUser(User user) {
		StringBuilder query = new StringBuilder();
		query.append("select * from user_role ur ");
		query.append("where user_id='" + user.getUserId() + "'");
		String jsonString = queryToJson(query.toString());
		Type type = new TypeToken<String[]>() {
		}.getType();
		userRoles = builder.create().fromJson(jsonString, type);
		return getUserRoles().toArray(new String[] {});
	}

	public static Contact getContactByLocationId(Integer locationId) {
		Location location = getLocationById(locationId);
		return getContactByLocationName(location.getName());
	}

	public static Contact getContactByLocationName(String locationName) {
		if (getUserContacts().isEmpty()) {
			loadUserContacts();
		}
		for (Contact email : getUserContacts()) {
			if (email.getLocationName().equals(locationName)) {
				return email;
			}
		}
		return null;
	}

	public static Patient getPatientByIdentifier(String patientIdentifier) {
		if (getPatients().isEmpty()) {
			loadPatients();
		}
		for (Patient patient : getPatients()) {
			if (patient.getPatientIdentifier().equalsIgnoreCase(patientIdentifier)) {
				return patient;
			}
		}
		return null;
	}

	public static Contact getUserContactByLocationId(int locationId) {
		if (getUserContacts().isEmpty()) {
			loadUserContacts();
		}
		for (Contact contact : getUserContacts()) {
			if (contact.getLocationId() == locationId) {
				return contact;
			}
		}
		return null;

	}

	public static Contact getUserContactByLocationName(String locationName) {
		Location location = getLocationByName(locationName);
		if (location == null) {
			return null;
		}
		return getUserContactByLocationId(location.getLocationId());
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
	 * @return the userContacts
	 */
	public static List<Contact> getUserContacts() {
		return userContacts;
	}

	/**
	 * @param userContacts the userContacts to set
	 */
	public static void setUserContacts(List<Contact> userContacts) {
		Context.userContacts = userContacts;
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
}
