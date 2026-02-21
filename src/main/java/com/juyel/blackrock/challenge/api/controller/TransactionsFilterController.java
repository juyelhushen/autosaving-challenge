package com.juyel.blackrock.challenge.api.controller;

import com.juyel.blackrock.challenge.api.dto.TransactionsFilterRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionsFilterResponse;
import com.juyel.blackrock.challenge.application.service.TransactionFilteringApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
@RequiredArgsConstructor
public class TransactionsFilterController {

    private final TransactionFilteringApplicationService filteringService;

    @PostMapping("/transactions:filter")
    public TransactionsFilterResponse filter(
            @RequestBody @Valid TransactionsFilterRequest request
    ) {
        return filteringService.filterTransactions(request);
    }
}
