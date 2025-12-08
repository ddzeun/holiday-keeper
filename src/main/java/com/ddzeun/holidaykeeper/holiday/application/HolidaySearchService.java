package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import com.ddzeun.holidaykeeper.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidaySearchService {

    private final HolidayRepository holidayRepository;


    public Page<HolidaySearchResponse> searchHolidays(String countryCode, Pageable pageable) {

        Page<Holiday> page = holidayRepository.findByCountryCode(countryCode, pageable);

        return page.map(HolidaySearchResponse::from);
    }
}
