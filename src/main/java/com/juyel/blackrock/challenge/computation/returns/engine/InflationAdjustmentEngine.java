package com.juyel.blackrock.challenge.computation.returns.engine;

import org.springframework.stereotype.Component;

@Component
public class InflationAdjustmentEngine {

    public double adjust(double futureValue, double inflationRate, int years) {
        return futureValue / Math.pow(1 + inflationRate, years);
    }
}