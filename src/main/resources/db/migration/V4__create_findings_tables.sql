CREATE TABLE findings (
                          id UUID PRIMARY KEY,
                          website_id UUID NOT NULL,
                          monitoring_run_id UUID NOT NULL,
                          finding_type VARCHAR(100) NOT NULL,
                          title VARCHAR(220) NOT NULL,
                          description TEXT NOT NULL,
                          confidence_score INTEGER NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          CONSTRAINT fk_findings_website
                              FOREIGN KEY (website_id)
                                  REFERENCES websites(id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_findings_monitoring_run
                              FOREIGN KEY (monitoring_run_id)
                                  REFERENCES monitoring_runs(id)
                                  ON DELETE CASCADE,
                          CONSTRAINT chk_findings_confidence_score
                              CHECK (confidence_score >= 0 AND confidence_score <= 100)
);

CREATE INDEX idx_findings_website_id
    ON findings(website_id);

CREATE INDEX idx_findings_monitoring_run_id
    ON findings(monitoring_run_id);

CREATE INDEX idx_findings_type
    ON findings(finding_type);


CREATE TABLE finding_evidence (
                                  id UUID PRIMARY KEY,
                                  finding_id UUID NOT NULL,
                                  collected_evidence_id UUID NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                  CONSTRAINT fk_finding_evidence_finding
                                      FOREIGN KEY (finding_id)
                                          REFERENCES findings(id)
                                          ON DELETE CASCADE,
                                  CONSTRAINT fk_finding_evidence_collected_evidence
                                      FOREIGN KEY (collected_evidence_id)
                                          REFERENCES collected_evidence(id)
                                          ON DELETE CASCADE,
                                  CONSTRAINT uk_finding_evidence_pair
                                      UNIQUE (finding_id, collected_evidence_id)
);

CREATE INDEX idx_finding_evidence_finding_id
    ON finding_evidence(finding_id);

CREATE INDEX idx_finding_evidence_collected_evidence_id
    ON finding_evidence(collected_evidence_id);