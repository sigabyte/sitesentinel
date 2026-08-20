package com.cigabyte.sitesentinel.dashboard;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardAttentionRequiredTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/dashboard/index.html";

    @Test
    void attentionRegionShowsUnreadHighAndCriticalNotificationPreviews()
            throws IOException {

        String attentionRegion =
                loadAttentionRegion();

        assertTrue(
                attentionRegion.contains(
                        "th:each=\"event : ${latestNotificationEvents}\""
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "event.status.name() == 'UNREAD'"
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "event.severity.name() == 'CRITICAL'"
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "event.severity.name() == 'HIGH'"
                )
        );
    }

    @Test
    void attentionPreviewExposesOperationalNotificationContext()
            throws IOException {

        String attentionRegion =
                loadAttentionRegion();

        assertTrue(
                attentionRegion.contains(
                        "th:text=\"${event.severity}\""
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "th:text=\"${event.title}\""
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "th:text=\"${event.message}\""
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "${event.id}"
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "Review event"
                )
        );
    }

    @Test
    void attentionRegionAnnouncesOperationalChangesAccessibly()
            throws IOException {

        String attentionRegion =
                loadAttentionRegion();

        assertTrue(
                attentionRegion.contains(
                        "aria-live=\"polite\""
                )
        );

        assertTrue(
                attentionRegion.contains(
                        "aria-labelledby=\"attention-required-heading\""
                )
        );
    }

    private String loadAttentionRegion()
            throws IOException {

        String template =
                loadTemplate();

        String attentionMarker =
                "class=\"dashboard-attention\"";

        int regionStart =
                template.indexOf(
                        attentionMarker
                );

        assertTrue(
                regionStart >= 0,
                "Dashboard must contain the Attention Required region."
        );

        int regionEnd =
                template.indexOf(
                        "</section>",
                        regionStart
                );

        assertTrue(
                regionEnd > regionStart,
                "Attention Required region must be a complete section."
        );

        return template.substring(
                regionStart,
                regionEnd
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