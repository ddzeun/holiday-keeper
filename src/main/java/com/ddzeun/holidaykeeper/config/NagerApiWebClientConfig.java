package com.ddzeun.holidaykeeper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NagerApiWebClientConfig {

    @Bean
    public WebClient nagerWebClient() {
        return WebClient.builder()
                .baseUrl("https://date.nager.at/api/v3")
                .defaultHeaders(headers -> {
                    headers.add("Accept", "application/json");
                })
                .build();
    }

}
