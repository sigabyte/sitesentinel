package com.cigabyte.sitesentinel.website;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebsiteCreatePremiumTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/websites/new.html";

    @Test
    void addWebsiteUsesResponsiveApplicationShellAndSharedHeader()
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
    void addWebsiteProvidesConsistentHeadingAndBackAction()
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
                        "th:href=\"@{/websites}\""
                )
        );

        assertTrue(
                template.contains(
                        "application-button--secondary"
                )
        );
    }

    @Test
    void addWebsitePreservesPostFormAndRequestBinding()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "th:action=\"@{/websites}\""
                )
        );

        assertTrue(
                template.contains(
                        "th:object=\"${request}\""
                )
        );

        assertTrue(
                template.contains(
                        "method=\"post\""
                )
        );

        assertTrue(
                template.contains(
                        "th:field=\"*{name}\""
                )
        );

        assertTrue(
                template.contains(
                        "th:field=\"*{domain}\""
                )
        );
    }

    @Test
    void addWebsiteProvidesAccessibleFormFieldsAndValidationStates()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"application-panel form-panel\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"form-field\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"form-control\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"field-error\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"state-message state-message--failure\""
                )
        );

        assertTrue(
                template.contains(
                        "aria-describedby=\"name-help name-error\""
                )
        );

        assertTrue(
                template.contains(
                        "aria-describedby=\"domain-help domain-error\""
                )
        );
    }

    @Test
    void addWebsiteRemovesInlinePresentationAndExternalDependencies()
            throws IOException {

        String template =
                loadTemplate();

        assertFalse(
                template.contains(
                        "style="
                )
        );

        assertFalse(
                template.contains(
                        "<br"
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
                    "Add website template must exist "
                            + "on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}