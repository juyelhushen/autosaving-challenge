package com.juyel.blackrock.challenge.computation.returns.calculator;

import com.juyel.blackrock.challenge.computation.returns.strategy.IndexFundReturnsStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndexFundReturnCalculator implements InvestmentReturnCalculator {

    private final IndexFundReturnsStrategy strategy;

    @Override
    public double calculateFutureValue(double investedAmount, int years) {
        return investedAmount * Math.pow(1 + strategy.annualRate(), years);
    }

    @Override
    public double calculateProfit(double investedAmount, int years) {
        return calculateFutureValue(investedAmount, years) - investedAmount;
    }
}
