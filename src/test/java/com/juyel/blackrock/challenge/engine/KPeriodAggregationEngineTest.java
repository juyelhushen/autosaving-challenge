package com.juyel.blackrock.challenge.engine;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.engine.KPeriodAggregationEngine;
import com.juyel.blackrock.challenge.computation.temporal.model.KPeriodRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KPeriodAggregationEngineTest {

    private KPeriodAggregationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new KPeriodAggregationEngine();
    }

    // ------------------------------------------------------
    // ✅ Happy Path
    // ------------------------------------------------------
    @Test
    void shouldAggregateRemanentWithinPeriod() {

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);

        KPeriodRule period = new KPeriodRule(start, end);

        TransactionResponse tx1 = transaction("2023-03-01T10:00:00", 50);
        TransactionResponse tx2 = transaction("2023-06-01T10:00:00", 25);

        double result = engine.aggregateForPeriod(List.of(tx1, tx2), period);

        assertThat(result).isEqualTo(75);
    }

    // ------------------------------------------------------
    // ✅ Inclusive Start Boundary
    // ------------------------------------------------------
    @Test
    void shouldIncludeTransactionOnStartBoundary() {

        LocalDateTime boundary = LocalDateTime.of(2023, 5, 1, 0, 0);

        KPeriodRule period = new KPeriodRule(boundary, boundary.plusDays(10));

        TransactionResponse tx = new TransactionResponse(boundary, 500, 600, 40);

        double result = engine.aggregateForPeriod(List.of(tx), period);

        assertThat(result).isEqualTo(40);
    }

    // ------------------------------------------------------
    // ✅ Inclusive End Boundary
    // ------------------------------------------------------
    @Test
    void shouldIncludeTransactionOnEndBoundary() {

        LocalDateTime boundary = LocalDateTime.of(2023, 5, 10, 23, 59);

        KPeriodRule period = new KPeriodRule(boundary.minusDays(10), boundary);

        TransactionResponse tx = new TransactionResponse(boundary, 500, 600, 60);

        double result = engine.aggregateForPeriod(List.of(tx), period);

        assertThat(result).isEqualTo(60);
    }

    // ------------------------------------------------------
    // ✅ No Matching Transactions
    // ------------------------------------------------------
    @Test
    void shouldReturnZeroWhenNoTransactionsMatch() {

        KPeriodRule period = new KPeriodRule(
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 31, 23, 59)
        );

        TransactionResponse tx = transaction("2023-06-01T10:00:00", 50);

        double result = engine.aggregateForPeriod(List.of(tx), period);

        assertThat(result).isZero();
    }

    // ------------------------------------------------------
    // ✅ Empty Transaction List
    // ------------------------------------------------------
    @Test
    void shouldHandleEmptyTransactionList() {

        KPeriodRule period = new KPeriodRule(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        double result = engine.aggregateForPeriod(List.of(), period);

        assertThat(result).isZero();
    }

    // ------------------------------------------------------
    // ✅ Multiple Transactions Partial Match
    // ------------------------------------------------------
    @Test
    void shouldAggregateOnlyMatchingTransactions() {

        KPeriodRule period = new KPeriodRule(
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 6, 30, 23, 59)
        );

        TransactionResponse tx1 = transaction("2023-03-01T10:00:00", 50);
        TransactionResponse tx2 = transaction("2023-09-01T10:00:00", 100);

        double result = engine.aggregateForPeriod(List.of(tx1, tx2), period);

        assertThat(result).isEqualTo(50);
    }

    // ------------------------------------------------------
    // ✅ Zero Remanent Handling
    // ------------------------------------------------------
    @Test
    void shouldHandleZeroRemanentTransactions() {

        KPeriodRule period = new KPeriodRule(
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 12, 31, 23, 59)
        );

        TransactionResponse tx = transaction("2023-03-01T10:00:00", 0);

        double result = engine.aggregateForPeriod(List.of(tx), period);

        assertThat(result).isZero();
    }

    // ------------------------------------------------------
    // 🧰 Helpers
    // ------------------------------------------------------
    private TransactionResponse transaction(String isoDate, double remanent) {
        return new TransactionResponse(
                LocalDateTime.parse(isoDate),
                500,
                600,
                remanent
        );
    }
}
