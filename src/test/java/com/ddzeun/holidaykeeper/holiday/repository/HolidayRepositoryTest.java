package com.ddzeun.holidaykeeper.holiday.repository;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HolidayRepositoryTest {

    @Autowired
    HolidayRepository holidayRepository;

    @Test
    @DisplayName("PrePersist 테스트")
    void save_holiday() {
        Holiday h = new Holiday(
                LocalDate.of(2025, 1, 1),
                "새해",
                "New Year",
                "KR",
                true,
                true,
                null,
                HolidayType.PUBLIC,
                "Public"
        );

        Holiday saved = holidayRepository.save(h);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        System.out.println("======== ID = " + saved.getId() + " ========");
        System.out.println("======== created = " + saved.getCreatedAt() + " ========");
        System.out.println("======== updated = " + saved.getUpdatedAt() + " ========");
    }
}
