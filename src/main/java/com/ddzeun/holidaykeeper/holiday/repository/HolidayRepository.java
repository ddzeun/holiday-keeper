package com.ddzeun.holidaykeeper.holiday.repository;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    Page<Holiday> findByCountryCode(String countryCode, Pageable pageable);
}
