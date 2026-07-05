CREATE TABLE collected_evidence (
                                    id UUID PRIMARY KEY,
                                    website_id UUID NOT NULL,
                                    monitoring_run_id UUID NOT NULL,
                                    source_type VARCHAR(80) NOT NULL,
                                    evidence_type VARCHAR(80) NOT NULL,
                                    source_url VARCHAR(500),
                                    raw_value TEXT NOT NULL,
                                    collected_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                    CONSTRAINT fk_collected_evidence_website
                                        FOREIGN KEY (website_id)
                                            REFERENCES websites(id)
                                            ON DELETE CASCADE,
                                    CONSTRAINT fk_collected_evidence_monitoring_run
                                        FOREIGN KEY (monitoring_run_id)
                                            REFERENCES monitoring_runs(id)
                                            ON DELETE CASCADE
);

CREATE INDEX idx_collected_evidence_website_id
    ON collected_evidence(website_id);

CREATE INDEX idx_collected_evidence_monitoring_run_id
    ON collected_evidence(monitoring_run_id);

CREATE INDEX idx_collected_evidence_type
    ON collected_evidence(evidence_type);


CREATE TABLE normalized_evidence (
                                     id UUID PRIMARY KEY,
                                     website_id UUID NOT NULL,
                                     monitoring_run_id UUID NOT NULL,
                                     collected_evidence_id UUID NOT NULL,
                                     normalized_type VARCHAR(80) NOT NULL,
                                     normalized_value TEXT NOT NULL,
                                     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                     CONSTRAINT fk_normalized_evidence_website
                                         FOREIGN KEY (website_id)
                                             REFERENCES websites(id)
                                             ON DELETE CASCADE,
                                     CONSTRAINT fk_normalized_evidence_monitoring_run
                                         FOREIGN KEY (monitoring_run_id)
                                             REFERENCES monitoring_runs(id)
                                             ON DELETE CASCADE,
                                     CONSTRAINT fk_normalized_evidence_collected_evidence
                                         FOREIGN KEY (collected_evidence_id)
                                             REFERENCES collected_evidence(id)
                                             ON DELETE CASCADE
);

CREATE INDEX idx_normalized_evidence_website_id
    ON normalized_evidence(website_id);

CREATE INDEX idx_normalized_evidence_monitoring_run_id
    ON normalized_evidence(monitoring_run_id);

CREATE INDEX idx_normalized_evidence_collected_evidence_id
    ON normalized_evidence(collected_evidence_id);

CREATE INDEX idx_normalized_evidence_type
    ON normalized_evidence(normalized_type);