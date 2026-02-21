package com.juyel.blackrock.challenge.engine;

import com.juyel.blackrock.challenge.api.dto.ExpenseRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.engine.TransactionComputationEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

class TransactionComputationEngineTest {

    private TransactionComputationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new TransactionComputationEngine();
    }

    // Happy Path
    @Test
    void shouldCalculateCeilingAndRemanentCorrectly() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                1519
        );

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.ceiling()).isEqualTo(1600);
        assertThat(response.remanent()).isEqualTo(81);
    }

    // Exact Multiple of 100
    @Test
    void shouldReturnZeroRemanentForExactMultipleOfHundred() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                500
        );

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.ceiling()).isEqualTo(500);
        assertThat(response.remanent()).isZero();
    }

    // Smallest Valid Amount
    @Test
    void shouldHandleSmallAmountsCorrectly() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                1
        );

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.ceiling()).isEqualTo(100);
        assertThat(response.remanent()).isEqualTo(99);
    }

    // Boundary Case Near Next Hundred
    @Test
    void shouldRoundUpCorrectlyNearBoundary() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                199
        );

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.ceiling()).isEqualTo(200);
        assertThat(response.remanent()).isEqualTo(1);
    }

    // Large Amount
    @Test
    void shouldHandleLargeAmountsCorrectly() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                499_999
        );

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.ceiling()).isEqualTo(500_000);
        assertThat(response.remanent()).isEqualTo(1);
    }

    // Negative Amount (if validation expected upstream)
    @Test
    void shouldStillComputeForNegativeAmountIfValidatorNotApplied() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                -250
        );

        TransactionResponse response = engine.computeFromExpense(request);

        // Engine computes mathematically; validator rejects later
        assertThat(response.ceiling()).isEqualTo(-200);
        assertThat(response.remanent()).isEqualTo(50);
    }

    // Date Preservation
    @Test
    void shouldPreserveOriginalExpenseDate() {

        LocalDateTime now = LocalDateTime.now();

        ExpenseRequest request = new ExpenseRequest(now, 250);

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.date()).isEqualTo(now);
    }

    // Precision Safety
    @Test
    void shouldHandleDecimalAmountsCorrectly() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                250.75
        );

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.ceiling()).isEqualTo(300);
        assertThat(response.remanent()).isCloseTo(49.25, within(0.001));
    }

    // Zero Amount Edge Case
    @Test
    void shouldHandleZeroAmount() {

        ExpenseRequest request = new ExpenseRequest(
                LocalDateTime.now(),
                0
        );

        TransactionResponse response = engine.computeFromExpense(request);

        assertThat(response.ceiling()).isZero();
        assertThat(response.remanent()).isZero();
    }
}