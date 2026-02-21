package com.juyel.blackrock.challenge.api.controller;

import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceMetricsResponse;
import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceMonitoringService monitoringService;

    @GetMapping("/performance")
    public PerformanceMetricsResponse performance() {

        long start = System.nanoTime();

        // Simulated lightweight processing (optional)
        // In real scenario this could wrap a computation block

        return monitoringService.captureMetrics(start);
    }
}