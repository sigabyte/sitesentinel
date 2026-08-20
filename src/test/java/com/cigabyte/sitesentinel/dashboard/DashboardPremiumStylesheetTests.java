package com.cigabyte.sitesentinel.dashboard;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardPremiumStylesheetTests {

    private static final String STYLESHEET_PATH =
            "/static/css/app.css";

    @Test
    void stylesheetProvidesPremiumDashboardCanvasAndBoundedShell()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String dashboardPageRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-page"
                );

        String dashboardShellRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-shell"
                );

        assertTrue(
                dashboardPageRule.contains(
                        "background:"
                )
        );

        assertTrue(
                dashboardPageRule.contains(
                        "color:"
                )
        );

        assertTrue(
                dashboardShellRule.contains(
                        "max-width:"
                )
        );

        assertTrue(
                dashboardShellRule.contains(
                        "margin: 0 auto"
                )
        );

        assertTrue(
                dashboardShellRule.contains(
                        "padding:"
                )
        );
    }

    @Test
    void stylesheetProvidesPremiumHeaderAndResponsiveActions()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String headerRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-header"
                );

        String brandMarkRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-brand-mark"
                );

        String headerActionsRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-header-actions"
                );

        assertTrue(
                headerRule.contains(
                        "border-radius:"
                )
        );

        assertTrue(
                headerRule.contains(
                        "background:"
                )
        );

        assertTrue(
                brandMarkRule.contains(
                        "display:"
                )
        );

        assertTrue(
                brandMarkRule.contains(
                        "border-radius:"
                )
        );

        assertTrue(
                headerActionsRule.contains(
                        "flex-wrap:"
                )
        );
    }

    @Test
    void stylesheetProvidesResponsiveKpiCardGrid()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String kpiGridRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-kpi-grid"
                );

        String kpiCardRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-kpi-card"
                );

        String kpiValueRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-kpi-value"
                );

        assertTrue(
                kpiGridRule.contains(
                        "display: grid"
                )
        );

        assertTrue(
                kpiGridRule.contains(
                        "repeat(auto-fit"
                )
        );

        assertTrue(
                kpiCardRule.contains(
                        "border-radius:"
                )
        );

        assertTrue(
                kpiCardRule.contains(
                        "box-shadow:"
                )
        );

        assertTrue(
                kpiValueRule.contains(
                        "font-size:"
                )
        );
    }

    @Test
    void stylesheetProtectsTablesAndIdentifiersFromViewportOverflow()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String tableWrapperRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-table-wrapper"
                );

        String identifierRule =
                ruleFor(
                        stylesheet,
                        ".dashboard-identifier"
                );

        assertTrue(
                tableWrapperRule.contains(
                        "overflow-x: auto"
                )
        );

        assertTrue(
                identifierRule.contains(
                        "display: inline-block"
                )
        );

        assertTrue(
                identifierRule.contains(
                        "max-width:"
                )
        );

        assertTrue(
                identifierRule.contains(
                        "overflow: hidden"
                )
        );

        assertTrue(
                identifierRule.contains(
                        "text-overflow: ellipsis"
                )
        );
    }

    @Test
    void stylesheetProvidesVisibleKeyboardFocusAndMobileLayout()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        assertTrue(
                stylesheet.contains(
                        ":focus-visible"
                )
        );

        assertTrue(
                stylesheet.contains(
                        "outline:"
                )
        );

        assertTrue(
                stylesheet.contains(
                        "@media (max-width: 768px)"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".dashboard-content-grid"
                )
        );
    }

    @Test
    void stylesheetProvidesOperationalStatusAndSeverityBadges()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String statusBadgeRule =
                ruleFor(
                        stylesheet,
                        ".status-badge"
                );

        assertTrue(
                statusBadgeRule.contains(
                        "display: inline-flex"
                )
        );

        assertTrue(
                statusBadgeRule.contains(
                        "border-radius:"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".status-badge--failed"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".status-badge--completed"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".status-badge--unread"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".notification-severity--critical"
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