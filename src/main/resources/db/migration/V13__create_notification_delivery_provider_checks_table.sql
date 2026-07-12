CREATE TABLE notification_delivery_provider_checks (
                                                       id UUID PRIMARY KEY,
                                                       channel VARCHAR(40) NOT NULL,
                                                       status VARCHAR(40) NOT NULL,
                                                       checked_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                                       result_message VARCHAR(500) NOT NULL,
                                                       created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                                       CONSTRAINT chk_notification_delivery_provider_checks_channel
                                                           CHECK (
                                                               channel IN (
                                                                           'EMAIL',
                                                                           'WHATSAPP',
                                                                           'SLACK',
                                                                           'WEBHOOK',
                                                                           'IN_APP',
                                                                           'TELEGRAM'
                                                                   )
                                                               ),

                                                       CONSTRAINT chk_notification_delivery_provider_checks_status
                                                           CHECK (
                                                               status IN (
                                                                          'HEALTHY',
                                                                          'DISABLED',
                                                                          'CONFIGURATION_MISSING',
                                                                          'AUTHENTICATION_FAILED',
                                                                          'UNREACHABLE',
                                                                          'FAILED'
                                                                   )
                                                               )
);

CREATE INDEX idx_notification_delivery_provider_checks_channel
    ON notification_delivery_provider_checks(channel);

CREATE INDEX idx_notification_delivery_provider_checks_status
    ON notification_delivery_provider_checks(status);

CREATE INDEX idx_notification_delivery_provider_checks_checked_at
    ON notification_delivery_provider_checks(checked_at);

CREATE INDEX idx_notification_delivery_provider_checks_channel_checked_at
    ON notification_delivery_provider_checks(channel, checked_at DESC);