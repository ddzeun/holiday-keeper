package com.ddzeun.holidaykeeper.holiday.repository;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long>, JpaSpecificationExecutor<Holiday> {

    @Query("""
            SELECT  h
            FROM    Holiday h
            WHERE   h.countryCode = :countryCode
            AND     h.date BETWEEN :start AND :end
            """)
    List<Holiday> findByYearAndCountry(
            @Param("countryCode") String countryCode,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

}
