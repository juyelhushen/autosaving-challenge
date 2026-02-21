package com.juyel.blackrock.challenge.controller;


import com.juyel.blackrock.challenge.api.controller.TransactionsFilterController;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.api.dto.TransactionsFilterResponse;
import com.juyel.blackrock.challenge.application.service.TransactionFilteringApplicationService;
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

class TransactionsFilterControllerTest {

    private MockMvc mockMvc;
    private TransactionFilteringApplicationService filteringService;

    @BeforeEach
    void setUp() {

        filteringService = mock(TransactionFilteringApplicationService.class);

        TransactionsFilterController controller =
                new TransactionsFilterController(filteringService);

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
    void shouldReturnFilteredTransactions() throws Exception {

        TransactionResponse filteredTx = new TransactionResponse(
                LocalDateTime.parse("2023-07-10T10:00:00"),
                620,
                700,
                25
        );

        TransactionsFilterResponse response =
                new TransactionsFilterResponse(List.of(filteredTx));

        when(filteringService.filterTransactions(any())).thenReturn(response);

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:filter")
                        .contentType("application/json")
                        .content("""
                        {
                          "transactions": [
                            {
                              "date": "2023-07-10T10:00:00",
                              "amount": 620,
                              "ceiling": 700,
                              "remanent": 80
                            }
                          ],
                          "qPeriods": [
                            {
                              "fixed": 0,
                              "start": "2023-07-01T00:00:00",
                              "end": "2023-07-31T23:59:59"
                            }
                          ],
                          "pPeriods": [
                            {
                              "extra": 25,
                              "start": "2023-07-01T00:00:00",
                              "end": "2023-12-31T23:59:59"
                            }
                          ]
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filteredTransactions").isArray())
                .andExpect(jsonPath("$.filteredTransactions[0].remanent").value(25));

        verify(filteringService).filterTransactions(any());
    }

    // ------------------------------------------------------
    // ✅ Empty Transactions
    // ------------------------------------------------------
    @Test
    void shouldHandleEmptyTransactionsList() throws Exception {

        TransactionsFilterResponse response =
                new TransactionsFilterResponse(List.of());

        when(filteringService.filterTransactions(any())).thenReturn(response);

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:filter")
                        .contentType("application/json")
                        .content("""
                        {
                          "transactions": [],
                          "qPeriods": [],
                          "pPeriods": []
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filteredTransactions").isEmpty());
    }

    // ------------------------------------------------------
    // ❌ Invalid Payload (missing transactions)
    // ------------------------------------------------------
    @Test
    void shouldReturnBadRequestWhenTransactionsMissing() throws Exception {

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:filter")
                        .contentType("application/json")
                        .content("""
                        {
                          "qPeriods": [],
                          "pPeriods": []
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}