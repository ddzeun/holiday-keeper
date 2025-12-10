package com.ddzeun.holidaykeeper.holiday.presentation;

import com.ddzeun.holidaykeeper.common.dto.ApiResponse;
import com.ddzeun.holidaykeeper.holiday.application.HolidaySearchService;
import com.ddzeun.holidaykeeper.holiday.application.HolidayService;
import com.ddzeun.holidaykeeper.holiday.application.HolidayYearPolicy;
import com.ddzeun.holidaykeeper.holiday.application.dto.HolidaySearchRequest;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
@Validated
public class HolidaySearchController {

    private final HolidaySearchService holidaySearchService;
    private final HolidayService holidayService;
    private final HolidayYearPolicy holidayYearPolicy;

    @GetMapping
    public ApiResponse<Page<HolidaySearchResponse>> searchHoliday(

            @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다")
            @RequestParam(required = false)
            String countryCode,

            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) HolidayType type,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }

        HolidaySearchRequest request = new HolidaySearchRequest(countryCode, from, to, type);
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        return ApiResponse.ok(result);
    }

    @PostMapping("/refresh")
    public ApiResponse<Void> refreshHoliday(
            @RequestParam int year,
            @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다")
            @RequestParam
            String countryCode
    ) {
        holidayYearPolicy.validateYear(year);
        holidayService.refreshHolidays(year, countryCode);
        return ApiResponse.okMessage("공휴일 데이터가 새로고침되었습니다.");
    }

    @DeleteMapping
    public ApiResponse<Void> deleteHolidays(
            @RequestParam int year,
            @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다")
            @RequestParam String countryCode
    ) {
        holidayYearPolicy.validateYear(year);
        holidayService.deleteHolidays(year, countryCode);
        return ApiResponse.okMessage(year + "년 공휴일 데이터가 삭제되었습니다.");
    }

}
