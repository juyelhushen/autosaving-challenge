package com.juyel.blackrock.challenge.computation.returns.engine;

import com.juyel.blackrock.challenge.api.dto.ReturnsCalculationRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.returns.calculator.IndexFundReturnCalculator;
import com.juyel.blackrock.challenge.computation.returns.calculator.InvestmentReturnCalculator;
import com.juyel.blackrock.challenge.computation.returns.calculator.NpsReturnCalculator;
import com.juyel.blackrock.challenge.computation.returns.model.PeriodReturns;
import com.juyel.blackrock.challenge.computation.returns.model.ReturnsCalculationResponse;
import com.juyel.blackrock.challenge.computation.temporal.engine.KPeriodAggregationEngine;
import com.juyel.blackrock.challenge.computation.temporal.model.KPeriodRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReturnsCalculationEngine {

    private final KPeriodAggregationEngine aggregationEngine;
    private final TaxComputationEngine taxEngine;
    private final InflationAdjustmentEngine inflationEngine;

    private final NpsReturnCalculator npsCalculator;
    private final IndexFundReturnCalculator indexCalculator;

    public ReturnsCalculationResponse calculateNps(ReturnsCalculationRequest request) {
        return calculate(request, npsCalculator, true);
    }

    public ReturnsCalculationResponse calculateIndex(ReturnsCalculationRequest request) {
        return calculate(request, indexCalculator, false);
    }

    private ReturnsCalculationResponse calculate(
            ReturnsCalculationRequest request,
            InvestmentReturnCalculator calculator,
            boolean applyTaxBenefit
    ) {

        int years = request.age() >= 60 ? 5 : (60 - request.age());

        double totalAmount = request.transactions().stream()
                .mapToDouble(TransactionResponse::amount)
                .sum();

        double totalCeiling = request.transactions().stream()
                .mapToDouble(TransactionResponse::ceiling)
                .sum();

        List<PeriodReturns> periodResults = request.kPeriods().stream()
                .map(period -> computePeriodReturn(period, request, calculator, years, applyTaxBenefit))
                .toList();

        return new ReturnsCalculationResponse(totalAmount, totalCeiling, periodResults);
    }

    private PeriodReturns computePeriodReturn(
            KPeriodRule period,
            ReturnsCalculationRequest request,
            InvestmentReturnCalculator calculator,
            int years,
            boolean applyTaxBenefit
    ) {

        double invested = aggregationEngine.aggregateForPeriod(request.transactions(), period);

        double futureValue = calculator.calculateFutureValue(invested, years);
        double profit = futureValue - invested;

        double taxBenefit = 0;

        if (applyTaxBenefit) {

            double annualIncome = request.wage() * 12;
            double deductionCap = Math.min(invested, Math.min(annualIncome * 0.10, 200_000));

            taxBenefit = taxEngine.calculateTaxBenefit(annualIncome, deductionCap);
        }

        double realValue = inflationEngine.adjust(futureValue, request.inflation(), years);

        return new PeriodReturns(
                period.start(),
                period.end(),
                invested,
                profit,
                taxBenefit,
                realValue
        );
    }
}