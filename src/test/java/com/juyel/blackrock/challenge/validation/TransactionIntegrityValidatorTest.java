package com.juyel.blackrock.challenge.validation;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.infrastructure.exception.BusinessRuleException;
import com.juyel.blackrock.challenge.validator.TransactionIntegrityValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TransactionIntegrityValidatorTest {


    private final TransactionIntegrityValidator validator
            = new TransactionIntegrityValidator();

    @Test
    void shouldPassValidTransaction() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                500,
                600,
                100
        );

        assertThatCode(() -> validator.validate(tx, 50000))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectNegativeAmount() {

        TransactionResponse tx = new TransactionResponse(
                LocalDateTime.now(),
                -100,
                0,
                0
        );

        assertThatThrownBy(() -> validator.validate(tx, 50000))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("positive");
    }
}
