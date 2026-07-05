package com.cigabyte.sitesentinel.engine.collection;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoOpEvidenceCollectionEngine implements EvidenceCollectionEngine {

    @Override
    public void collect(UUID monitoringRunId) {
        // Sprint 1 baseline:
        // Collection boundary exists, but no real website scanning is executed yet.
    }
}