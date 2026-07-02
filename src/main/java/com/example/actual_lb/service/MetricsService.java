package com.example.actual_lb.service;
import com.example.actual_lb.model.BackendServer;
import com.example.actual_lb.model.Metrics;
import com.example.actual_lb.model.ServerMetrics;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong rateLimitedRequests = new AtomicLong(0);
    private final AtomicLong totalLatency = new AtomicLong(0);
    private final ConcurrentHashMap<String, ServerMetrics> serverMetrics = new ConcurrentHashMap<>();

    public void recordRequest() {
        totalRequests.incrementAndGet();
    }

    public void recordRateLimit() {
        rateLimitedRequests.incrementAndGet();
    }

    public void recordSuccess(BackendServer server, long latency)
    {
        successfulRequests.incrementAndGet();
        totalLatency.addAndGet(latency);

        ServerMetrics metrics = serverMetrics.computeIfAbsent(server.getUrl(), url -> {ServerMetrics m = new ServerMetrics();
            m.setUrl(url);
            return m;
        }
        );

        metrics.setRequestsHandled(metrics.getRequestsHandled() + 1);
        metrics.setTotalLatency(metrics.getTotalLatency() + latency);
    }

    public void recordFailure(BackendServer server)
      {
        failedRequests.incrementAndGet();
        ServerMetrics metrics = serverMetrics.computeIfAbsent(server.getUrl(), url -> {ServerMetrics m = new ServerMetrics();
                            m.setUrl(url);
                            return m;
                        }
                );
        metrics.setFailures(metrics.getFailures() + 1);
    }

    public Metrics getGlobalMetrics() {

        Metrics metrics = new Metrics();
        metrics.setTotalRequests(totalRequests.get());
        metrics.setSuccessfulRequests(successfulRequests.get());
        metrics.setFailedRequests(failedRequests.get());
        metrics.setRateLimitedRequests(rateLimitedRequests.get());
        if (successfulRequests.get() != 0)
        {
            metrics.setAverageLatency((double) totalLatency.get() / successfulRequests.get());
        }
        return metrics;
    }
    public Collection<ServerMetrics> getServerMetrics()
    {
        return serverMetrics.values();
    }
}