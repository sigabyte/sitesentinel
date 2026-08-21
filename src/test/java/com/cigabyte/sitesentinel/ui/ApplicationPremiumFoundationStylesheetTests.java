package com.cigabyte.sitesentinel.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationPremiumFoundationStylesheetTests {

    private static final String STYLESHEET_PATH =
            "/static/css/app.css";

    @Test
    void stylesheetProvidesResponsiveApplicationPageAndShell()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String applicationPageRule =
                ruleFor(
                        stylesheet,
                        ".application-page"
                );

        String applicationShellRule =
                ruleFor(
                        stylesheet,
                        ".application-shell"
                );

        assertTrue(
                applicationPageRule.contains(
                        "background:"
                )
        );

        assertTrue(
                applicationPageRule.contains(
                        "color:"
                )
        );

        assertTrue(
                applicationShellRule.contains(
                        "max-width:"
                )
        );

        assertTrue(
                applicationShellRule.contains(
                        "margin: 0 auto"
                )
        );

        assertTrue(
                applicationShellRule.contains(
                        "padding:"
                )
        );
    }

    @Test
    void stylesheetProvidesReusablePremiumPanel()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String panelRule =
                ruleFor(
                        stylesheet,
                        ".application-panel"
                );

        assertTrue(
                panelRule.contains(
                        "background:"
                )
        );

        assertTrue(
                panelRule.contains(
                        "border:"
                )
        );

        assertTrue(
                panelRule.contains(
                        "border-radius:"
                )
        );

        assertTrue(
                panelRule.contains(
                        "box-shadow:"
                )
        );
    }

    @Test
    void stylesheetProvidesOverflowSafeTableAndTechnicalValues()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String tableContainerRule =
                ruleFor(
                        stylesheet,
                        ".table-container"
                );

        String technicalValueRule =
                ruleFor(
                        stylesheet,
                        ".technical-value"
                );

        assertTrue(
                tableContainerRule.contains(
                        "overflow-x: auto"
                )
        );

        assertTrue(
                technicalValueRule.contains(
                        "overflow-wrap: anywhere"
                )
        );
    }

    @Test
    void stylesheetProvidesApplicationWideKeyboardFocusAndMobileLayout()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String focusRule =
                ruleFor(
                        stylesheet,
                        ".application-page :focus-visible"
                );

        assertTrue(
                focusRule.contains(
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
                        ".application-shell"
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