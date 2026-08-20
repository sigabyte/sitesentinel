ALTER TABLE risk_remediation_recommendations
    ADD COLUMN report_language VARCHAR(20);

UPDATE risk_remediation_recommendations
SET report_language = 'ENGLISH'
WHERE report_language IS NULL;

ALTER TABLE risk_remediation_recommendations
    ALTER COLUMN report_language SET NOT NULL;

ALTER TABLE risk_remediation_recommendations
    ADD CONSTRAINT
        chk_risk_remediation_recommendations_report_language
        CHECK (
            report_language IN (
                                'ENGLISH',
                                'TURKISH'
                )
            );

CREATE INDEX
    idx_risk_remediation_recommendations_run_risk_language
    ON risk_remediation_recommendations (
                                         monitoring_run_id,
                                         risk_id,
                                         report_language
        );


ALTER TABLE monitoring_run_pdf_artifacts
    ADD COLUMN report_language VARCHAR(20);

UPDATE monitoring_run_pdf_artifacts
SET report_language = 'ENGLISH'
WHERE report_language IS NULL;

ALTER TABLE monitoring_run_pdf_artifacts
    ALTER COLUMN report_language SET NOT NULL;

ALTER TABLE monitoring_run_pdf_artifacts
    ADD CONSTRAINT
        chk_monitoring_run_pdf_artifacts_report_language
        CHECK (
            report_language IN (
                                'ENGLISH',
                                'TURKISH'
                )
            );

ALTER TABLE monitoring_run_pdf_artifacts
DROP CONSTRAINT
        uq_monitoring_run_pdf_artifacts_run_version;

ALTER TABLE monitoring_run_pdf_artifacts
    ADD CONSTRAINT
        uq_monitoring_run_pdf_artifacts_run_version_language
        UNIQUE (
                monitoring_run_id,
                report_version,
                report_language
            );

CREATE INDEX
    idx_monitoring_run_pdf_artifacts_run_language_generated
    ON monitoring_run_pdf_artifacts (
                                     monitoring_run_id,
                                     report_language,
                                     generated_at DESC,
                                     created_at DESC
        );