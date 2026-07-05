package com.cigabyte.sitesentinel.engine.trust;

import java.util.UUID;

public interface TrustEvaluationEngine {

    void assess(UUID monitoringRunId);
}