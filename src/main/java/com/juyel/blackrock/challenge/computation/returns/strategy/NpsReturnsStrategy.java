package com.juyel.blackrock.challenge.computation.returns.strategy;

import org.springframework.stereotype.Component;

@Component
public class NpsReturnsStrategy implements InvestmentReturnsStrategy {

    @Override
    public double annualRate() {
        return 0.0711;   // 7.11%
    }
}
