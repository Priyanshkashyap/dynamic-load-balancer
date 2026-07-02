package com.example.actual_lb.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class TokenBucket {

    private  int capacity;
    private int tokens; // default 0
    private long lastRefillTime; // default value as 0L

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

        long now = System.currentTimeMillis(); // milliseconds since Jan 1 1970
        long secondsPassed = (now - lastRefillTime) / 1000;
        if (secondsPassed > 0) { // agar jldi dabadiya toh itll be 0

            tokens = Math.min(capacity,tokens +(int) secondsPassed); // max 5 tokens it can store . starting value will be 5 only
            lastRefillTime = now;
        }
    }

}