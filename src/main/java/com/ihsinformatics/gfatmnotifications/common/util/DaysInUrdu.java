package com.ihsinformatics.gfatmnotifications.common.util;

public enum DaysInUrdu {

	SATURDAY("Hafta"), SUNDAY("Itwar"), MONDAY("Peer"), TUESDAY("Mangal"), WEDNESDAY("Budh"), THURSDAY("Jummayraat"),
	FRIDAY("Jumma");

	private String key;
	 
	DaysInUrdu(String key) {
        this.key = key;
    }
 
    public String getValue() {
        return key;
    }
}
