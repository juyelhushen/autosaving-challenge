package com.juyel.blackrock.challenge.application.service;

import com.juyel.blackrock.challenge.api.dto.TransactionsFilterRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionsFilterResponse;
import com.juyel.blackrock.challenge.computation.temporal.engine.TemporalRulesEngine;
import com.juyel.blackrock.challenge.computation.temporal.strategy.PRuleStrategy;
import com.juyel.blackrock.challenge.computation.temporal.strategy.QRuleStrategy;
import com.juyel.blackrock.challenge.computation.temporal.strategy.TemporalRuleStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionFilteringApplicationService {

    private final TemporalRulesEngine rulesEngine;
    private final TemporalRuleStrategyFactory strategyFactory;

    public TransactionsFilterResponse filterTransactions(TransactionsFilterRequest request) {

        QRuleStrategy qStrategy = strategyFactory.createQStrategy(request.qPeriods());
        PRuleStrategy pStrategy = strategyFactory.createPStrategy(request.pPeriods());

        var filtered = rulesEngine.applyTemporalRules(
                request.transactions(),
                qStrategy,
                pStrategy
        );

        return new TransactionsFilterResponse(filtered);
    }
}
