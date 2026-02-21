package com.juyel.blackrock.challenge.api.controller;

import com.juyel.blackrock.challenge.api.dto.TransactionsValidationRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionsValidationResponse;
import com.juyel.blackrock.challenge.application.service.TransactionValidationApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
@RequiredArgsConstructor
public class TransactionsValidatorController {

    private final TransactionValidationApplicationService validationService;

    @PostMapping("/transactions:validator")
    public TransactionsValidationResponse validate(
            @RequestBody @Valid TransactionsValidationRequest request
    ) {
        return validationService.validate(request);
    }
}
