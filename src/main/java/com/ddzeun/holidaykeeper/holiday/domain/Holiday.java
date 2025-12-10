package com.ddzeun.holidaykeeper.holiday.domain;

import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "holiday", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_holiday_country_date_local",
                columnNames = {"country_code", "date", "local_name"}
        )
})
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String localName;

    @Column(nullable = false)
    private String englishName;

    @Column(nullable = false, length = 4)
    private String countryCode;

    private Boolean fixed;
    private Boolean globalHoliday;
    private Integer launchYear;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "holiday_types",
            joinColumns = @JoinColumn(name = "holiday_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "type_name")
    private Set<HolidayType> types = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "holiday_counties",
            joinColumns = @JoinColumn(name = "holiday_id")
    )
    @Column(name = "county_code")
    private List<String> counties = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Holiday() {}

    public Holiday(LocalDate date,
                   String localName,
                   String englishName,
                   String countryCode,
                   Boolean fixed,
                   Boolean globalHoliday,
                   Integer launchYear,
                   Set<HolidayType> types,
                   List<String> counties) {

        this.date = date;
        this.localName = localName;
        this.englishName = englishName;
        this.countryCode = countryCode;
        this.fixed = fixed;
        this.globalHoliday = globalHoliday;
        this.launchYear = launchYear;
        this.types = types;
        this.counties = counties;
    }

    public static Holiday create(
            LocalDate date,
            String countryCode,
            String localName,
            String englishName,
            boolean fixed,
            boolean global,
            Integer launchYear,
            Set<HolidayType> types,
            List<String> counties
    ) {
        Holiday holiday = new Holiday();
        holiday.date = date;
        holiday.countryCode = countryCode;
        holiday.localName = localName;
        holiday.englishName = englishName;
        holiday.fixed = fixed;
        holiday.globalHoliday = global;
        holiday.launchYear = launchYear;
        holiday.types = types;
        holiday.counties = counties;
        return holiday;
    }

    public void update(
            String localName,
            String englishName,
            boolean fixed,
            boolean global,
            Integer launchYear,
            Set<HolidayType> types,
            List<String> counties
    ) {
        this.localName = localName;
        this.englishName = englishName;
        this.fixed = fixed;
        this.globalHoliday = global;
        this.launchYear = launchYear;
        this.types = types;
        this.counties = counties;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
