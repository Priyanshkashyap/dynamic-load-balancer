package com.example.actual_lb.controller;

import com.example.actual_lb.model.BackendServer;
import com.example.actual_lb.service.LoadBalancerService;
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

    @GetMapping("/hello")
    public Object forwardRequest() {

        List<BackendServer> servers =
                loadBalancerService
                        .getHealthyServers();

        Exception lastException = null;

        for (
                BackendServer server
                : servers
        ) {

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

                loadBalancerService
                        .onSuccess(server);

                return response;

            }

            catch (Exception e) {

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