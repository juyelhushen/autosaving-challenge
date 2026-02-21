package com.juyel.blackrock.challenge.controller;

import com.juyel.blackrock.challenge.api.controller.TransactionsValidatorController;
import com.juyel.blackrock.challenge.api.dto.InvalidTransactionResponse;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.api.dto.TransactionsValidationResponse;
import com.juyel.blackrock.challenge.application.service.TransactionValidationApplicationService;
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

public class TransactionsValidatorControllerTest {
    private MockMvc mockMvc;
    private TransactionValidationApplicationService validationService;

    @BeforeEach
    void setUp() {

        validationService = mock(TransactionValidationApplicationService.class);

        TransactionsValidatorController controller =
                new TransactionsValidatorController(validationService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    // ------------------------------------------------------
    // ✅ Happy Path
    // ------------------------------------------------------
    @Test
    void shouldReturnValidAndInvalidTransactions() throws Exception {

        TransactionResponse validTx = new TransactionResponse(
                LocalDateTime.parse("2023-10-12T20:15:00"),
                250,
                300,
                50
        );

        InvalidTransactionResponse invalidTx = new InvalidTransactionResponse(
                LocalDateTime.parse("2023-07-01T10:00:00"),
                -100,
                0,
                0,
                "Transaction amount must be positive"
        );

        TransactionsValidationResponse response =
                new TransactionsValidationResponse(
                        List.of(validTx),
                        List.of(invalidTx)
                );

        when(validationService.validate(any())).thenReturn(response);

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:validator")
                        .contentType("application/json")
                        .content("""
                        {
                          "wage": 50000,
                          "transactions": [
                            {
                              "date": "2023-10-12T20:15:00",
                              "amount": 250,
                              "ceiling": 300,
                              "remanent": 50
                            }
                          ]
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").isArray())
                .andExpect(jsonPath("$.invalid").isArray())
                .andExpect(jsonPath("$.valid[0].ceiling").value(300))
                .andExpect(jsonPath("$.invalid[0].message")
                        .value("Transaction amount must be positive"));

        verify(validationService).validate(any());
    }

    // ------------------------------------------------------
    // ✅ Empty Transactions
    // ------------------------------------------------------
    @Test
    void shouldHandleEmptyTransactionsList() throws Exception {

        TransactionsValidationResponse response =
                new TransactionsValidationResponse(List.of(), List.of());

        when(validationService.validate(any())).thenReturn(response);

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:validator")
                        .contentType("application/json")
                        .content("""
                        {
                          "wage": 50000,
                          "transactions": []
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").isEmpty())
                .andExpect(jsonPath("$.invalid").isEmpty());
    }

    // ------------------------------------------------------
    // ❌ Invalid Request Payload
    // ------------------------------------------------------
    @Test
    void shouldReturnBadRequestForInvalidPayload() throws Exception {

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:validator")
                        .contentType("application/json")
                        .content("""
                        {
                          "wage": -50000,
                          "transactions": []
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
