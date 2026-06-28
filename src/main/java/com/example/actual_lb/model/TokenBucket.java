package com.example.actual_lb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class TokenBucket {

    private  int capacity;
    private int tokens;
    private long lastRefillTime;

    public TokenBucket(int capacity) {
        this.capacity = capacity;
    }
    public synchronized boolean tryConsume() {

        refill();

        if (tokens > 0) {

            tokens--;

            return true;
        }

        return false;
    }

    private void refill() {

        long now =
                System.currentTimeMillis();

        long secondsPassed =
                (now - lastRefillTime)
                        / 1000;

        if (secondsPassed > 0) {

            tokens =
                    Math.min(
                            capacity,
                            tokens
                                    +
                                    (int) secondsPassed
                    );

            lastRefillTime = now;
        }
    }

}