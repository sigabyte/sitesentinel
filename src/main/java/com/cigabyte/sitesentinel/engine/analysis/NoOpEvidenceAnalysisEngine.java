package com.cigabyte.sitesentinel.engine.analysis;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoOpEvidenceAnalysisEngine implements EvidenceAnalysisEngine {

    @Override
    public void analyze(UUID monitoringRunId) {
        // Sprint 1 baseline:
        // Analysis boundary exists, but no findings are produced yet.
    }
}