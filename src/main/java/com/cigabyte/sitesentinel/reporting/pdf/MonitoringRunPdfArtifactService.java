package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonitoringRunPdfArtifactService {

    private static final String SHA_256_ALGORITHM =
            "SHA-256";

    private static final byte[] PDF_HEADER =
            "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final MonitoringRunPdfArtifactRepository
            artifactRepository;

    private final MonitoringRunRepository
            monitoringRunRepository;

    public MonitoringRunPdfArtifactService(
            MonitoringRunPdfArtifactRepository
                    artifactRepository,
            MonitoringRunRepository monitoringRunRepository
    ) {
        this.artifactRepository = artifactRepository;
        this.monitoringRunRepository =
                monitoringRunRepository;
    }

    @Transactional
    public MonitoringRunPdfArtifact saveValidated(
            MonitoringRunPdfArtifact artifact
    ) {
        MonitoringRunPdfArtifact requiredArtifact =
                Objects.requireNonNull(
                        artifact,
                        "Monitoring run PDF artifact is required."
                );

        validatePersistenceBoundary(requiredArtifact);

        return artifactRepository.saveAndFlush(
                requiredArtifact
        );
    }

    @Transactional(readOnly = true)
    public Optional<MonitoringRunPdfArtifact>
    findByMonitoringRunIdAndReportVersion(
            UUID monitoringRunId,
            MonitoringRunPdfVersion reportVersion
    ) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        MonitoringRunPdfVersion requiredReportVersion =
                Objects.requireNonNull(
                        reportVersion,
                        "PDF report version is required."
                );

        return artifactRepository
                .findByMonitoringRunIdAndReportVersion(
                        requiredMonitoringRunId,
                        requiredReportVersion.getValue()
                );
    }

    @Transactional(readOnly = true)
    public List<MonitoringRunPdfArtifact>
    findByMonitoringRunId(UUID monitoringRunId) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return artifactRepository
                .findByMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
                        requiredMonitoringRunId
                );
    }

    @Transactional(readOnly = true)
    public MonitoringRunPdfArtifact
    findByIdAndMonitoringRunId(
            UUID artifactId,
            UUID monitoringRunId
    ) {
        UUID requiredArtifactId =
                requireId(
                        artifactId,
                        "PDF artifact ID"
                );

        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return artifactRepository
                .findByIdAndMonitoringRunId(
                        requiredArtifactId,
                        requiredMonitoringRunId
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Monitoring run PDF artifact not found: "
                                        + requiredArtifactId
                        )
                );
    }

    @Transactional(readOnly = true)
    public long countByMonitoringRunId(
            UUID monitoringRunId
    ) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return artifactRepository.countByMonitoringRunId(
                requiredMonitoringRunId
        );
    }

    private void validatePersistenceBoundary(
            MonitoringRunPdfArtifact artifact
    ) {
        if (artifact.getId() != null) {
            throw new IllegalArgumentException(
                    "Existing monitoring run PDF artifacts "
                            + "must not be updated."
            );
        }

        UUID monitoringRunId =
                requireId(
                        artifact.getMonitoringRunId(),
                        "Monitoring run ID"
                );

        validateMonitoringRun(monitoringRunId);
        validateReportVersion(artifact);
        validateArtifactIntegrity(artifact);

        if (artifactRepository
                .existsByMonitoringRunIdAndReportVersion(
                        monitoringRunId,
                        artifact.getReportVersion()
                )) {

            throw new IllegalStateException(
                    "A PDF artifact already exists for "
                            + "monitoringRunId="
                            + monitoringRunId
                            + " and reportVersion="
                            + artifact.getReportVersion()
                            + "."
            );
        }
    }

    private void validateMonitoringRun(
            UUID monitoringRunId
    ) {
        MonitoringRun monitoringRun =
                monitoringRunRepository
                        .findById(monitoringRunId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Monitoring run not found: "
                                                + monitoringRunId
                                )
                        );

        if (monitoringRun.getStatus()
                != MonitoringRunStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "PDF artifacts may only be persisted "
                            + "for completed monitoring runs."
            );
        }
    }

    private void validateReportVersion(
            MonitoringRunPdfArtifact artifact
    ) {
        String supportedReportVersion =
                MonitoringRunPdfVersion.V1.getValue();

        if (!supportedReportVersion.equals(
                artifact.getReportVersion()
        )) {
            throw new IllegalArgumentException(
                    "Unsupported PDF report version: "
                            + artifact.getReportVersion()
            );
        }
    }

    private void validateArtifactIntegrity(
            MonitoringRunPdfArtifact artifact
    ) {
        byte[] artifactBytes =
                artifact.getArtifactBytes();

        if (!hasPdfHeader(artifactBytes)) {
            throw new IllegalArgumentException(
                    "PDF artifact content must begin "
                            + "with a valid PDF header."
            );
        }

        Long recordedSize =
                artifact.getSizeBytes();

        if (recordedSize == null
                || recordedSize != artifactBytes.length) {

            throw new IllegalArgumentException(
                    "PDF artifact size metadata does not "
                            + "match the binary content."
            );
        }

        String calculatedFingerprint =
                calculateSha256Fingerprint(
                        artifactBytes
                );

        if (!calculatedFingerprint.equals(
                artifact.getSha256Fingerprint()
        )) {
            throw new IllegalArgumentException(
                    "PDF artifact SHA-256 fingerprint does not "
                            + "match the binary content."
            );
        }
    }

    private boolean hasPdfHeader(
            byte[] artifactBytes
    ) {
        if (artifactBytes == null
                || artifactBytes.length < PDF_HEADER.length) {

            return false;
        }

        for (int index = 0;
             index < PDF_HEADER.length;
             index++) {

            if (artifactBytes[index]
                    != PDF_HEADER[index]) {

                return false;
            }
        }

        return true;
    }

    private String calculateSha256Fingerprint(
            byte[] artifactBytes
    ) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            SHA_256_ALGORITHM
                    );

            byte[] digest =
                    messageDigest.digest(
                            artifactBytes
                    );

            return HexFormat.of().formatHex(digest);
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