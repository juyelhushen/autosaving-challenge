package com.juyel.blackrock.challenge.computation.returns.calculator;

public interface InvestmentReturnCalculator {

    double calculateFutureValue(double investedAmount, int years);

    double calculateProfit(double investedAmount, int years);
}
