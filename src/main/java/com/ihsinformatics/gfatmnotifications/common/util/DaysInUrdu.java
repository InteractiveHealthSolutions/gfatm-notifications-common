package com.ihsinformatics.gfatmnotifications.common.util;

public enum DaysInUrdu {


	SATURDAY("hafta"),SUNDAY("itwar"),MONDAY("peer"),TUESDAY("mangal"),WEDNESDAY("budh"),THURSDAY("jummayraat"),FRIDAY("jumma");
	
		

	private final String key;
	
	private DaysInUrdu(String key) {
		this.key = key;
	}
	
	public boolean equalsName(String otherName) {
		// (otherName == null) check is not needed because name.equals(null) returns false 
		return key.equalsIgnoreCase(otherName);
	}
	
	public String toString() {
		return this.key;
	}
}
