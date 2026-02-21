package com.juyel.blackrock.challenge.validator;


import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionBusinessValidator  {

    public void validate(TransactionResponse tx) {

        if (tx.remanent() < 0)
            throw new BusinessRuleException("Remanent cannot be negative");

        if (tx.ceiling() < tx.amount())
            throw new BusinessRuleException("Ceiling must be >= amount");
    }

}
