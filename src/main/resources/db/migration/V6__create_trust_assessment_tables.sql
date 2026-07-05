CREATE TABLE trust_assessments (
                                   id UUID PRIMARY KEY,
                                   website_id UUID NOT NULL,
                                   monitoring_run_id UUID NOT NULL,
                                   trust_status VARCHAR(40) NOT NULL,
                                   trust_score INTEGER NOT NULL,
                                   confidence_score INTEGER NOT NULL,
                                   summary TEXT NOT NULL,
                                   created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                   CONSTRAINT fk_trust_assessments_website
                                       FOREIGN KEY (website_id)
                                           REFERENCES websites(id)
                                           ON DELETE CASCADE,
                                   CONSTRAINT fk_trust_assessments_monitoring_run
                                       FOREIGN KEY (monitoring_run_id)
                                           REFERENCES monitoring_runs(id)
                                           ON DELETE CASCADE,
                                   CONSTRAINT chk_trust_assessments_trust_score
                                       CHECK (trust_score >= 0 AND trust_score <= 100),
                                   CONSTRAINT chk_trust_assessments_confidence_score
                                       CHECK (confidence_score >= 0 AND confidence_score <= 100),
                                   CONSTRAINT chk_trust_assessments_status
                                       CHECK (trust_status IN ('TRUSTED', 'NEEDS_ATTENTION', 'HIGH_RISK', 'UNKNOWN'))
);

CREATE INDEX idx_trust_assessments_website_id
    ON trust_assessments(website_id);

CREATE INDEX idx_trust_assessments_monitoring_run_id
    ON trust_assessments(monitoring_run_id);

CREATE INDEX idx_trust_assessments_status
    ON trust_assessments(trust_status);


CREATE TABLE trust_assessment_risk (
                                       id UUID PRIMARY KEY,
                                       trust_assessment_id UUID NOT NULL,
                                       risk_id UUID NOT NULL,
                                       created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                       CONSTRAINT fk_trust_assessment_risk_assessment
                                           FOREIGN KEY (trust_assessment_id)
                                               REFERENCES trust_assessments(id)
                                               ON DELETE CASCADE,
                                       CONSTRAINT fk_trust_assessment_risk_risk
                                           FOREIGN KEY (risk_id)
                                               REFERENCES risks(id)
                                               ON DELETE CASCADE,
                                       CONSTRAINT uk_trust_assessment_risk_pair
                                           UNIQUE (trust_assessment_id, risk_id)
);

CREATE INDEX idx_trust_assessment_risk_assessment_id
    ON trust_assessment_risk(trust_assessment_id);

CREATE INDEX idx_trust_assessment_risk_risk_id
    ON trust_assessment_risk(risk_id);