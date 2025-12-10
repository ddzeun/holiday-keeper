package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.holiday.application.dto.HolidaySearchRequest;
import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import com.ddzeun.holidaykeeper.holiday.repository.HolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(HolidaySearchService.class)
class HolidaySearchServiceTest {

    @Autowired
    private HolidaySearchService holidaySearchService;

    @Autowired
    private HolidayRepository holidayRepository;

    @BeforeEach
    void setUp() {

        // 테스트 데이터 초기화
        holidayRepository.deleteAll();

        // KR 공휴일 데이터
        Holiday newYear = new Holiday(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                "KR",
                true,
                true,
                1949,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );

        Holiday buddha = new Holiday(
                LocalDate.of(2025, 5, 5),
                "부처님 오신 날",
                "Buddha's Birthday",
                "KR",
                false,
                true,
                null,
                Set.of(HolidayType.PUBLIC, HolidayType.OBSERVANCE),
                List.of()
        );

        // US 공휴일 데이터
        Holiday usNewYear = new Holiday(
                LocalDate.of(2025, 1, 1),
                "New Year's Day",
                "New Year's Day",
                "US",
                true,
                true,
                null,
                Set.of(HolidayType.PUBLIC, HolidayType.BANK),
                List.of()
        );

        holidayRepository.saveAll(List.of(newYear, buddha, usNewYear));
    }

    @Test
    @DisplayName("국가 코드로 필터링하여 조회 테스트")
    void searchByCountryCode() {

        // given
        HolidaySearchRequest request = new HolidaySearchRequest("KR", null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .allMatch(h -> h.countryCode().equals("KR"));
    }

    @Test
    @DisplayName("날짜 범위로 필터링하여 조회 테스트")
    void searchByDateRange() {

        // given
        LocalDate from = LocalDate.of(2025, 5, 1);
        LocalDate to = LocalDate.of(2025, 5, 31);
        HolidaySearchRequest request = new HolidaySearchRequest(null, from, to, null);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).localName()).isEqualTo("부처님 오신 날");
    }

    @Test
    @DisplayName("공휴일 타입으로 필터링하여 조회 테스트")
    void searchByType() {
        // given
        HolidaySearchRequest request = new HolidaySearchRequest(null, null, null, HolidayType.BANK);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).countryCode()).isEqualTo("US");
        assertThat(result.getContent().get(0).types()).contains(HolidayType.BANK);
    }

    @Test
    @DisplayName("복합 조건으로 필터링하여 조회 테스트")
    void searchByMultipleConditions() {

        // given
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);
        HolidaySearchRequest request = new HolidaySearchRequest("KR", from, to, HolidayType.PUBLIC);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .allMatch(h -> h.countryCode().equals("KR"))
                .allMatch(h -> h.types().contains(HolidayType.PUBLIC));
    }

    @Test
    @DisplayName("조건 없이 전체 조회 테스트")
    void searchAll() {

        // given
        HolidaySearchRequest request = new HolidaySearchRequest(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}