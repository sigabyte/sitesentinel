package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.risk.RiskSeverity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class RiskRemediationRecommendationContext {

    private final UUID monitoringRunId;
    private final UUID riskId;

    private final String riskType;
    private final RiskSeverity severity;
    private final int riskScore;
    private final int confidenceScore;
    private final String rationale;

    private final List<RiskRemediationRecommendationFindingContext>
            findings;

    private final String fingerprint;

    private RiskRemediationRecommendationContext(
            UUID monitoringRunId,
            UUID riskId,
            String riskType,
            RiskSeverity severity,
            int riskScore,
            int confidenceScore,
            String rationale,
            List<RiskRemediationRecommendationFindingContext> findings,
            String fingerprint
    ) {
        this.monitoringRunId = monitoringRunId;
        this.riskId = riskId;
        this.riskType = riskType;
        this.severity = severity;
        this.riskScore = riskScore;
        this.confidenceScore = confidenceScore;
        this.rationale = rationale;
        this.findings = findings;
        this.fingerprint = fingerprint;
    }

    public static RiskRemediationRecommendationContext create(
            UUID monitoringRunId,
            UUID riskId,
            String riskType,
            RiskSeverity severity,
            int riskScore,
            int confidenceScore,
            String rationale,
            List<RiskRemediationRecommendationFindingContext> findings
    ) {
        UUID requiredMonitoringRunId = Objects.requireNonNull(
                monitoringRunId,
                "Monitoring run ID is required."
        );

        UUID requiredRiskId = Objects.requireNonNull(
                riskId,
                "Risk ID is required."
        );

        String requiredRiskType = requireText(
                riskType,
                "Risk type"
        );

        RiskSeverity requiredSeverity = Objects.requireNonNull(
                severity,
                "Risk severity is required."
        );

        validateScore(
                riskScore,
                "Risk score"
        );

        validateScore(
                confidenceScore,
                "Risk confidence score"
        );

        String requiredRationale = requireText(
                rationale,
                "Risk rationale"
        );

        List<RiskRemediationRecommendationFindingContext>
                immutableFindings = findings == null
                ? List.of()
                : List.copyOf(findings);

        String fingerprint = calculateFingerprint(
                requiredMonitoringRunId,
                requiredRiskId,
                requiredRiskType,
                requiredSeverity,
                riskScore,
                confidenceScore,
                requiredRationale,
                immutableFindings
        );

        return new RiskRemediationRecommendationContext(
                requiredMonitoringRunId,
                requiredRiskId,
                requiredRiskType,
                requiredSeverity,
                riskScore,
                confidenceScore,
                requiredRationale,
                immutableFindings,
                fingerprint
        );
    }

    private static String calculateFingerprint(
            UUID monitoringRunId,
            UUID riskId,
            String riskType,
            RiskSeverity severity,
            int riskScore,
            int confidenceScore,
            String rationale,
            List<RiskRemediationRecommendationFindingContext> findings
    ) {
        StringBuilder canonicalContext =
                new StringBuilder();

        appendCanonical(
                canonicalContext,
                "monitoringRunId",
                monitoringRunId.toString()
        );

        appendCanonical(
                canonicalContext,
                "riskId",
                riskId.toString()
        );

        appendCanonical(
                canonicalContext,
                "riskType",
                riskType
        );

        appendCanonical(
                canonicalContext,
                "severity",
                severity.name()
        );

        appendCanonical(
                canonicalContext,
                "riskScore",
                Integer.toString(riskScore)
        );

        appendCanonical(
                canonicalContext,
                "confidenceScore",
                Integer.toString(confidenceScore)
        );

        appendCanonical(
                canonicalContext,
                "rationale",
                rationale
        );

        appendCanonical(
                canonicalContext,
                "findingCount",
                Integer.toString(findings.size())
        );

        for (int findingIndex = 0;
             findingIndex < findings.size();
             findingIndex++) {

            RiskRemediationRecommendationFindingContext finding =
                    findings.get(findingIndex);

            String findingPrefix =
                    "finding[" + findingIndex + "].";

            appendCanonical(
                    canonicalContext,
                    findingPrefix + "id",
                    finding.findingId().toString()
            );

            appendCanonical(
                    canonicalContext,
                    findingPrefix + "type",
                    finding.findingType()
            );

            appendCanonical(
                    canonicalContext,
                    findingPrefix + "title",
                    finding.title()
            );

            appendCanonical(
                    canonicalContext,
                    findingPrefix + "description",
                    finding.description()
            );

            appendCanonical(
                    canonicalContext,
                    findingPrefix + "confidenceScore",
                    Integer.toString(
                            finding.confidenceScore()
                    )
            );

            appendCanonical(
                    canonicalContext,
                    findingPrefix + "evidenceCount",
                    Integer.toString(
                            finding.evidenceItems().size()
                    )
            );

            for (int evidenceIndex = 0;
                 evidenceIndex
                         < finding.evidenceItems().size();
                 evidenceIndex++) {

                RiskRemediationRecommendationEvidenceContext
                        evidence =
                        finding.evidenceItems().get(
                                evidenceIndex
                        );

                String evidencePrefix =
                        findingPrefix
                                + "evidence["
                                + evidenceIndex
                                + "].";

                appendCanonical(
                        canonicalContext,
                        evidencePrefix + "normalizedEvidenceId",
                        evidence.normalizedEvidenceId().toString()
                );

                appendCanonical(
                        canonicalContext,
                        evidencePrefix + "collectedEvidenceId",
                        evidence.collectedEvidenceId().toString()
                );

                appendCanonical(
                        canonicalContext,
                        evidencePrefix + "type",
                        evidence.normalizedType()
                );

                appendCanonical(
                        canonicalContext,
                        evidencePrefix + "value",
                        evidence.normalizedValue()
                );
            }
        }

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] fingerprintBytes = digest.digest(
                    canonicalContext
                            .toString()
                            .getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(
                    fingerprintBytes
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 context fingerprinting is unavailable.",
                    exception
            );
        }
    }

    private static void appendCanonical(
            StringBuilder target,
            String fieldName,
            String fieldValue
    ) {
        target.append(fieldName.length())
                .append(':')
                .append(fieldName)
                .append('=')
                .append(fieldValue.length())
                .append(':')
                .append(fieldValue)
                .append('|');
    }

    private static String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value.trim();
    }

    private static void validateScore(
            int value,
            String fieldName
    ) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be between 0 and 100."
            );
        }
    }

    public UUID getMonitoringRunId() {
        return monitoringRunId;
    }

    public UUID getRiskId() {
        return riskId;
    }

    public String getRiskType() {
        return riskType;
    }

    public RiskSeverity getSeverity() {
        return severity;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public String getRationale() {
        return rationale;
    }

    public List<RiskRemediationRecommendationFindingContext>
    getFindings() {
        return findings;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public int getFindingCount() {
        return findings.size();
    }

    public int getEvidenceCount() {
        return findings.stream()
                .mapToInt(
                        finding ->
                                finding.evidenceItems().size()
                )
                .sum();
    }
}