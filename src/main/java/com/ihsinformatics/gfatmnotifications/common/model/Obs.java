package com.ihsinformatics.gfatmnotifications.common.model;

import java.util.Date;

import com.ihsinformatics.util.DateTimeUtil;

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
	private Double valueNumeric;
	private Boolean valueBoolean;
	private Integer valueCoded;
	private Long valueDatetime;
	private String valueText;
	private String uuid;

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

	public Double getValueNumeric() {
		return valueNumeric;
	}

	public void setValueNumeric(Double valueNumeric) {
		this.valueNumeric = valueNumeric;
	}

	public Boolean getValueBoolean() {
		return valueBoolean;
	}

	public void setValueBoolean(Boolean valueBoolean) {
		this.valueBoolean = valueBoolean;
	}

	public boolean isValueBoolean() {
		return getValueBoolean();
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

	public Long getValueDatetime() {
		return valueDatetime;
	}

	public void setValueDatetime(Long valueDatetime) {
		this.valueDatetime = valueDatetime;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	/**
	 * Returns any qualifying (non-null) value of the observation
	 * 
	 * @return
	 */
	public Object getValue() {
		if (getValueText() != null) {
			return valueText;
		} else if (getValueDatetime() != null) {
			return DateTimeUtil.toSqlDateTimeString(new Date(valueDatetime));
		} else if (getValueNumeric() != null) {
			return getValueNumeric();
		} else if (getValueCoded() != null) {
			return getValueCoded();
		} else if (getValueBoolean() != null) {
			return getValueBoolean();
		} else
			return null;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
