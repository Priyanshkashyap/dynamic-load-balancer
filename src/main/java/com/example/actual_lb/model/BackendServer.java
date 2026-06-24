package com.example.actual_lb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BackendServer {
    private String url;
    private boolean healthy;
    private int activeConnections;
}
