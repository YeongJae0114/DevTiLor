package com.toy.devtilor.devtilor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient githubWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.github.com/users/YeongJae0114/events")
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }
}
