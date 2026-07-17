package com.cigabyte.sitesentinel.reporting.pdf;

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
    void createsDeterministicPathSafeVersionedFileName() {
        UUID monitoringRunId =
                UUID.fromString(
                        "40eaa15c-7236-4d64-b957-606b78af1f88"
                );

        String firstFileName =
                fileNameFactory.create(
                        monitoringRunId,
                        MonitoringRunPdfVersion.V1
                );

        String secondFileName =
                fileNameFactory.create(
                        monitoringRunId,
                        MonitoringRunPdfVersion.V1
                );

        assertEquals(
                "sitesentinel-monitoring-run-"
                        + "40eaa15c-7236-4d64-b957-606b78af1f88"
                        + "-v1.pdf",
                firstFileName
        );

        assertEquals(
                firstFileName,
                secondFileName
        );

        assertTrue(
                firstFileName.endsWith(".pdf")
        );

        assertFalse(
                firstFileName.contains("/")
        );

        assertFalse(
                firstFileName.contains("\\")
        );

        assertTrue(
                firstFileName.length() <= 255
        );
    }

    @Test
    void rejectsMissingMonitoringRunIdOrVersion() {
        assertThrows(
                NullPointerException.class,
                () -> fileNameFactory.create(
                        null,
                        MonitoringRunPdfVersion.V1
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> fileNameFactory.create(
                        UUID.randomUUID(),
                        null
                )
        );
    }
}