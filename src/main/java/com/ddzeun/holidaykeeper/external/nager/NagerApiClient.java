package com.ddzeun.holidaykeeper.external.nager;

import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import com.ddzeun.holidaykeeper.external.nager.dto.HolidayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NagerApiClient {

    private final WebClient nagerWebClient;


    /**
     * 국가 목록 조회(국가 코드, 국가명)
     * @return 국가 코드, 국가명 리스트
     * @throws WebClientResponseException 외부 API 호출 실패 시
     */
    public List<AvailableCountryResponse> getAvailableCountries() {
        return nagerWebClient.get()
                .uri("/AvailableCountries")
                .retrieve()
                .bodyToFlux(AvailableCountryResponse.class)
                .collectList()
                .block();
    }

    /**
     * 특정 연도와 국가 코드에 해당하는 공휴일 목록을 조회
     * @param year 조회할 연도
     * @param countryCode 조회할 국가 코드 (예: KR, US)
     * @return 지정 연도/국가의 공휴일 리스트
     * @throws WebClientResponseException 외부 API 호출 실패 시
     */
    public List<HolidayResponse> getPublicHolidays(int year, String countryCode) {
        return nagerWebClient.get()
                .uri("/PublicHolidays/{year}/{code}", year, countryCode)
                .retrieve()
                .bodyToFlux(HolidayResponse.class)
                .collectList()
                .block();
    }
}
