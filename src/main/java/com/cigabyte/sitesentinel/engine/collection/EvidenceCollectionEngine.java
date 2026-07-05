package com.cigabyte.sitesentinel.engine.collection;

import java.util.UUID;

public interface EvidenceCollectionEngine {

    void collect(UUID monitoringRunId);
}