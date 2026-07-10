ALTER TABLE notification_delivery_attempts
DROP CONSTRAINT IF EXISTS chk_notification_delivery_attempts_status;

ALTER TABLE notification_delivery_attempts
    ADD CONSTRAINT chk_notification_delivery_attempts_status
        CHECK (status IN (
                          'PENDING',
                          'SIMULATED_SUCCESS',
                          'SIMULATED_FAILURE',
                          'SKIPPED',
                          'SENT',
                          'FAILED',
                          'CONFIGURATION_MISSING',
                          'DISABLED'
            ));