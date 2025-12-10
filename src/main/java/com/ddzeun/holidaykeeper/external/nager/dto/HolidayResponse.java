package com.ddzeun.holidaykeeper.external.nager.dto;

import java.time.LocalDate;
import java.util.List;

public record HolidayResponse(
        LocalDate date,
        String localName,
        String name,
        String countryCode,
        Boolean fixed,
        Boolean global,
        List<String> counties,
        Integer launchYear,
        List<String> types
) {}
