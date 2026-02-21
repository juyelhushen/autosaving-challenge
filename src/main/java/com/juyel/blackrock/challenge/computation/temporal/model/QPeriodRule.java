package com.juyel.blackrock.challenge.computation.temporal.model;

import java.time.LocalDateTime;

public record QPeriodRule(
        double fixed,
        LocalDateTime start,
        LocalDateTime end
) implements TimeBoundRule {}