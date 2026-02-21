package com.juyel.blackrock.challenge.util;

import com.juyel.blackrock.challenge.api.dto.ExpenseRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionTestDataFactory {
    private TransactionTestDataFactory() {}

    // Valid Transaction
    public static TransactionResponse validTransaction() {
        return new TransactionResponse(
                LocalDateTime.of(2023, 10, 12, 20, 15),
                250,
                300,
                50
        );
    }


    // Negative Amount Transaction
    public static TransactionResponse negativeAmountTransaction() {
        return new TransactionResponse(
                LocalDateTime.of(2023, 7, 1, 10, 0),
                -100,
                0,
                0
        );
    }


    //Invalid Remanent Transaction

    public static TransactionResponse invalidRemanentTransaction() {
        return new TransactionResponse(
                LocalDateTime.of(2023, 8, 1, 10, 0),
                500,
                600,
                -50
        );
    }

    // Duplicate Transactions
    public static List<TransactionResponse> duplicateTransactions() {

        LocalDateTime sameDate = LocalDateTime.of(2023, 10, 12, 20, 15);

        return List.of(
                new TransactionResponse(sameDate, 250, 300, 50),
                new TransactionResponse(sameDate, 375, 400, 25)
        );
    }

    // ✅ Expense Sample
    public static ExpenseRequest validExpense() {
        return new ExpenseRequest(
                LocalDateTime.of(2023, 10, 12, 20, 15),
                1519
        );
    }
}
