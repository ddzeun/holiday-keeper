package com.ddzeun.holidaykeeper.holiday.application;

import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class HolidayYearPolicy {

    private static final int YEAR_RANGE = 5;

    public int getEndYear() {
        return LocalDate.now().getYear();
    }

    public int getStartYear() {
        return getEndYear() - YEAR_RANGE;
    }

    public void validateYear(int year) {
        int start = getStartYear();
        int end = getEndYear();

        if (year < start || year > end) {
            throw new IllegalArgumentException(
                    String.format("연도는 %d년과 %d년 사이여야 합니다.", start, end)
            );
        }
    }
}