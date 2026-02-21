package com.juyel.blackrock.challenge.api.dto;

import java.time.LocalDateTime;

public record TransactionResponse(
        LocalDateTime date,
        double amount,
        double ceiling,
        double remanent
) {}