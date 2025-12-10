package com.ddzeun.holidaykeeper.external.nager;

import com.ddzeun.holidaykeeper.common.exception.ExternalApiException;
import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import com.ddzeun.holidaykeeper.external.nager.dto.HolidayResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles
@Disabled("Nager API 실제 호출 테스트 - 수동 실행용")
class NagerApiClientIntegrationTest {

    @Autowired
    private NagerApiClient nagerApiClient;

    @Test
    @DisplayName("실제 API 호출 - 사용 가능한 국가 목록 조회")
    void getAvailableCountries_realApi() {
        // when
        List<AvailableCountryResponse> result = nagerApiClient.getAvailableCountries();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).anyMatch(c -> c.countryCode().equals("KR"));
        assertThat(result).anyMatch(c -> c.countryCode().equals("US"));

        System.out.println("총 국가 수: " + result.size());
        result.stream()
                .limit(5)
                .forEach(c -> System.out.println(c.countryCode() + " - " + c.name()));
    }

    @Test
    @DisplayName("실제 API 호출 - 특정 연도/국가의 공휴일 조회")
    void getPublicHolidays_realApi() {
        // given
        int year = 2025;
        String countryCode = "KR";

        // when
        List<HolidayResponse> result = nagerApiClient.getPublicHolidays(year, countryCode);

        // then
        assertThat(result).isNotEmpty();

        System.out.println("\n=== " + year + "년 " + countryCode + " 공휴일 ===");
        result.stream()
                .limit(5)
                .forEach(h -> System.out.println(
                        h.date() + " - " + h.localName() + " (" + h.name() + ")"
                ));
    }

    @Test
    @DisplayName("실제 API 호출 - 존재하지 않는 국가 코드로 예외 발생")
    void getPublicHolidays_invalidCountryCode() {
        // given
        int year = 2025;
        String invalidCountryCode = "XX";

        // when & then
        assertThatThrownBy(() -> nagerApiClient.getPublicHolidays(year, invalidCountryCode))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("Nager API 공휴일 조회 실패");
    }
}