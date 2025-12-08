package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.HolidayResponse;
import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final NagerApiClient nagerApiClient;
    private final HolidayRepository holidayRepository;

    @Transactional
    public void loadYearForCountry(int year, String countryCode) {

        List<HolidayResponse> responses = nagerApiClient.getPublicHolidays(year, countryCode);

        List<Holiday> holidays = responses.stream()
                .map(response -> {
                    Set<HolidayType> types = response.types() == null ? new HashSet<>() :
                            response.types().stream()
                                    .map(HolidayType::from)
                                    .collect(Collectors.toSet());

                    List<String> counties = response.counties() == null ? new ArrayList<>() : response.counties();

                    return new Holiday(
                            response.date(),
                            response.localName(),
                            response.name(),
                            response.countryCode(),
                            response.fixed(),
                            response.global(),
                            response.launchYear(),
                            types,
                            counties
                    );
                })
                .collect(Collectors.toList());

        holidayRepository.saveAll(holidays);
    }
}