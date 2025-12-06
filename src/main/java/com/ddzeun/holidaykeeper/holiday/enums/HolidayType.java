package com.ddzeun.holidaykeeper.holiday.enums;

public enum HolidayType {
    PUBLIC,
    BANK,
    SCHOOL,
    AUTHORITIES,
    OPTIONAL,
    OBSERVANCE,
    UNKNOWN;

    public static HolidayType from(String value) {
        if (value == null) {
            return UNKNOWN;
        }
        try {
            return HolidayType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
