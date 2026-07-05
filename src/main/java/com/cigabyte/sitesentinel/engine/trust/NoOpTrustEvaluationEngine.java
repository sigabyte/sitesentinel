package com.cigabyte.sitesentinel.engine.trust;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoOpTrustEvaluationEngine implements TrustEvaluationEngine {

    @Override
    public void assess(UUID monitoringRunId) {
        // Sprint 1 baseline:
        // Trust evaluation boundary exists, but no trust assessment is produced yet.
    }
}