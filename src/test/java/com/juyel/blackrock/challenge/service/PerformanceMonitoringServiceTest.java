package com.juyel.blackrock.challenge.service;

import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceMetricsResponse;
import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceMonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PerformanceMonitoringServiceTest {

    private PerformanceMonitoringService service;

    @BeforeEach
    void setUp() {
        service = new PerformanceMonitoringService();
    }

    // ------------------------------------------------------
    // ✅ Happy Path
    // ------------------------------------------------------
    @Test
    void shouldCapturePerformanceMetrics() {

        long start = System.nanoTime();

        PerformanceMetricsResponse metrics = service.captureMetrics(start);

        assertThat(metrics).isNotNull();
        assertThat(metrics.time()).isNotBlank();
        assertThat(metrics.memory()).isNotBlank();
        assertThat(metrics.threads()).isGreaterThan(0);
    }

    // ------------------------------------------------------
    // ✅ Time Format Validation
    // ------------------------------------------------------
    @Test
    void shouldFormatExecutionTimeCorrectly() {

        long artificialStart = System.nanoTime() - 5_000_000; // ~5 ms

        PerformanceMetricsResponse metrics =
                service.captureMetrics(artificialStart);

        assertThat(metrics.time())
                .endsWith(" ms")
                .matches("\\d+\\.\\d{3} ms");
    }

    // ------------------------------------------------------
    // ✅ Memory Format Validation
    // ------------------------------------------------------
    @Test
    void shouldFormatMemoryUsageCorrectly() {

        PerformanceMetricsResponse metrics =
                service.captureMetrics(System.nanoTime());

        assertThat(metrics.memory())
                .endsWith(" MB")
                .matches("\\d+\\.\\d{2} MB");
    }

    // ------------------------------------------------------
    // ✅ Thread Count Sanity
    // ------------------------------------------------------
    @Test
    void shouldReturnValidThreadCount() {

        PerformanceMetricsResponse metrics =
                service.captureMetrics(System.nanoTime());

        assertThat(metrics.threads())
                .isGreaterThan(0)
                .isLessThan(1000); // sanity upper bound
    }

    // ------------------------------------------------------
    // ✅ Duration Should Not Be Negative
    // ------------------------------------------------------
    @Test
    void durationShouldAlwaysBeNonNegative() {

        long start = System.nanoTime();

        PerformanceMetricsResponse metrics =
                service.captureMetrics(start);

        double parsedMillis = extractMillis(metrics.time());

        assertThat(parsedMillis).isGreaterThanOrEqualTo(0);
    }

    // ------------------------------------------------------
    // 🧰 Helper
    // ------------------------------------------------------
    private double extractMillis(String formattedTime) {
        return Double.parseDouble(formattedTime.replace(" ms", ""));
    }
}
