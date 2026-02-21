package com.juyel.blackrock.challenge.api.dto;


import com.juyel.blackrock.challenge.computation.temporal.model.KPeriodRule;
import com.juyel.blackrock.challenge.computation.temporal.model.PPeriodRule;
import com.juyel.blackrock.challenge.computation.temporal.model.QPeriodRule;

import java.util.List;

public record ReturnsCalculationRequest(

        int age,
        double wage,
        double inflation,

        List<QPeriodRule> qPeriods,
        List<PPeriodRule> pPeriods,
        List<KPeriodRule> kPeriods,
        List<TransactionResponse> transactions

) {}
