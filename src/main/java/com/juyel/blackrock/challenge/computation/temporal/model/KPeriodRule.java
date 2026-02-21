package com.juyel.blackrock.challenge.computation.temporal.model;

import java.time.LocalDateTime;

public record KPeriodRule(
        LocalDateTime start,
        LocalDateTime end
) implements TimeBoundRule {}
