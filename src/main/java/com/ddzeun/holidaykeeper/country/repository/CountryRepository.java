package com.ddzeun.holidaykeeper.country.repository;

import com.ddzeun.holidaykeeper.country.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query("SELECT c.countryCode FROM Country c")
    Set<String> findAllCountryCodes();
}