package com.juyel.blackrock.challenge.controller;

import com.juyel.blackrock.challenge.api.controller.PerformanceController;
import com.juyel.blackrock.challenge.infrastructure.exception.GlobalApiExceptionHandler;
import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceMetricsResponse;
import com.juyel.blackrock.challenge.infrastructure.performance.PerformanceMonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PerformanceControllerTest {

    private MockMvc mockMvc;
    private PerformanceMonitoringService monitoringService;

    @BeforeEach
    void setUp() {

        monitoringService = mock(PerformanceMonitoringService.class);

        PerformanceController controller =
                new PerformanceController(monitoringService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalApiExceptionHandler())  // ⭐ KEY FIX
                .build();
    }

    // ------------------------------------------------------
    // ✅ Happy Path
    // ------------------------------------------------------
    @Test
    void shouldReturnPerformanceMetrics() throws Exception {

        PerformanceMetricsResponse metrics =
                new PerformanceMetricsResponse(
                        "12.345 ms",
                        "128.50 MB",
                        42
                );

        when(monitoringService.captureMetrics(anyLong()))
                .thenReturn(metrics);

        mockMvc.perform(get("/blackrock/challenge/v1/performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value("12.345 ms"))
                .andExpect(jsonPath("$.memory").value("128.50 MB"))
                .andExpect(jsonPath("$.threads").value(42));

        verify(monitoringService).captureMetrics(anyLong());
    }

    // ------------------------------------------------------
    // ✅ Response Structure Validation
    // ------------------------------------------------------
    @Test
    void shouldContainAllExpectedFields() throws Exception {

        PerformanceMetricsResponse metrics =
                new PerformanceMetricsResponse("1 ms", "10 MB", 5);

        when(monitoringService.captureMetrics(anyLong()))
                .thenReturn(metrics);

        mockMvc.perform(get("/blackrock/challenge/v1/performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.memory").exists())
                .andExpect(jsonPath("$.threads").exists());
    }

    // ------------------------------------------------------
    // ❌ Service Failure (Optional resilience test)
    // ------------------------------------------------------
    @Test
    void shouldReturnServerErrorWhenServiceFails() throws Exception {

        when(monitoringService.captureMetrics(anyLong()))
                .thenThrow(new RuntimeException("Metrics failure"));

        mockMvc.perform(get("/blackrock/challenge/v1/performance"))
                .andExpect(status().isInternalServerError());
    }
}
