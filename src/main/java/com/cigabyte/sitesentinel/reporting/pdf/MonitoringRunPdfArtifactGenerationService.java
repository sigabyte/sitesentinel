package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.reporting.MonitoringRunReportService;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportView;
import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

@Service
public class MonitoringRunPdfArtifactGenerationService {

    private static final String SHA_256_ALGORITHM =
            "SHA-256";

    private static final int MAX_ARTIFACT_SIZE_BYTES =
            10 * 1024 * 1024;

    private final MonitoringRunReportService
            monitoringRunReportService;

    private final MonitoringRunPdfRenderer
            pdfRenderer;

    private final MonitoringRunPdfFileNameFactory
            fileNameFactory;

    private final MonitoringRunPdfArtifactService
            artifactService;

    public MonitoringRunPdfArtifactGenerationService(
            MonitoringRunReportService
                    monitoringRunReportService,
            MonitoringRunPdfRenderer pdfRenderer,
            MonitoringRunPdfFileNameFactory
                    fileNameFactory,
            MonitoringRunPdfArtifactService
                    artifactService
    ) {
        this.monitoringRunReportService =
                monitoringRunReportService;

        this.pdfRenderer = pdfRenderer;
        this.fileNameFactory = fileNameFactory;
        this.artifactService = artifactService;
    }

    public MonitoringRunPdfArtifact generate(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        return generate(
                websiteId,
                monitoringRunId,
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    public MonitoringRunPdfArtifact generate(
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

        MonitoringRunReportView reportView =
                monitoringRunReportService.buildReport(
                        requiredWebsiteId,
                        requiredMonitoringRunId,
                        requiredReportLanguage
                );

        UUID persistedMonitoringRunId =
                extractAndValidateMonitoringRunId(
                        reportView,
                        requiredMonitoringRunId
                );

        validateReportLanguage(
                reportView,
                requiredReportLanguage
        );

        MonitoringRunPdfVersion reportVersion =
                Objects.requireNonNull(
                        pdfRenderer.getReportVersion(),
                        "PDF renderer report version is required."
                );

        rejectExistingArtifact(
                persistedMonitoringRunId,
                reportVersion,
                requiredReportLanguage
        );

        byte[] artifactBytes =
                pdfRenderer.render(
                        reportView
                );

        validateRenderedBytes(
                artifactBytes
        );

        String fileName =
                fileNameFactory.create(
                        persistedMonitoringRunId,
                        reportVersion,
                        requiredReportLanguage
                );

        String sha256Fingerprint =
                calculateSha256Fingerprint(
                        artifactBytes
                );

        MonitoringRunPdfArtifact artifact =
                MonitoringRunPdfArtifact.create(
                        persistedMonitoringRunId,
                        reportVersion,
                        requiredReportLanguage,
                        fileName,
                        artifactBytes,
                        sha256Fingerprint,
                        OffsetDateTime.now(
                                ZoneOffset.UTC
                        )
                );

        return artifactService.saveValidated(
                artifact
        );
    }

    private UUID extractAndValidateMonitoringRunId(
            MonitoringRunReportView reportView,
            UUID requestedMonitoringRunId
    ) {
        MonitoringRunReportView requiredReportView =
                Objects.requireNonNull(
                        reportView,
                        "Monitoring run report view is required."
                );

        if (requiredReportView.getMonitoringRun()
                == null) {

            throw new IllegalStateException(
                    "Monitoring run report does not contain "
                            + "a monitoring run."
            );
        }

        UUID persistedMonitoringRunId =
                requiredReportView
                        .getMonitoringRun()
                        .getId();

        if (persistedMonitoringRunId == null) {
            throw new IllegalStateException(
                    "Monitoring run report contains "
                            + "an unpersisted monitoring run."
            );
        }

        if (!requestedMonitoringRunId.equals(
                persistedMonitoringRunId
        )) {
            throw new IllegalStateException(
                    "Monitoring run report does not match "
                            + "the requested monitoring run."
            );
        }

        return persistedMonitoringRunId;
    }

    private void validateReportLanguage(
            MonitoringRunReportView reportView,
            SiteSentinelReportLanguage requestedReportLanguage
    ) {
        if (reportView.getReportLanguage()
                != requestedReportLanguage) {

            throw new IllegalStateException(
                    "Monitoring run report language does not "
                            + "match the requested PDF language."
            );
        }
    }

    private void rejectExistingArtifact(
            UUID monitoringRunId,
            MonitoringRunPdfVersion reportVersion,
            SiteSentinelReportLanguage reportLanguage
    ) {
        boolean artifactAlreadyExists =
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRunId,
                                reportVersion,
                                reportLanguage
                        )
                        .isPresent();

        if (artifactAlreadyExists) {
            throw new IllegalStateException(
                    "A PDF artifact already exists for "
                            + "monitoringRunId="
                            + monitoringRunId
                            + ", reportVersion="
                            + reportVersion.getValue()
                            + " and reportLanguage="
                            + reportLanguage.getPersistenceValue()
                            + "."
            );
        }
    }

    private void validateRenderedBytes(
            byte[] artifactBytes
    ) {
        if (artifactBytes == null
                || artifactBytes.length == 0) {

            throw new IllegalStateException(
                    "PDF renderer produced empty content."
            );
        }

        if (artifactBytes.length
                > MAX_ARTIFACT_SIZE_BYTES) {

            throw new IllegalStateException(
                    "PDF artifact exceeds the maximum "
                            + "allowed size of "
                            + MAX_ARTIFACT_SIZE_BYTES
                            + " bytes."
            );
        }
    }

    private String calculateSha256Fingerprint(
            byte[] artifactBytes
    ) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            SHA_256_ALGORITHM
                    );

            return HexFormat.of().formatHex(
                    messageDigest.digest(
                            artifactBytes
                    )
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable.",
                    exception
            );
        }
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