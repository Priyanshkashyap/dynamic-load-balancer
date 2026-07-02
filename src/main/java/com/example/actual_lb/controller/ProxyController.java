package com.example.actual_lb.controller;
import com.example.actual_lb.service.MetricsService;
import jakarta.servlet.http.HttpServletRequest;
import com.example.actual_lb.model.BackendServer;
import com.example.actual_lb.service.LoadBalancerService;
import com.example.actual_lb.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProxyController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerService loadBalancerService;
    @Autowired
    private RateLimiterService rateLimiterService;
    @Autowired
    private MetricsService metricsService; // if other class also injects this they both receive reference to the same object

    @GetMapping("/hello")
    public Object forwardRequest(
            HttpServletRequest request
    ) {

        metricsService.recordRequest();

        String clientIp =
                request.getRemoteAddr();

        if (
                !rateLimiterService
                        .allowRequest(clientIp)
        ) {

            metricsService.recordRateLimit();

            throw new RuntimeException(
                    "429 Too Many Requests"
            );
        }

        List<BackendServer> servers =
                loadBalancerService
                        .getHealthyServers();

        Exception lastException = null;

        for (
                BackendServer server
                : servers
        ) {

            long start =
                    System.currentTimeMillis();

            try {

                server.setActiveConnections(
                        server.getActiveConnections()
                                + 1
                );

                Object response =
                        restTemplate.getForObject(
                                server.getUrl()
                                        + "/hello",
                                Object.class
                        );

                long latency =
                        System.currentTimeMillis()
                                - start;

                metricsService.recordSuccess(
                        server,
                        latency
                );

                loadBalancerService
                        .onSuccess(server);

                return response;
            }

            catch (Exception e) {

                metricsService.recordFailure(
                        server
                );

                loadBalancerService
                        .onFailure(server);

                lastException = e;
            }

            finally {

                server.setActiveConnections(
                        server.getActiveConnections()
                                - 1
                );
            }
        }

        throw new RuntimeException(
                "All servers failed",
                lastException
        );
    }
}