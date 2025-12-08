package com.ddzeun.holidaykeeper.holiday.repository;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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
                Set.of(HolidayType.PUBLIC, HolidayType.AUTHORITIES),
                List.of("KR-11")
        );

        Holiday saved = holidayRepository.save(h);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        assertThat(saved.getTypes()).contains(HolidayType.PUBLIC);
        assertThat(saved.getCounties()).contains("KR-11");

        System.out.println("======== ID = " + saved.getId() + " ========");
        System.out.println("======== Types = " + saved.getTypes() + " ========");
        System.out.println("======== Counties = " + saved.getCounties() + " ========");
        System.out.println("======== created = " + saved.getCreatedAt() + " ========");
        System.out.println("======== updated = " + saved.getUpdatedAt() + " ========");
    }
}
