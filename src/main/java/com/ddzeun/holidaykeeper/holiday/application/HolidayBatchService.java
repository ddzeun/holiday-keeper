package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.country.domain.Country;
import com.ddzeun.holidaykeeper.country.repository.CountryRepository;
import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayBatchService {

    private final NagerApiClient nagerApiClient;
    private final CountryRepository countryRepository;
    private final HolidayService holidayService;
    private final HolidayYearPolicy holidayYearPolicy;

    public void loadRecentYearsForAllCountries() {

        List<AvailableCountryResponse> apiCountries = nagerApiClient.getAvailableCountries();
        Set<String> savedCountryCodes = countryRepository.findAllCountryCodes();

        List<Country> newCountries = apiCountries.stream()
                .filter(c -> !savedCountryCodes.contains(c.countryCode()))
                .map(c -> new Country(c.countryCode(), c.name()))
                .toList();

        if (!newCountries.isEmpty()) {
            countryRepository.saveAll(newCountries);
        }

        int startYear = holidayYearPolicy.getStartYear();
        int endYear = holidayYearPolicy.getEndYear();

        for (int year = startYear; year <= endYear; year++) {
            int finalYear = year;
            newCountries.forEach(country -> {
                try {
                    holidayService.loadYearForCountry(finalYear, country.getCountryCode());
                    log.info("저장 성공: {}년, {}", finalYear, country.getCountryCode());
                } catch (Exception e) {
                    log.error("저장 실패: {}년, {}, 원인: {}", finalYear, country.getCountryCode(), e.getMessage(), e);}
            });
        }
    }
}