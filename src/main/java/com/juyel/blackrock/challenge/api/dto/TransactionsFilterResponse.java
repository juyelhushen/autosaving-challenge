package com.juyel.blackrock.challenge.api.dto;

import java.util.List;

public record TransactionsFilterResponse(
        List<TransactionResponse> filteredTransactions
) {}
