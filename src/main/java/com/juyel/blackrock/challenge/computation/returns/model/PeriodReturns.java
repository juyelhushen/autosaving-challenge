package com.juyel.blackrock.challenge.computation.returns.model;

import java.time.LocalDateTime;

public record PeriodReturns(
        LocalDateTime start,
        LocalDateTime end,
        double investedAmount,
        double profit,
        double taxBenefit,
        double inflationAdjustedValue
) {}