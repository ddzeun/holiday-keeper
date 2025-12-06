package com.ddzeun.holidaykeeper.holiday.domain;

import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "holiday")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HolidayType type;

    @Column(name = "raw_types")
    private String rawTypes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Holiday() {
    }

    public Holiday(LocalDate date,
                   String localName,
                   String englishName,
                   String countryCode,
                   Boolean fixed,
                   Boolean globalHoliday,
                   Integer launchYear,
                   HolidayType type,
                   String rawTypes) {

        this.date = date;
        this.localName = localName;
        this.englishName = englishName;
        this.countryCode = countryCode;
        this.fixed = fixed;
        this.globalHoliday = globalHoliday;
        this.launchYear = launchYear;
        this.type = type;
        this.rawTypes = rawTypes;
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
