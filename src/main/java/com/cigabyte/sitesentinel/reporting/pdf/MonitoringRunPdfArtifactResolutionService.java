package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
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
            MonitoringRunPdfArtifactGenerationService
                    generationService
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
        return resolveCurrentVersion(
                websiteId,
                monitoringRunId,
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    public MonitoringRunPdfArtifact resolveCurrentVersion(
            UUID websiteId,
            UUID monitoringRunId,
            SiteSentinelReportLanguage reportLanguage
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

        SiteSentinelReportLanguage
                requiredReportLanguage =
                Objects.requireNonNull(
                        reportLanguage,
                        "PDF report language is required."
                );

        MonitoringRunPdfArtifact artifact =
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                requiredMonitoringRunId,
                                MonitoringRunPdfVersion.V1,
                                requiredReportLanguage
                        )
                        .orElseGet(
                                () ->
                                        generationService.generate(
                                                requiredWebsiteId,
                                                requiredMonitoringRunId,
                                                requiredReportLanguage
                                        )
                        );

        return validateResolvedArtifact(
                artifact,
                requiredMonitoringRunId,
                requiredReportLanguage
        );
    }

    private MonitoringRunPdfArtifact
    validateResolvedArtifact(
            MonitoringRunPdfArtifact artifact,
            UUID expectedMonitoringRunId,
            SiteSentinelReportLanguage expectedReportLanguage
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

        if (requiredArtifact.getReportLanguage()
                != expectedReportLanguage) {

            throw new IllegalStateException(
                    "Resolved monitoring run PDF artifact "
                            + "does not use the requested "
                            + "report language."
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