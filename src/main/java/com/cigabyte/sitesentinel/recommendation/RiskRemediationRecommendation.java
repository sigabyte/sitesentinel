package com.cigabyte.sitesentinel.recommendation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "risk_remediation_recommendations")
public class RiskRemediationRecommendation {

    private static final int PROVIDER_NAME_MAX_LENGTH = 80;
    private static final int MODEL_NAME_MAX_LENGTH = 120;
    private static final int VERSION_MAX_LENGTH = 80;
    private static final int CONTEXT_FINGERPRINT_LENGTH = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Column(name = "risk_id", nullable = false)
    private UUID riskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RiskRemediationRecommendationSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "fallback_reason", nullable = false, length = 40)
    private RiskRemediationFallbackReason fallbackReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false, length = 20)
    private RiskRemediationRecommendationValidationStatus validationStatus =
            RiskRemediationRecommendationValidationStatus.VALID;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(
            name = "remediation_steps",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String remediationSteps;

    @Column(
            name = "verification_steps",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String verificationSteps;

    @Column(nullable = false)
    private boolean advisory = true;

    @Column(name = "provider_name", length = 80)
    private String providerName;

    @Column(name = "model_name", length = 120)
    private String modelName;

    @Column(name = "prompt_version", nullable = false, length = 80)
    private String promptVersion;

    @Column(name = "fallback_rule_version", length = 80)
    private String fallbackRuleVersion;

    @Column(
            name = "context_fingerprint",
            nullable = false,
            length = 64
    )
    private String contextFingerprint;

    @Column(name = "context_finding_count", nullable = false)
    private Integer contextFindingCount;

    @Column(name = "context_evidence_count", nullable = false)
    private Integer contextEvidenceCount;

    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected RiskRemediationRecommendation() {
    }

    private RiskRemediationRecommendation(
            UUID monitoringRunId,
            UUID riskId,
            RiskRemediationRecommendationSource source,
            RiskRemediationFallbackReason fallbackReason,
            RiskRemediationRecommendationContent content,
            String providerName,
            String modelName,
            String promptVersion,
            String fallbackRuleVersion,
            String contextFingerprint,
            Integer contextFindingCount,
            Integer contextEvidenceCount,
            OffsetDateTime generatedAt
    ) {
        this.monitoringRunId = Objects.requireNonNull(
                monitoringRunId,
                "Monitoring run ID is required."
        );

        this.riskId = Objects.requireNonNull(
                riskId,
                "Risk ID is required."
        );

        this.source = Objects.requireNonNull(
                source,
                "Recommendation source is required."
        );

        this.fallbackReason = Objects.requireNonNull(
                fallbackReason,
                "Recommendation fallback reason is required."
        );

        RiskRemediationRecommendationContent requiredContent =
                Objects.requireNonNull(
                        content,
                        "Recommendation content is required."
                );

        this.title = requiredContent.title();
        this.summary = requiredContent.summary();
        this.remediationSteps =
                requiredContent.remediationSteps();
        this.verificationSteps =
                requiredContent.verificationSteps();

        this.providerName = normalizeOptionalText(
                providerName,
                "Provider name",
                PROVIDER_NAME_MAX_LENGTH
        );

        this.modelName = normalizeOptionalText(
                modelName,
                "Model name",
                MODEL_NAME_MAX_LENGTH
        );

        this.promptVersion = normalizeRequiredText(
                promptVersion,
                "Prompt version",
                VERSION_MAX_LENGTH
        );

        this.fallbackRuleVersion = normalizeOptionalText(
                fallbackRuleVersion,
                "Fallback rule version",
                VERSION_MAX_LENGTH
        );

        this.contextFingerprint =
                validateContextFingerprint(
                        contextFingerprint
                );

        this.contextFindingCount =
                validateContextCount(
                        contextFindingCount,
                        "Context finding count"
                );

        this.contextEvidenceCount =
                validateContextCount(
                        contextEvidenceCount,
                        "Context evidence count"
                );

        this.generatedAt = Objects.requireNonNull(
                generatedAt,
                "Recommendation generation timestamp is required."
        );

        validateGenerationMetadata();

        this.validationStatus =
                RiskRemediationRecommendationValidationStatus.VALID;

        this.advisory = true;
    }

    public static RiskRemediationRecommendation aiGenerated(
            UUID monitoringRunId,
            UUID riskId,
            RiskRemediationRecommendationContent content,
            String providerName,
            String modelName,
            String promptVersion,
            String contextFingerprint,
            Integer contextFindingCount,
            Integer contextEvidenceCount,
            OffsetDateTime generatedAt
    ) {
        return new RiskRemediationRecommendation(
                monitoringRunId,
                riskId,
                RiskRemediationRecommendationSource.AI,
                RiskRemediationFallbackReason.NONE,
                content,
                providerName,
                modelName,
                promptVersion,
                null,
                contextFingerprint,
                contextFindingCount,
                contextEvidenceCount,
                generatedAt
        );
    }

    public static RiskRemediationRecommendation ruleBasedFallback(
            UUID monitoringRunId,
            UUID riskId,
            RiskRemediationRecommendationContent content,
            RiskRemediationFallbackReason fallbackReason,
            String attemptedProviderName,
            String attemptedModelName,
            String promptVersion,
            String fallbackRuleVersion,
            String contextFingerprint,
            Integer contextFindingCount,
            Integer contextEvidenceCount,
            OffsetDateTime generatedAt
    ) {
        return new RiskRemediationRecommendation(
                monitoringRunId,
                riskId,
                RiskRemediationRecommendationSource.RULE_BASED_FALLBACK,
                fallbackReason,
                content,
                attemptedProviderName,
                attemptedModelName,
                promptVersion,
                fallbackRuleVersion,
                contextFingerprint,
                contextFindingCount,
                contextEvidenceCount,
                generatedAt
        );
    }

    private void validateGenerationMetadata() {
        if (source == RiskRemediationRecommendationSource.AI) {
            if (fallbackReason
                    != RiskRemediationFallbackReason.NONE) {

                throw new IllegalArgumentException(
                        "AI recommendation must use fallback reason NONE."
                );
            }

            if (providerName == null) {
                throw new IllegalArgumentException(
                        "AI recommendation provider name is required."
                );
            }

            if (modelName == null) {
                throw new IllegalArgumentException(
                        "AI recommendation model name is required."
                );
            }

            if (fallbackRuleVersion != null) {
                throw new IllegalArgumentException(
                        "AI recommendation must not contain a fallback rule version."
                );
            }

            return;
        }

        if (source
                == RiskRemediationRecommendationSource.RULE_BASED_FALLBACK) {

            if (fallbackReason
                    == RiskRemediationFallbackReason.NONE) {

                throw new IllegalArgumentException(
                        "Rule-based fallback requires a fallback reason."
                );
            }

            if (fallbackRuleVersion == null) {
                throw new IllegalArgumentException(
                        "Rule-based fallback version is required."
                );
            }
        }
    }

    private String validateContextFingerprint(String value) {
        String normalizedValue =
                normalizeRequiredText(
                        value,
                        "Context fingerprint"
                );

        if (normalizedValue.length()
                != CONTEXT_FINGERPRINT_LENGTH) {

            throw new IllegalArgumentException(
                    "Context fingerprint must contain exactly "
                            + CONTEXT_FINGERPRINT_LENGTH
                            + " characters."
            );
        }

        return normalizedValue;
    }

    private Integer validateContextCount(
            Integer value,
            String fieldName
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        if (value < 0) {
            throw new IllegalArgumentException(
                    fieldName + " must not be negative."
            );
        }

        return value;
    }

    private String normalizeRequiredText(
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

    private String normalizeRequiredText(
            String value,
            String fieldName,
            int maximumLength
    ) {
        String normalizedValue =
                normalizeRequiredText(
                        value,
                        fieldName
                );

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

    private String normalizeOptionalText(
            String value,
            String fieldName,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            return null;
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
        if (validationStatus
                != RiskRemediationRecommendationValidationStatus.VALID) {

            throw new IllegalStateException(
                    "Only validated recommendations may be persisted."
            );
        }

        if (!advisory) {
            throw new IllegalStateException(
                    "Risk remediation recommendations must remain advisory."
            );
        }

        validateGenerationMetadata();

        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getMonitoringRunId() {
        return monitoringRunId;
    }

    public UUID getRiskId() {
        return riskId;
    }

    public RiskRemediationRecommendationSource getSource() {
        return source;
    }

    public RiskRemediationFallbackReason getFallbackReason() {
        return fallbackReason;
    }

    public RiskRemediationRecommendationValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getRemediationSteps() {
        return remediationSteps;
    }

    public String getVerificationSteps() {
        return verificationSteps;
    }

    public boolean isAdvisory() {
        return advisory;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String getFallbackRuleVersion() {
        return fallbackRuleVersion;
    }

    public String getContextFingerprint() {
        return contextFingerprint;
    }

    public Integer getContextFindingCount() {
        return contextFindingCount;
    }

    public Integer getContextEvidenceCount() {
        return contextEvidenceCount;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}