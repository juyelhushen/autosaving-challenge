package com.juyel.blackrock.challenge.api.dto;

import com.juyel.blackrock.challenge.computation.temporal.model.PPeriodRule;
import com.juyel.blackrock.challenge.computation.temporal.model.QPeriodRule;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TransactionsFilterRequest(

        @NotNull(message = "Transactions list is required")
        List<TransactionResponse> transactions,

        List<QPeriodRule> qPeriods,
        List<PPeriodRule> pPeriods

) {}