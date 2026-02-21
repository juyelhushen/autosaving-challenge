package com.juyel.blackrock.challenge.api.controller;

import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceMetricsService;
import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceMetricsService metricsService;

    @GetMapping("/performance")
    public PerformanceResponse getPerformanceMetrics() {

        long start = System.nanoTime();

        return metricsService.capture(start);
    }
}