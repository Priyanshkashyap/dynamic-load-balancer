package com.example.actual_lb.controller;

import com.example.actual_lb.model.Metrics;
import com.example.actual_lb.model.ServerMetrics;
import com.example.actual_lb.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping
    public Metrics getMetrics() {

        return metricsService.getGlobalMetrics();
    }

    @GetMapping("/servers")
    public Collection<ServerMetrics> getServerMetrics() {

        return metricsService.getServerMetrics();
    }
}