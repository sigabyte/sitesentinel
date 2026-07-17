package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.risk.RiskSeverity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RiskRemediationRecommendationGenerationServiceTests {

    private final RiskRemediationRecommendationContextBuilder
            contextBuilder =
            mock(
                    RiskRemediationRecommendationContextBuilder.class
            );

    private final RiskRemediationPromptFactory promptFactory =
            mock(RiskRemediationPromptFactory.class);

    private final RiskRemediationRecommendationValidator
            recommendationValidator =
            mock(
                    RiskRemediationRecommendationValidator.class
            );

    private final RiskRemediationRuleBasedFallbackGenerator
            fallbackGenerator =
            mock(
                    RiskRemediationRuleBasedFallbackGenerator.class
            );

    private final RiskRemediationRecommendationService
            recommendationService =
            mock(
                    RiskRemediationRecommendationService.class
            );

    private final RiskRemediationRecommendationContextSanitizer
            textSafetyBoundary =
            new RiskRemediationRecommendationContextSanitizer();

    @Test
    void noProviderPersistsProviderUnavailableFallback() {
        GenerationFixture fixture =
                prepareFixture();

        RiskRemediationRecommendationGenerationService service =
                createService(List.of());

        RiskRemediationRecommendation result =
                service.generateAndPersist(
                        fixture.monitoringRunId(),
                        fixture.riskId()
                );

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                result.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .PROVIDER_UNAVAILABLE,
                result.getFallbackReason()
        );

        assertNull(result.getProviderName());
        assertNull(result.getModelName());

        assertEquals(
                "risk-remediation-v1",
                result.getPromptVersion()
        );

        assertEquals(
                "risk-remediation-fallback-v1",
                result.getFallbackRuleVersion()
        );

        assertEquals(
                fixture.context().getFingerprint(),
                result.getContextFingerprint()
        );

        assertEquals(
                fixture.context().getFindingCount(),
                result.getContextFindingCount()
        );

        assertEquals(
                fixture.context().getEvidenceCount(),
                result.getContextEvidenceCount()
        );

        assertNotNull(result.getGeneratedAt());

        verify(
                recommendationService
        ).saveValidated(result);

        verify(
                recommendationValidator,
                never()
        ).validate(any(), any());
    }

    @Test
    void providerUnavailableResultPersistsAttemptedMetadata() {
        GenerationFixture fixture =
                prepareFixture();

        RiskRemediationAiProvider provider =
                availableProvider(
                        fixture.request(),
                        RiskRemediationAiProviderResult
                                .unavailable()
                );

        RiskRemediationRecommendationGenerationService service =
                createService(
                        List.of(provider)
                );

        RiskRemediationRecommendation result =
                service.generateAndPersist(
                        fixture.monitoringRunId(),
                        fixture.riskId()
                );

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                result.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .PROVIDER_UNAVAILABLE,
                result.getFallbackReason()
        );

        assertEquals(
                "test-provider",
                result.getProviderName()
        );

        assertEquals(
                "test-model",
                result.getModelName()
        );
    }

    @Test
    void providerFailureResultPersistsProviderFailureFallback() {
        GenerationFixture fixture =
                prepareFixture();

        RiskRemediationAiProvider provider =
                availableProvider(
                        fixture.request(),
                        RiskRemediationAiProviderResult
                                .failure()
                );

        RiskRemediationRecommendationGenerationService service =
                createService(
                        List.of(provider)
                );

        RiskRemediationRecommendation result =
                service.generateAndPersist(
                        fixture.monitoringRunId(),
                        fixture.riskId()
                );

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                result.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .PROVIDER_FAILURE,
                result.getFallbackReason()
        );

        assertEquals(
                "test-provider",
                result.getProviderName()
        );

        assertEquals(
                "test-model",
                result.getModelName()
        );
    }

    @Test
    void providerExceptionIsConvertedToSafeFallback() {
        GenerationFixture fixture =
                prepareFixture();

        RiskRemediationAiProvider provider =
                mock(RiskRemediationAiProvider.class);

        when(provider.isAvailable())
                .thenReturn(true);

        when(provider.getProviderName())
                .thenReturn("test-provider");

        when(provider.getModelName())
                .thenReturn("test-model");

        when(provider.generate(fixture.request()))
                .thenThrow(
                        new IllegalStateException(
                                "Bearer provider-secret-must-not-persist"
                        )
                );

        RiskRemediationRecommendationGenerationService service =
                createService(
                        List.of(provider)
                );

        RiskRemediationRecommendation result =
                service.generateAndPersist(
                        fixture.monitoringRunId(),
                        fixture.riskId()
                );

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                result.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .PROVIDER_FAILURE,
                result.getFallbackReason()
        );

        assertEquals(
                "test-provider",
                result.getProviderName()
        );

        assertEquals(
                "test-model",
                result.getModelName()
        );

        String persistedContent =
                result.getTitle()
                        + result.getSummary()
                        + result.getRemediationSteps()
                        + result.getVerificationSteps();

        org.junit.jupiter.api.Assertions.assertFalse(
                persistedContent.contains(
                        "provider-secret-must-not-persist"
                )
        );
    }

    @Test
    void validAiOutputPersistsAiRecommendation() {
        GenerationFixture fixture =
                prepareFixture();

        RiskRemediationAiOutput output =
                validAiOutput();

        RiskRemediationRecommendationContent content =
                validatedAiContent();

        when(
                recommendationValidator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                )
        ).thenReturn(
                RiskRemediationRecommendationValidationResult
                        .valid(content)
        );

        RiskRemediationAiProvider provider =
                availableProvider(
                        fixture.request(),
                        RiskRemediationAiProviderResult
                                .success(output)
                );

        RiskRemediationRecommendationGenerationService service =
                createService(
                        List.of(provider)
                );

        RiskRemediationRecommendation result =
                service.generateAndPersist(
                        fixture.monitoringRunId(),
                        fixture.riskId()
                );

        assertEquals(
                RiskRemediationRecommendationSource.AI,
                result.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason.NONE,
                result.getFallbackReason()
        );

        assertEquals(
                RiskRemediationRecommendationValidationStatus.VALID,
                result.getValidationStatus()
        );

        assertEquals(
                "test-provider",
                result.getProviderName()
        );

        assertEquals(
                "test-model",
                result.getModelName()
        );

        assertEquals(
                "risk-remediation-v1",
                result.getPromptVersion()
        );

        assertNull(
                result.getFallbackRuleVersion()
        );

        assertEquals(
                content.title(),
                result.getTitle()
        );

        assertEquals(
                content.summary(),
                result.getSummary()
        );

        assertEquals(
                content.remediationSteps(),
                result.getRemediationSteps()
        );

        assertEquals(
                content.verificationSteps(),
                result.getVerificationSteps()
        );

        verify(
                fallbackGenerator,
                never()
        ).generate(any());
    }

    @Test
    void invalidAiOutputPersistsValidationFailureFallback() {
        GenerationFixture fixture =
                prepareFixture();

        RiskRemediationAiOutput output =
                validAiOutput();

        when(
                recommendationValidator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                )
        ).thenReturn(
                RiskRemediationRecommendationValidationResult
                        .invalid(
                                List.of(
                                        RiskRemediationRecommendationValidationIssueCode
                                                .SCHEMA_VERSION_MISMATCH
                                )
                        )
        );

        RiskRemediationAiProvider provider =
                availableProvider(
                        fixture.request(),
                        RiskRemediationAiProviderResult
                                .success(output)
                );

        RiskRemediationRecommendationGenerationService service =
                createService(
                        List.of(provider)
                );

        RiskRemediationRecommendation result =
                service.generateAndPersist(
                        fixture.monitoringRunId(),
                        fixture.riskId()
                );

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                result.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .VALIDATION_FAILURE,
                result.getFallbackReason()
        );

        assertEquals(
                "test-provider",
                result.getProviderName()
        );

        assertEquals(
                "test-model",
                result.getModelName()
        );

        assertEquals(
                "risk-remediation-fallback-v1",
                result.getFallbackRuleVersion()
        );
    }

    @Test
    void sensitiveProviderMetadataIsRejectedBeforeProviderCall() {
        GenerationFixture fixture =
                prepareFixture();

        RiskRemediationAiProvider provider =
                mock(RiskRemediationAiProvider.class);

        when(provider.isAvailable())
                .thenReturn(true);

        when(provider.getProviderName())
                .thenReturn(
                        "BOT_TOKEN=provider-secret"
                );

        when(provider.getModelName())
                .thenReturn("test-model");

        RiskRemediationRecommendationGenerationService service =
                createService(
                        List.of(provider)
                );

        RiskRemediationRecommendation result =
                service.generateAndPersist(
                        fixture.monitoringRunId(),
                        fixture.riskId()
                );

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                result.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .PROVIDER_FAILURE,
                result.getFallbackReason()
        );

        assertNull(result.getProviderName());
        assertNull(result.getModelName());

        verify(
                provider,
                never()
        ).generate(any());
    }

    private RiskRemediationRecommendationGenerationService
    createService(
            List<RiskRemediationAiProvider> providers
    ) {
        return new RiskRemediationRecommendationGenerationService(
                providers,
                contextBuilder,
                promptFactory,
                recommendationValidator,
                fallbackGenerator,
                recommendationService,
                textSafetyBoundary
        );
    }

    private GenerationFixture prepareFixture() {
        UUID monitoringRunId = UUID.randomUUID();
        UUID riskId = UUID.randomUUID();

        RiskRemediationRecommendationContext context =
                recommendationContext(
                        monitoringRunId,
                        riskId
                );

        RiskRemediationAiRequest request =
                new RiskRemediationAiRequest(
                        "risk-remediation-v1",
                        "risk-remediation-output-v1",
                        "System instruction.",
                        "User instruction.",
                        context.getFingerprint()
                );

        RiskRemediationRuleBasedFallbackResult fallbackResult =
                new RiskRemediationRuleBasedFallbackResult(
                        "risk-remediation-fallback-v1",
                        fallbackContent()
                );

        when(
                contextBuilder.build(
                        monitoringRunId,
                        riskId
                )
        ).thenReturn(context);

        when(
                promptFactory.create(context)
        ).thenReturn(request);

        when(
                promptFactory.getCurrentVersion()
        ).thenReturn(
                RiskRemediationPromptVersion.V1
        );

        when(
                fallbackGenerator.generate(context)
        ).thenReturn(fallbackResult);

        when(
                recommendationService.saveValidated(
                        any(
                                RiskRemediationRecommendation.class
                        )
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        return new GenerationFixture(
                monitoringRunId,
                riskId,
                context,
                request
        );
    }

    private RiskRemediationAiProvider availableProvider(
            RiskRemediationAiRequest request,
            RiskRemediationAiProviderResult result
    ) {
        RiskRemediationAiProvider provider =
                mock(RiskRemediationAiProvider.class);

        when(provider.isAvailable())
                .thenReturn(true);

        when(provider.getProviderName())
                .thenReturn("test-provider");

        when(provider.getModelName())
                .thenReturn("test-model");

        when(provider.generate(request))
                .thenReturn(result);

        return provider;
    }

    private RiskRemediationRecommendationContext
    recommendationContext(
            UUID monitoringRunId,
            UUID riskId
    ) {
        RiskRemediationRecommendationEvidenceContext evidence =
                new RiskRemediationRecommendationEvidenceContext(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "SECURITY_HEADER",
                        "strict-transport-security-missing"
                );

        RiskRemediationRecommendationFindingContext finding =
                new RiskRemediationRecommendationFindingContext(
                        UUID.randomUUID(),
                        "SECURITY_HEADER",
                        "Security header finding",
                        "Persisted normalized finding description.",
                        92,
                        List.of(evidence)
                );

        return RiskRemediationRecommendationContext.create(
                monitoringRunId,
                riskId,
                "TLS_CONFIGURATION",
                RiskSeverity.HIGH,
                86,
                92,
                "Persisted risk rationale.",
                List.of(finding)
        );
    }

    private RiskRemediationRecommendationContent
    fallbackContent() {
        return new RiskRemediationRecommendationContent(
                "Review and remediate TLS_CONFIGURATION",
                "Rule-based advisory summary.",
                "1. Review the persisted condition.",
                "1. Re-run the monitoring checks."
        );
    }

    private RiskRemediationAiOutput validAiOutput() {
        return new RiskRemediationAiOutput(
                "risk-remediation-output-v1",
                "Correct the TLS configuration",
                "Apply a controlled correction.",
                List.of(
                        "Review the affected configuration.",
                        "Apply the corrective change."
                ),
                List.of(
                        "Re-run the monitoring checks."
                ),
                true
        );
    }

    private RiskRemediationRecommendationContent
    validatedAiContent() {
        return new RiskRemediationRecommendationContent(
                "Correct the TLS configuration",
                "Apply a controlled correction.",
                """
                1. Review the affected configuration.
                2. Apply the corrective change.
                """.strip(),
                "1. Re-run the monitoring checks."
        );
    }

    private record GenerationFixture(
            UUID monitoringRunId,
            UUID riskId,
            RiskRemediationRecommendationContext context,
            RiskRemediationAiRequest request
    ) {
    }
}