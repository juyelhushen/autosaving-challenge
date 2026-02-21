package com.juyel.blackrock.challenge.computation.returns.strategy;


public interface InvestmentReturnsStrategy {

    double annualRate();

    default double calculateFutureValue(double principal, int years) {
        return principal * Math.pow(1 + annualRate(), years);
    }
}