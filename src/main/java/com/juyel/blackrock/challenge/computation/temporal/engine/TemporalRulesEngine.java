package com.juyel.blackrock.challenge.computation.temporal.engine;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.strategy.PRuleStrategy;
import com.juyel.blackrock.challenge.computation.temporal.strategy.QRuleStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TemporalRulesEngine {

    public List<TransactionResponse> applyTemporalRules(
            List<TransactionResponse> transactions,
            QRuleStrategy qStrategy,
            PRuleStrategy pStrategy
    ) {

        return transactions.stream()
                .map(qStrategy::apply)
                .map(pStrategy::apply)
                .toList();
    }
}