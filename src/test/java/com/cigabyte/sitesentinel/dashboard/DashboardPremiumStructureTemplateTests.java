package com.cigabyte.sitesentinel.dashboard;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardPremiumStructureTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/dashboard/index.html";

    @Test
    void dashboardProvidesResponsiveDocumentAndPageShell()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "name=\"viewport\""
                )
        );

        assertTrue(
                template.contains(
                        "content=\"width=device-width, initial-scale=1\""
                )
        );

        assertTrue(
                template.contains(
                        "<body class=\"dashboard-page\">"
                )
        );

        assertTrue(
                template.contains(
                        "<main class=\"dashboard-shell\""
                )
        );
    }

    @Test
    void dashboardProvidesPremiumProductHeader()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"dashboard-brand-mark\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"dashboard-eyebrow\""
                )
        );

        assertTrue(
                template.contains(
                        "Website Trust Operations"
                )
        );

        assertTrue(
                template.contains(
                        "class=\"dashboard-header-actions\""
                )
        );
    }

    @Test
    void dashboardPresentsExistingOperationalCountsAsKpiCards()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"dashboard-kpi-grid\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"dashboard-kpi-card"
                )
        );

        assertTrue(
                template.contains(
                        "class=\"dashboard-kpi-value\" "
                                + "th:text=\"${activeWebsites}\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"dashboard-kpi-value\" "
                                + "th:text=\"${runningMonitoringRuns}\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"dashboard-kpi-value\" "
                                + "th:text=\"${failedMonitoringRuns}\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"dashboard-kpi-value\" "
                                + "th:text=\"${unreadNotificationEventCount}\""
                )
        );
    }

    @Test
    void dashboardProvidesAttentionRequiredRegionFromExistingNotificationData()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"dashboard-attention\""
                )
        );

        assertTrue(
                template.contains(
                        "th:if=\"${unreadNotificationEventCount > 0}\""
                )
        );

        assertTrue(
                template.contains(
                        "Attention Required"
                )
        );

        assertTrue(
                template.contains(
                        "th:text=\"${unreadNotificationEventCount}\""
                )
        );

        assertTrue(
                template.contains(
                        "href=\"/notifications\""
                )
        );
    }

    @Test
    void dashboardPreservesPostLogoutSecurityContract()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "th:action=\"@{/logout}\""
                )
        );

        assertTrue(
                template.contains(
                        "method=\"post\""
                )
        );

        assertTrue(
                template.contains(
                        "type=\"submit\""
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
                    "Dashboard template must exist "
                            + "on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}