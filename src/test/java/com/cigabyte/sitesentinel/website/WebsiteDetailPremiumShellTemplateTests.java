package com.cigabyte.sitesentinel.website;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebsiteDetailPremiumShellTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/websites/detail.html";

    @Test
    void websiteDetailUsesResponsiveApplicationShellAndSharedHeader()
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
                        "<body class=\"application-page\">"
                )
        );

        assertTrue(
                template.contains(
                        "<main class=\"application-shell\">"
                )
        );

        assertTrue(
                template.contains(
                        "fragments/application-header"
                )
        );

        assertTrue(
                template.contains(
                        "applicationHeader('websites')"
                )
        );
    }

    @Test
    void websiteDetailProvidesDynamicPageHeadingAndActions()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"page-heading\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"page-heading__content\""
                )
        );

        assertTrue(
                template.contains(
                        "th:text=\"${website.name}\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"page-heading__description technical-value\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"page-actions\""
                )
        );

        assertTrue(
                template.contains(
                        "th:href=\"@{/websites}\""
                )
        );

        assertTrue(
                template.contains(
                        "th:href=\"@{/notifications(websiteId=${website.id})}\""
                )
        );
    }

    @Test
    void websiteDetailUsesVerticalSectionStack()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"section-stack\""
                )
        );
    }

    @Test
    void websiteDetailProvidesPremiumBaselineMetadataPanel()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"application-panel website-baseline-panel\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"detail-grid\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"detail-label\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"detail-value\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"status-badge\""
                )
        );

        assertTrue(
                template.contains(
                        "status-badge--${#strings.toLowerCase(website.status)}"
                )
        );
    }

    @Test
    void websiteDetailPreservesExistingPostLifecycleForms()
            throws IOException {

        String template =
                loadTemplate();

        assertEquals(
                3,
                countOccurrences(
                        template,
                        "method=\"post\""
                )
        );

        assertTrue(
                template.contains(
                        "schedules/enable-daily"
                )
        );

        assertTrue(
                template.contains(
                        "schedules/disable"
                )
        );

        assertTrue(
                template.contains(
                        "/monitoring-runs"
                )
        );
    }

    private int countOccurrences(
            String content,
            String expected
    ) {

        int count = 0;
        int searchFrom = 0;

        while (
                (
                        searchFrom =
                                content.indexOf(
                                        expected,
                                        searchFrom
                                )
                ) >= 0
        ) {
            count++;
            searchFrom += expected.length();
        }

        return count;
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
                    "Website detail template must exist "
                            + "on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}