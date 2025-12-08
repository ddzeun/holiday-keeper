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

    private static final int END_YEAR = LocalDate.now().getYear();
    private static final int START_YEAR = END_YEAR - 5;

    private final NagerApiClient nagerApiClient;
    private final CountryRepository countryRepository;
    private final HolidayService holidayService;

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
        for (int year = START_YEAR; year <= END_YEAR; year++) {
            int finalYear = year;
            newCountries.forEach(country -> {
                try {
                    holidayService.loadYearForCountry(finalYear, country.getCountryCode());
                } catch (Exception e) {
                    System.err.println("실패: " + finalYear + " " + country.getCountryCode());
                }
            });
        }
    }
}