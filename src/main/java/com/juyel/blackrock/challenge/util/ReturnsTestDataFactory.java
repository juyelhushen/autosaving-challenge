package com.juyel.blackrock.challenge.util;

import com.juyel.blackrock.challenge.api.dto.ReturnsCalculationRequest;
import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.computation.temporal.model.KPeriodRule;

import java.time.LocalDateTime;
import java.util.List;

public class ReturnsTestDataFactory {

    private ReturnsTestDataFactory() {}

    public static TransactionResponse sampleTransaction() {
        return new TransactionResponse(
                LocalDateTime.of(2023, 10, 12, 20, 15),
                250,
                300,
                50
        );
    }

    // Standard K Period
    public static KPeriodRule fullYearPeriod() {
        return new KPeriodRule(
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 12, 31, 23, 59)
        );
    }

    // Base Returns Request
    public static ReturnsCalculationRequest standardReturnsRequest() {
        return new ReturnsCalculationRequest(
                29,
                50_000,
                0.055,
                List.of(),
                List.of(),
                List.of(fullYearPeriod()),
                List.of(TransactionTestDataFactory.validTransaction())
        );
    }

    // Retirement Age Scenario
    public static ReturnsCalculationRequest retirementAgeRequest() {
        return new ReturnsCalculationRequest(
                65,
                50_000,
                0.055,
                List.of(),
                List.of(),
                List.of(fullYearPeriod()),
                List.of(TransactionTestDataFactory.validTransaction())
        );
    }

    // Zero Inflation Scenario
    public static ReturnsCalculationRequest zeroInflationRequest() {
        return new ReturnsCalculationRequest(
                29,
                50_000,
                0.0,
                List.of(),
                List.of(),
                List.of(fullYearPeriod()),
                List.of(TransactionTestDataFactory.validTransaction())
        );
    }

    // Zero Investment Scenario
    public static ReturnsCalculationRequest zeroInvestmentRequest() {

        TransactionResponse zeroTx = new TransactionResponse(
                LocalDateTime.of(2023, 10, 12, 20, 15),
                100,
                100,
                0
        );

        return new ReturnsCalculationRequest(
                29,
                50_000,
                0.055,
                List.of(),
                List.of(),
                List.of(fullYearPeriod()),
                List.of(zeroTx)
        );
    }

    public static ReturnsCalculationRequest sampleReturnsRequest() {

        KPeriodRule kPeriod = new KPeriodRule(
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 12, 31, 23, 59)
        );

        return new ReturnsCalculationRequest(
                29,                 // age
                50_000,             // wage
                0.055,              // inflation
                List.of(),          // qPeriods
                List.of(),          // pPeriods
                List.of(kPeriod),   // kPeriods
                List.of(sampleTransaction())
        );
    }

    // With Custom Age
    public static ReturnsCalculationRequest sampleReturnsRequestWithAge(int age) {

        ReturnsCalculationRequest base = sampleReturnsRequest();

        return new ReturnsCalculationRequest(
                age,
                base.wage(),
                base.inflation(),
                base.qPeriods(),
                base.pPeriods(),
                base.kPeriods(),
                base.transactions()
        );
    }

    // With Custom Inflation
    public static ReturnsCalculationRequest sampleReturnsRequestWithInflation(double inflation) {

        ReturnsCalculationRequest base = sampleReturnsRequest();

        return new ReturnsCalculationRequest(
                base.age(),
                base.wage(),
                inflation,
                base.qPeriods(),
                base.pPeriods(),
                base.kPeriods(),
                base.transactions()
        );
    }

    // Zero Investment Scenario
    public static ReturnsCalculationRequest sampleReturnsRequestWithZeroInvestment() {

        TransactionResponse zeroTx = new TransactionResponse(
                LocalDateTime.of(2023, 10, 12, 20, 15),
                100,
                100,
                0
        );

        KPeriodRule kPeriod = new KPeriodRule(
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 12, 31, 23, 59)
        );

        return new ReturnsCalculationRequest(
                29,
                50_000,
                0.055,
                List.of(),
                List.of(),
                List.of(kPeriod),
                List.of(zeroTx)
        );
    }

}
