package com.juyel.blackrock.challenge.infrastructure.performance;

public record PerformanceMetricsResponse(
        String time,
        String memory,
        int threads
) {}