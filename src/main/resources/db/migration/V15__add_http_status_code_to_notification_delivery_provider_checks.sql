ALTER TABLE notification_delivery_provider_checks
    ADD COLUMN http_status_code INTEGER;

ALTER TABLE notification_delivery_provider_checks
    ADD CONSTRAINT chk_notification_delivery_provider_checks_http_status_code
        CHECK (
            http_status_code IS NULL
                OR http_status_code BETWEEN 100 AND 599
            );