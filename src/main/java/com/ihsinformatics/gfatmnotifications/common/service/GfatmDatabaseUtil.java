/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.service;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ihsinformatics.gfatmnotifications.common.model.Contact;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.User;
import com.ihsinformatics.gfatmnotifications.common.util.DateDeserializer;
import com.ihsinformatics.gfatmnotifications.common.util.DateSerializer;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class GfatmDatabaseUtil {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static Map<Integer, String> encounterTypes;
	private DatabaseUtil db;

	public GfatmDatabaseUtil() {
	}

	public GfatmDatabaseUtil(DatabaseUtil db) {
		setDb(db);
	}

	public DatabaseUtil getDb() {
		return db;
	}

	public void setDb(DatabaseUtil db) {
		this.db = db;
	}

	/**
	 * Execultes query and converts result set into JSON string
	 *
	 * @param query
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public String queryToJson(String query) {
		List<Map<String, Object>> listOfMaps = null;
		QueryRunner queryRunner = new QueryRunner();
		try {
			listOfMaps = queryRunner.query(db.getConnection(), query, new MapListHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new DateDeserializer());
		builder.registerTypeAdapter(Date.class, new DateSerializer());
		builder.setPrettyPrinting();
		builder.serializeNulls();
		String json = builder.create().toJson(listOfMaps);
		System.out.println(json);
		return json;
	}

	public String convertToString(Object obj) {
		return obj == null ? null : obj.toString();
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
		GfatmDatabaseUtil.encounterTypes = encounterTypes;
	}

	public void loadEncounterTypes() {
		UtilityCollection.getInstance().setEncounterTypes(new HashMap<Integer, String>());
		HashMap<Integer, String> mappingEncounter = new HashMap<Integer, String>();
		StringBuilder query = new StringBuilder("SELECT encounter_type_id, name FROM encounter_type where retired = 0");
		Object[][] data = db.getTableData(query.toString());
		if (data == null) {
			return;
		}
		for (Object[] element : data) {
			mappingEncounter.put(Integer.parseInt(element[0].toString()), element[1].toString());
		}

		UtilityCollection.getInstance().setEncounterTypes(mappingEncounter);
	}

	/**
	 * Fetch all locations from DB and store into locations
	 */
	public void loadLocations() {
		UtilityCollection.getInstance().setLocations(new ArrayList<Location>());
		List<Location> locations = new ArrayList<Location>();
		StringBuilder query = new StringBuilder();
		query.append(
				"select l.location_id as locationId, l.name, l.parent_location as parentId, l.uuid, (case ifnull(ltfast.location_id, 0) when 0 then 0 else 1 end) as fast, (case ifnull(ltpet.location_id, 0) when 0 then 0 else 1 end) as pet, (case ifnull(ltpmdt.location_id, 0) when 0 then 0 else 1 end) as pmdt, (case ifnull(ltctb.location_id, 0) when 0 then 0 else 1 end) as childhood_tb, (case ifnull(ltcomorb.location_id, 0) when 0 then 0 else 1 end) as comorbidities, ");
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
		Field[] fields = Location.class.getDeclaredFields();
		JSONArray array = new JSONArray(queryToJson(query.toString()));
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			Location location = new Location();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = null;
				try {
					value = json.get(field.getName());
					field.set(location, value);
				} catch (JSONException e) {
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
			locations.add(location);
		}
		UtilityCollection.getInstance().setLocations(locations);
	}

	/**
	 * Fetch all users from DB and store into users
	 */
	public void loadUsers() {
		UtilityCollection.getInstance().setUsers(new ArrayList<User>());
		List<User> users = new ArrayList<User>();
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
		Field[] fields = User.class.getDeclaredFields();
		JSONArray array = new JSONArray(queryToJson(query.toString()));
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			User user = new User();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = null;
				try {
					value = json.get(field.getName());
					field.set(user, value);
				} catch (JSONException e) {
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
			users.add(user);
		}
		UtilityCollection.getInstance().setUsers(users);
	}

	public void loadPatients() {

		UtilityCollection.getInstance().setPatients(new ArrayList<Patient>());
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
				"inner join patient_identifier pi on pi.patient_id =pt.patient_id and pi.identifier_type = 3 and pi.voided =0 ");
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
		Gson gson = new Gson();
		List<Patient> patients = gson.fromJson(jsonString, listType);
		UtilityCollection.getInstance().setPatients(patients);
	}

	/**
	 * Fetch Encounter object by encounter ID This method may be recycle ////
	 *
	 * @param encounterId
	 * @param encounterTypeId
	 * @return
	 */
	public Encounter getEncounter(int encounterId, int encounterTypeId) {

		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.description as encounter_location, pc.value as patient_contact, lc.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
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
		query.append("where e.encounter_id = " + encounterId + " and e.encounter_type = " + encounterTypeId);
		query.append(" and e.voided = 0 ");

		String jsonString = queryToJson(query.toString());
		Gson gson = new Gson();

		Type type = new TypeToken<List<Encounter>>() {
		}.getType();
		List<Encounter> encounter = gson.fromJson(jsonString, type);

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
	public List<Encounter> getEncounters(DateTime from, DateTime to, Integer type) {
		if (from == null || to == null) {
			return null;
		}
		@SuppressWarnings("deprecation")
		String sqlFrom = DateTimeUtil.getSqlDateTime(from.toDate());
		@SuppressWarnings("deprecation")
		String sqlTo = DateTimeUtil.getSqlDateTime(to.toDate());
		StringBuilder filter = new StringBuilder();
		filter.append("where e.voided = 0 and e.date_created between ");
		// filter.append("timestamp('" + sqlFrom + "')");
		filter.append("timestamp('2017-06-01')");
		filter.append(" and ");
		// filter.append("timestamp('" + sqlTo + "')");
		filter.append("timestamp('2017-10-02')");
		filter.append(" and ");
		filter.append("timestampdiff(HOUR, e.date_created, e.encounter_datetime) <= 24");
		if (type != null) {
			filter.append(" and e.encounter_type=" + type);
		}
		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.description as encounter_location, pc.value as patient_contact, lc.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
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
		Gson gson = new Gson();
		Type listType = new TypeToken<ArrayList<Encounter>>() {
		}.getType();
		List<Encounter> encounters = gson.fromJson(jsonString, listType);
		return encounters;
	}

	public Encounter getEncounterByPatientIdentifier(String patientIdentifier, int encounterTypeId) {

		StringBuilder query = new StringBuilder();
		query.append(
				"select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.description as encounter_location, pc.value as patient_contact, lc.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
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
		Gson gson = new Gson();
		Type type = new TypeToken<List<Encounter>>() {
		}.getType();
		List<Encounter> encounter = gson.fromJson(jsonString, type);

		return encounter.get(0);
	}

	public Map<String, Object> getEncounterObservations(Encounter encounter) {
		Map<String, Object> observations;

		StringBuilder query = new StringBuilder();
		query.append(
				"select q.name as obs, concat(ifnull(a.name, ''), ifnull(o.value_datetime, ''), ifnull(o.value_text, ''), ifnull(o.value_numeric, '')) as value from obs as o ");
		query.append(
				"left outer join concept_name as q on q.concept_id = o.concept_id and q.locale = 'en' and q.concept_name_type = 'SHORT' and q.voided = 0 ");
		query.append(
				"left outer join concept_name as a on a.concept_id = o.value_coded and a.locale = 'en' and a.locale_preferred = 1 and a.voided = 0 ");
		query.append("where o.voided = 0 and o.encounter_id = " + encounter.getEncounterId());

		// System.out.println(query);
		Object[][] data = db.getTableData(query.toString());
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

	public Location getLocationById(Integer id) {
		if (UtilityCollection.getInstance().getLocations().isEmpty()) {
			loadLocations();
		}
		for (Location location : UtilityCollection.getInstance().getLocations()) {
			if (location.getLocationId().equals(id)) {
				return location;
			}
		}
		return null;
	}

	// get the location against the location Name
	public Location getLocationByShortCode(String code) {
		// /first we need to load all the locations
		if (UtilityCollection.getInstance().getLocations().isEmpty()) {
			loadLocations();
		}
		for (Location location : UtilityCollection.getInstance().getLocations()) {

			if (location.getName().equals(code)) {
				return location;
			}
		}
		return null;
	}

	public User getUserById(Integer id) {
		// if user data is loaded then first we need to load all the user data
		if (UtilityCollection.getInstance().getUsers().isEmpty()) {
			loadUsers();
		}
		for (User user : UtilityCollection.getInstance().getUsers()) {
			if (user.getUserId().equals(id)) {
				return user;
			}
		}
		return null;
	}

	public String[] getUserRolesByUser(User user) {
		// when we load all the user roles then this code change..

		StringBuilder query = new StringBuilder();
		query.append("select * from user_role ur ");
		query.append("where user_id='" + user.getUserId() + "'");
		String jsonString = queryToJson(query.toString());

		Gson gson = new Gson();
		Type type = new TypeToken<String[]>() {
		}.getType();
		String[] userRoleArray = gson.fromJson(jsonString, type);

		return userRoleArray;
	}

	public List<Contact> LoadAllUsersContact() {
		UtilityCollection.getInstance().setEmailList(new ArrayList<Contact>());
		StringBuilder query = new StringBuilder();
		query.append(
				" select distinct dl.location_id as locationId,dl.location_name as locationName ,pam.email_address as emailAdress,dl.primary_contact as primaryContact,dl.secondary_contact as secondaryContact from person_attribute_merged pam ");
		query.append(" inner join users u on u.person_id = pam.person_id ");
		query.append(" inner join dim_location dl on dl.Site_Supervisor_System_ID = u.system_id ");
		String jsonString = queryToJson(query.toString());
		Type listType = new TypeToken<List<Contact>>() {
		}.getType();
		Gson gson = new Gson();
		List<Contact> emailList = gson.fromJson(jsonString, listType);
		UtilityCollection.getInstance().setEmailList(emailList);
		return UtilityCollection.getInstance().getEmailList();
	}

	public Contact getContactByLocationId(int locationId) {
		if (UtilityCollection.getInstance().getEmailList().isEmpty()) {
			LoadAllUsersContact();
		}
		for (Contact email : UtilityCollection.getInstance().getEmailList()) {
			if (email.getLocationId() == locationId) {
				return email;
			}
		}
		return null;
	}

	public Contact getContactByLocationName(String locationName) {

		if (UtilityCollection.getInstance().getEmailList().isEmpty()) {
			LoadAllUsersContact();
		}
		for (Contact email : UtilityCollection.getInstance().getEmailList()) {
			if (email.getLocationName().equals(locationName)) {
				return email;
			}
		}
		return null;
	}
}
