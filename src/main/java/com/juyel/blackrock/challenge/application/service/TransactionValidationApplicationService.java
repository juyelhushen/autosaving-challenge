package com.juyel.blackrock.challenge.application.service;

import com.juyel.blackrock.challenge.api.dto.InvalidTransactionResponse;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.api.dto.TransactionsValidationRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionsValidationResponse;
import com.juyel.blackrock.challenge.validator.DuplicateTransactionDetector;
import com.juyel.blackrock.challenge.validator.TransactionIntegrityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionValidationApplicationService {

    private final TransactionIntegrityValidator validator;
    private final DuplicateTransactionDetector duplicateDetector;

    public TransactionsValidationResponse validate(TransactionsValidationRequest request) {

        List<TransactionResponse> valid = new ArrayList<>();
        List<InvalidTransactionResponse> invalid = new ArrayList<>();

        var seenDates = duplicateDetector.newTracker();

        for (TransactionResponse tx : request.transactions()) {

            try {

                if (duplicateDetector.isDuplicate(tx, seenDates)) {
                    invalid.add(toInvalid(tx, "Duplicate transaction date"));
                    continue;
                }

                validator.validate(tx, request.wage());

                valid.add(tx);

            } catch (Exception ex) {

                invalid.add(toInvalid(tx, ex.getMessage()));
            }
        }

        return new TransactionsValidationResponse(valid, invalid);
    }

    private InvalidTransactionResponse toInvalid(TransactionResponse tx, String message) {
        return new InvalidTransactionResponse(
                tx.date(),
                tx.amount(),
                tx.ceiling(),
                tx.remanent(),
                message
        );
    }


}
