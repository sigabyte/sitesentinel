package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonitoringRunPdfArtifactLanguageTests {

    private static final UUID MONITORING_RUN_ID =
            UUID.fromString(
                    "f14be9b5-f05b-41c9-b0ee-d605876a83b8"
            );

    private static final OffsetDateTime GENERATED_AT =
            OffsetDateTime.of(
                    2026,
                    8,
                    17,
                    12,
                    30,
                    0,
                    0,
                    ZoneOffset.UTC
            );

    @Test
    void createsEnglishPdfArtifactWithExplicitLanguage() {
        MonitoringRunPdfArtifact artifact =
                MonitoringRunPdfArtifact.create(
                        MONITORING_RUN_ID,
                        MonitoringRunPdfVersion.V1,
                        SiteSentinelReportLanguage.ENGLISH,
                        "sitesentinel-monitoring-run-"
                                + MONITORING_RUN_ID
                                + "-en-v1.pdf",
                        validPdfBytes(),
                        validFingerprint(),
                        GENERATED_AT
                );

        assertEquals(
                SiteSentinelReportLanguage.ENGLISH,
                artifact.getReportLanguage()
        );
    }

    @Test
    void createsTurkishPdfArtifactWithExplicitLanguage() {
        MonitoringRunPdfArtifact artifact =
                MonitoringRunPdfArtifact.create(
                        MONITORING_RUN_ID,
                        MonitoringRunPdfVersion.V1,
                        SiteSentinelReportLanguage.TURKISH,
                        "sitesentinel-monitoring-run-"
                                + MONITORING_RUN_ID
                                + "-tr-v1.pdf",
                        validPdfBytes(),
                        validFingerprint(),
                        GENERATED_AT
                );

        assertEquals(
                SiteSentinelReportLanguage.TURKISH,
                artifact.getReportLanguage()
        );
    }

    @Test
    void rejectsMissingPdfArtifactLanguage() {
        assertThrows(
                NullPointerException.class,
                () -> MonitoringRunPdfArtifact.create(
                        MONITORING_RUN_ID,
                        MonitoringRunPdfVersion.V1,
                        null,
                        "sitesentinel-monitoring-run-"
                                + MONITORING_RUN_ID
                                + "-en-v1.pdf",
                        validPdfBytes(),
                        validFingerprint(),
                        GENERATED_AT
                )
        );
    }

    private byte[] validPdfBytes() {
        return "%PDF-1.7\ncontrolled-language-test"
                .getBytes(
                        StandardCharsets.US_ASCII
                );
    }

    private String validFingerprint() {
        return "a".repeat(64);
    }
}