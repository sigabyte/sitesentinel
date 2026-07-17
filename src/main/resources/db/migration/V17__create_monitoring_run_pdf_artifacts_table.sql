CREATE TABLE monitoring_run_pdf_artifacts (
                                              id UUID PRIMARY KEY,
                                              monitoring_run_id UUID NOT NULL,
                                              report_version VARCHAR(80) NOT NULL,
                                              file_name VARCHAR(255) NOT NULL,
                                              content_type VARCHAR(100) NOT NULL,
                                              artifact_bytes BYTEA NOT NULL,
                                              size_bytes BIGINT NOT NULL,
                                              sha256_fingerprint VARCHAR(64) NOT NULL,
                                              generated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                              created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                              CONSTRAINT fk_monitoring_run_pdf_artifacts_monitoring_run
                                                  FOREIGN KEY (monitoring_run_id)
                                                      REFERENCES monitoring_runs(id)
                                                      ON DELETE CASCADE,

                                              CONSTRAINT uq_monitoring_run_pdf_artifacts_run_version
                                                  UNIQUE (
                                                          monitoring_run_id,
                                                          report_version
                                                      ),

                                              CONSTRAINT chk_monitoring_run_pdf_artifacts_report_version
                                                  CHECK (
                                                      report_version = 'monitoring-run-pdf-v1'
                                                      ),

                                              CONSTRAINT chk_monitoring_run_pdf_artifacts_file_name
                                                  CHECK (
                                                      LOWER(file_name) LIKE '%.pdf'
                                                          AND POSITION('/' IN file_name) = 0
                                                          AND POSITION(CHR(92) IN file_name) = 0
                                                      ),

                                              CONSTRAINT chk_monitoring_run_pdf_artifacts_content_type
                                                  CHECK (
                                                      content_type = 'application/pdf'
                                                      ),

                                              CONSTRAINT chk_monitoring_run_pdf_artifacts_content_present
                                                  CHECK (
                                                      OCTET_LENGTH(artifact_bytes) > 0
                                                      ),

                                              CONSTRAINT chk_monitoring_run_pdf_artifacts_pdf_header
                                                  CHECK (
                                                      SUBSTRING(
                                                              artifact_bytes
                                                              FROM 1
                                                              FOR 5
                                                      ) = DECODE(
                                                              '255044462d',
                                                              'hex'
                                                          )
                                                      ),

                                              CONSTRAINT chk_monitoring_run_pdf_artifacts_size
                                                  CHECK (
                                                      size_bytes > 0
                                                          AND size_bytes =
                                                              OCTET_LENGTH(artifact_bytes)
                                                      ),

                                              CONSTRAINT chk_monitoring_run_pdf_artifacts_fingerprint
                                                  CHECK (
                                                      sha256_fingerprint
                                                      ~ '^[0-9a-f]{64}$'
)
    );

CREATE INDEX idx_monitoring_run_pdf_artifacts_run_generated
    ON monitoring_run_pdf_artifacts (
                                     monitoring_run_id,
                                     generated_at DESC,
                                     created_at DESC
        );

CREATE INDEX idx_monitoring_run_pdf_artifacts_generated_at
    ON monitoring_run_pdf_artifacts (
                                     generated_at DESC
        );