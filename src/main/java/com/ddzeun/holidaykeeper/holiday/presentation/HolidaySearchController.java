package com.ddzeun.holidaykeeper.holiday.presentation;

import com.ddzeun.holidaykeeper.holiday.application.HolidaySearchService;
import com.ddzeun.holidaykeeper.holiday.application.HolidayService;
import com.ddzeun.holidaykeeper.holiday.application.dto.HolidaySearchRequest;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidaySearchController {

    private final HolidaySearchService holidaySearchService;
    private final HolidayService holidayService;

    @GetMapping
    public Page<HolidaySearchResponse> searchHoliday(
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) HolidayType type,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        HolidaySearchRequest request = new HolidaySearchRequest(countryCode, from, to, type);

        return holidaySearchService.searchHolidays(request, pageable);
    }

    @PostMapping("/refresh")
    public void refreshHoliday(
            @RequestParam int year,
            @RequestParam String countryCode
    ) {
        holidayService.refreshHolidays(year, countryCode);
    }
}
