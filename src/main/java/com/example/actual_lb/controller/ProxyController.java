package com.example.actual_lb.controller;

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

        System.out.println(
                "Received request in LB"
        );

        String server =
                loadBalancerService.getNextServer();

        System.out.println(
                "Forwarding to " + server
        );

        return restTemplate.getForObject(
                server + "/hello",
                Object.class
        );
    }
}