package com.ddzeun.holidaykeeper.holiday.application.dto;

import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public record HolidaySearchRequest(
        String countryCode,
        LocalDate from,
        LocalDate to,
        HolidayType type
) {}
