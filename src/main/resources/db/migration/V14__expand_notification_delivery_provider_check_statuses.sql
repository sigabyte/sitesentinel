ALTER TABLE notification_delivery_provider_checks
DROP CONSTRAINT chk_notification_delivery_provider_checks_status;

ALTER TABLE notification_delivery_provider_checks
    ADD CONSTRAINT chk_notification_delivery_provider_checks_status
        CHECK (
            status IN (
                       'HEALTHY',
                       'DISABLED',
                       'CONFIGURATION_MISSING',
                       'AUTHENTICATION_FAILED',
                       'TIMEOUT',
                       'UNREACHABLE',
                       'INVALID_RESPONSE',
                       'INTERRUPTED',
                       'FAILED'
                )
            );