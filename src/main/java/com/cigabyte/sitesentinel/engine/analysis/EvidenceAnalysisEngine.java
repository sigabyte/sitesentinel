package com.cigabyte.sitesentinel.engine.analysis;

import java.util.UUID;

public interface EvidenceAnalysisEngine {

    void analyze(UUID monitoringRunId);
}