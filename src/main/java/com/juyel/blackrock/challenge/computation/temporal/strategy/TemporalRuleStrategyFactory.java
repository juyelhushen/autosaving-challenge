package com.juyel.blackrock.challenge.computation.temporal.strategy;

import com.juyel.blackrock.challenge.computation.temporal.model.PPeriodRule;
import com.juyel.blackrock.challenge.computation.temporal.model.QPeriodRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TemporalRuleStrategyFactory {

    public QRuleStrategy createQStrategy(List<QPeriodRule> qRules) {
        return new QRuleStrategy(qRules);
    }

    public PRuleStrategy createPStrategy(List<PPeriodRule> pRules) {
        return new PRuleStrategy(pRules);
    }
}
