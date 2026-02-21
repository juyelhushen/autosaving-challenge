package com.juyel.blackrock.challenge.validator;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DuplicateTransactionDetector {

    public boolean isDuplicate(TransactionResponse tx, Set<LocalDateTime> seenDates) {
        return !seenDates.add(tx.date());
    }

    public Set<LocalDateTime> newTracker() {
        return new HashSet<>();
    }
}