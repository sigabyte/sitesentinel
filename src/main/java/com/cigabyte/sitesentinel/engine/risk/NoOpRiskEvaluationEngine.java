package com.cigabyte.sitesentinel.engine.risk;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoOpRiskEvaluationEngine implements RiskEvaluationEngine {

    @Override
    public void evaluate(UUID monitoringRunId) {
        // Sprint 1 baseline:
        // Risk evaluation boundary exists, but no risks are produced yet.
    }
}