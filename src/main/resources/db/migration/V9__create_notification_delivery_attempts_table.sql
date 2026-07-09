CREATE TABLE notification_delivery_attempts (
                                                id UUID PRIMARY KEY,
                                                notification_event_id UUID NOT NULL,
                                                channel VARCHAR(40) NOT NULL,
                                                status VARCHAR(40) NOT NULL,
                                                attempted_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                                completed_at TIMESTAMP WITH TIME ZONE,
                                                result_message VARCHAR(500),
                                                technical_detail TEXT,
                                                created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                                CONSTRAINT fk_notification_delivery_attempts_event
                                                    FOREIGN KEY (notification_event_id)
                                                        REFERENCES notification_events(id)
                                                        ON DELETE CASCADE,
                                                CONSTRAINT chk_notification_delivery_attempts_channel
                                                    CHECK (channel IN (
                                                                       'EMAIL',
                                                                       'WHATSAPP',
                                                                       'SLACK',
                                                                       'WEBHOOK',
                                                                       'IN_APP'
                                                                       'TELEGRAM'
                                                        )),
                                                CONSTRAINT chk_notification_delivery_attempts_status
                                                    CHECK (status IN (
                                                                      'PENDING',
                                                                      'SIMULATED_SUCCESS',
                                                                      'SIMULATED_FAILURE',
                                                                      'SKIPPED'
                                                        ))
);

CREATE INDEX idx_notification_delivery_attempts_event_id
    ON notification_delivery_attempts(notification_event_id);

CREATE INDEX idx_notification_delivery_attempts_channel
    ON notification_delivery_attempts(channel);

CREATE INDEX idx_notification_delivery_attempts_status
    ON notification_delivery_attempts(status);

CREATE INDEX idx_notification_delivery_attempts_attempted_at
    ON notification_delivery_attempts(attempted_at);

CREATE INDEX idx_notification_delivery_attempts_created_at
    ON notification_delivery_attempts(created_at);