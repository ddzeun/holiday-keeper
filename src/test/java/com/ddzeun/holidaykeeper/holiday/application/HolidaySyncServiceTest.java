package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.country.domain.Country;
import com.ddzeun.holidaykeeper.country.repository.CountryRepository;
import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class HolidaySyncServiceTest {

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        NagerApiClient nagerApiClient() {
            return mock(NagerApiClient.class);
        }

        @Bean
        @Primary
        HolidayService holidayService() {
            return mock(HolidayService.class);
        }
    }

    @Autowired
    HolidaySyncService holidaySyncService;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    NagerApiClient nagerApiClient;
    @Autowired
    HolidayService holidayService;

    @BeforeEach
    void setUp() {
        countryRepository.deleteAll();
    }

    @Test
    @DisplayName("DB에 없는 국가 저장 및 HolidayService 호출 테스트")
    void loadRecentYearsForAllCountries_onlyNewCountriesProcessed() {
        int endYear = LocalDate.now().getYear();
        int startYear = endYear - 5;

        when(nagerApiClient.getAvailableCountries())
                .thenReturn(List.of(
                        new AvailableCountryResponse("KR", "South Korea"),
                        new AvailableCountryResponse("US", "United States")
                ));

        countryRepository.save(new Country("KR", "South Korea"));

        holidaySyncService.loadRecentYearsForAllCountries();

        var codes = countryRepository.findAllCountryCodes();
        assertThat(codes).contains("KR", "US");

        for (int year = startYear; year <= endYear; year++) {
            verify(holidayService).loadYearForCountry(year, "US");
        }

        verify(holidayService, never()).loadYearForCountry(anyInt(), eq("KR"));
    }

    @Test
    @DisplayName("모든 국가가 이미 DB에 있을 경우 Service 호출 테스트")
    void loadRecentYearsForAllCountries_whenNoNewCountries_doNothing() {

        // given
        countryRepository.save(new Country("KR", "South Korea"));
        countryRepository.save(new Country("US", "United States"));

        when(nagerApiClient.getAvailableCountries())
                .thenReturn(List.of(
                        new AvailableCountryResponse("KR", "South Korea"),
                        new AvailableCountryResponse("US", "United States")
                ));

        // when
        holidaySyncService.loadRecentYearsForAllCountries();

        // then
        verify(holidayService, never()).loadYearForCountry(anyInt(), anyString());
    }
}
