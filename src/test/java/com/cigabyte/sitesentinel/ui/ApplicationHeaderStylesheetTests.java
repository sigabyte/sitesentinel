package com.cigabyte.sitesentinel.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationHeaderStylesheetTests {

    private static final String STYLESHEET_PATH =
            "/static/css/app.css";

    @Test
    void stylesheetProvidesPremiumApplicationHeader()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String headerRule =
                ruleFor(
                        stylesheet,
                        ".application-header"
                );

        assertTrue(
                headerRule.contains(
                        "background:"
                )
        );

        assertTrue(
                headerRule.contains(
                        "border:"
                )
        );

        assertTrue(
                headerRule.contains(
                        "border-radius:"
                )
        );

        assertTrue(
                headerRule.contains(
                        "box-shadow:"
                )
        );
    }

    @Test
    void stylesheetProvidesResponsiveHeaderTopRowAndBrand()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String topRowRule =
                ruleFor(
                        stylesheet,
                        ".application-header__top-row"
                );

        String brandRule =
                ruleFor(
                        stylesheet,
                        ".application-brand"
                );

        String brandMarkRule =
                ruleFor(
                        stylesheet,
                        ".application-brand-mark"
                );

        assertTrue(
                topRowRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                topRowRule.contains(
                        "flex-wrap:"
                )
        );

        assertTrue(
                brandRule.contains(
                        "display: inline-flex"
                )
        );

        assertTrue(
                brandMarkRule.contains(
                        "border-radius:"
                )
        );
    }

    @Test
    void stylesheetProvidesOverflowSafePrimaryNavigation()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String navigationRule =
                ruleFor(
                        stylesheet,
                        ".application-navigation"
                );

        String navigationLinkRule =
                ruleFor(
                        stylesheet,
                        ".application-navigation__link"
                );

        assertTrue(
                navigationRule.contains(
                        "display: flex"
                )
        );

        assertTrue(
                navigationRule.contains(
                        "overflow-x: auto"
                )
        );

        assertTrue(
                navigationLinkRule.contains(
                        "min-height:"
                )
        );
    }

    @Test
    void stylesheetProvidesVisibleActiveNavigationAndLogoutControl()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        String activeLinkRule =
                ruleFor(
                        stylesheet,
                        ".application-navigation__link--active"
                );

        String logoutButtonRule =
                ruleFor(
                        stylesheet,
                        ".application-logout-button"
                );

        assertTrue(
                activeLinkRule.contains(
                        "background:"
                )
        );

        assertTrue(
                activeLinkRule.contains(
                        "color:"
                )
        );

        assertTrue(
                logoutButtonRule.contains(
                        "border:"
                )
        );

        assertTrue(
                logoutButtonRule.contains(
                        "cursor: pointer"
                )
        );
    }

    @Test
    void stylesheetProvidesMobileApplicationHeaderLayout()
            throws IOException {

        String stylesheet =
                loadStylesheet();

        assertTrue(
                stylesheet.contains(
                        "@media (max-width: 768px)"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".application-header__top-row"
                )
        );

        assertTrue(
                stylesheet.contains(
                        ".application-navigation"
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