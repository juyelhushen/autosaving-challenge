package com.juyel.blackrock.challenge.controller;



import com.juyel.blackrock.challenge.api.controller.TransactionController;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.application.service.TransactionApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TransactionControllerTest {

    private MockMvc mockMvc;
    private TransactionApplicationService applicationService;

    @BeforeEach
    void setUp() {

        applicationService = mock(TransactionApplicationService.class);

        TransactionController controller =
                new TransactionController(applicationService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)   // ⭐ enable validation
                .build();
    }

    // ------------------------------------------------------
    // ✅ Happy Path
    // ------------------------------------------------------
    @Test
    void shouldParseTransactionsSuccessfully() throws Exception {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.parse("2023-10-12T20:15:00"),
                1519,
                1600,
                81
        );

        when(applicationService.parseExpenses(any()))
                .thenReturn(List.of(tx));

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:parse")
                        .contentType("application/json")
                        .content("""
                        [
                          {
                            "date": "2023-10-12T20:15:00",
                            "amount": 1519
                          }
                        ]
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ceiling").value(1600))
                .andExpect(jsonPath("$[0].remanent").value(81));

        verify(applicationService).parseExpenses(any());
    }

    // ------------------------------------------------------
    // ✅ Empty List
    // ------------------------------------------------------
    @Test
    void shouldHandleEmptyExpenseList() throws Exception {

        when(applicationService.parseExpenses(any()))
                .thenReturn(List.of());

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:parse")
                        .contentType("application/json")
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ------------------------------------------------------
    // ❌ Invalid Payload — Negative Amount
    // ------------------------------------------------------
    @Test
    void shouldReturnBadRequestForInvalidExpense() throws Exception {

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:parse")
                        .contentType("application/json")
                        .content("""
                        [
                          {
                            "date": "2023-10-12T20:15:00",
                            "amount": -100
                          }
                        ]
                        """))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------
    // ❌ Invalid Payload — Missing Fields
    // ------------------------------------------------------
    @Test
    void shouldReturnBadRequestWhenAmountMissing() throws Exception {

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:parse")
                        .contentType("application/json")
                        .content("""
                        [
                          {
                            "date": "2023-10-12T20:15:00"
                          }
                        ]
                        """))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------
    // ❌ Invalid Payload — Malformed JSON
    // ------------------------------------------------------
    @Test
    void shouldReturnBadRequestForMalformedJson() throws Exception {

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:parse")
                        .contentType("application/json")
                        .content("INVALID_JSON"))
                .andExpect(status().isBadRequest());
    }
}