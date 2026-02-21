package com.juyel.blackrock.challenge.computation.temporal.strategy;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.model.PPeriodRule;

import java.time.LocalDateTime;
import java.util.List;

public class PRuleStrategy implements TemporalRuleStrategy {

    private final List<PPeriodRule> pRules;

    public PRuleStrategy(List<PPeriodRule> pRules) {
        this.pRules = pRules;
    }

    @Override
    public TransactionResponse apply(TransactionResponse tx) {

        LocalDateTime date = tx.date();

        double extraSum = pRules.stream()
                .filter(rule -> isWithin(date, rule.start(), rule.end()))
                .mapToDouble(PPeriodRule::extra)
                .sum();

        if (extraSum == 0) return tx;

        return new TransactionResponse(
                tx.date(),
                tx.amount(),
                tx.ceiling(),
                tx.remanent() + extraSum
        );
    }

    private boolean isWithin(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
