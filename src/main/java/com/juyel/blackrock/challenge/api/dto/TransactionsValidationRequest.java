package com.juyel.blackrock.challenge.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record TransactionsValidationRequest(
        @Positive(message = "Wage must be positive")
        double wage,

        @NotNull(message = "Transactions list cannot be null")
        List<TransactionResponse> transactions
) {}
