package com.cigabyte.sitesentinel.reporting;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonitoringRunReportPdfLanguageTemplateTests {

    private static final String TEMPLATE_PATH =
            "/templates/reports/monitoring-run-report.html";

    @Test
    void templateContainsSeparateEnglishAndTurkishPdfArtifactSections()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "English PDF report"
                )
        );

        assertTrue(
                template.contains(
                        "Turkish PDF report"
                )
        );

        assertTrue(
                template.contains(
                        "${englishPdfArtifactAvailable}"
                )
        );

        assertTrue(
                template.contains(
                        "${turkishPdfArtifactAvailable}"
                )
        );
    }

    @Test
    void templateContainsSeparateEnglishAndTurkishDownloadBindings()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "${englishPdfArtifact.id}"
                )
        );

        assertTrue(
                template.contains(
                        "${englishPdfArtifact.fileName}"
                )
        );

        assertTrue(
                template.contains(
                        "${turkishPdfArtifact.id}"
                )
        );

        assertTrue(
                template.contains(
                        "${turkishPdfArtifact.fileName}"
                )
        );

        assertTrue(
                template.contains(
                        "Download English PDF"
                )
        );

        assertTrue(
                template.contains(
                        "Download Turkish PDF"
                )
        );
    }

    @Test
    void templateContainsLanguageSpecificGenerationAvailabilityBindings()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "${englishPdfArtifactGenerationAvailable}"
                )
        );

        assertTrue(
                template.contains(
                        "${turkishPdfArtifactGenerationAvailable}"
                )
        );
    }

    @Test
    void templateContainsLanguageSpecificManualGenerationForms()
            throws IOException {

        String template =
                loadTemplate();

        assertTrue(
                template.contains(
                        "name=\"reportLanguage\" value=\"ENGLISH\""
                )
        );

        assertTrue(
                template.contains(
                        "name=\"reportLanguage\" value=\"TURKISH\""
                )
        );

        assertTrue(
                template.contains(
                        "Generate English PDF"
                )
        );

        assertTrue(
                template.contains(
                        "Generate Turkish PDF"
                )
        );

        assertTrue(
                template.contains(
                        "th:if=\"${englishPdfArtifactGenerationAvailable}\""
                )
        );

        assertTrue(
                template.contains(
                        "th:if=\"${turkishPdfArtifactGenerationAvailable}\""
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
                    "Monitoring run report template "
                            + "must exist on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}