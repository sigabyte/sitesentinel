CREATE TABLE notification_events (
                                     id UUID PRIMARY KEY,
                                     website_id UUID NOT NULL,
                                     monitoring_run_id UUID NOT NULL,
                                     event_type VARCHAR(80) NOT NULL,
                                     severity VARCHAR(30) NOT NULL,
                                     status VARCHAR(30) NOT NULL,
                                     title VARCHAR(220) NOT NULL,
                                     message TEXT NOT NULL,
                                     deduplication_key VARCHAR(300) NOT NULL,
                                     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                     updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                     CONSTRAINT fk_notification_events_website
                                         FOREIGN KEY (website_id)
                                             REFERENCES websites(id)
                                             ON DELETE CASCADE,
                                     CONSTRAINT fk_notification_events_monitoring_run
                                         FOREIGN KEY (monitoring_run_id)
                                             REFERENCES monitoring_runs(id)
                                             ON DELETE CASCADE,
                                     CONSTRAINT uk_notification_events_deduplication_key
                                         UNIQUE (deduplication_key),
                                     CONSTRAINT chk_notification_events_type
                                         CHECK (event_type IN (
                                                               'MONITORING_RUN_FAILED',
                                                               'HIGH_RISK_TRUST_ASSESSMENT',
                                                               'TRUST_STATUS_CHANGED',
                                                               'TRUST_SCORE_DECLINED',
                                                               'NEW_RISK_TYPE_DETECTED'
                                             )),
                                     CONSTRAINT chk_notification_events_severity
                                         CHECK (severity IN ('INFO', 'WARNING', 'HIGH', 'CRITICAL')),
                                     CONSTRAINT chk_notification_events_status
                                         CHECK (status IN ('UNREAD', 'READ'))
);

CREATE INDEX idx_notification_events_website_id
    ON notification_events(website_id);

CREATE INDEX idx_notification_events_monitoring_run_id
    ON notification_events(monitoring_run_id);

CREATE INDEX idx_notification_events_type
    ON notification_events(event_type);

CREATE INDEX idx_notification_events_severity
    ON notification_events(severity);

CREATE INDEX idx_notification_events_status
    ON notification_events(status);

CREATE INDEX idx_notification_events_created_at
    ON notification_events(created_at);

CREATE INDEX idx_notification_events_deduplication_key
    ON notification_events(deduplication_key);