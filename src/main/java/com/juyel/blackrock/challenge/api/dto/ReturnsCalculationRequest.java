package com.juyel.blackrock.challenge.api.dto;


import com.juyel.blackrock.challenge.computation.temporal.model.KPeriodRule;
import com.juyel.blackrock.challenge.computation.temporal.model.PPeriodRule;
import com.juyel.blackrock.challenge.computation.temporal.model.QPeriodRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ReturnsCalculationRequest(

        @Min(value = 1, message = "Age must be >= 1")
        int age,

        @Positive(message = "Wage must be positive")
        double wage,

        @DecimalMin(value = "0.0", message = "Inflation cannot be negative")
        double inflation,

        List<QPeriodRule> qPeriods,
        List<PPeriodRule> pPeriods,

        @Valid
        @NotEmpty(message = "K periods cannot be empty")
        List<KPeriodRule> kPeriods,

        @Valid
        @NotEmpty(message = "Transactions cannot be empty")
        List<TransactionResponse> transactions

) {}
