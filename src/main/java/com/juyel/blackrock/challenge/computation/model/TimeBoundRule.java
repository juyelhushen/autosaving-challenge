package com.juyel.blackrock.challenge.computation.model;

import java.time.LocalDateTime;

public interface TimeBoundRule {
    LocalDateTime start();
    LocalDateTime end();
}