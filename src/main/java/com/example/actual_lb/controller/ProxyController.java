package com.example.actual_lb.controller;

import com.example.actual_lb.model.BackendServer;
import com.example.actual_lb.service.LoadBalancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ProxyController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerService loadBalancerService;

    @GetMapping("/hello")
    public Object forwardRequest() {

        BackendServer server = loadBalancerService.getLeastLoadedServer();

        try {
            server.setActiveConnections(server.getActiveConnections() + 1);
            return restTemplate.getForObject(server.getUrl() + "/hello", Object.class);

        } finally { // Java guarantees this runs whether: result is success or amy exceptions

            server.setActiveConnections(server.getActiveConnections() - 1);
        }
    }
}