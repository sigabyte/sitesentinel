CREATE TABLE monitoring_schedules (
                                      id UUID PRIMARY KEY,
                                      website_id UUID NOT NULL,
                                      status VARCHAR(30) NOT NULL,
                                      frequency VARCHAR(30) NOT NULL,
                                      next_run_at TIMESTAMP WITH TIME ZONE,
                                      last_triggered_at TIMESTAMP WITH TIME ZONE,
                                      last_monitoring_run_id UUID,
                                      last_failure_reason TEXT,
                                      created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                      updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                      CONSTRAINT fk_monitoring_schedules_website
                                          FOREIGN KEY (website_id)
                                              REFERENCES websites(id)
                                              ON DELETE CASCADE,
                                      CONSTRAINT fk_monitoring_schedules_last_run
                                          FOREIGN KEY (last_monitoring_run_id)
                                              REFERENCES monitoring_runs(id)
                                              ON DELETE SET NULL,
                                      CONSTRAINT uk_monitoring_schedules_website
                                          UNIQUE (website_id),
                                      CONSTRAINT chk_monitoring_schedules_status
                                          CHECK (status IN ('ENABLED', 'DISABLED')),
                                      CONSTRAINT chk_monitoring_schedules_frequency
                                          CHECK (frequency IN ('DAILY'))
);

CREATE INDEX idx_monitoring_schedules_website_id
    ON monitoring_schedules(website_id);

CREATE INDEX idx_monitoring_schedules_status
    ON monitoring_schedules(status);

CREATE INDEX idx_monitoring_schedules_next_run_at
    ON monitoring_schedules(next_run_at);

CREATE INDEX idx_monitoring_schedules_last_run
    ON monitoring_schedules(last_monitoring_run_id);


ALTER TABLE monitoring_runs
    ADD COLUMN trigger_type VARCHAR(30) NOT NULL DEFAULT 'MANUAL';

ALTER TABLE monitoring_runs
    ADD COLUMN monitoring_schedule_id UUID;

ALTER TABLE monitoring_runs
    ADD CONSTRAINT chk_monitoring_runs_trigger_type
        CHECK (trigger_type IN ('MANUAL', 'SCHEDULED'));

ALTER TABLE monitoring_runs
    ADD CONSTRAINT fk_monitoring_runs_schedule
        FOREIGN KEY (monitoring_schedule_id)
            REFERENCES monitoring_schedules(id)
            ON DELETE SET NULL;

CREATE INDEX idx_monitoring_runs_trigger_type
    ON monitoring_runs(trigger_type);

CREATE INDEX idx_monitoring_runs_schedule_id
    ON monitoring_runs(monitoring_schedule_id);