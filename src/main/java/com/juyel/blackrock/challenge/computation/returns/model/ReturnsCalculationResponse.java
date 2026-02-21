package com.juyel.blackrock.challenge.computation.returns.model;

import java.util.List;

public record ReturnsCalculationResponse(
        double transactionsTotalAmount,
        double transactionsTotalCeiling,
        List<PeriodReturns> savingsByDates
) {}