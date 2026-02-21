package com.juyel.blackrock.challenge.api.dto;

import java.time.LocalDateTime;

public record InvalidTransactionResponse(
        LocalDateTime date,
        double amount,
        double ceiling,
        double remanent,
        String message
) {}