package com.ddzeun.holidaykeeper.holiday.presentation;

import com.ddzeun.holidaykeeper.common.dto.ApiResponse;
import com.ddzeun.holidaykeeper.holiday.application.HolidaySearchService;
import com.ddzeun.holidaykeeper.holiday.application.HolidayService;
import com.ddzeun.holidaykeeper.holiday.application.HolidayYearPolicy;
import com.ddzeun.holidaykeeper.holiday.application.dto.HolidaySearchRequest;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Holiday API", description = "공휴일 조회 및 관리 API")
public class HolidaySearchController {

    private final HolidaySearchService holidaySearchService;
    private final HolidayService holidayService;
    private final HolidayYearPolicy holidayYearPolicy;

    @GetMapping
    @Operation(
            summary = "공휴일 조회",
            description = "국가 코드, 기간, 공휴일 타입을 기준으로 공휴일을 조회합니다. 결과는 페이징으로 반환됩니다."
    )
    public ApiResponse<Page<HolidaySearchResponse>> searchHoliday(

            @Parameter(description = "국가 코드 (2자리 대문자). 예: KR, US", example = "KR")
            @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다")
            @RequestParam(required = false)
            String countryCode,

            @Parameter(description = "조회 시작일", example = "2024-01-01")
            @RequestParam(required = false) LocalDate from,

            @Parameter(description = "조회 종료일", example = "2024-12-31")
            @RequestParam(required = false) LocalDate to,

            @Parameter(description = "공휴일 타입", example = "PUBLIC")
            @RequestParam(required = false) HolidayType type,

            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }

        HolidaySearchRequest request = new HolidaySearchRequest(countryCode, from, to, type);
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        return ApiResponse.ok(result);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "공휴일 데이터 새로고침",
            description = "지정한 연도와 국가 코드 기준으로 외부 API에서 불러온 공휴일 데이터로 업데이트 합니다."
    )
    public ApiResponse<Void> refreshHoliday(
            @Parameter(description = "조회 연도", example = "2024")
            @RequestParam int year,

            @Parameter(description = "국가 코드 (2자리 대문자)", example = "KR")
            @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다")
            @RequestParam
            String countryCode
    ) {
        holidayYearPolicy.validateYear(year);
        holidayService.refreshHolidays(year, countryCode);
        return ApiResponse.okMessage("공휴일 데이터가 새로고침되었습니다.");
    }

    @DeleteMapping
    @Operation(
            summary = "공휴일 데이터 삭제",
            description = "특정 연도와 국가의 공휴일 데이터를 삭제합니다."
    )
    public ApiResponse<Void> deleteHolidays(
            @Parameter(description = "삭제할 연도", example = "2024")
            @RequestParam int year,

            @Parameter(description = "국가 코드 (2자리 대문자)", example = "KR")
            @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다")
            @RequestParam String countryCode
    ) {
        holidayYearPolicy.validateYear(year);
        holidayService.deleteHolidays(year, countryCode);
        return ApiResponse.okMessage(year + "년 공휴일 데이터가 삭제되었습니다.");
    }
}
