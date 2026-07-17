package com.cigabyte.sitesentinel.reporting.pdf;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Entity
@Table(name = "monitoring_run_pdf_artifacts")
public class MonitoringRunPdfArtifact {

    private static final String PDF_CONTENT_TYPE =
            "application/pdf";

    private static final int REPORT_VERSION_MAX_LENGTH = 80;
    private static final int FILE_NAME_MAX_LENGTH = 255;
    private static final int CONTENT_TYPE_MAX_LENGTH = 100;
    private static final int SHA_256_FINGERPRINT_LENGTH = 64;

    private static final byte[] PDF_HEADER =
            "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private static final Pattern SHA_256_PATTERN =
            Pattern.compile("[0-9a-f]{64}");

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Column(
            name = "report_version",
            nullable = false,
            length = REPORT_VERSION_MAX_LENGTH
    )
    private String reportVersion;

    @Column(
            name = "file_name",
            nullable = false,
            length = FILE_NAME_MAX_LENGTH
    )
    private String fileName;

    @Column(
            name = "content_type",
            nullable = false,
            length = CONTENT_TYPE_MAX_LENGTH
    )
    private String contentType;

    @Column(name = "artifact_bytes", nullable = false)
    private byte[] artifactBytes;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(
            name = "sha256_fingerprint",
            nullable = false,
            length = SHA_256_FINGERPRINT_LENGTH
    )
    private String sha256Fingerprint;

    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected MonitoringRunPdfArtifact() {
    }

    private MonitoringRunPdfArtifact(
            UUID monitoringRunId,
            MonitoringRunPdfVersion reportVersion,
            String fileName,
            byte[] artifactBytes,
            String sha256Fingerprint,
            OffsetDateTime generatedAt
    ) {
        this.monitoringRunId = Objects.requireNonNull(
                monitoringRunId,
                "Monitoring run ID is required."
        );

        MonitoringRunPdfVersion requiredReportVersion =
                Objects.requireNonNull(
                        reportVersion,
                        "PDF report version is required."
                );

        this.reportVersion = normalizeRequiredText(
                requiredReportVersion.getValue(),
                "PDF report version",
                REPORT_VERSION_MAX_LENGTH
        );

        this.fileName = validateFileName(fileName);
        this.contentType = PDF_CONTENT_TYPE;

        this.artifactBytes =
                validateAndCopyArtifactBytes(
                        artifactBytes
                );

        this.sizeBytes =
                (long) this.artifactBytes.length;

        this.sha256Fingerprint =
                validateSha256Fingerprint(
                        sha256Fingerprint
                );

        this.generatedAt = Objects.requireNonNull(
                generatedAt,
                "PDF generation timestamp is required."
        );
    }

    public static MonitoringRunPdfArtifact create(
            UUID monitoringRunId,
            MonitoringRunPdfVersion reportVersion,
            String fileName,
            byte[] artifactBytes,
            String sha256Fingerprint,
            OffsetDateTime generatedAt
    ) {
        return new MonitoringRunPdfArtifact(
                monitoringRunId,
                reportVersion,
                fileName,
                artifactBytes,
                sha256Fingerprint,
                generatedAt
        );
    }

    private String validateFileName(String value) {
        String normalizedValue = normalizeRequiredText(
                value,
                "PDF file name",
                FILE_NAME_MAX_LENGTH
        );

        if (normalizedValue.contains("/")
                || normalizedValue.contains("\\")
                || normalizedValue.indexOf('\0') >= 0) {

            throw new IllegalArgumentException(
                    "PDF file name must not contain path separators "
                            + "or null characters."
            );
        }

        if (!normalizedValue
                .toLowerCase(Locale.ROOT)
                .endsWith(".pdf")) {

            throw new IllegalArgumentException(
                    "PDF file name must end with .pdf."
            );
        }

        return normalizedValue;
    }

    private byte[] validateAndCopyArtifactBytes(
            byte[] value
    ) {
        if (value == null || value.length == 0) {
            throw new IllegalArgumentException(
                    "PDF artifact content is required."
            );
        }

        if (!hasPdfHeader(value)) {
            throw new IllegalArgumentException(
                    "PDF artifact content must begin with a valid PDF header."
            );
        }

        return Arrays.copyOf(
                value,
                value.length
        );
    }

    private boolean hasPdfHeader(byte[] value) {
        if (value.length < PDF_HEADER.length) {
            return false;
        }

        for (int index = 0;
             index < PDF_HEADER.length;
             index++) {

            if (value[index] != PDF_HEADER[index]) {
                return false;
            }
        }

        return true;
    }

    private String validateSha256Fingerprint(
            String value
    ) {
        String normalizedValue = normalizeRequiredText(
                value,
                "PDF SHA-256 fingerprint",
                SHA_256_FINGERPRINT_LENGTH
        );

        if (!SHA_256_PATTERN
                .matcher(normalizedValue)
                .matches()) {

            throw new IllegalArgumentException(
                    "PDF SHA-256 fingerprint must contain exactly "
                            + "64 lowercase hexadecimal characters."
            );
        }

        return normalizedValue;
    }

    private String normalizeRequiredText(
            String value,
            String fieldName,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() > maximumLength) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }

        return normalizedValue;
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getMonitoringRunId() {
        return monitoringRunId;
    }

    public String getReportVersion() {
        return reportVersion;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getArtifactBytes() {
        return Arrays.copyOf(
                artifactBytes,
                artifactBytes.length
        );
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public String getSha256Fingerprint() {
        return sha256Fingerprint;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}