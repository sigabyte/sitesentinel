package com.cigabyte.sitesentinel.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationFormComponentsStylesheetTests {

    private static final String STYLESHEET_PATH =
            "/static/css/app.css";

    @Test
    void stylesheetProvidesResponsiveFormLayout()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String formLayoutRule =
                ruleFor(
                        stylesheet,
                        ".form-layout"
                );

        assertTrue(
                formLayoutRule.contains(
                        "display: grid"
                )
        );

        assertTrue(
                formLayoutRule.contains(
                        "grid-template-columns:"
                )
        );

        assertTrue(
                formLayoutRule.contains(
                        "gap:"
                )
        );
    }

    @Test
    void stylesheetProvidesReadableFormFieldsAndControls()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String fieldRule =
                ruleFor(
                        stylesheet,
                        ".form-field"
                );

        String labelRule =
                ruleFor(
                        stylesheet,
                        ".form-field label"
                );

        String controlRule =
                ruleFor(
                        stylesheet,
                        ".form-control"
                );

        assertTrue(
                fieldRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                labelRule.contains(
                        "font-weight:"
                )
        );

        assertTrue(
                controlRule.contains(
                        "width: 100%"
                )
        );

        assertTrue(
                controlRule.contains(
                        "border:"
                )
        );

        assertTrue(
                controlRule.contains(
                        "border-radius:"
                )
        );
    }

    @Test
    void stylesheetProvidesVisibleControlFocusAndValidationErrors()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String focusRule =
                ruleFor(
                        stylesheet,
                        ".form-control:focus"
                );

        String invalidRule =
                ruleFor(
                        stylesheet,
                        ".form-control--invalid"
                );

        String fieldErrorRule =
                ruleFor(
                        stylesheet,
                        ".field-error"
                );

        assertTrue(
                focusRule.contains(
                        "border-color:"
                )
        );

        assertTrue(
                focusRule.contains(
                        "box-shadow:"
                )
        );

        assertTrue(
                invalidRule.contains(
                        "border-color:"
                )
        );

        assertTrue(
                fieldErrorRule.contains(
                        "color:"
                )
        );
    }

    @Test
    void stylesheetProvidesFormActionsAndFailureState()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String formActionsRule =
                ruleFor(
                        stylesheet,
                        ".form-actions"
                );

        String failureStateRule =
                ruleFor(
                        stylesheet,
                        ".state-message--failure"
                );

        assertTrue(
                formActionsRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                formActionsRule.contains(
                        "flex-wrap:"
                )
        );

        assertTrue(
                failureStateRule.contains(
                        "background:"
                )
        );

        assertTrue(
                failureStateRule.contains(
                        "border:"
                )
        );

        assertTrue(
                failureStateRule.contains(
                        "color:"
                )
        );
    }

    @Test
    void stylesheetProvidesGuidancePanelAndMobileFormLayout()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String guidanceRule =
                ruleFor(
                        stylesheet,
                        ".form-guidance"
                );

        String guidanceListRule =
                ruleFor(
                        stylesheet,
                        ".guidance-list"
                );

        assertTrue(
                guidanceRule.contains(
                        "align-self:"
                )
        );

        assertTrue(
                guidanceListRule.contains(
                        "padding-left:"
                )
        );

        assertTrue(
                stylesheet.contains(
                        "@media (max-width: 900px)"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".form-layout"
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