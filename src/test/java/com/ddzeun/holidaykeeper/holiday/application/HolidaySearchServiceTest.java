package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.holiday.application.dto.HolidaySearchRequest;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
class HolidaySearchServiceTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public HolidayBatchService holidayBatchService() {
            return mock(HolidayBatchService.class);
        }
    }

    @Autowired
    HolidaySearchService holidaySearchService;

    @Test
    @DisplayName("국가 코드 필터링 테스트")
    void search_by_country() {
        // given
        HolidaySearchRequest request = new HolidaySearchRequest("KR", null, null, null);
        Pageable pageable = PageRequest.of(0, 100);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isGreaterThan(0);

        int i = 1;
        for (HolidaySearchResponse response : result) {
            assertThat(response.countryCode()).isEqualTo("KR");

            System.out.println(i++ + ": " + response);
        }
    }

    @Test
    @DisplayName("기간 필터링 테스트")
    void search_by_date_range() {

        // given
        LocalDate from = LocalDate.of(2025, 5, 1);
        LocalDate to = LocalDate.of(2025, 5, 31);

        HolidaySearchRequest request = new HolidaySearchRequest("KR", from, to, null);
        Pageable pageable = PageRequest.of(0, 100);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isGreaterThan(0);

        int i = 1;
        for (HolidaySearchResponse response : result) {
            assertThat(response.date()).isAfterOrEqualTo(from);
            assertThat(response.date()).isBeforeOrEqualTo(to);

            System.out.println(i++ + ": " + response.localName() + " (" + response.date() + ")");
        }
    }

    @Test
    @DisplayName("타입 필터링 테스트")
    void search_by_type() {

        // given
        HolidayType searchType = HolidayType.PUBLIC;

        HolidaySearchRequest request = new HolidaySearchRequest("KR", null, null, searchType);
        Pageable pageable = PageRequest.of(0, 100);

        // when
        Page<HolidaySearchResponse> result = holidaySearchService.searchHolidays(request, pageable);

        // then
        assertThat(result.getTotalElements()).isGreaterThan(0);

        int i = 1;
        for (HolidaySearchResponse response : result) {
            assertThat(response.types()).contains(searchType);

            System.out.println(i++ + ": " + response.localName() + " Types=" + response.types());
        }
    }
}