package com.cigabyte.sitesentinel.monitoring;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonitoringRunDetailNotificationTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/monitoring-runs/detail.html";

    @Test
    void emptyNotificationStateExplainsUnchangedExistingRiskCondition()
            throws IOException {

        String template = loadTemplate();

        assertTrue(
                template.contains(
                        "No new notification event was generated "
                                + "for this monitoring run."
                )
        );

        assertTrue(
                template.contains(
                        "an existing high-risk condition "
                                + "remains unchanged"
                )
        );

        assertTrue(
                template.contains(
                        "no comparison-based notification rule matched"
                )
        );
    }

    @Test
    void emptyNotificationStateDoesNotClaimThatNoHighRiskWasDetected()
            throws IOException {

        String template = loadTemplate();

        assertFalse(
                template.contains(
                        "no high-risk assessment was detected"
                )
        );
    }

    private String loadTemplate()
            throws IOException {

        try (
                InputStream inputStream =
                        getClass().getResourceAsStream(
                                TEMPLATE_PATH
                        )
        ) {
            assertNotNull(
                    inputStream,
                    "Monitoring run detail template "
                            + "must exist on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}
