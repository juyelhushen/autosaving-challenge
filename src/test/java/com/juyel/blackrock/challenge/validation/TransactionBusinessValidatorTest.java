package com.juyel.blackrock.challenge.validation;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.infrastructure.exception.BusinessRuleException;
import com.juyel.blackrock.challenge.validator.TransactionBusinessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class TransactionBusinessValidatorTest {

    private TransactionBusinessValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TransactionBusinessValidator();
    }

    @Test
    void shouldPassForValidTransaction() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                500,   // amount
                600,   // ceiling
                100    // remanent
        );

        assertThatCode(() -> validator.validate(tx))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenRemanentIsNegative() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                500,
                600,
                -10    // invalid remanent
        );

        assertThatThrownBy(() -> validator.validate(tx))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Remanent cannot be negative");
    }

    @Test
    void shouldThrowExceptionWhenCeilingLessThanAmount() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                700,   // amount
                600,   // invalid ceiling
                50
        );

        assertThatThrownBy(() -> validator.validate(tx))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Ceiling must be >= amount");
    }

    @Test
    void shouldAllowZeroRemanent() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                500,
                500,
                0
        );

        assertThatCode(() -> validator.validate(tx))
                .doesNotThrowAnyException();
    }
}
