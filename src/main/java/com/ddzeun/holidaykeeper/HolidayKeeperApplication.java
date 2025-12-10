package com.ddzeun.holidaykeeper;

import com.ddzeun.holidaykeeper.holiday.application.HolidaySyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class HolidayKeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayKeeperApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner initData(HolidaySyncService holidaySyncService) {
        return args -> {
            System.out.println("======== [초기화] 공휴일 데이터 적재 시작 ========");
            holidaySyncService.loadRecentYearsForAllCountries();
            System.out.println("======== [초기화] 공휴일 데이터 적재 완료 ========");
        };
    }

}
