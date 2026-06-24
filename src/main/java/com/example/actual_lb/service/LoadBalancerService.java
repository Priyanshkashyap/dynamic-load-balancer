package com.example.actual_lb.service;

import com.example.actual_lb.model.BackendServer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadBalancerService {
    // constructors and objects are only created and injected which new addresses only after running the application
    private final ConcurrentHashMap<String, BackendServer> servers = new ConcurrentHashMap<>();//Normal HashMap is not thread-safe.

    private final AtomicInteger counter = new AtomicInteger(0);

    public void registerServer(String url) {

        servers.put(url, new BackendServer(url,true,0));
    }

    public List<BackendServer> getAllServers() {

        return new ArrayList<>(
                servers.values()
        );
    }

    public String getNextServer() {

        List<BackendServer> list = servers.values().stream().filter(BackendServer::isHealthy).toList();// for each server it checks health, Backe

        if (list.isEmpty()) {
            throw new RuntimeException(
                    "No servers registered"
            );
        }

        int index = Math.abs(counter.getAndIncrement());

        return list.get(index % list.size()).getUrl();
    }

    public void deregisterServer(String url) {
        if (url.isEmpty()) {
            throw new RuntimeException("Empty url");
        } else {
            servers.remove(url);
        }
    }
    public BackendServer getLeastLoadedServer() {

        List<BackendServer> healthyServers = servers.values().stream().filter(BackendServer::isHealthy).toList();

        if (healthyServers.isEmpty()) {
            throw new RuntimeException(
                    "No healthy servers"
            );
        }

        BackendServer best = healthyServers.get(0);

        for (BackendServer server : healthyServers) {

            if (server.getActiveConnections() < best.getActiveConnections()) {
                best = server;
            }
        }
        return best;
    }
}