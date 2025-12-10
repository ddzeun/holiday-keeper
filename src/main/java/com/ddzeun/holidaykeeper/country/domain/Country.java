package com.ddzeun.holidaykeeper.country.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Country {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4, nullable = false, unique = true)
    private String countryCode;

    @Column(nullable = false)
    private String name;

    public Country(String countryCode, String name) {
        this.countryCode = countryCode;
        this.name = name;
    }
}