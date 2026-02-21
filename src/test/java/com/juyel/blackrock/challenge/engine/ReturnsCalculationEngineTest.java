package com.juyel.blackrock.challenge.engine;

import com.juyel.blackrock.challenge.api.dto.ReturnsCalculationRequest;
import com.juyel.blackrock.challenge.computation.returns.calculator.IndexFundReturnCalculator;
import com.juyel.blackrock.challenge.computation.returns.calculator.NpsReturnCalculator;
import com.juyel.blackrock.challenge.computation.returns.engine.InflationAdjustmentEngine;
import com.juyel.blackrock.challenge.computation.returns.engine.ReturnsCalculationEngine;
import com.juyel.blackrock.challenge.computation.returns.engine.TaxComputationEngine;
import com.juyel.blackrock.challenge.computation.returns.model.PeriodReturns;
import com.juyel.blackrock.challenge.computation.returns.model.ReturnsCalculationResponse;
import com.juyel.blackrock.challenge.computation.temporal.engine.KPeriodAggregationEngine;
import com.juyel.blackrock.challenge.util.ReturnsTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnsCalculationEngineTest {

    @Mock
    KPeriodAggregationEngine aggregationEngine;

    @Mock
    TaxComputationEngine taxEngine;

    @Mock
    InflationAdjustmentEngine inflationEngine;

    @Mock
    NpsReturnCalculator npsCalculator;

    @Mock
    IndexFundReturnCalculator indexCalculator;

    @InjectMocks
    ReturnsCalculationEngine engine;


    // Happy Path — NPS
    @Test
    void shouldCalculateNpsReturnsSuccessfully() {

        ReturnsCalculationRequest request = ReturnsTestDataFactory.sampleReturnsRequest();

        when(aggregationEngine.aggregateForPeriod(any(), any())).thenReturn(100.0);
        when(npsCalculator.calculateFutureValue(100.0, 31)).thenReturn(200.0);
        when(inflationEngine.adjust(200.0, 0.055, 31)).thenReturn(150.0);
        when(taxEngine.calculateTaxBenefit(anyDouble(), anyDouble())).thenReturn(10.0);

        ReturnsCalculationResponse response = engine.calculateNps(request);

        assertThat(response).isNotNull();
        assertThat(response.savingsByDates()).hasSize(1);

        PeriodReturns period = response.savingsByDates().get(0);

        assertThat(period.investedAmount()).isEqualTo(100.0);
        assertThat(period.profit()).isEqualTo(100.0);
        assertThat(period.taxBenefit()).isEqualTo(10.0);
        assertThat(period.inflationAdjustedValue()).isEqualTo(150.0);
    }


    //Happy Path — Index Fund
    @Test
    void shouldCalculateIndexReturnsSuccessfully() {

        ReturnsCalculationRequest request = ReturnsTestDataFactory.sampleReturnsRequest();

        when(aggregationEngine.aggregateForPeriod(any(), any())).thenReturn(100.0);
        when(indexCalculator.calculateFutureValue(100.0, 31)).thenReturn(500.0);
        when(inflationEngine.adjust(500.0, 0.055, 31)).thenReturn(300.0);

        ReturnsCalculationResponse response = engine.calculateIndex(request);

        PeriodReturns period = response.savingsByDates().get(0);

        assertThat(period.taxBenefit()).isZero();   //No tax for index
        assertThat(period.profit()).isEqualTo(400.0);
    }


    // Age ≥ 60 → Use 5 Years
    @Test
    void shouldUseFiveYearsWhenAgeAboveSixty() {

        ReturnsCalculationRequest request = ReturnsTestDataFactory.sampleReturnsRequestWithAge(65);

        when(aggregationEngine.aggregateForPeriod(any(), any())).thenReturn(100.0);
        when(npsCalculator.calculateFutureValue(100.0, 5)).thenReturn(120.0);
        when(inflationEngine.adjust(120.0, 0.055, 5)).thenReturn(100.0);
        when(taxEngine.calculateTaxBenefit(anyDouble(), anyDouble())).thenReturn(5.0);

        ReturnsCalculationResponse response = engine.calculateNps(request);

        verify(npsCalculator).calculateFutureValue(100.0, 5);
    }

    // Tax Benefit Applied (NPS only)

    @Test
    void shouldApplyTaxBenefitForNps() {

        ReturnsCalculationRequest request = ReturnsTestDataFactory.sampleReturnsRequest();

        when(aggregationEngine.aggregateForPeriod(any(), any())).thenReturn(200_000.0);
        when(npsCalculator.calculateFutureValue(anyDouble(), anyInt())).thenReturn(300_000.0);
        when(inflationEngine.adjust(anyDouble(), anyDouble(), anyInt())).thenReturn(250_000.0);
        when(taxEngine.calculateTaxBenefit(anyDouble(), anyDouble())).thenReturn(20_000.0);

        engine.calculateNps(request);

        verify(taxEngine).calculateTaxBenefit(anyDouble(), anyDouble());
    }


    //  No Tax Benefit for Index
    @Test
    void shouldNotApplyTaxBenefitForIndex() {

        ReturnsCalculationRequest request = ReturnsTestDataFactory.sampleReturnsRequest();

        when(aggregationEngine.aggregateForPeriod(any(), any())).thenReturn(100.0);
        when(indexCalculator.calculateFutureValue(anyDouble(), anyInt())).thenReturn(300.0);
        when(inflationEngine.adjust(anyDouble(), anyDouble(), anyInt())).thenReturn(200.0);

        engine.calculateIndex(request);

        verifyNoInteractions(taxEngine);
    }


    // Zero Investment Edge Case
    @Test
    void shouldHandleZeroInvestmentGracefully() {

        ReturnsCalculationRequest request = ReturnsTestDataFactory.sampleReturnsRequest();

        when(aggregationEngine.aggregateForPeriod(any(), any())).thenReturn(0.0);
        when(npsCalculator.calculateFutureValue(0.0, 31)).thenReturn(0.0);
        when(inflationEngine.adjust(0.0, 0.055, 31)).thenReturn(0.0);

        ReturnsCalculationResponse response = engine.calculateNps(request);

        PeriodReturns period = response.savingsByDates().get(0);

        assertThat(period.investedAmount()).isZero();
        assertThat(period.profit()).isZero();
        assertThat(period.inflationAdjustedValue()).isZero();
    }


    // Zero Inflation Edge Case
    @Test
    void shouldHandleZeroInflationCorrectly() {

        ReturnsCalculationRequest request = ReturnsTestDataFactory.sampleReturnsRequestWithInflation(0.0);

        when(aggregationEngine.aggregateForPeriod(any(), any())).thenReturn(100.0);
        when(npsCalculator.calculateFutureValue(100.0, 31)).thenReturn(200.0);
        when(inflationEngine.adjust(200.0, 0.0, 31)).thenReturn(200.0);
        when(taxEngine.calculateTaxBenefit(anyDouble(), anyDouble())).thenReturn(5.0);

        ReturnsCalculationResponse response = engine.calculateNps(request);

        PeriodReturns period = response.savingsByDates().get(0);

        assertThat(period.inflationAdjustedValue()).isEqualTo(200.0);
    }
}