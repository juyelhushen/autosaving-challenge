package com.juyel.blackrock.challenge.api.controller;

import com.juyel.blackrock.challenge.api.dto.ReturnsCalculationRequest;
import com.juyel.blackrock.challenge.computation.returns.engine.ReturnsCalculationEngine;
import com.juyel.blackrock.challenge.computation.returns.model.ReturnsCalculationResponse;
import com.juyel.blackrock.challenge.computation.returns.strategy.IndexFundReturnsStrategy;
import com.juyel.blackrock.challenge.computation.returns.strategy.NpsReturnsStrategy;
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
    private final NpsReturnsStrategy npsStrategy;
    private final IndexFundReturnsStrategy indexStrategy;

    @PostMapping("/returns:nps")
    public ReturnsCalculationResponse calculateNps(@RequestBody ReturnsCalculationRequest request) {
        return engine.calculate(request, npsStrategy, true);
    }

    @PostMapping("/returns:index")
    public ReturnsCalculationResponse calculateIndex(@RequestBody ReturnsCalculationRequest request) {
        return engine.calculate(request, indexStrategy, false);
    }
}