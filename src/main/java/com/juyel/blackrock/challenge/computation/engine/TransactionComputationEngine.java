package com.juyel.blackrock.challenge.computation.engine;

import com.juyel.blackrock.challenge.api.dto.ExpenseRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionComputationEngine {

    public TransactionResponse computeFromExpense(ExpenseRequest request) {

        double ceiling = Math.ceil(request.amount() / 100.0) * 100;
        double remanent = ceiling - request.amount();

        return new TransactionResponse(
                request.date(),
                request.amount(),
                ceiling,
                remanent
        );
    }
}
