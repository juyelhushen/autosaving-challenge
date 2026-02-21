package com.juyel.blackrock.challenge.api.dto;

public record ErrorResponse(
        String code,
        String message
) {}