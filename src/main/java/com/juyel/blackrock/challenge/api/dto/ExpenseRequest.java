package com.juyel.blackrock.challenge.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ExpenseRequest(

        @NotNull(message = "Expense date is required")
        LocalDateTime date,

        @Positive(message = "Expense amount must be positive")
        double amount
) {}