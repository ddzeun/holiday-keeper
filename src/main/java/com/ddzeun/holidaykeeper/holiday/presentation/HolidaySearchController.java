package com.ddzeun.holidaykeeper.holiday.presentation;

import com.ddzeun.holidaykeeper.holiday.application.HolidaySearchService;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidaySearchController {

    private final HolidaySearchService holidaySearchService;

    @GetMapping
    public Page<HolidaySearchResponse> searchHoliday(
            @RequestParam String countryCode,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return holidaySearchService.searchHolidays(countryCode, pageable);
    }
}
