package com.cigabyte.sitesentinel.recommendation;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class RiskRemediationRecommendationGenerationService {

    private static final int PROVIDER_NAME_MAX_LENGTH = 80;
    private static final int MODEL_NAME_MAX_LENGTH = 120;

    private final List<RiskRemediationAiProvider> aiProviders;

    private final RiskRemediationRecommendationContextBuilder
            contextBuilder;

    private final RiskRemediationPromptFactory promptFactory;

    private final RiskRemediationRecommendationValidator
            recommendationValidator;

    private final RiskRemediationRuleBasedFallbackGenerator
            fallbackGenerator;

    private final RiskRemediationRecommendationService
            recommendationService;

    private final RiskRemediationRecommendationContextSanitizer
            textSafetyBoundary;

    public RiskRemediationRecommendationGenerationService(
            List<RiskRemediationAiProvider> aiProviders,
            RiskRemediationRecommendationContextBuilder
                    contextBuilder,
            RiskRemediationPromptFactory promptFactory,
            RiskRemediationRecommendationValidator
                    recommendationValidator,
            RiskRemediationRuleBasedFallbackGenerator
                    fallbackGenerator,
            RiskRemediationRecommendationService
                    recommendationService,
            RiskRemediationRecommendationContextSanitizer
                    textSafetyBoundary
    ) {
        this.aiProviders = aiProviders == null
                ? List.of()
                : List.copyOf(aiProviders);

        this.contextBuilder = contextBuilder;
        this.promptFactory = promptFactory;
        this.recommendationValidator =
                recommendationValidator;
        this.fallbackGenerator = fallbackGenerator;
        this.recommendationService =
                recommendationService;
        this.textSafetyBoundary = textSafetyBoundary;
    }

    public RiskRemediationRecommendation generateAndPersist(
            UUID monitoringRunId,
            UUID riskId
    ) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        UUID requiredRiskId =
                requireId(
                        riskId,
                        "Risk ID"
                );

        RiskRemediationRecommendationContext context =
                contextBuilder.build(
                        requiredMonitoringRunId,
                        requiredRiskId
                );

        RiskRemediationAiRequest request =
                promptFactory.create(context);

        ProviderSelection providerSelection =
                selectProvider();

        if (providerSelection.status()
                == ProviderSelectionStatus.UNAVAILABLE) {

            return generateAndPersistFallback(
                    context,
                    request,
                    RiskRemediationFallbackReason
                            .PROVIDER_UNAVAILABLE,
                    null
            );
        }

        if (providerSelection.status()
                == ProviderSelectionStatus.FAILURE) {

            return generateAndPersistFallback(
                    context,
                    request,
                    RiskRemediationFallbackReason
                            .PROVIDER_FAILURE,
                    null
            );
        }

        RiskRemediationAiProvider provider =
                providerSelection.provider();

        ProviderMetadata providerMetadata =
                readSafeProviderMetadata(provider);

        if (providerMetadata == null) {
            return generateAndPersistFallback(
                    context,
                    request,
                    RiskRemediationFallbackReason
                            .PROVIDER_FAILURE,
                    null
            );
        }

        RiskRemediationAiProviderResult providerResult =
                callProviderSafely(
                        provider,
                        request
                );

        if (providerResult == null) {
            return generateAndPersistFallback(
                    context,
                    request,
                    RiskRemediationFallbackReason
                            .PROVIDER_FAILURE,
                    providerMetadata
            );
        }

        return processProviderResult(
                context,
                request,
                providerMetadata,
                providerResult
        );
    }

    private RiskRemediationRecommendation processProviderResult(
            RiskRemediationRecommendationContext context,
            RiskRemediationAiRequest request,
            ProviderMetadata providerMetadata,
            RiskRemediationAiProviderResult providerResult
    ) {
        return switch (providerResult.getStatus()) {
            case UNAVAILABLE ->
                    generateAndPersistFallback(
                            context,
                            request,
                            RiskRemediationFallbackReason
                                    .PROVIDER_UNAVAILABLE,
                            providerMetadata
                    );

            case FAILURE ->
                    generateAndPersistFallback(
                            context,
                            request,
                            RiskRemediationFallbackReason
                                    .PROVIDER_FAILURE,
                            providerMetadata
                    );

            case SUCCESS ->
                    processSuccessfulProviderOutput(
                            context,
                            request,
                            providerMetadata,
                            providerResult
                    );
        };
    }

    private RiskRemediationRecommendation
    processSuccessfulProviderOutput(
            RiskRemediationRecommendationContext context,
            RiskRemediationAiRequest request,
            ProviderMetadata providerMetadata,
            RiskRemediationAiProviderResult providerResult
    ) {
        RiskRemediationAiOutput output =
                providerResult.getOutput()
                        .orElse(null);

        RiskRemediationRecommendationValidationResult
                validationResult =
                recommendationValidator.validate(
                        output,
                        promptFactory.getCurrentVersion()
                );

        if (!validationResult.isValid()) {
            return generateAndPersistFallback(
                    context,
                    request,
                    RiskRemediationFallbackReason
                            .VALIDATION_FAILURE,
                    providerMetadata
            );
        }

        RiskRemediationRecommendationContent content =
                validationResult.getContent()
                        .orElse(null);

        if (content == null) {
            return generateAndPersistFallback(
                    context,
                    request,
                    RiskRemediationFallbackReason
                            .VALIDATION_FAILURE,
                    providerMetadata
            );
        }

        RiskRemediationRecommendation recommendation =
                RiskRemediationRecommendation.aiGenerated(
                        context.getMonitoringRunId(),
                        context.getRiskId(),
                        content,
                        providerMetadata.providerName(),
                        providerMetadata.modelName(),
                        request.promptVersion(),
                        context.getFingerprint(),
                        context.getFindingCount(),
                        context.getEvidenceCount(),
                        OffsetDateTime.now()
                );

        return recommendationService.saveValidated(
                recommendation
        );
    }

    private RiskRemediationRecommendation
    generateAndPersistFallback(
            RiskRemediationRecommendationContext context,
            RiskRemediationAiRequest request,
            RiskRemediationFallbackReason fallbackReason,
            ProviderMetadata attemptedProviderMetadata
    ) {
        RiskRemediationRuleBasedFallbackResult
                fallbackResult =
                fallbackGenerator.generate(context);

        String attemptedProviderName =
                attemptedProviderMetadata == null
                        ? null
                        : attemptedProviderMetadata.providerName();

        String attemptedModelName =
                attemptedProviderMetadata == null
                        ? null
                        : attemptedProviderMetadata.modelName();

        RiskRemediationRecommendation recommendation =
                RiskRemediationRecommendation
                        .ruleBasedFallback(
                                context.getMonitoringRunId(),
                                context.getRiskId(),
                                fallbackResult.content(),
                                fallbackReason,
                                attemptedProviderName,
                                attemptedModelName,
                                request.promptVersion(),
                                fallbackResult.ruleVersion(),
                                context.getFingerprint(),
                                context.getFindingCount(),
                                context.getEvidenceCount(),
                                OffsetDateTime.now()
                        );

        return recommendationService.saveValidated(
                recommendation
        );
    }

    private ProviderSelection selectProvider() {
        boolean providerSelectionFailureDetected =
                false;

        for (RiskRemediationAiProvider provider :
                aiProviders) {

            if (provider == null) {
                providerSelectionFailureDetected =
                        true;

                continue;
            }

            try {
                if (provider.isAvailable()) {
                    return ProviderSelection.available(
                            provider
                    );
                }
            } catch (RuntimeException exception) {
                providerSelectionFailureDetected =
                        true;
            }
        }

        if (providerSelectionFailureDetected) {
            return ProviderSelection.failure();
        }

        return ProviderSelection.unavailable();
    }

    private RiskRemediationAiProviderResult callProviderSafely(
            RiskRemediationAiProvider provider,
            RiskRemediationAiRequest request
    ) {
        try {
            return provider.generate(request);
        } catch (RuntimeException exception) {
            return RiskRemediationAiProviderResult.failure();
        }
    }

    private ProviderMetadata readSafeProviderMetadata(
            RiskRemediationAiProvider provider
    ) {
        try {
            String providerName =
                    normalizeProviderMetadata(
                            provider.getProviderName(),
                            "Provider name",
                            PROVIDER_NAME_MAX_LENGTH
                    );

            String modelName =
                    normalizeProviderMetadata(
                            provider.getModelName(),
                            "Model name",
                            MODEL_NAME_MAX_LENGTH
                    );

            if (textSafetyBoundary
                    .containsSensitiveMaterial(
                            providerName
                    )
                    || textSafetyBoundary
                    .containsSensitiveMaterial(
                            modelName
                    )) {

                return null;
            }

            return new ProviderMetadata(
                    providerName,
                    modelName
            );
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String normalizeProviderMetadata(
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

        if (normalizedValue.length()
                > maximumLength) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }

        return normalizedValue;
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

    private enum ProviderSelectionStatus {

        AVAILABLE,

        UNAVAILABLE,

        FAILURE
    }

    private record ProviderSelection(
            ProviderSelectionStatus status,
            RiskRemediationAiProvider provider
    ) {

        private ProviderSelection {
            status = Objects.requireNonNull(
                    status,
                    "Provider selection status is required."
            );

            if (status
                    == ProviderSelectionStatus.AVAILABLE
                    && provider == null) {

                throw new IllegalArgumentException(
                        "Available provider selection requires a provider."
                );
            }

            if (status
                    != ProviderSelectionStatus.AVAILABLE
                    && provider != null) {

                throw new IllegalArgumentException(
                        "Unavailable provider selection must not "
                                + "contain a provider."
                );
            }
        }

        static ProviderSelection available(
                RiskRemediationAiProvider provider
        ) {
            return new ProviderSelection(
                    ProviderSelectionStatus.AVAILABLE,
                    Objects.requireNonNull(
                            provider,
                            "AI provider is required."
                    )
            );
        }

        static ProviderSelection unavailable() {
            return new ProviderSelection(
                    ProviderSelectionStatus.UNAVAILABLE,
                    null
            );
        }

        static ProviderSelection failure() {
            return new ProviderSelection(
                    ProviderSelectionStatus.FAILURE,
                    null
            );
        }
    }

    private record ProviderMetadata(
            String providerName,
            String modelName
    ) {

        private ProviderMetadata {
            providerName = Objects.requireNonNull(
                    providerName,
                    "Provider name is required."
            );

            modelName = Objects.requireNonNull(
                    modelName,
                    "Model name is required."
            );
        }
    }
}