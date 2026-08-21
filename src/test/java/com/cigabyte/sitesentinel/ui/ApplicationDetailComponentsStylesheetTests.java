package com.cigabyte.sitesentinel.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationDetailComponentsStylesheetTests {

    private static final String STYLESHEET_PATH =
            "/static/css/app.css";

    @Test
    void stylesheetProvidesResponsiveDetailGrid()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String detailGridRule =
                ruleFor(
                        stylesheet,
                        ".detail-grid"
                );

        assertTrue(
                detailGridRule.contains(
                        "display: grid"
                )
        );

        assertTrue(
                detailGridRule.contains(
                        "grid-template-columns:"
                )
        );

        assertTrue(
                detailGridRule.contains(
                        "gap:"
                )
        );
    }

    @Test
    void stylesheetProvidesReadableDetailItems()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String detailItemRule =
                ruleFor(
                        stylesheet,
                        ".detail-item"
                );

        String detailLabelRule =
                ruleFor(
                        stylesheet,
                        ".detail-label"
                );

        String detailValueRule =
                ruleFor(
                        stylesheet,
                        ".detail-value"
                );

        assertTrue(
                detailItemRule.contains(
                        "min-width:"
                )
        );

        assertTrue(
                detailItemRule.contains(
                        "padding:"
                )
        );

        assertTrue(
                detailLabelRule.contains(
                        "text-transform:"
                )
        );

        assertTrue(
                detailValueRule.contains(
                        "overflow-wrap: anywhere"
                )
        );
    }

    @Test
    void stylesheetProvidesVerticalSectionStack()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String sectionStackRule =
                ruleFor(
                        stylesheet,
                        ".section-stack"
                );

        assertTrue(
                sectionStackRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                sectionStackRule.contains(
                        "flex-direction: column"
                )
        );

        assertTrue(
                sectionStackRule.contains(
                        "gap:"
                )
        );
    }

    @Test
    void stylesheetProvidesReusableActionGroup()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String actionGroupRule =
                ruleFor(
                        stylesheet,
                        ".action-group"
                );

        assertTrue(
                actionGroupRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                actionGroupRule.contains(
                        "flex-wrap:"
                )
        );

        assertTrue(
                actionGroupRule.contains(
                        "gap:"
                )
        );
    }

    @Test
    void stylesheetProvidesSuccessWarningAndMobileDetailStates()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String successStateRule =
                ruleFor(
                        stylesheet,
                        ".state-message--success"
                );

        String warningStateRule =
                ruleFor(
                        stylesheet,
                        ".state-message--warning"
                );

        assertTrue(
                successStateRule.contains(
                        "background:"
                )
        );

        assertTrue(
                successStateRule.contains(
                        "border:"
                )
        );

        assertTrue(
                warningStateRule.contains(
                        "background:"
                )
        );

        assertTrue(
                warningStateRule.contains(
                        "border:"
                )
        );

        assertTrue(
                stylesheet.contains(
                        "@media (max-width: 768px)"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".detail-grid"
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