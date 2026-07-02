package com.example.actual_lb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metrics {

    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private long rateLimitedRequests;
    private double averageLatency;
}