package com.example.actual_lb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerMetrics {

    private String url;
    private long requestsHandled;
    private long failures;
    private long totalLatency;

    public double getAverageLatency() {

        if (requestsHandled == 0) {
            return 0;
        }

        return (double) totalLatency / requestsHandled;
    }
}