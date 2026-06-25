package com.example.actual_lb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "loadbalancer") // takes everything with this prefix from app.properties.  Spring Boot does this automatically through Configuration Properties Binding.(Spring looks for all properties that start with loadbalancer in application.properties (or application.yml) and tries to bind them to fields in your class by matching names.)
public class LoadBalancerProperties {
    private List<String> servers;
}