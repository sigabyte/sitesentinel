package com.cigabyte.sitesentinel.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationContentComponentsStylesheetTests {

    private static final String STYLESHEET_PATH =
            "/static/css/app.css";

    @Test
    void stylesheetProvidesResponsivePageHeadingAndActions()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String pageHeadingRule =
                ruleFor(
                        stylesheet,
                        ".page-heading"
                );

        String pageActionsRule =
                ruleFor(
                        stylesheet,
                        ".page-actions"
                );

        assertTrue(
                pageHeadingRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                pageHeadingRule.contains(
                        "flex-wrap:"
                )
        );

        assertTrue(
                pageActionsRule.contains(
                        "display: flex"
                )
        );
    }

    @Test
    void stylesheetProvidesReusablePanelStructure()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String panelHeaderRule =
                ruleFor(
                        stylesheet,
                        ".application-panel__header"
                );

        String panelBodyRule =
                ruleFor(
                        stylesheet,
                        ".application-panel__body"
                );

        assertTrue(
                panelHeaderRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                panelHeaderRule.contains(
                        "border-bottom:"
                )
        );

        assertTrue(
                panelBodyRule.contains(
                        "padding:"
                )
        );
    }

    @Test
    void stylesheetProvidesAccessibleApplicationButtons()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String buttonRule =
                ruleFor(
                        stylesheet,
                        ".application-button"
                );

        String primaryButtonRule =
                ruleFor(
                        stylesheet,
                        ".application-button--primary"
                );

        assertTrue(
                buttonRule.contains(
                        "display: inline-flex"
                )
        );

        assertTrue(
                buttonRule.contains(
                        "min-height:"
                )
        );

        assertTrue(
                primaryButtonRule.contains(
                        "background:"
                )
        );

        assertTrue(
                primaryButtonRule.contains(
                        "color:"
                )
        );
    }

    @Test
    void stylesheetProvidesReadableResponsiveDataTable()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String tableRule =
                ruleFor(
                        stylesheet,
                        ".data-table"
                );

        String tableHeaderRule =
                ruleFor(
                        stylesheet,
                        ".data-table th"
                );

        String tableCellRule =
                ruleFor(
                        stylesheet,
                        ".data-table td"
                );

        assertTrue(
                tableRule.contains(
                        "width: 100%"
                )
        );

        assertTrue(
                tableRule.contains(
                        "border-collapse:"
                )
        );

        assertTrue(
                tableHeaderRule.contains(
                        "text-align: left"
                )
        );

        assertTrue(
                tableCellRule.contains(
                        "border-bottom:"
                )
        );
    }

    @Test
    void stylesheetProvidesPremiumEmptyStateAndRowAction()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String emptyStateRule =
                ruleFor(
                        stylesheet,
                        ".state-message--empty"
                );

        String rowActionRule =
                ruleFor(
                        stylesheet,
                        ".application-row-action"
                );

        assertTrue(
                emptyStateRule.contains(
                        "background:"
                )
        );

        assertTrue(
                emptyStateRule.contains(
                        "border:"
                )
        );

        assertTrue(
                rowActionRule.contains(
                        "display: inline-flex"
                )
        );

        assertTrue(
                rowActionRule.contains(
                        "min-height:"
                )
        );
    }

    @Test
    void stylesheetProvidesActiveWebsiteStatusAndMobileContentLayout()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String activeStatusRule =
                ruleFor(
                        stylesheet,
                        ".status-badge--active"
                );

        assertTrue(
                activeStatusRule.contains(
                        "background:"
                )
        );

        assertTrue(
                activeStatusRule.contains(
                        "color:"
                )
        );

        assertTrue(
                stylesheet.contains(
                        "@media (max-width: 768px)"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".page-heading"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".application-panel__header"
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