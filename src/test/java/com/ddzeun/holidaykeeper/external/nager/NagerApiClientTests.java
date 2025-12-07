package com.ddzeun.holidaykeeper.external.nager;

import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import com.ddzeun.holidaykeeper.external.nager.dto.HolidayResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NagerApiClientTests {

    @Autowired
    NagerApiClient nagerApiClient;

    @Test
    @DisplayName("국가 목록 조회 API 요청 테스트")
    void getAvailableCountries() {
        // when
        List<AvailableCountryResponse> result = nagerApiClient.getAvailableCountries();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        result.stream()
                .forEach(c -> System.out.println("======== " + c.countryCode() + " - " + c.name() + " ========"));
    }

    @Test
    @DisplayName("연도/국가별 공휴일 조회 API 요청 테스트")
    void getPublicHolidays() {

        // given
        int year = 2026;
        String countryCode = "KR";

        // when
        List<HolidayResponse> result = nagerApiClient.getPublicHolidays(year, countryCode);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        result.stream()
                .forEach(h -> System.out.println("======== " + h.date() + " - " + h.localName() + " ========"));
    }

    @Test
    @DisplayName("DTO 구조 검증 테스트 - 국가 목록")
    void getAvailableCountries_DTO() {
        // when
        List<AvailableCountryResponse> result = nagerApiClient.getAvailableCountries();

        // then
        assertThat(result).isNotEmpty();

        AvailableCountryResponse kr = result.stream()
                .filter(c -> c.countryCode().equals("KR"))
                .findFirst()
                .orElse(null);

        System.out.println(kr);

        assertThat(kr).isNotNull();
        assertThat(kr.name()).isEqualTo("South Korea");
    }

    @Test
    @DisplayName("DTO 구조 검증 테스트 - 공휴일 목록")
    void getPublicHolidays_DTO() {
        // when
        List<HolidayResponse> result = nagerApiClient.getPublicHolidays(2025, "KR");

        // then
        assertThat(result).isNotEmpty();

        HolidayResponse newYear = result.stream()
                .filter(h -> h.date().equals(LocalDate.of(2025, 1, 1)))
                .findFirst()
                .orElse(null);

        System.out.println(newYear);

        assertThat(newYear).isNotNull();
        assertThat(newYear.localName()).isEqualTo("새해");
        assertThat(newYear.countryCode()).isEqualTo("KR");
        assertThat(newYear.types()).contains("Public");
    }

}

