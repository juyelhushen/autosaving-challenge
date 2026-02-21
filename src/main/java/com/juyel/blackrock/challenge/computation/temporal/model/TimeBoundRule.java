package com.juyel.blackrock.challenge.computation.temporal.model;

import java.time.LocalDateTime;

public interface TimeBoundRule {
    LocalDateTime start();
    LocalDateTime end();
}