package com.juyel.blackrock.challenge.computation.temporal.strategy;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.model.QPeriodRule;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class QRuleStrategy implements TemporalRuleStrategy {

    private final List<QPeriodRule> qRules;

    public QRuleStrategy(List<QPeriodRule> qRules) {
        this.qRules = qRules;
    }

    @Override
    public TransactionResponse apply(TransactionResponse tx) {

        LocalDateTime date = tx.date();

        return qRules.stream()
                .filter(rule -> isWithin(date, rule.start(), rule.end()))
                .max(Comparator.comparing(QPeriodRule::start)) // latest start wins
                .map(rule -> override(tx, rule.fixed()))
                .orElse(tx);
    }

    private boolean isWithin(LocalDateTime date, LocalDateTime start,
                             LocalDateTime end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    private TransactionResponse override(TransactionResponse tx, double fixed) {
        return new TransactionResponse(
                tx.date(),
                tx.amount(),
                tx.ceiling(),
                fixed
        );
    }
}