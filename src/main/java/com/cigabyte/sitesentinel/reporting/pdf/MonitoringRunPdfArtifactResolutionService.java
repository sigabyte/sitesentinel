package com.cigabyte.sitesentinel.reporting.pdf;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class MonitoringRunPdfArtifactResolutionService {

    private final MonitoringRunPdfArtifactService
            artifactService;

    private final MonitoringRunPdfArtifactGenerationService
            generationService;

    public MonitoringRunPdfArtifactResolutionService(
            MonitoringRunPdfArtifactService artifactService,
            MonitoringRunPdfArtifactGenerationService generationService
    ) {
        this.artifactService =
                artifactService;

        this.generationService =
                generationService;
    }

    public MonitoringRunPdfArtifact resolveCurrentVersion(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        UUID requiredWebsiteId =
                requireId(
                        websiteId,
                        "Website ID"
                );

        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        MonitoringRunPdfArtifact artifact =
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                requiredMonitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
                        .orElseGet(
                                () -> generationService.generate(
                                        requiredWebsiteId,
                                        requiredMonitoringRunId
                                )
                        );

        return validateResolvedArtifact(
                artifact,
                requiredMonitoringRunId
        );
    }

    private MonitoringRunPdfArtifact
    validateResolvedArtifact(
            MonitoringRunPdfArtifact artifact,
            UUID expectedMonitoringRunId
    ) {
        MonitoringRunPdfArtifact requiredArtifact =
                Objects.requireNonNull(
                        artifact,
                        "Resolved monitoring run PDF artifact is required."
                );

        if (requiredArtifact.getId() == null) {
            throw new IllegalStateException(
                    "Resolved monitoring run PDF artifact "
                            + "must be persisted."
            );
        }

        if (!expectedMonitoringRunId.equals(
                requiredArtifact.getMonitoringRunId()
        )) {
            throw new IllegalStateException(
                    "Resolved monitoring run PDF artifact "
                            + "does not belong to the requested "
                            + "monitoring run."
            );
        }

        if (!MonitoringRunPdfVersion.V1
                .getValue()
                .equals(
                        requiredArtifact.getReportVersion()
                )) {

            throw new IllegalStateException(
                    "Resolved monitoring run PDF artifact "
                            + "does not use the current "
                            + "report version."
            );
        }

        artifactService.validateIntegrity(
                requiredArtifact
        );

        return requiredArtifact;
    }

    private UUID requireId(
            UUID value,
            String fieldName
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value;
    }
}