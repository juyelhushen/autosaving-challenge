package com.juyel.blackrock.challenge.infrastructure.performance;

public record PerformanceResponse(
        String time,
        String memory,
        int threads
) {}
