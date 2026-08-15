package com.cigabyte.sitesentinel.reporting;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonitoringRunReportDispatchTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/reports/monitoring-run-report.html";

    @Test
    void templateContainsDispatchHistoryBindings()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "<h2>Telegram PDF dispatch</h2>"
                )
        );

        assertTrue(
                template.contains(
                        "${reportDispatchAttemptCount}"
                )
        );

        assertTrue(
                template.contains(
                        "${latestReportDispatchAttempt != null"
                )
        );

        assertTrue(
                template.contains(
                        "th:each=\"attempt : "
                                + "${reportDispatchAttempts}\""
                )
        );
    }

    @Test
    void templateContainsLatestFailedAttemptRetryContract()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "${reportDispatchRetryAvailable"
                )
        );

        assertTrue(
                template.contains(
                        "${latestReportDispatchAttempt.id}"
                )
        );

        assertTrue(
                template.contains(
                        "/report/dispatch-attempts/"
                                + "{attemptId}/retry"
                )
        );

        assertTrue(
                template.contains(
                        "Retry Telegram PDF dispatch"
                )
        );
    }

    @Test
    void templateContainsSafeRedirectFeedbackBindings()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "${reportDispatchStatus == 'SUCCESS'}"
                )
        );

        assertTrue(
                template.contains(
                        "${reportDispatchStatus == 'FAILURE'}"
                )
        );

        assertTrue(
                template.contains(
                        "${reportDispatchStatus == 'UNAVAILABLE'}"
                )
        );

        assertTrue(
                template.contains(
                        "${reportDispatchMessage}"
                )
        );
    }

    @Test
    void templateUsesCanonicalImpactAwareRecommendationStructure()
            throws IOException {

        String template =
                loadTemplate();

        String riskContextHeading =
                "1. Risk Type, Severity and Score";

        String summaryHeading =
                "2. Risk and Potential Impact Summary";

        String remediationHeading =
                "3. Remediation Steps";

        String verificationHeading =
                "4. Verification Steps";

        String auditHeading =
                "5. Recommendation Audit Metadata";

        assertTrue(
                template.contains(
                        riskContextHeading
                )
        );

        assertTrue(
                template.contains(
                        summaryHeading
                )
        );

        assertTrue(
                template.contains(
                        remediationHeading
                )
        );

        assertTrue(
                template.contains(
                        verificationHeading
                )
        );

        assertTrue(
                template.contains(
                        auditHeading
                )
        );

        assertTrue(
                template.indexOf(riskContextHeading)
                        < template.indexOf(summaryHeading)
        );

        assertTrue(
                template.indexOf(summaryHeading)
                        < template.indexOf(remediationHeading)
        );

        assertTrue(
                template.indexOf(remediationHeading)
                        < template.indexOf(verificationHeading)
        );

        assertTrue(
                template.indexOf(verificationHeading)
                        < template.indexOf(auditHeading)
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
                    "Monitoring run report template "
                            + "must exist on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}