package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.Year;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayBatchScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NagerApiClient nagerApiClient;
    private final HolidayService holidayService;

    /**
     * 매년 1월 2일 01:00 KST에
     * 전년도 / 금년도 공휴일 데이터를 전세계 국가 기준으로 재동기화
     */
    @Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
    public void syncPreviousAndCurrentYear() {

        int currentYear = Year.now(KST).getValue();
        int previousYear = currentYear - 1;

        log.info("[HolidayBatchScheduler] 연간 공휴일 동기화 배치 시작 - 이전 연도: {}, 현재 연도: {}",
                previousYear, currentYear);

        List<AvailableCountryResponse> countries = nagerApiClient.getAvailableCountries();

        int totalTasks = countries.size() * 2;
        int successCount = 0;
        int failCount = 0;

        for (AvailableCountryResponse country : countries) {
            String countryCode = country.countryCode();

            if (syncYear(previousYear, countryCode)) {
                successCount++;
            } else {
                failCount++;
            }

            if (syncYear(currentYear, countryCode)) {
                successCount++;
            } else {
                failCount++;
            }
        }

        log.info(
                "[HolidayBatchScheduler] 연간 공휴일 동기화 배치 종료 - 이전 연도: {}, 현재 연도: {}, 전체 작업 수: {}, 성공: {}, 실패: {}",
                previousYear, currentYear, totalTasks, successCount, failCount
        );
    }

    private boolean syncYear(int year, String countryCode) {
        try {
            holidayService.refreshHolidays(year, countryCode);
            log.info("🟢 [HolidayBatchScheduler] 동기화 성공 - 연도: {}, 국가 코드: {}", year, countryCode);
            return true;
        } catch (Exception e) {
            log.error("🔴 [HolidayBatchScheduler] 동기화 실패 - 연도: {}, 국가 코드: {}", year, countryCode, e);
            return false;
        }
    }
}
