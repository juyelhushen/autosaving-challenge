package com.juyel.blackrock.challenge.api.dto;

import java.util.List;

public record TransactionsValidationResponse(
        List<TransactionResponse> valid,
        List<InvalidTransactionResponse> invalid
) {}
