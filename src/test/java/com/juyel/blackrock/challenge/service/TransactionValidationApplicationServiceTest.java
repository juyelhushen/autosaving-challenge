package com.juyel.blackrock.challenge.service;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.api.dto.TransactionsValidationRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionsValidationResponse;
import com.juyel.blackrock.challenge.application.service.TransactionValidationApplicationService;
import com.juyel.blackrock.challenge.infrastructure.exception.BusinessRuleException;
import com.juyel.blackrock.challenge.validator.DuplicateTransactionDetector;
import com.juyel.blackrock.challenge.validator.TransactionIntegrityValidator;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionValidationApplicationServiceTest {

    @Mock
    private TransactionIntegrityValidator validator;

    @Mock
    private DuplicateTransactionDetector duplicateDetector;

    @InjectMocks
    private TransactionValidationApplicationService service;

    // ------------------------------------------------------
    // ✅ Happy Path — All Valid
    // ------------------------------------------------------
    @Test
    void shouldClassifyAllTransactionsAsValid() {

        TransactionResponse tx1 = transaction("2023-10-12T10:00:00", 50);
        TransactionResponse tx2 = transaction("2023-10-13T10:00:00", 25);

        TransactionsValidationRequest request =
                new TransactionsValidationRequest(50_000, List.of(tx1, tx2));

        Set<LocalDateTime> tracker = new HashSet<>();

        when(duplicateDetector.newTracker()).thenReturn(tracker);
        when(duplicateDetector.isDuplicate(any(), eq(tracker))).thenReturn(false);

        TransactionsValidationResponse response = service.validate(request);

        assertThat(response.valid()).hasSize(2);
        assertThat(response.invalid()).isEmpty();

        verify(validator).validate(tx1, request.wage());
        verify(validator).validate(tx2, request.wage());
    }

    // ------------------------------------------------------
    // ❌ Duplicate Transaction
    // ------------------------------------------------------
    @Test
    void shouldClassifyDuplicateTransactionAsInvalid() {

        TransactionResponse tx = transaction("2023-10-12T10:00:00", 50);

        TransactionsValidationRequest request =
                new TransactionsValidationRequest(50_000, List.of(tx));

        Set<LocalDateTime> tracker = new HashSet<>();

        when(duplicateDetector.newTracker()).thenReturn(tracker);
        when(duplicateDetector.isDuplicate(tx, tracker)).thenReturn(true);

        TransactionsValidationResponse response = service.validate(request);

        assertThat(response.valid()).isEmpty();
        assertThat(response.invalid()).hasSize(1);
        assertThat(response.invalid().get(0).message())
                .isEqualTo("Duplicate transaction date");

        verifyNoInteractions(validator);
    }

    // ------------------------------------------------------
    // ❌ Validation Failure
    // ------------------------------------------------------
    @Test
    void shouldClassifyInvalidTransactionWhenValidatorFails() {

        TransactionResponse tx = transaction("2023-10-12T10:00:00", -50);

        TransactionsValidationRequest request =
                new TransactionsValidationRequest(50_000, List.of(tx));

        Set<LocalDateTime> tracker = new HashSet<>();

        when(duplicateDetector.newTracker()).thenReturn(tracker);
        when(duplicateDetector.isDuplicate(tx, tracker)).thenReturn(false);

        doThrow(new BusinessRuleException("Remanent cannot be negative"))
                .when(validator).validate(tx, request.wage());

        TransactionsValidationResponse response = service.validate(request);

        assertThat(response.valid()).isEmpty();
        assertThat(response.invalid()).hasSize(1);
        assertThat(response.invalid().get(0).message())
                .contains("Remanent");
    }


    @Test
    void shouldHandleEmptyTransactionList() {

        TransactionsValidationRequest request =
                new TransactionsValidationRequest(50_000, List.of());

        when(duplicateDetector.newTracker()).thenReturn(new HashSet<>());

        TransactionsValidationResponse response = service.validate(request);

        assertThat(response.valid()).isEmpty();
        assertThat(response.invalid()).isEmpty();

        verifyNoInteractions(validator);
    }

    // ------------------------------------------------------
    // 🧰 Helper
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
