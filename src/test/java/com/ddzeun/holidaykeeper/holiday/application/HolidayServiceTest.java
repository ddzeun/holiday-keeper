package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.HolidayResponse;
import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.repository.HolidayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class HolidayServiceTest {

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        NagerApiClient nagerApiClient() {
            return mock(NagerApiClient.class);
        }
    }

    @Autowired
    HolidayService holidayService;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    NagerApiClient nagerApiClient;

    @Test
    @DisplayName("연도/국가별 공휴일 API 응답 저장 테스트")
    void loadYearForCountry_savesMappedEntities() {
        // given
        int year = 2025;
        String countryCode = "KR";

        HolidayResponse dto = new HolidayResponse(
                LocalDate.of(2025, 1, 1),
                "새해",
                "New Year's Day",
                "KR",
                true,
                true,
                List.of("KR-11", "KR-26"),
                1949,
                List.of("Public", "School")
        );

        when(nagerApiClient.getPublicHolidays(year, countryCode))
                .thenReturn(List.of(dto));

        // when
        holidayService.loadYearForCountry(year, countryCode);

        // then
        List<Holiday> results = holidayRepository.findAll();
        assertThat(results).hasSize(1);

        Holiday holiday = results.get(0);

        assertThat(holiday.getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(holiday.getLocalName()).isEqualTo("새해");
        assertThat(holiday.getEnglishName()).isEqualTo("New Year's Day");
        assertThat(holiday.getCountryCode()).isEqualTo("KR");
        assertThat(holiday.getFixed()).isTrue();
        assertThat(holiday.getGlobalHoliday()).isTrue();
        assertThat(holiday.getLaunchYear()).isEqualTo(1949);

        // types: List<String> -> Set<HolidayType>
        assertThat(holiday.getTypes())
                .containsExactlyInAnyOrder(HolidayType.PUBLIC, HolidayType.SCHOOL);

        // counties: List<String>
        assertThat(holiday.getCounties())
                .containsExactlyInAnyOrder("KR-11", "KR-26");
    }
}
