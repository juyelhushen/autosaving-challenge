package com.juyel.blackrock.challenge.infrastructure.performance;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

@Service
public class PerformanceMetricsService {

    public PerformanceResponse capture(long startNanoTime) {

        long endNanoTime = System.nanoTime();
        long durationMs = (endNanoTime - startNanoTime) / 1_000_000;

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        double usedMemoryMB = heapUsage.getUsed() / (1024.0 * 1024.0);

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadBean.getThreadCount();

        return new PerformanceResponse(
                durationMs + " ms",
                String.format("%.2f MB", usedMemoryMB),
                threadCount
        );
    }
}
