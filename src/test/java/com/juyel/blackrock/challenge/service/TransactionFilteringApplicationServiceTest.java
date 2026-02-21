package com.juyel.blackrock.challenge.service;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.api.dto.TransactionsFilterRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionsFilterResponse;
import com.juyel.blackrock.challenge.application.service.TransactionFilteringApplicationService;
import com.juyel.blackrock.challenge.computation.temporal.engine.TemporalRulesEngine;
import com.juyel.blackrock.challenge.computation.temporal.strategy.PRuleStrategy;
import com.juyel.blackrock.challenge.computation.temporal.strategy.QRuleStrategy;
import com.juyel.blackrock.challenge.computation.temporal.strategy.TemporalRuleStrategyFactory;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.*;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionFilteringApplicationServiceTest {

    @Mock
    private TemporalRulesEngine rulesEngine;

    @Mock
    private TemporalRuleStrategyFactory strategyFactory;

    @Mock
    private QRuleStrategy qStrategy;

    @Mock
    private PRuleStrategy pStrategy;

    @InjectMocks
    private TransactionFilteringApplicationService service;

    // ------------------------------------------------------
    // ✅ Happy Path
    // ------------------------------------------------------
    @Test
    void shouldFilterTransactionsSuccessfully() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                500,
                600,
                100
        );

        TransactionsFilterRequest request = new TransactionsFilterRequest(
                List.of(tx),
                List.of(),   // qPeriods
                List.of()    // pPeriods
        );

        when(strategyFactory.createQStrategy(request.qPeriods()))
                .thenReturn(qStrategy);

        when(strategyFactory.createPStrategy(request.pPeriods()))
                .thenReturn(pStrategy);

        when(rulesEngine.applyTemporalRules(
                request.transactions(),
                qStrategy,
                pStrategy
        )).thenReturn(List.of(tx));

        TransactionsFilterResponse response =
                service.filterTransactions(request);

        assertThat(response).isNotNull();
        assertThat(response.filteredTransactions())
                .hasSize(1)
                .containsExactly(tx);

        verify(strategyFactory).createQStrategy(request.qPeriods());
        verify(strategyFactory).createPStrategy(request.pPeriods());
        verify(rulesEngine).applyTemporalRules(request.transactions(), qStrategy, pStrategy);
    }

    // ------------------------------------------------------
    // ✅ Empty Transactions
    // ------------------------------------------------------
    @Test
    void shouldHandleEmptyTransactionsList() {

        TransactionsFilterRequest request = new TransactionsFilterRequest(
                List.of(),
                List.of(),
                List.of()
        );

        when(strategyFactory.createQStrategy(any())).thenReturn(qStrategy);
        when(strategyFactory.createPStrategy(any())).thenReturn(pStrategy);
        when(rulesEngine.applyTemporalRules(any(), any(), any()))
                .thenReturn(List.of());

        TransactionsFilterResponse response =
                service.filterTransactions(request);

        assertThat(response.filteredTransactions()).isEmpty();

        verify(rulesEngine).applyTemporalRules(any(), eq(qStrategy), eq(pStrategy));
    }

    // ------------------------------------------------------
    // ❌ Rules Engine Failure
    // ------------------------------------------------------
    @Test
    void shouldPropagateExceptionWhenRulesEngineFails() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                500,
                600,
                100
        );

        TransactionsFilterRequest request = new TransactionsFilterRequest(
                List.of(tx),
                List.of(),
                List.of()
        );

        when(strategyFactory.createQStrategy(any())).thenReturn(qStrategy);
        when(strategyFactory.createPStrategy(any())).thenReturn(pStrategy);

        when(rulesEngine.applyTemporalRules(any(), any(), any()))
                .thenThrow(new RuntimeException("Rules failure"));

        assertThatThrownBy(() -> service.filterTransactions(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rules failure");
    }

    // ------------------------------------------------------
    // ✅ Strategy Creation Order
    // ------------------------------------------------------
    @Test
    void shouldCreateStrategiesBeforeApplyingRules() {

        TransactionsFilterRequest request = new TransactionsFilterRequest(
                List.of(),
                List.of(),
                List.of()
        );

        when(strategyFactory.createQStrategy(any())).thenReturn(qStrategy);
        when(strategyFactory.createPStrategy(any())).thenReturn(pStrategy);
        when(rulesEngine.applyTemporalRules(any(), any(), any()))
                .thenReturn(List.of());

        service.filterTransactions(request);

        InOrder order = inOrder(strategyFactory, rulesEngine);

        order.verify(strategyFactory).createQStrategy(any());
        order.verify(strategyFactory).createPStrategy(any());
        order.verify(rulesEngine).applyTemporalRules(any(), any(), any());
    }
}