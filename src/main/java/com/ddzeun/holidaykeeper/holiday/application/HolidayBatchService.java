package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.country.domain.Country;
import com.ddzeun.holidaykeeper.country.repository.CountryRepository;
import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
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
                    System.out.println("저장 성공: " + finalYear + "년, " + country.getCountryCode());
                } catch (Exception e) {
                    System.err.println("저장 실패: " + finalYear + "년, " + country.getCountryCode());
                }
            });
        }
    }
}