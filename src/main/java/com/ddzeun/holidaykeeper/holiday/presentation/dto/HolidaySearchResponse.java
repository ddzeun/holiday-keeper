package com.ddzeun.holidaykeeper.holiday.presentation.dto;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record HolidaySearchResponse(
        LocalDate date,
        String localName,
        String englishName,
        String countryCode,
        Boolean fixed,
        Boolean globalHoliday,
        Integer launchYear,
        Set<HolidayType> types,
        List<String> counties
) {
    public static HolidaySearchResponse from(Holiday holiday) {
        return new HolidaySearchResponse(
                holiday.getDate(),
                holiday.getLocalName(),
                holiday.getEnglishName(),
                holiday.getCountryCode(),
                holiday.getFixed(),
                holiday.getGlobalHoliday(),
                holiday.getLaunchYear(),
                holiday.getTypes(),
                holiday.getCounties()
        );
    }
}