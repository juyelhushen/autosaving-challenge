package com.juyel.blackrock.challenge.computation.returns.strategy;

import org.springframework.stereotype.Component;

@Component
public class IndexFundReturnsStrategy implements InvestmentReturnsStrategy {

    @Override
    public double annualRate() {
        return 0.1449;   // 14.49%
    }
}