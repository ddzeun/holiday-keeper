package com.ddzeun.holidaykeeper.holiday.application;

import com.ddzeun.holidaykeeper.country.domain.Country;
import com.ddzeun.holidaykeeper.country.repository.CountryRepository;
import com.ddzeun.holidaykeeper.external.nager.NagerApiClient;
import com.ddzeun.holidaykeeper.external.nager.dto.AvailableCountryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidaySyncService {

    private final NagerApiClient nagerApiClient;
    private final CountryRepository countryRepository;
    private final HolidayService holidayService;
    private final HolidayYearPolicy holidayYearPolicy;

    public void loadRecentYearsForAllCountries() {

        List<AvailableCountryResponse> apiCountries = nagerApiClient.getAvailableCountries();
        Set<String> savedCountryCodes = countryRepository.findAllCountryCodes();

        List<Country> newCountries = apiCountries.stream()
                .filter(c -> !savedCountryCodes.contains(c.countryCode()))
                .map(c -> new Country(c.countryCode(), c.name()))
                .toList();

        if (!newCountries.isEmpty()) {
            countryRepository.saveAll(newCountries);
        }

        int startYear = holidayYearPolicy.getStartYear();
        int endYear = holidayYearPolicy.getEndYear();

        int threadCount = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        log.info("======== 병렬 데이터 적재 시작 (Thread Pool Size: {}) ========", threadCount);

        for (int year = startYear; year <= endYear; year++) {
            int finalYear = year;
            for (Country country : newCountries) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        holidayService.loadYearForCountry(finalYear, country.getCountryCode());
                        log.info("저장 성공: {}년, {} - Thread: {}", finalYear, country.getCountryCode(), Thread.currentThread().getName());
                    } catch (Exception e) {
                        log.error("저장 실패: {}년, {}, 원인: {}", finalYear, country.getCountryCode(), e.getMessage());
                    }
                }, executor);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        executor.shutdown();

        long duration = System.currentTimeMillis() - startTime;
        log.info("======== 병렬 데이터 적재 완료 (소요시간: {}ms) ========", duration);
    }
}