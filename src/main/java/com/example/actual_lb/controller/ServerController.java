package com.example.actual_lb.controller;

import com.example.actual_lb.dto.RegisterServerRequest;
import com.example.actual_lb.model.BackendServer;
import com.example.actual_lb.service.LoadBalancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/servers")
public class ServerController {

    @Autowired
    private  LoadBalancerService loadBalancerService;

    @PostMapping("/register")
    public String register(
            @RequestBody
            RegisterServerRequest request
    ) {

        loadBalancerService.registerServer(request.getUrl());

        return "Server Registered";
    }

    @PostMapping("/deregister")
    public String deregister(@RequestBody RegisterServerRequest request)
    {
        loadBalancerService.deregisterServer(request.getUrl());
        return "Server Deregistered";
    }
    @GetMapping
    public List<BackendServer> getServers() {

        return loadBalancerService.getAllServers();
    }
    @GetMapping("/stats")
    public List<BackendServer> stats() {

        return loadBalancerService.getAllServers();
    }
}
