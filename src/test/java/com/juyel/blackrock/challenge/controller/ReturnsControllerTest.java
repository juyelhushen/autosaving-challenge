package com.juyel.blackrock.challenge.controller;

import com.juyel.blackrock.challenge.api.controller.ReturnsController;
import com.juyel.blackrock.challenge.computation.returns.engine.ReturnsCalculationEngine;
import com.juyel.blackrock.challenge.computation.returns.model.PeriodReturns;
import com.juyel.blackrock.challenge.computation.returns.model.ReturnsCalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ReturnsControllerTest {

    private MockMvc mockMvc;
    private ReturnsCalculationEngine returnsEngine;

    @BeforeEach
    void setUp() {

        returnsEngine = mock(ReturnsCalculationEngine.class);

        ReturnsController controller = new ReturnsController(returnsEngine);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    // ------------------------------------------------------
    // ✅ Happy Path — NPS
    // ------------------------------------------------------
    @Test
    void shouldCalculateNpsReturns() throws Exception {

        ReturnsCalculationResponse response = sampleResponse();

        when(returnsEngine.calculateNps(any())).thenReturn(response);

        mockMvc.perform(post("/blackrock/challenge/v1/returns:nps")
                        .contentType("application/json")
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionsTotalAmount").value(250))
                .andExpect(jsonPath("$.savingsByDates").isArray());

        verify(returnsEngine).calculateNps(any());
    }

    // ------------------------------------------------------
    // ✅ Happy Path — Index Fund
    // ------------------------------------------------------
    @Test
    void shouldCalculateIndexReturns() throws Exception {

        ReturnsCalculationResponse response = sampleResponse();

        when(returnsEngine.calculateIndex(any())).thenReturn(response);

        mockMvc.perform(post("/blackrock/challenge/v1/returns:index")
                        .contentType("application/json")
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionsTotalAmount").value(250));

        verify(returnsEngine).calculateIndex(any());
    }

    // ------------------------------------------------------
    // ❌ Invalid Payload
    // ------------------------------------------------------
    @Test
    void shouldReturnBadRequestForInvalidPayload() throws Exception {

        mockMvc.perform(post("/blackrock/challenge/v1/returns:nps")
                        .contentType("application/json")
                        .content("{}"))   // missing required fields
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------
    // ❌ Malformed JSON
    // ------------------------------------------------------
    @Test
    void shouldReturnBadRequestForMalformedJson() throws Exception {

        mockMvc.perform(post("/blackrock/challenge/v1/returns:index")
                        .contentType("application/json")
                        .content("INVALID_JSON"))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------
    // 🧰 Helpers
    // ------------------------------------------------------
    private ReturnsCalculationResponse sampleResponse() {

        PeriodReturns period = new PeriodReturns(
                null,
                null,
                50,
                20,
                0,
                60
        );

        return new ReturnsCalculationResponse(
                250,
                300,
                List.of(period)
        );
    }

    private String sampleRequestJson() {
        return """
        {
          "age": 29,
          "wage": 50000,
          "inflation": 0.055,
          "transactions": [
            {
              "date": "2023-10-12T20:15:00",
              "amount": 250,
              "ceiling": 300,
              "remanent": 50
            }
          ],
          "kPeriods": [
            {
              "start": "2023-01-01T00:00:00",
              "end": "2023-12-31T23:59:59"
            }
          ]
        }
        """;
    }
}
