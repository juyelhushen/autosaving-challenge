package com.juyel.blackrock.challenge.api.dto;

import com.juyel.blackrock.challenge.computation.temporal.model.PPeriodRule;
import com.juyel.blackrock.challenge.computation.temporal.model.QPeriodRule;

import java.util.List;

public record TransactionsFilterRequest(

        List<TransactionResponse> transactions,
        List<QPeriodRule> qPeriods,
        List<PPeriodRule> pPeriods

) {}