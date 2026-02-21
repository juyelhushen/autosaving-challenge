package com.juyel.blackrock.challenge.validator;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.infrastructure.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public class TransactionIntegrityValidator {

    public void validate(TransactionResponse tx, double wage) {

        if (tx.amount() <= 0)
            throw new BusinessRuleException("Transaction amount must be positive");

        if (tx.ceiling() < tx.amount())
            throw new BusinessRuleException("Ceiling cannot be less than amount");

        if (tx.remanent() < 0)
            throw new BusinessRuleException("Remanent cannot be negative");

        if (tx.remanent() > tx.ceiling())
            throw new BusinessRuleException("Remanent exceeds ceiling");

        if (wage < 0)
            throw new BusinessRuleException("Wage cannot be negative");
    }
}