package com.cigabyte.sitesentinel.reporting.pdf;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MonitoringRunPdfArtifactResolutionServiceTests {

    private final MonitoringRunPdfArtifactService
            artifactService =
            mock(MonitoringRunPdfArtifactService.class);

    private final MonitoringRunPdfArtifactGenerationService
            generationService =
            mock(
                    MonitoringRunPdfArtifactGenerationService.class
            );

    private final MonitoringRunPdfArtifactResolutionService
            resolutionService =
            new MonitoringRunPdfArtifactResolutionService(
                    artifactService,
                    generationService
            );

    @Test
    void resolveCurrentVersionReusesExistingPersistedArtifact() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunPdfArtifact existingArtifact =
                persistedArtifact(
                        monitoringRunId
                );

        when(
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
        ).thenReturn(
                Optional.of(
                        existingArtifact
                )
        );

        MonitoringRunPdfArtifact resolvedArtifact =
                resolutionService.resolveCurrentVersion(
                        websiteId,
                        monitoringRunId
                );

        assertSame(
                existingArtifact,
                resolvedArtifact
        );

        verify(
                artifactService
        ).findByMonitoringRunIdAndReportVersion(
                monitoringRunId,
                MonitoringRunPdfVersion.V1
        );

        verify(
                artifactService
        ).validateIntegrity(
                existingArtifact
        );

        verify(
                generationService,
                never()
        ).generate(
                websiteId,
                monitoringRunId
        );
    }

    @Test
    void resolveCurrentVersionGeneratesArtifactWhenNoneExists() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunPdfArtifact generatedArtifact =
                persistedArtifact(
                        monitoringRunId
                );

        when(
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(
                generationService.generate(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(
                generatedArtifact
        );

        MonitoringRunPdfArtifact resolvedArtifact =
                resolutionService.resolveCurrentVersion(
                        websiteId,
                        monitoringRunId
                );

        assertSame(
                generatedArtifact,
                resolvedArtifact
        );

        verify(
                artifactService
        ).findByMonitoringRunIdAndReportVersion(
                monitoringRunId,
                MonitoringRunPdfVersion.V1
        );

        verify(
                generationService
        ).generate(
                websiteId,
                monitoringRunId
        );

        verify(
                artifactService
        ).validateIntegrity(
                generatedArtifact
        );
    }

    @Test
    void resolveCurrentVersionRejectsMissingIdentifiers() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> resolutionService.resolveCurrentVersion(
                        null,
                        monitoringRunId
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolutionService.resolveCurrentVersion(
                        websiteId,
                        null
                )
        );

        verifyNoInteractions(
                artifactService,
                generationService
        );
    }

    @Test
    void resolveCurrentVersionRejectsArtifactOwnedByDifferentRun() {
        UUID websiteId =
                UUID.randomUUID();

        UUID requestedMonitoringRunId =
                UUID.randomUUID();

        UUID differentMonitoringRunId =
                UUID.randomUUID();

        MonitoringRunPdfArtifact wrongArtifact =
                persistedArtifact(
                        differentMonitoringRunId
                );

        when(
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                requestedMonitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
        ).thenReturn(
                Optional.of(
                        wrongArtifact
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> resolutionService.resolveCurrentVersion(
                        websiteId,
                        requestedMonitoringRunId
                )
        );

        verify(
                generationService,
                never()
        ).generate(
                websiteId,
                requestedMonitoringRunId
        );

        verify(
                artifactService,
                never()
        ).validateIntegrity(
                wrongArtifact
        );
    }

    @Test
    void resolveCurrentVersionRejectsArtifactThatFailsIntegrityValidation() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunPdfArtifact corruptedArtifact =
                persistedArtifact(
                        monitoringRunId
                );

        when(
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
        ).thenReturn(
                Optional.of(
                        corruptedArtifact
                )
        );

        doThrow(
                new IllegalArgumentException(
                        "Controlled artifact integrity failure."
                )
        ).when(
                artifactService
        ).validateIntegrity(
                corruptedArtifact
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolutionService.resolveCurrentVersion(
                        websiteId,
                        monitoringRunId
                )
        );

        verify(
                artifactService
        ).validateIntegrity(
                corruptedArtifact
        );

        verify(
                generationService,
                never()
        ).generate(
                websiteId,
                monitoringRunId
        );
    }

    private MonitoringRunPdfArtifact persistedArtifact(
            UUID monitoringRunId
    ) {
        MonitoringRunPdfArtifact artifact =
                mock(
                        MonitoringRunPdfArtifact.class
                );

        when(
                artifact.getId()
        ).thenReturn(
                UUID.randomUUID()
        );

        when(
                artifact.getMonitoringRunId()
        ).thenReturn(
                monitoringRunId
        );

        when(
                artifact.getReportVersion()
        ).thenReturn(
                MonitoringRunPdfVersion.V1.getValue()
        );

        return artifact;
    }
}