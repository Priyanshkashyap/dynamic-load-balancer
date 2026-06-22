package com.example.actual_lb.service;

import com.example.actual_lb.model.BackendServer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class HealthCheckService {

    @Autowired
    private LoadBalancerService loadBalancerService;

    @Autowired
    private RestTemplate restTemplate;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct // Run this method automatically once after Spring creates the bean.
    public void startHealthChecks() {

        scheduler.scheduleAtFixedRate(this::checkHealth,0,5, TimeUnit.SECONDS);//Run some task repeatedly after fixed intervals.This is a method reference.Equivalent to:() -> checkHealth()
    } //this means the current object being executed now ka checkHealth
    // () -> this.checkHealth().This does not run checkHealth().It creates an object that says:"If someone calls me later, then I will execute checkHealth()."

/*Create HealthCheckService Object
        |
Inject Dependencies
        |
Run @PostConstruct Methods*/
    public void checkHealth() {

        List<BackendServer> servers = loadBalancerService.getAllServers();

        for (BackendServer server : servers) {

            String healthUrl =
                    server.getUrl() + "/health";

            try {

                String response =
                        restTemplate.getForObject(
                                healthUrl,
                                String.class
                        );

                if ("UP".equals(response)) {

                    server.setHealthy(true);
                    System.out.println(
                            server.getUrl()
                                    + " is healthy"
                    );
                }

            } catch (Exception e) {

                server.setHealthy(false); // can return anything

                System.out.println(
                        server.getUrl()
                                + " is DOWN"
                );
            }
        }
    }
}