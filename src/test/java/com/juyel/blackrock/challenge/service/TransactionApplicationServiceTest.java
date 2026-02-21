package com.juyel.blackrock.challenge.service;

import com.juyel.blackrock.challenge.api.dto.ExpenseRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.application.service.TransactionApplicationService;
import com.juyel.blackrock.challenge.computation.temporal.engine.TransactionComputationEngine;
import com.juyel.blackrock.challenge.infrastructure.exception.BusinessRuleException;
import com.juyel.blackrock.challenge.validator.TransactionBusinessValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionApplicationServiceTest {

    @Mock
    private TransactionComputationEngine computationEngine;

    @Mock
    private TransactionBusinessValidator validator;

    @InjectMocks
    private TransactionApplicationService service;

    // ------------------------------------------------------
    // ✅ Happy Path
    // ------------------------------------------------------
    @Test
    void shouldParseExpensesSuccessfully() {

        ExpenseRequest expense = new ExpenseRequest(
                LocalDateTime.parse("2023-10-12T20:15:00"),
                1519
        );

        TransactionResponse computed = new TransactionResponse(
                expense.date(),
                1519,
                1600,
                81
        );

        when(computationEngine.computeFromExpense(expense))
                .thenReturn(computed);

        List<TransactionResponse> result =
                service.parseExpenses(List.of(expense));

        assertThat(result)
                .hasSize(1)
                .containsExactly(computed);

        verify(computationEngine).computeFromExpense(expense);
        verify(validator).validate(computed);
    }

    // ------------------------------------------------------
    // ❌ Validation Failure
    // ------------------------------------------------------
    @Test
    void shouldFailWhenValidationFails() {

        ExpenseRequest expense = new ExpenseRequest(
                LocalDateTime.now(),
                500
        );

        TransactionResponse computed = new TransactionResponse(
                expense.date(),
                500,
                400,
                -100
        );

        when(computationEngine.computeFromExpense(expense))
                .thenReturn(computed);

        doThrow(new BusinessRuleException("Remanent cannot be negative"))
                .when(validator).validate(computed);

        assertThatThrownBy(() ->
                service.parseExpenses(List.of(expense))
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Remanent");

        verify(computationEngine).computeFromExpense(expense);
        verify(validator).validate(computed);
    }

    // ------------------------------------------------------
    // ✅ Multiple Expenses
    // ------------------------------------------------------
    @Test
    void shouldHandleMultipleExpenses() {

        ExpenseRequest expense1 = new ExpenseRequest(
                LocalDateTime.now(),
                250
        );

        ExpenseRequest expense2 = new ExpenseRequest(
                LocalDateTime.now().plusMinutes(5),
                375
        );

        TransactionResponse tx1 = new TransactionResponse(
                expense1.date(),
                250,
                300,
                50
        );

        TransactionResponse tx2 = new TransactionResponse(
                expense2.date(),
                375,
                400,
                25
        );

        when(computationEngine.computeFromExpense(expense1)).thenReturn(tx1);
        when(computationEngine.computeFromExpense(expense2)).thenReturn(tx2);

        List<TransactionResponse> result =
                service.parseExpenses(List.of(expense1, expense2));

        assertThat(result)
                .hasSize(2)
                .containsExactly(tx1, tx2);

        verify(validator).validate(tx1);
        verify(validator).validate(tx2);
    }

    // ------------------------------------------------------
    // ✅ Empty Input
    // ------------------------------------------------------
    @Test
    void shouldReturnEmptyListWhenNoExpensesProvided() {

        List<TransactionResponse> result =
                service.parseExpenses(List.of());

        assertThat(result).isEmpty();

        verifyNoInteractions(computationEngine, validator);
    }
}
