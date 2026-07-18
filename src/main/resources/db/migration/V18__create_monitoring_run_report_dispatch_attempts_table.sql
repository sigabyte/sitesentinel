ALTER TABLE monitoring_run_pdf_artifacts
    ADD CONSTRAINT
        uq_monitoring_run_pdf_artifacts_id_run
        UNIQUE (
                id,
                monitoring_run_id
            );

CREATE TABLE monitoring_run_report_dispatch_attempts (
                                                         id UUID PRIMARY KEY,
                                                         monitoring_run_id UUID NOT NULL,
                                                         pdf_artifact_id UUID NOT NULL,
                                                         channel VARCHAR(40) NOT NULL,
                                                         dispatch_type VARCHAR(40) NOT NULL,
                                                         status VARCHAR(40) NOT NULL,
                                                         attempt_number INTEGER NOT NULL,
                                                         retry_of_attempt_id UUID,
                                                         attempted_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                                         completed_at TIMESTAMP WITH TIME ZONE,
                                                         result_message VARCHAR(500),
                                                         technical_detail TEXT,
                                                         telegram_message_id BIGINT,
                                                         created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                                         CONSTRAINT
                                                             uq_monitoring_run_report_dispatch_attempts_lineage_reference
                                                             UNIQUE (
                                                                     id,
                                                                     monitoring_run_id,
                                                                     pdf_artifact_id
                                                                 ),

                                                         CONSTRAINT
                                                             uq_monitoring_run_report_dispatch_attempts_sequence
                                                             UNIQUE (
                                                                     monitoring_run_id,
                                                                     pdf_artifact_id,
                                                                     channel,
                                                                     attempt_number
                                                                 ),

                                                         CONSTRAINT
                                                             fk_monitoring_run_report_dispatch_attempts_run
                                                             FOREIGN KEY (
                                                                          monitoring_run_id
                                                                 )
                                                                 REFERENCES monitoring_runs(id)
                                                                 ON DELETE CASCADE,

                                                         CONSTRAINT
                                                             fk_monitoring_run_report_dispatch_attempts_artifact
                                                             FOREIGN KEY (
                                                                          pdf_artifact_id,
                                                                          monitoring_run_id
                                                                 )
                                                                 REFERENCES monitoring_run_pdf_artifacts(
                                                                                                         id,
                                                                                                         monitoring_run_id
                                                                     )
                                                                 ON DELETE CASCADE,

                                                         CONSTRAINT
                                                             fk_monitoring_run_report_dispatch_attempts_retry
                                                             FOREIGN KEY (
                                                                          retry_of_attempt_id,
                                                                          monitoring_run_id,
                                                                          pdf_artifact_id
                                                                 )
                                                                 REFERENCES monitoring_run_report_dispatch_attempts(
                                                                                                                    id,
                                                                                                                    monitoring_run_id,
                                                                                                                    pdf_artifact_id
                                                                     ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_channel
                                                             CHECK (
                                                                 channel = 'TELEGRAM'
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_type
                                                             CHECK (
                                                                 dispatch_type IN (
                                                                                   'AUTOMATIC',
                                                                                   'MANUAL_RETRY'
                                                                     )
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_status
                                                             CHECK (
                                                                 status IN (
                                                                            'PENDING',
                                                                            'SENT',
                                                                            'FAILED'
                                                                     )
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_number
                                                             CHECK (
                                                                 attempt_number > 0
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_lineage
                                                             CHECK (
                                                                 (
                                                                     dispatch_type = 'AUTOMATIC'
                                                                         AND attempt_number = 1
                                                                         AND retry_of_attempt_id IS NULL
                                                                     )
                                                                     OR
                                                                 (
                                                                     dispatch_type = 'MANUAL_RETRY'
                                                                         AND attempt_number >= 2
                                                                         AND retry_of_attempt_id IS NOT NULL
                                                                     )
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_not_self_retry
                                                             CHECK (
                                                                 retry_of_attempt_id IS NULL
                                                                     OR retry_of_attempt_id <> id
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_completion_time
                                                             CHECK (
                                                                 completed_at IS NULL
                                                                     OR completed_at >= attempted_at
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_result_message
                                                             CHECK (
                                                                 result_message IS NULL
                                                                     OR CHAR_LENGTH(
                                                                         BTRIM(result_message)
                                                                        ) BETWEEN 1 AND 500
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_technical_detail
                                                             CHECK (
                                                                 technical_detail IS NULL
                                                                     OR CHAR_LENGTH(
                                                                         BTRIM(technical_detail)
                                                                        ) BETWEEN 1 AND 2000
                                                                 ),

                                                         CONSTRAINT
                                                             chk_monitoring_run_report_dispatch_attempts_state
                                                             CHECK (
                                                                 (
                                                                     status = 'PENDING'
                                                                         AND completed_at IS NULL
                                                                         AND result_message IS NULL
                                                                         AND technical_detail IS NULL
                                                                         AND telegram_message_id IS NULL
                                                                     )
                                                                     OR
                                                                 (
                                                                     status = 'SENT'
                                                                         AND completed_at IS NOT NULL
                                                                         AND result_message IS NOT NULL
                                                                         AND technical_detail IS NOT NULL
                                                                         AND telegram_message_id > 0
                                                                     )
                                                                     OR
                                                                 (
                                                                     status = 'FAILED'
                                                                         AND completed_at IS NOT NULL
                                                                         AND result_message IS NOT NULL
                                                                         AND technical_detail IS NOT NULL
                                                                         AND telegram_message_id IS NULL
                                                                     )
                                                                 )
);

CREATE UNIQUE INDEX
    uq_monitoring_run_report_dispatch_attempts_automatic_artifact
    ON monitoring_run_report_dispatch_attempts (
                                                monitoring_run_id,
                                                pdf_artifact_id,
                                                channel
        )
    WHERE dispatch_type = 'AUTOMATIC';

CREATE INDEX
    idx_monitoring_run_report_dispatch_attempts_run_attempted
    ON monitoring_run_report_dispatch_attempts (
                                                monitoring_run_id,
                                                attempted_at DESC,
                                                created_at DESC
        );

CREATE INDEX
    idx_monitoring_run_report_dispatch_attempts_artifact_sequence
    ON monitoring_run_report_dispatch_attempts (
                                                pdf_artifact_id,
                                                channel,
                                                attempt_number DESC
        );

CREATE INDEX
    idx_monitoring_run_report_dispatch_attempts_status
    ON monitoring_run_report_dispatch_attempts (
                                                status,
                                                attempted_at DESC
        );

CREATE INDEX
    idx_monitoring_run_report_dispatch_attempts_retry
    ON monitoring_run_report_dispatch_attempts (
                                                retry_of_attempt_id
        )
    WHERE retry_of_attempt_id IS NOT NULL;