package com.ddzeun.holidaykeeper.external.nager;

import com.ddzeun.holidaykeeper.external.nager.dto.HolidayResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Disabled("Nager API 실제 호출 테스트 - 수동 실행")
class NagerApiClientIntegrationTest {

    @Autowired
    NagerApiClient nagerApiClient;

    @Test
    @DisplayName("실제 Nager API 호출 - 공휴일 목록을 조회")
    void callRealPublicHolidaysApi() {

        // given
        int year = 2025;
        String countryCode = "KR";

        // when
        List<HolidayResponse> result = nagerApiClient.getPublicHolidays(year, countryCode);

        // then
        assertThat(result).isNotEmpty();

        result.stream()
                .limit(3)
                .forEach(h -> System.out.println(h.date() + " - " + h.localName()));
    }
}
