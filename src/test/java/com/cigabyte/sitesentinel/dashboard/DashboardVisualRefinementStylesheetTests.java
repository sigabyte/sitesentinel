package com.cigabyte.sitesentinel.dashboard;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardVisualRefinementStylesheetTests {

    private static final String STYLESHEET_PATH =
            "/static/css/app.css";

    @Test
    void dashboardActionLinksPreserveAccessibleForegroundColors()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String secondaryButtonRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-page .dashboard-button--secondary"
                );

        String attentionButtonRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-page .dashboard-button--attention"
                );

        assertTrue(
                secondaryButtonRule.contains(
                        "color: #ffffff"
                )
        );

        assertTrue(
                attentionButtonRule.contains(
                        "color: #ffffff"
                )
        );
    }

    @Test
    void desktopNotificationTableUsesControlledColumnLayout()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String notificationTableRule =
                ruleFor(
                        stylesheet,
                        ".notification-table"
                );

        assertTrue(
                notificationTableRule.contains(
                        "width: 100%"
                )
        );

        assertTrue(
                notificationTableRule.contains(
                        "min-width: 0"
                )
        );

        assertTrue(
                notificationTableRule.contains(
                        "table-layout: fixed"
                )
        );
    }

    @Test
    void notificationContentCanShrinkInsideItsAssignedColumn()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String notificationContentRule =
                ruleFor(
                        stylesheet,
                        ".notification-table .dashboard-notification-content"
                );

        String notificationIdentifierRule =
                ruleFor(
                        stylesheet,
                        ".notification-table .dashboard-identifier"
                );

        assertTrue(
                notificationContentRule.contains(
                        "min-width: 0"
                )
        );

        assertTrue(
                notificationIdentifierRule.contains(
                        "max-width: 100%"
                )
        );
    }

    @Test
    void longNotificationEventTypesCanWrapWithoutHidingActions()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String eventTypeRule =
                ruleFor(
                        stylesheet,
                        ".notification-table td:nth-child(3)"
                );

        String actionColumnRule =
                ruleFor(
                        stylesheet,
                        ".notification-table th:last-child"
                );

        assertTrue(
                eventTypeRule.contains(
                        "overflow-wrap: anywhere"
                )
        );

        assertTrue(
                actionColumnRule.contains(
                        "width:"
                )
        );
    }

    @Test
    void notificationCreatedValuesRemainReadable()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String createdColumnRule =
                ruleFor(
                        stylesheet,
                        ".notification-table td:nth-child(7)"
                );

        assertTrue(
                createdColumnRule.contains(
                        "word-break: normal"
                )
        );

        assertTrue(
                createdColumnRule.contains(
                        "overflow-wrap: anywhere"
                )
        );
    }

    private String ruleFor(
            String stylesheet,
            String selector
    ) {

        Pattern pattern =
                Pattern.compile(
                        "^\\s*"
                                + Pattern.quote(selector)
                                + "\\s*\\{([^}]*)}",
                        Pattern.DOTALL
                                | Pattern.MULTILINE
                );

        Matcher matcher =
                pattern.matcher(
                        stylesheet
                );

        assertTrue(
                matcher.find(),
                "Expected stylesheet rule for "
                        + selector
        );

        return matcher.group(1);
    }

    private String loadStylesheet()
            throws IOException {

        try (
                InputStream inputStream =
                        getClass().getResourceAsStream(
                                STYLESHEET_PATH
                        )
        ) {
            assertNotNull(
                    inputStream,
                    "SiteSentinel stylesheet must exist "
                            + "on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}