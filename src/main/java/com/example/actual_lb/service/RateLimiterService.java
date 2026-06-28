package com.example.actual_lb.service;

import com.example.actual_lb.model.TokenBucket;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final ConcurrentHashMap<
            String,
            TokenBucket
            >
            buckets =
            new ConcurrentHashMap<>();

    private static final int LIMIT =
            5;

    public boolean allowRequest(
            String clientIp
    ) {

        TokenBucket bucket =
                buckets.computeIfAbsent( // If the key already exists, it returns the existing value.If the key does not exist, it computes a new value using the provided function, stores it in the map, and returns it.
                        clientIp,
                        ip ->
                                new TokenBucket(
                                        LIMIT
                                )
                );

        return bucket.tryConsume();
    }
}