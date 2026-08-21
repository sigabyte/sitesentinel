package com.cigabyte.sitesentinel.website;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebsiteListPremiumTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/websites/list.html";

    @Test
    void websiteListUsesResponsiveApplicationShellAndSharedHeader()
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
    void websiteListProvidesConsistentPageHeadingAndPrimaryAction()
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
                        "class=\"page-heading__eyebrow\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"page-heading__description\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"page-actions\""
                )
        );

        assertTrue(
                template.contains(
                        "th:href=\"@{/websites/new}\""
                )
        );

        assertTrue(
                template.contains(
                        "application-button--primary"
                )
        );
    }

    @Test
    void websiteListProvidesAccessiblePremiumEmptyState()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"application-panel website-list-panel\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"state-message state-message--empty\""
                )
        );

        assertTrue(
                template.contains(
                        "th:if=\"${#lists.isEmpty(websites)}\""
                )
        );

        assertTrue(
                template.contains(
                        "Add your first website"
                )
        );
    }

    @Test
    void websiteListProvidesResponsiveDataTableAndStatusBadges()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"table-container\""
                )
        );

        assertTrue(
                template.contains(
                        "tabindex=\"0\""
                )
        );

        assertTrue(
                template.contains(
                        "aria-label=\"Monitored websites table\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"data-table website-table\""
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

        assertTrue(
                template.contains(
                        "class=\"application-row-action\""
                )
        );
    }

    @Test
    void websiteListRemovesLegacyPresentationAttributesAndExternalDependencies()
            throws IOException {

        String template =
                loadTemplate();

        assertFalse(
                template.contains(
                        "border=\"1\""
                )
        );

        assertFalse(
                template.contains(
                        "cellpadding="
                )
        );

        assertFalse(
                template.contains(
                        "style="
                )
        );

        assertFalse(
                template.contains(
                        "<script"
                )
        );

        assertFalse(
                template.contains(
                        "src=\"http://"
                )
        );

        assertFalse(
                template.contains(
                        "src=\"https://"
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
                    "Website list template must exist "
                            + "on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}