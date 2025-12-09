package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.HolidayResponse;
import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import com.ddzeun.holidaykeeper.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        holidayRepository.saveAll(holidays);
    }

    @Transactional
    public void refreshHolidays(int year, String countryCode) {

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<HolidayResponse> apiResponses = nagerApiClient.getPublicHolidays(year, countryCode);
        List<Holiday> dbHolidays = holidayRepository.findByYearAndCountry(countryCode, start, end);

        Map<String, Holiday> dbMap = dbHolidays.stream()
                .collect(Collectors.toMap(
                        h -> buildKey(h.getCountryCode(), h.getDate(), h.getEnglishName()),
                        h -> h
                ));


        List<Holiday> upsertList = new ArrayList<>();

        for (HolidayResponse res : apiResponses) {
            String key = buildKey(countryCode, res.date(), res.name());
            Holiday existing = dbMap.get(key);

            Set<HolidayType> types = mapToHolidayTypes(res.types());
            List<String> counties = mapToCounties(res.counties());

            if (existing != null) {

                existing.update(
                        res.localName(),
                        res.name(),
                        res.fixed(),
                        res.global(),
                        res.launchYear(),
                        types,
                        counties
                );
                dbMap.remove(key);
            } else {
                Holiday newHoliday = Holiday.create(
                        res.date(),
                        countryCode,
                        res.localName(),
                        res.name(),
                        res.fixed(),
                        res.global(),
                        res.launchYear(),
                        types,
                        counties
                );
                upsertList.add(newHoliday);
            }
        }

        Collection<Holiday> toDelete = dbMap.values();
        if (!toDelete.isEmpty()) {
            holidayRepository.deleteAll(toDelete);
        }

        holidayRepository.saveAll(upsertList);
    }

    private String buildKey(String countryCode, LocalDate date, String englishName) {
        return countryCode + "|" + date + "|" + englishName;
    }

    private Holiday mapToEntity(HolidayResponse response) {
        return new Holiday(
                response.date(),
                response.localName(),
                response.name(),
                response.countryCode(),
                response.fixed(),
                response.global(),
                response.launchYear(),
                mapToHolidayTypes(response.types()),
                mapToCounties(response.counties())
        );
    }

    private Set<HolidayType> mapToHolidayTypes(List<String> types) {
        if (types == null) {
            return new HashSet<>();
        }
        return types.stream()
                .map(HolidayType::from)
                .collect(Collectors.toSet());
    }

    private List<String> mapToCounties(List<String> counties) {
        return counties == null ? new ArrayList<>() : counties;
    }
}