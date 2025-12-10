package com.ddzeun.holidaykeeper.external.nager;

import com.ddzeun.holidaykeeper.common.exception.ExternalApiException;
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
     * @throws ExternalApiException 외부 API 호출 실패 시
     */
    public List<AvailableCountryResponse> getAvailableCountries() {
        try {
            return nagerWebClient.get()
                    .uri("/AvailableCountries")
                    .retrieve()
                    .bodyToFlux(AvailableCountryResponse.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            throw new ExternalApiException(
                    "Nager API 국가 목록 조회 실패 - status: " + ex.getStatusCode().value(),
                    ex
            );
        } catch (Exception ex) {
            throw new ExternalApiException(
                    "Nager API 국가 목록 조회 중 알 수 없는 오류가 발생했습니다.",
                    ex
            );
        }
    }

    /**
     * 특정 연도와 국가 코드에 해당하는 공휴일 목록을 조회
     * @param year 조회할 연도
     * @param countryCode 조회할 국가 코드 (예: KR, US)
     * @return 지정 연도/국가의 공휴일 리스트
     * @throws ExternalApiException 외부 API 호출 실패 시
     */
    public List<HolidayResponse> getPublicHolidays(int year, String countryCode) {
        try {
            return nagerWebClient.get()
                    .uri("/PublicHolidays/{year}/{code}", year, countryCode)
                    .retrieve()
                    .bodyToFlux(HolidayResponse.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            throw new ExternalApiException(
                    "Nager API 공휴일 조회 실패 - year: " + year
                            + ", country: " + countryCode
                            + ", status: " + ex.getStatusCode().value(),
                    ex
            );
        } catch (Exception ex) {
            throw new ExternalApiException(
                    "Nager API 공휴일 조회 중 오류가 발생했습니다: ",
                    ex
            );
        }
    }
}
