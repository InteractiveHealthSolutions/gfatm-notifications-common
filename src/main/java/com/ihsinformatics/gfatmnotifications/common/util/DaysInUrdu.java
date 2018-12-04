package com.ihsinformatics.gfatmnotifications.common.util;

public enum DaysInUrdu {

	SATURDAY("Hafta"), SUNDAY("Itwar"), MONDAY("Peer"), TUESDAY("Mangal"), WEDNESDAY("Budh"), THURSDAY("Jummayraat"),
	FRIDAY("Jumma");

	private final String key;

	private DaysInUrdu(String key) {
		this.key = key;
	}

	public boolean equalsName(String otherName) {
		return key.equalsIgnoreCase(otherName);
	}

	@Override
	public String toString() {
		return this.key;
	}
}
