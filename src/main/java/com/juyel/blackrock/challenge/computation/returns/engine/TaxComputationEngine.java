package com.juyel.blackrock.challenge.computation.returns.engine;


import org.springframework.stereotype.Component;

@Component
public class TaxComputationEngine {

    public double calculateTax(double income) {

        if (income <= 700_000) return 0;

        if (income <= 1_000_000)
            return (income - 700_000) * 0.10;

        if (income <= 1_200_000)
            return 30_000 + (income - 1_000_000) * 0.15;

        if (income <= 1_500_000)
            return 60_000 + (income - 1_200_000) * 0.20;

        return 120_000 + (income - 1_500_000) * 0.30;
    }

    public double calculateTaxBenefit(double income, double deduction) {

        double taxBefore = calculateTax(income);
        double taxAfter = calculateTax(income - deduction);
        return taxBefore - taxAfter;

    }
}
