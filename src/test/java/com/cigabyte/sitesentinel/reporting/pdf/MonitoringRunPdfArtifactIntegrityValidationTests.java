package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class MonitoringRunPdfArtifactIntegrityValidationTests {

    private final MonitoringRunPdfArtifactRepository
            artifactRepository =
            mock(
                    MonitoringRunPdfArtifactRepository.class
            );

    private final MonitoringRunRepository
            monitoringRunRepository =
            mock(
                    MonitoringRunRepository.class
            );

    private final MonitoringRunPdfArtifactService
            artifactService =
            new MonitoringRunPdfArtifactService(
                    artifactRepository,
                    monitoringRunRepository
            );

    @Test
    void validateIntegrityAcceptsMatchingBinarySizeAndFingerprint() {
        MonitoringRunPdfArtifact artifact =
                validArtifact();

        assertDoesNotThrow(
                () -> artifactService.validateIntegrity(
                        artifact
                )
        );
    }

    @Test
    void validateIntegrityRejectsCorruptedSizeMetadata() {
        MonitoringRunPdfArtifact artifact =
                validArtifact();

        ReflectionTestUtils.setField(
                artifact,
                "sizeBytes",
                artifact.getSizeBytes() + 1
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> artifactService.validateIntegrity(
                        artifact
                )
        );
    }

    @Test
    void validateIntegrityRejectsCorruptedFingerprintMetadata() {
        MonitoringRunPdfArtifact artifact =
                validArtifact();

        ReflectionTestUtils.setField(
                artifact,
                "sha256Fingerprint",
                "a".repeat(64)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> artifactService.validateIntegrity(
                        artifact
                )
        );
    }

    private MonitoringRunPdfArtifact validArtifact() {
        UUID monitoringRunId =
                UUID.randomUUID();

        byte[] artifactBytes =
                (
                        "%PDF-1.7\n"
                                + "controlled-integrity-test"
                                + "\n%%EOF"
                ).getBytes(
                        StandardCharsets.UTF_8
                );

        return MonitoringRunPdfArtifact.create(
                monitoringRunId,
                MonitoringRunPdfVersion.V1,
                "sitesentinel-monitoring-run-"
                        + monitoringRunId
                        + "-v1.pdf",
                artifactBytes,
                calculateSha256Fingerprint(
                        artifactBytes
                ),
                OffsetDateTime.now(
                        ZoneOffset.UTC
                )
        );
    }

    private String calculateSha256Fingerprint(
            byte[] artifactBytes
    ) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            "SHA-256"
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
}