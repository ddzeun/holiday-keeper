package com.ddzeun.holidaykeeper.holiday.repository;

import com.ddzeun.holidaykeeper.holiday.domain.Holiday;
import com.ddzeun.holidaykeeper.holiday.enums.HolidayType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class HolidaySpecification {

    // 국가 코드 필터 (=)
    public static Specification<Holiday> equalCountryCode(String countryCode) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("countryCode"), countryCode);
    }

    // 기간 시작 필터 (>=)
    public static Specification<Holiday> greaterThanOrEqualDate(LocalDate from) {
        return (root, query, criteriaBuilder) ->
                from == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("date"), from);
    }

    // 기간 끝 필터 (<=)
    public static Specification<Holiday> lessThanOrEqualDate(LocalDate to) {
        return (root, query, criteriaBuilder) ->
                to == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("date"), to);
    }

    // 공휴일 타입 필터
    public static Specification<Holiday> equalType(HolidayType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) return null;

            Join<Holiday, HolidayType> typesJoin = root.join("types", JoinType.LEFT);

            // 중복 결과 제거
            query.distinct(true);

            return criteriaBuilder.equal(typesJoin, type);
        };
    }
}
