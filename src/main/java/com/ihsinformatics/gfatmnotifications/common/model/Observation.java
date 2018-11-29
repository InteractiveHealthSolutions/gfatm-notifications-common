package com.ihsinformatics.gfatmnotifications.common.model;

import java.util.Date;

import com.ihsinformatics.util.DateTimeUtil;

public class Observation {
	private int obsId;
	private Integer patientId;
	private Integer conceptId;
	private String conceptName;
	private String conceptShortName;
	private Integer encounterId;
	private String orderId;
	private long obsDatetime;
	private Integer locationId;
	private Double valueNumeric;
	private Boolean valueBoolean;
	private Integer valueCoded;
	private String valueCodedName;
	private Long valueDatetime;
	private String valueText;
	private String uuid;

	public int getObsId() {
		return obsId;
	}

	public void setObsId(int obsId) {
		this.obsId = obsId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
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

	public String getValueCodedName() {
		return valueCodedName;
	}

	public void setValueCodedName(String valueCodedName) {
		this.valueCodedName = valueCodedName;
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
		} else if (getValueCodedName() != null) {
			return getValueCodedName();
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

	public String getConceptShortName() {
		return conceptShortName;
	}

	public void setConceptShortName(String conceptShortName) {
		this.conceptShortName = conceptShortName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return obsId + ", " + patientId + ", " + conceptId + ", " + conceptName + ", " + encounterId + ", " + orderId
				+ ", " + obsDatetime + ", " + locationId + ", " + valueNumeric + ", " + valueBoolean + ", " + valueCoded
				+ ", " + valueCodedName + ", " + valueDatetime + ", " + valueText + ", " + uuid;
	}
}
