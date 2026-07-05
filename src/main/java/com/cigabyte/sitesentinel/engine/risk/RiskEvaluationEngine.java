package com.cigabyte.sitesentinel.engine.risk;

import java.util.UUID;

public interface RiskEvaluationEngine {

    void evaluate(UUID monitoringRunId);
}