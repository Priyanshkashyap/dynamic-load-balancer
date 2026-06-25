package com.example.actual_lb.config;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) { // Spring Boot already creates a RestTemplateBuilder bean internally."Spring, before creating my RestTemplate, give me your RestTemplateBuilder."

        return builder
                .connectTimeout(Duration.ofSeconds(5))// First it has to establish a TCP connection.If the server doesn't respond within 5 seconds, stop trying.
                .readTimeout(Duration.ofSeconds(10))// Now imagine the connection succeeded.If the server still hasn't sent a response after 10 seconds, cancel it.
                .build();
    }
}