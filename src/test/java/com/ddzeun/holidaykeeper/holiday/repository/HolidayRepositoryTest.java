package com.ddzeun.holidaykeeper.holiday.repository;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class HolidayRepositoryTest {

    @Autowired
    private HolidayRepository holidayRepository;

    @Test
    @DisplayName("Holiday 엔티티 저장 및 자동 타임스탬프 생성 확인")
    void saveHoliday_withAutoTimestamp() {

        // given
        Holiday holiday = new Holiday(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                "KR",
                true,
                true,
                1949,
                Set.of(HolidayType.PUBLIC, HolidayType.AUTHORITIES),
                List.of("KR-11")
        );

        // when
        Holiday saved = holidayRepository.save(holiday);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());

        assertThat(saved.getLocalName()).isEqualTo("신정");
        assertThat(saved.getTypes()).containsExactlyInAnyOrder(HolidayType.PUBLIC, HolidayType.AUTHORITIES);
        assertThat(saved.getCounties()).contains("KR-11");
    }

    @Test
    @DisplayName("특정 연도/국가 공휴일 조회")
    void findByYearAndCountry() {

        // given
        Holiday holiday1 = new Holiday(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                "KR",
                true, true, null,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );

        Holiday holiday2 = new Holiday(
                LocalDate.of(2025, 5, 5),
                "부처님 오신 날",
                "Buddha's Birthday",
                "KR",
                false, true, null,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );

        Holiday holiday3 = new Holiday(
                LocalDate.of(2024, 1, 1),
                "구정",
                "New Year",
                "KR",
                true, true, null,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );

        holidayRepository.saveAll(List.of(holiday1, holiday2, holiday3));

        // when
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        List<Holiday> result = holidayRepository.findByYearAndCountry("KR", start, end);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .allMatch(h -> h.getCountryCode().equals("KR"))
                .allMatch(h -> !h.getDate().isBefore(start) && !h.getDate().isAfter(end));
    }

    @Test
    @DisplayName("특정 연도와 국가의 공휴일 삭제")
    void deleteByYearAndCountry() {

        // given
        Holiday holiday2025 = new Holiday(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                "KR",
                true, true, null,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );

        Holiday holiday2024 = new Holiday(
                LocalDate.of(2024, 1, 1),
                "구정",
                "New Year",
                "KR",
                true, true, null,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );

        holidayRepository.saveAll(List.of(holiday2025, holiday2024));

        // when
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        holidayRepository.deleteByYearAndCountry("KR", start, end);

        // then
        List<Holiday> remaining = holidayRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getDate().getYear()).isEqualTo(2024);
    }

    @Test
    @DisplayName("UniqueConstraint 테스트 - 같은 국가/날짜/현지명은 중복 불가")
    void uniqueConstraintTest() {

        // given
        Holiday holiday1 = new Holiday(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                "KR",
                true, true, null,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );

        holidayRepository.saveAndFlush(holiday1);

        // when & then
        Holiday duplicate = new Holiday(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year",
                "KR",
                false, false, null,
                Set.of(HolidayType.BANK),
                List.of()
        );

        assertThat(holidayRepository.findAll()).hasSize(1);
    }
}