package com.juyel.blackrock.challenge.computation.temporal.engine;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.model.KPeriodRule;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class KPeriodAggregationEngine {

    public double aggregateForPeriod(
            List<TransactionResponse> transactions,
            KPeriodRule period
    ) {

        return transactions.stream()
                .filter(tx -> isWithin(tx.date(), period.start(), period.end()))
                .mapToDouble(TransactionResponse::remanent)
                .sum();
    }

    private boolean isWithin(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
