package com.example.actual_lb.service;

import com.example.actual_lb.model.BackendServer;
import com.example.actual_lb.model.CircuitState;
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
    private static final int FAILURE_THRESHOLD = 3;
    private static final long OPEN_DURATION = 30_000;

    public void registerServer(String url) {
        servers.put(url, new BackendServer(url,true,0, 0,CircuitState.CLOSED,0));
    }

    public List<BackendServer> getAllServers() {
        return new ArrayList<>(servers.values());
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
            throw new RuntimeException("No healthy servers");
        }

        BackendServer best = healthyServers.get(0);

        for (BackendServer server : healthyServers) {

            if (server.getActiveConnections() < best.getActiveConnections()) {
                best = server;
            }
        }
        return best;
    }
    public List<BackendServer> getHealthyServers() {
        return servers.values().stream().filter(BackendServer::isHealthy).filter(this::allowRequest).toList(); // this here means  this class ka object that was autoinjected .BackendServer::isHealthy as each stream element doesnt have a name and is a backendserver object
    }
    public void onFailure(
            BackendServer server
    ) {

        server.setFailureCount(
                server.getFailureCount() + 1
        );

        if (server.getFailureCount() >= FAILURE_THRESHOLD) {

            server.setCircuitState(CircuitState.OPEN);

            server.setLastFailureTime(System.currentTimeMillis());

            System.out.println(server.getUrl() + " circuit opened");
        }
    }
    public void onSuccess(
            BackendServer server
    ) {

        server.setFailureCount(0);

        server.setCircuitState(
                CircuitState.CLOSED
        );
    }
    public boolean allowRequest(BackendServer server) {

        if (server.getCircuitState() == CircuitState.CLOSED) {
            return true;
        }
        if (server.getCircuitState() == CircuitState.OPEN) {

            long now = System.currentTimeMillis();
            if (now - server.getLastFailureTime() > OPEN_DURATION)
            {
                server.setCircuitState(CircuitState.HALF_OPEN);
                return true;
            }
            return false;
        }
        return true;
    }
}