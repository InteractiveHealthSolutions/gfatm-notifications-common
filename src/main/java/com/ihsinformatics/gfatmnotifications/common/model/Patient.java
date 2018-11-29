/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.model;

import com.ihsinformatics.gfatmnotifications.common.Context;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class Patient {

	private Integer personId;
	private String patientIdentifier;
	private String givenName;
	private String lastName;
	private String gender;
	private String birthdate;
	private String estimated;
	private String birthplace;
	private String maritalStatus;
	private String healthCenter;
	private String healthDistrict;
	private String motherName;
	private String primaryContact;
	private String primaryContactOwner;
	private String secondaryContact;
	private String secondaryContactOwner;
	private String ethnicity;
	private String educationLevel;
	private String employmentStatus;
	private String occupation;
	private String motherTongue;
	private String incomeClass;
	private String nationalID;
	private String nationalIDOwner;
	private String guardianName;
	private String treatmentSupporter;
	private String otherIdentificationNumber;
	private String transgender;
	private String patientType;
	private Boolean dead;
	private String creator;
	private String address1;
	private String address2;
	private String district;
	private String cityVillage;
	private String country;
	private String landmark;
	private long dateCreated;
	private String uuid;

	public Patient() {
	}

	/**
	 * @return the personId
	 */
	public Integer getPersonId() {
		return personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param givenName the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the birthdate
	 */
	public String getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the estimated
	 */
	public String getEstimated() {
		return estimated;
	}

	/**
	 * @param estimated the estimated to set
	 */
	public void setEstimated(String estimated) {
		this.estimated = estimated;
	}

	/**
	 * @return the birthplace
	 */
	public String getBirthplace() {
		return birthplace;
	}

	/**
	 * @param birthplace the birthplace to set
	 */
	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	/**
	 * @return the maritalStatus
	 */
	public String getMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * @param maritalStatus the maritalStatus to set
	 */
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
	 * @return the healthCenter
	 */
	public String getHealthCenter() {
		if (healthCenter == null || healthCenter.equals("")) {
			return null;
		} else {
			return Context.getLocationById(Integer.parseInt(healthCenter.trim()), Context.getOpenmrsDb()).getDescription()
					.trim();
		} 
	}
	
	/**
	 * @return the healthCenter
	 */
	public String getHealthCenterId() {
		if (healthCenter == null || healthCenter.equals("")) {
			return null;
		} else {
			return healthCenter.trim();
		} 
	}

	/**
	 * @param healthCenter the healthCenter to set
	 */
	public void setHealthCenter(String healthCenter) {
		this.healthCenter = healthCenter;
	}

	/**
	 * @return the healthDistrict
	 */
	public String getHealthDistrict() {
		return healthDistrict;
	}

	/**
	 * @param healthDistrict the healthDistrict to set
	 */
	public void setHealthDistrict(String healthDistrict) {
		this.healthDistrict = healthDistrict;
	}

	/**
	 * @return the motherName
	 */
	public String getMotherName() {
		return motherName;
	}

	/**
	 * @param motherName the motherName to set
	 */
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	/**
	 * @return the primaryContact
	 */
	public String getPrimaryContact() {
		return primaryContact;
	}

	/**
	 * @param primaryContact the primaryContact to set
	 */
	public void setPrimaryContact(String primaryContact) {
		this.primaryContact = primaryContact;
	}

	/**
	 * @return the primaryContactOwner
	 */
	public String getPrimaryContactOwner() {
		return primaryContactOwner;
	}

	/**
	 * @param primaryContactOwner the primaryContactOwner to set
	 */
	public void setPrimaryContactOwner(String primaryContactOwner) {
		this.primaryContactOwner = primaryContactOwner;
	}

	/**
	 * @return the secondaryContact
	 */
	public String getSecondaryContact() {
		return secondaryContact;
	}

	/**
	 * @param secondaryContact the secondaryContact to set
	 */
	public void setSecondaryContact(String secondaryContact) {
		this.secondaryContact = secondaryContact;
	}

	/**
	 * @return the secondaryContactOwner
	 */
	public String getSecondaryContactOwner() {
		return secondaryContactOwner;
	}

	/**
	 * @param secondaryContactOwner the secondaryContactOwner to set
	 */
	public void setSecondaryContactOwner(String secondaryContactOwner) {
		this.secondaryContactOwner = secondaryContactOwner;
	}

	/**
	 * @return the ethnicity
	 */
	public String getEthnicity() {
		return ethnicity;
	}

	/**
	 * @param ethnicity the ethnicity to set
	 */
	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	/**
	 * @return the educationLevel
	 */
	public String getEducationLevel() {
		return educationLevel;
	}

	/**
	 * @param educationLevel the educationLevel to set
	 */
	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}

	/**
	 * @return the employmentStatus
	 */
	public String getEmploymentStatus() {
		return employmentStatus;
	}

	/**
	 * @param employmentStatus the employmentStatus to set
	 */
	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	/**
	 * @return the occupation
	 */
	public String getOccupation() {
		return occupation;
	}

	/**
	 * @param occupation the occupation to set
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	/**
	 * @return the motherTongue
	 */
	public String getMotherTongue() {
		return motherTongue;
	}

	/**
	 * @param motherTongue the motherTongue to set
	 */
	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}

	/**
	 * @return the incomeClass
	 */
	public String getIncomeClass() {
		return incomeClass;
	}

	/**
	 * @param incomeClass the incomeClass to set
	 */
	public void setIncomeClass(String incomeClass) {
		this.incomeClass = incomeClass;
	}

	/**
	 * @return the nationalID
	 */
	public String getNationalID() {
		return nationalID;
	}

	/**
	 * @param nationalID the nationalID to set
	 */
	public void setNationalID(String nationalID) {
		this.nationalID = nationalID;
	}

	/**
	 * @return the nationalIDOwner
	 */
	public String getNationalIDOwner() {
		return nationalIDOwner;
	}

	/**
	 * @param nationalIDOwner the nationalIDOwner to set
	 */
	public void setNationalIDOwner(String nationalIDOwner) {
		this.nationalIDOwner = nationalIDOwner;
	}

	/**
	 * @return the guardianName
	 */
	public String getGuardianName() {
		return guardianName;
	}

	/**
	 * @param guardianName the guardianName to set
	 */
	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	/**
	 * @return the treatmentSupporter
	 */
	public String getTreatmentSupporter() {
		return treatmentSupporter;
	}

	/**
	 * @param treatmentSupporter the treatmentSupporter to set
	 */
	public void setTreatmentSupporter(String treatmentSupporter) {
		this.treatmentSupporter = treatmentSupporter;
	}

	/**
	 * @return the otherIdentificationNumber
	 */
	public String getOtherIdentificationNumber() {
		return otherIdentificationNumber;
	}

	/**
	 * @param otherIdentificationNumber the otherIdentificationNumber to set
	 */
	public void setOtherIdentificationNumber(String otherIdentificationNumber) {
		this.otherIdentificationNumber = otherIdentificationNumber;
	}

	/**
	 * @return the transgender
	 */
	public String getTransgender() {
		return transgender;
	}

	/**
	 * @param transgender the transgender to set
	 */
	public void setTransgender(String transgender) {
		this.transgender = transgender;
	}

	/**
	 * @return the patientType
	 */
	public String getPatientType() {
		return patientType;
	}

	/**
	 * @param patientType the patientType to set
	 */
	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}

	/**
	 * @return the dead
	 */
	public Boolean isDead() {
		return dead;
	}

	/**
	 * @param dead the dead to set
	 */
	public void setDead(Boolean dead) {
		this.dead = dead;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the dateCreated
	 */
	public long getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}

	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}

	/**
	 * @return the cityVillage
	 */
	public String getCityVillage() {
		return cityVillage;
	}

	/**
	 * @param cityVillage the cityVillage to set
	 */
	public void setCityVillage(String cityVillage) {
		this.cityVillage = cityVillage;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the landmark
	 */
	public String getLandmark() {
		return landmark;
	}

	/**
	 * @param landmark the landmark to set
	 */
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	/**
	 * @return the patientIdentifier
	 */
	public String getPatientIdentifier() {
		return patientIdentifier;
	}

	/**
	 * @param patientIdentifier the patientIdentifier to set
	 */
	public void setPatientIdentifier(String patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the name of patient
	 */
	public String getFullName() {
		return (givenName == null ? "" : givenName) + " " + (lastName == null ? "" : lastName);
	}
}
