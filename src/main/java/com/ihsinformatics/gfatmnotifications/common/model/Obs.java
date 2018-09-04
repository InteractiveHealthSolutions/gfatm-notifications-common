package com.ihsinformatics.gfatmnotifications.common.model;

public class Obs {
	/**
	 * this field add according to needs .. obs_group_id accession_number
	 * value_group_id value_coded_name_id value_drug value_datetime value_numeric
	 * value_modifier value_text value_complex comments creator date_created voided
	 * voided_by date_voided void_reason uuid previous_version
	 * form_namespace_and_path
	 */
	private int obsId;
	private Integer personId;
	private Integer conceptId;
	private Integer encounterId;
	private String orderId;
	private long obsDatetime;
	private Integer locationId;
	private boolean valueBoolean;
	private Integer valueCoded;

	public int getObsId() {
		return obsId;
	}

	public void setObsId(int obsId) {
		this.obsId = obsId;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	public Integer getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public long getObsDatetime() {
		return obsDatetime;
	}

	public void setObsDatetime(long obsDatetime) {
		this.obsDatetime = obsDatetime;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public boolean isValueBoolean() {
		return valueBoolean;
	}

	public void setValueBoolean(boolean valueBoolean) {
		this.valueBoolean = valueBoolean;
	}

	public Integer getValueCoded() {
		return valueCoded;
	}

	public void setValueCoded(Integer valueCoded) {
		this.valueCoded = valueCoded;
	}

}
