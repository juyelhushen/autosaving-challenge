package com.juyel.blackrock.challenge.computation.returns.calculator;

import com.juyel.blackrock.challenge.computation.returns.strategy.NpsReturnsStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NpsReturnCalculator implements InvestmentReturnCalculator {

    private final NpsReturnsStrategy strategy;

    @Override
    public double calculateFutureValue(double investedAmount, int years) {
        return investedAmount * Math.pow(1 + strategy.annualRate(), years);
    }

    @Override
    public double calculateProfit(double investedAmount, int years) {
        return calculateFutureValue(investedAmount, years) - investedAmount;
    }
}