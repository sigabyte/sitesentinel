CREATE TABLE risks (
                       id UUID PRIMARY KEY,
                       website_id UUID NOT NULL,
                       monitoring_run_id UUID NOT NULL,
                       risk_type VARCHAR(100) NOT NULL,
                       severity VARCHAR(30) NOT NULL,
                       risk_score INTEGER NOT NULL,
                       confidence_score INTEGER NOT NULL,
                       rationale TEXT NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                       CONSTRAINT fk_risks_website
                           FOREIGN KEY (website_id)
                               REFERENCES websites(id)
                               ON DELETE CASCADE,
                       CONSTRAINT fk_risks_monitoring_run
                           FOREIGN KEY (monitoring_run_id)
                               REFERENCES monitoring_runs(id)
                               ON DELETE CASCADE,
                       CONSTRAINT chk_risks_risk_score
                           CHECK (risk_score >= 0 AND risk_score <= 100),
                       CONSTRAINT chk_risks_confidence_score
                           CHECK (confidence_score >= 0 AND confidence_score <= 100),
                       CONSTRAINT chk_risks_severity
                           CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

CREATE INDEX idx_risks_website_id
    ON risks(website_id);

CREATE INDEX idx_risks_monitoring_run_id
    ON risks(monitoring_run_id);

CREATE INDEX idx_risks_type
    ON risks(risk_type);

CREATE INDEX idx_risks_severity
    ON risks(severity);


CREATE TABLE risk_finding (
                              id UUID PRIMARY KEY,
                              risk_id UUID NOT NULL,
                              finding_id UUID NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              CONSTRAINT fk_risk_finding_risk
                                  FOREIGN KEY (risk_id)
                                      REFERENCES risks(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_risk_finding_finding
                                  FOREIGN KEY (finding_id)
                                      REFERENCES findings(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT uk_risk_finding_pair
                                  UNIQUE (risk_id, finding_id)
);

CREATE INDEX idx_risk_finding_risk_id
    ON risk_finding(risk_id);

CREATE INDEX idx_risk_finding_finding_id
    ON risk_finding(finding_id);