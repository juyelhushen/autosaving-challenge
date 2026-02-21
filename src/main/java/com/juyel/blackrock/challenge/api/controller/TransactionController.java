package com.juyel.blackrock.challenge.api.controller;

import com.juyel.blackrock.challenge.api.dto.ExpenseRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.application.service.TransactionApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blackrock/challenge/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionApplicationService applicationService;

    @PostMapping("/transactions:parse")
    public List<TransactionResponse> parseTransactions(
            @RequestBody @Valid List<ExpenseRequest> expenses
    ) {
        return applicationService.parseExpenses(expenses);
    }
}