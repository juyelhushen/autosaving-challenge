package com.juyel.blackrock.challenge.engine;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.engine.TemporalRulesEngine;
import com.juyel.blackrock.challenge.computation.temporal.strategy.PRuleStrategy;
import com.juyel.blackrock.challenge.computation.temporal.strategy.QRuleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

class TemporalRulesEngineTest {

    private TemporalRulesEngine engine;

    @Mock QRuleStrategy qStrategy;
    @Mock PRuleStrategy pStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        engine = new TemporalRulesEngine();
    }

    //Happy Path — Both Strategies Applied
    @Test
    void shouldApplyQThenPRulesInCorrectOrder() {

        TransactionResponse tx = sampleTransaction(100);

        when(qStrategy.apply(tx)).thenReturn(sampleTransaction(50));   // override
        when(pStrategy.apply(any())).thenReturn(sampleTransaction(75)); // add extra

        List<TransactionResponse> result = engine.applyTemporalRules(
                List.of(tx),
                qStrategy,
                pStrategy
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).remanent()).isEqualTo(75);

        InOrder order = inOrder(qStrategy, pStrategy);
        order.verify(qStrategy).apply(tx);
        order.verify(pStrategy).apply(any());
    }

    // Only Q Strategy Effective
    @Test
    void shouldApplyOnlyQRuleWhenPDoesNothing() {

        TransactionResponse tx = sampleTransaction(100);

        when(qStrategy.apply(tx)).thenReturn(sampleTransaction(20));
        when(pStrategy.apply(any())).thenAnswer(inv -> inv.getArgument(0));

        List<TransactionResponse> result = engine.applyTemporalRules(
                List.of(tx),
                qStrategy,
                pStrategy
        );

        assertThat(result.get(0).remanent()).isEqualTo(20);
    }

    // Only P Strategy Effective
    @Test
    void shouldApplyOnlyPRuleWhenQDoesNothing() {

        TransactionResponse tx = sampleTransaction(100);

        when(qStrategy.apply(tx)).thenReturn(tx);
        when(pStrategy.apply(tx)).thenReturn(sampleTransaction(125));

        List<TransactionResponse> result = engine.applyTemporalRules(
                List.of(tx),
                qStrategy,
                pStrategy
        );

        assertThat(result.get(0).remanent()).isEqualTo(125);
    }

    // No Rules Applied
    @Test
    void shouldReturnOriginalTransactionWhenNoRulesMatch() {

        TransactionResponse tx = sampleTransaction(100);

        when(qStrategy.apply(tx)).thenReturn(tx);
        when(pStrategy.apply(tx)).thenReturn(tx);

        List<TransactionResponse> result = engine.applyTemporalRules(
                List.of(tx),
                qStrategy,
                pStrategy
        );

        assertThat(result.get(0)).isEqualTo(tx);
    }

    // Multiple Transactions
    @Test
    void shouldProcessMultipleTransactions() {

        TransactionResponse tx1 = sampleTransaction(100);
        TransactionResponse tx2 = sampleTransaction(200);

        when(qStrategy.apply(any())).thenAnswer(inv -> inv.getArgument(0));
        when(pStrategy.apply(any())).thenAnswer(inv -> inv.getArgument(0));

        List<TransactionResponse> result = engine.applyTemporalRules(
                List.of(tx1, tx2),
                qStrategy,
                pStrategy
        );

        assertThat(result).hasSize(2);
    }

    // ❌ Strategy Throws Exception
    @Test
    void shouldPropagateExceptionWhenStrategyFails() {

        TransactionResponse tx = sampleTransaction(100);

        when(qStrategy.apply(tx)).thenThrow(new RuntimeException("Q failure"));

        assertThatThrownBy(() ->
                engine.applyTemporalRules(List.of(tx), qStrategy, pStrategy)
        ).hasMessageContaining("Q failure");
    }


    // ✅ Empty Transaction List
    @Test
    void shouldHandleEmptyTransactionList() {

        List<TransactionResponse> result = engine.applyTemporalRules(
                List.of(),
                qStrategy,
                pStrategy
        );

        assertThat(result).isEmpty();
    }

    // 🧰 Helpers
    private TransactionResponse sampleTransaction(double remanent) {
        return new TransactionResponse(
                LocalDateTime.now(),
                500,
                600,
                remanent
        );
    }
}