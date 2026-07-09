ALTER TABLE notification_delivery_attempts
DROP CONSTRAINT IF EXISTS chk_notification_delivery_attempts_channel;

ALTER TABLE notification_delivery_attempts
    ADD CONSTRAINT chk_notification_delivery_attempts_channel
        CHECK (channel IN (
                           'EMAIL',
                           'WHATSAPP',
                           'SLACK',
                           'WEBHOOK',
                           'IN_APP',
                           'TELEGRAM'
            ));