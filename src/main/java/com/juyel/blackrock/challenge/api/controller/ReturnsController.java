package com.juyel.blackrock.challenge.api.controller;

import com.juyel.blackrock.challenge.api.dto.ReturnsCalculationRequest;
import com.juyel.blackrock.challenge.computation.returns.engine.ReturnsCalculationEngine;
import com.juyel.blackrock.challenge.computation.returns.model.ReturnsCalculationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
@RequiredArgsConstructor
public class ReturnsController {

    private final ReturnsCalculationEngine engine;

    @PostMapping("/returns:nps")
    public ReturnsCalculationResponse calculateNps(@RequestBody @Valid ReturnsCalculationRequest request) {
        return engine.calculateNps(request);
    }

    @PostMapping("/returns:index")
    public ReturnsCalculationResponse calculateIndex(@RequestBody @Valid ReturnsCalculationRequest request) {
        return engine.calculateIndex(request);
    }
}