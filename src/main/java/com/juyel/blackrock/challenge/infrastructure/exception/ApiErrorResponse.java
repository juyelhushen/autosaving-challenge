package com.juyel.blackrock.challenge.infrastructure.exception;

public record ApiErrorResponse(
        String errorCode,
        String errorMessage
) {}
