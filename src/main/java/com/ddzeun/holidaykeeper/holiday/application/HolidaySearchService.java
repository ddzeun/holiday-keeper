package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.holiday.application.dto.HolidaySearchRequest;
import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.presentation.dto.HolidaySearchResponse;
import com.ddzeun.holidaykeeper.holiday.repository.HolidayRepository;
import com.ddzeun.holidaykeeper.holiday.repository.HolidaySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidaySearchService {

    private final HolidayRepository holidayRepository;

    public Page<HolidaySearchResponse> searchHolidays(
            HolidaySearchRequest request,
            Pageable pageable
    ) {

        Specification<Holiday> spec = Specification.where(null);

        if (request.countryCode() != null) {
            spec = spec.and(HolidaySpecification.equalCountryCode(request.countryCode()));
        }
        if (request.from() != null) {
            spec = spec.and(HolidaySpecification.greaterThanOrEqualDate(request.from()));
        }
        if (request.to() != null) {
            spec = spec.and(HolidaySpecification.lessThanOrEqualDate(request.to()));
        }
        if (request.type() != null) {
            spec = spec.and(HolidaySpecification.equalType(request.type()));
        }

        Page<Holiday> page = holidayRepository.findAll(spec, pageable);

        return page.map(HolidaySearchResponse::from);
    }
}