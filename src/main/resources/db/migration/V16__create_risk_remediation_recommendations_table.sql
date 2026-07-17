CREATE TABLE risk_remediation_recommendations (
                                                  id UUID PRIMARY KEY,
                                                  monitoring_run_id UUID NOT NULL,
                                                  risk_id UUID NOT NULL,
                                                  source VARCHAR(40) NOT NULL,
                                                  fallback_reason VARCHAR(40) NOT NULL,
                                                  validation_status VARCHAR(20) NOT NULL,
                                                  title VARCHAR(220) NOT NULL,
                                                  summary TEXT NOT NULL,
                                                  remediation_steps TEXT NOT NULL,
                                                  verification_steps TEXT NOT NULL,
                                                  advisory BOOLEAN NOT NULL,
                                                  provider_name VARCHAR(80),
                                                  model_name VARCHAR(120),
                                                  prompt_version VARCHAR(80) NOT NULL,
                                                  fallback_rule_version VARCHAR(80),
                                                  context_fingerprint VARCHAR(64) NOT NULL,
                                                  context_finding_count INTEGER NOT NULL,
                                                  context_evidence_count INTEGER NOT NULL,
                                                  generated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                                  CONSTRAINT fk_risk_remediation_recommendations_monitoring_run
                                                      FOREIGN KEY (monitoring_run_id)
                                                          REFERENCES monitoring_runs(id)
                                                          ON DELETE CASCADE,

                                                  CONSTRAINT fk_risk_remediation_recommendations_risk
                                                      FOREIGN KEY (risk_id)
                                                          REFERENCES risks(id)
                                                          ON DELETE CASCADE,

                                                  CONSTRAINT chk_risk_remediation_recommendations_source
                                                      CHECK (
                                                          source IN (
                                                                     'AI',
                                                                     'RULE_BASED_FALLBACK'
                                                              )
                                                          ),

                                                  CONSTRAINT chk_risk_remediation_recommendations_fallback_reason
                                                      CHECK (
                                                          fallback_reason IN (
                                                                              'NONE',
                                                                              'PROVIDER_UNAVAILABLE',
                                                                              'PROVIDER_FAILURE',
                                                                              'VALIDATION_FAILURE'
                                                              )
                                                          ),

                                                  CONSTRAINT chk_risk_remediation_recommendations_validation
                                                      CHECK (validation_status = 'VALID'),

                                                  CONSTRAINT chk_risk_remediation_recommendations_advisory
                                                      CHECK (advisory = TRUE),

                                                  CONSTRAINT chk_risk_remediation_recommendations_finding_count
                                                      CHECK (context_finding_count >= 0),

                                                  CONSTRAINT chk_risk_remediation_recommendations_evidence_count
                                                      CHECK (context_evidence_count >= 0),

                                                  CONSTRAINT chk_risk_remediation_recommendations_fingerprint
                                                      CHECK (CHAR_LENGTH(context_fingerprint) = 64),

                                                  CONSTRAINT chk_risk_remediation_recommendations_source_metadata
                                                      CHECK (
                                                          (
                                                              source = 'AI'
                                                                  AND fallback_reason = 'NONE'
                                                                  AND provider_name IS NOT NULL
                                                                  AND model_name IS NOT NULL
                                                                  AND fallback_rule_version IS NULL
                                                              )
                                                              OR
                                                          (
                                                              source = 'RULE_BASED_FALLBACK'
                                                                  AND fallback_reason <> 'NONE'
                                                                  AND fallback_rule_version IS NOT NULL
                                                              )
                                                          )
);

CREATE INDEX idx_risk_remediation_recommendations_monitoring_run
    ON risk_remediation_recommendations(monitoring_run_id);

CREATE INDEX idx_risk_remediation_recommendations_risk
    ON risk_remediation_recommendations(risk_id);

CREATE INDEX idx_risk_remediation_recommendations_source
    ON risk_remediation_recommendations(source);

CREATE INDEX idx_risk_remediation_recommendations_generated_at
    ON risk_remediation_recommendations(generated_at);