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
public class Relationship {

	private Integer personA;
	private String aIsToB;
	private Integer personB;
	private String bIsToA;
	private String uuid;

	/**
	 * @param personA
	 * @param aIsToB
	 * @param personB
	 * @param bIsToA
	 * @param uuid
	 */
	public Relationship(Integer personA, String aIsToB, Integer personB, String bIsToA, String uuid) {
		super();
		this.setPersonA(personA);
		this.setaIsToB(aIsToB);
		this.setPersonB(personB);
		this.setbIsToA(bIsToA);
		this.setUuid(uuid);
	}

	/**
	 * @return the personA
	 */
	public Integer getPersonA() {
		return personA;
	}

	/**
	 * @param personA the personA to set
	 */
	public void setPersonA(Integer personA) {
		this.personA = personA;
	}

	/**
	 * @return the aIsToB
	 */
	public String getaIsToB() {
		return aIsToB;
	}

	/**
	 * @param aIsToB the aIsToB to set
	 */
	public void setaIsToB(String aIsToB) {
		this.aIsToB = aIsToB;
	}

	/**
	 * @return the personB
	 */
	public Integer getPersonB() {
		return personB;
	}

	/**
	 * @param personB the personB to set
	 */
	public void setPersonB(Integer personB) {
		this.personB = personB;
	}

	/**
	 * @return the bIsToA
	 */
	public String getbIsToA() {
		return bIsToA;
	}

	/**
	 * @param bIsToA the bIsToA to set
	 */
	public void setbIsToA(String bIsToA) {
		this.bIsToA = bIsToA;
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

}
