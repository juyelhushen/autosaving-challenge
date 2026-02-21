package com.juyel.blackrock.challenge.infrastructure.performance;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

@Service
public class PerformanceMonitoringService {

    public PerformanceMetricsResponse captureMetrics(long startNanoTime) {

        long durationNs = System.nanoTime() - startNanoTime;
        String formattedTime = formatDuration(durationNs);

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedBytes = memoryBean.getHeapMemoryUsage().getUsed();
        String formattedMemory = formatMemory(usedBytes);

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadBean.getThreadCount();

        return new PerformanceMetricsResponse(
                formattedTime,
                formattedMemory,
                threadCount
        );
    }

    private String formatDuration(long durationNs) {
        double millis = durationNs / 1_000_000.0;
        return String.format("%.3f ms", millis);
    }

    private String formatMemory(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return String.format("%.2f MB", mb);
    }
}
