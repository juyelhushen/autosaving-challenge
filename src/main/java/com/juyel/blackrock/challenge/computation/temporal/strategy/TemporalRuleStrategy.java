package com.juyel.blackrock.challenge.computation.temporal.strategy;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;

public interface TemporalRuleStrategy {
    TransactionResponse apply(TransactionResponse tx);
}
