package com.example.actual_lb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
//your LB needs to make HTTP calls to backend servers.

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() { // RestTemplate is the object that performs(creates and sends) outgoing HTTP call.
        return new RestTemplate();
    }

}
