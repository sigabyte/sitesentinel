package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonitoringRunPdfFileNameFactoryTests {

    private final MonitoringRunPdfFileNameFactory
            fileNameFactory =
            new MonitoringRunPdfFileNameFactory();

    @Test
    void createsDeterministicEnglishFileName() {
        UUID monitoringRunId =
                UUID.fromString(
                        "40eaa15c-7236-4d64-b957-606b78af1f88"
                );

        String firstFileName =
                fileNameFactory.create(
                        monitoringRunId,
                        MonitoringRunPdfVersion.V1,
                        SiteSentinelReportLanguage.ENGLISH
                );

        String secondFileName =
                fileNameFactory.create(
                        monitoringRunId,
                        MonitoringRunPdfVersion.V1,
                        SiteSentinelReportLanguage.ENGLISH
                );

        assertEquals(
                "sitesentinel-monitoring-run-"
                        + "40eaa15c-7236-4d64-b957-606b78af1f88"
                        + "-en-v1.pdf",
                firstFileName
        );

        assertEquals(
                firstFileName,
                secondFileName
        );

        assertPathSafePdfFileName(
                firstFileName
        );
    }

    @Test
    void createsDeterministicTurkishFileName() {
        UUID monitoringRunId =
                UUID.fromString(
                        "40eaa15c-7236-4d64-b957-606b78af1f88"
                );

        String fileName =
                fileNameFactory.create(
                        monitoringRunId,
                        MonitoringRunPdfVersion.V1,
                        SiteSentinelReportLanguage.TURKISH
                );

        assertEquals(
                "sitesentinel-monitoring-run-"
                        + "40eaa15c-7236-4d64-b957-606b78af1f88"
                        + "-tr-v1.pdf",
                fileName
        );

        assertPathSafePdfFileName(
                fileName
        );
    }

    @Test
    void rejectsMissingMonitoringRunIdVersionOrLanguage() {
        assertThrows(
                NullPointerException.class,
                () -> fileNameFactory.create(
                        null,
                        MonitoringRunPdfVersion.V1,
                        SiteSentinelReportLanguage.ENGLISH
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> fileNameFactory.create(
                        UUID.randomUUID(),
                        null,
                        SiteSentinelReportLanguage.ENGLISH
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> fileNameFactory.create(
                        UUID.randomUUID(),
                        MonitoringRunPdfVersion.V1,
                        null
                )
        );
    }

    private void assertPathSafePdfFileName(
            String fileName
    ) {
        assertTrue(
                fileName.endsWith(".pdf")
        );

        assertFalse(
                fileName.contains("/")
        );

        assertFalse(
                fileName.contains("\\")
        );

        assertTrue(
                fileName.length() <= 255
        );
    }
}