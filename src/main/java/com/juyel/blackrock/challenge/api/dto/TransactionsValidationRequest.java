package com.juyel.blackrock.challenge.api.dto;

import java.util.List;

public record TransactionsValidationRequest(
        double wage,
        List<TransactionResponse> transactions
) {}
