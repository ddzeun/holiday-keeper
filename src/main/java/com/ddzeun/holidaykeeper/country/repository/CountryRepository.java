package com.ddzeun.holidaykeeper.country.repository;

import com.ddzeun.holidaykeeper.country.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
