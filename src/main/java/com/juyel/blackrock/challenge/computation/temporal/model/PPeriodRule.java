package com.juyel.blackrock.challenge.computation.temporal.model;

import java.time.LocalDateTime;

public record PPeriodRule(
        double extra,
        LocalDateTime start,
        LocalDateTime end
) implements TimeBoundRule {}