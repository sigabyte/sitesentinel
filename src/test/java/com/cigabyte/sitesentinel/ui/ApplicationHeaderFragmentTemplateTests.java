package com.cigabyte.sitesentinel.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationHeaderFragmentTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/fragments/application-header.html";

    @Test
    void fragmentProvidesPremiumSiteSentinelProductIdentity()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "th:fragment=\"applicationHeader(activeSection)\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"application-header\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"application-brand-mark\""
                )
        );

        assertTrue(
                template.contains(
                        "SiteSentinel"
                )
        );

        assertTrue(
                template.contains(
                        "Website Trust Operations"
                )
        );
    }

    @Test
    void fragmentProvidesPrimaryApplicationNavigation()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "class=\"application-navigation\""
                )
        );

        assertTrue(
                template.contains(
                        "th:href=\"@{/}\""
                )
        );

        assertTrue(
                template.contains(
                        "th:href=\"@{/websites}\""
                )
        );

        assertTrue(
                template.contains(
                        "th:href=\"@{/notifications}\""
                )
        );

        assertTrue(
                template.contains(
                        "th:href=\"@{/notifications/delivery/settings}\""
                )
        );

        assertTrue(
                template.contains(
                        "aria-label=\"Primary navigation\""
                )
        );
    }

    @Test
    void fragmentSupportsAccessibleActiveNavigationState()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "application-navigation__link--active"
                )
        );

        assertTrue(
                template.contains(
                        "aria-current"
                )
        );

        assertTrue(
                template.contains(
                        "activeSection"
                )
        );
    }

    @Test
    void fragmentPreservesPostBasedLogoutControl()
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
                        "class=\"application-logout-form\""
                )
        );

        assertTrue(
                template.contains(
                        "class=\"application-logout-button\""
                )
        );

        assertFalse(
                template.contains(
                        "href=\"/logout\""
                )
        );

        assertFalse(
                template.contains(
                        "th:href=\"@{/logout}\""
                )
        );
    }

    @Test
    void fragmentDoesNotIntroduceExternalDependencies()
            throws IOException {

        String template =
                loadTemplate();

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

        assertFalse(
                template.contains(
                        "href=\"http://"
                )
        );

        assertFalse(
                template.contains(
                        "href=\"https://"
                )
        );

        assertFalse(
                template.contains(
                        "th:src="
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
                    "Application header fragment must exist "
                            + "on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}