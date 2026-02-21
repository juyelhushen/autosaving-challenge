package com.juyel.blackrock.challenge.application.service;

import com.juyel.blackrock.challenge.api.dto.ExpenseRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.engine.TransactionComputationEngine;
import com.juyel.blackrock.challenge.validator.TransactionBusinessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionApplicationService  {

    private final TransactionComputationEngine computationEngine;
    private final TransactionBusinessValidator validator;

    public List<TransactionResponse> parseExpenses(List<ExpenseRequest> requests) {

        return requests.stream()
                .map(computationEngine::computeFromExpense)
                .peek(validator::validate)
                .toList();
    }
}
