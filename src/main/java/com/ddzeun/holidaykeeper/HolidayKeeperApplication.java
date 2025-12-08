package com.ddzeun.holidaykeeper;

import com.ddzeun.holidaykeeper.holiday.application.HolidayBatchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HolidayKeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayKeeperApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(HolidayBatchService holidayBatchService) {
        return args -> {
            System.out.println("======== [초기화] 공휴일 데이터 적재 시작 ========");
            holidayBatchService.loadRecentYearsForAllCountries();
            System.out.println("======== [초기화] 공휴일 데이터 적재 완료 ========");
        };
    }

}
