package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.risk.RiskSeverity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class RiskRemediationRuleBasedFallbackGenerator {

    private static final RiskRemediationFallbackRuleVersion
            CURRENT_VERSION =
            RiskRemediationFallbackRuleVersion.V1;

    public RiskRemediationRuleBasedFallbackResult generate(
            RiskRemediationRecommendationContext context
    ) {
        RiskRemediationRecommendationContext requiredContext =
                Objects.requireNonNull(
                        context,
                        "Risk remediation recommendation context is required."
                );

        RiskRemediationRecommendationContent content =
                new RiskRemediationRecommendationContent(
                        buildTitle(requiredContext),
                        buildSummary(requiredContext),
                        formatSteps(
                                buildRemediationSteps(
                                        requiredContext
                                )
                        ),
                        formatSteps(
                                buildVerificationSteps(
                                        requiredContext
                                )
                        )
                );

        return new RiskRemediationRuleBasedFallbackResult(
                CURRENT_VERSION.getVersion(),
                content
        );
    }

    public RiskRemediationFallbackRuleVersion
    getCurrentVersion() {
        return CURRENT_VERSION;
    }

    private String buildTitle(
            RiskRemediationRecommendationContext context
    ) {
        return "Review and remediate "
                + context.getRiskType();
    }

    private String buildSummary(
            RiskRemediationRecommendationContext context
    ) {
        return """
                This advisory recommendation is based only on the persisted %s risk, its %s severity classification, %d linked findings, and %d normalized evidence items. Confirm the affected control and root cause before applying a corrective change. The recommendation does not create a new risk or modify any risk, confidence, severity, or trust score.
                """.formatted(
                context.getRiskType(),
                context.getSeverity().name(),
                context.getFindingCount(),
                context.getEvidenceCount()
        ).strip();
    }

    private List<String> buildRemediationSteps(
            RiskRemediationRecommendationContext context
    ) {
        return List.of(
                buildTraceabilityReviewStep(context),

                "Reproduce the persisted condition in a controlled "
                        + "environment and confirm the affected page, "
                        + "endpoint, dependency, configuration, or "
                        + "security control before changing production.",

                buildSeverityActionStep(
                        context.getSeverity()
                ),

                "Apply the smallest corrective change that addresses "
                        + "the confirmed cause. Document the change, "
                        + "the responsible owner, and a rollback path.",

                "Re-run the same monitoring checks after the change "
                        + "and compare the result with the persisted "
                        + "monitoring baseline.",

                "Escalate the risk for specialist review when the "
                        + "condition remains unresolved, available "
                        + "evidence is insufficient, or remediation "
                        + "requires a broader architectural change."
        );
    }

    private List<String> buildVerificationSteps(
            RiskRemediationRecommendationContext context
    ) {
        return List.of(
                "Run a new monitoring cycle for the affected website "
                        + "and confirm that the original "
                        + context.getRiskType()
                        + " condition is no longer reproduced.",

                buildFindingVerificationStep(context),

                "Confirm that post-change normalized evidence supports "
                        + "the corrected state and remains traceable to "
                        + "the new monitoring run.",

                "Check that the corrective change did not introduce "
                        + "new functional, availability, security, "
                        + "content, or dependency regressions.",

                "Record the verification date, responsible reviewer, "
                        + "supporting evidence, and any accepted "
                        + "residual risk."
        );
    }

    private String buildTraceabilityReviewStep(
            RiskRemediationRecommendationContext context
    ) {
        if (context.getFindingCount() == 0) {
            return "Review the persisted risk rationale and obtain "
                    + "sufficient traceable findings and normalized "
                    + "evidence before applying a permanent change.";
        }

        if (context.getEvidenceCount() == 0) {
            return "Review the "
                    + context.getFindingCount()
                    + " linked findings and confirm them with "
                    + "traceable normalized evidence before applying "
                    + "a permanent change.";
        }

        return "Review the "
                + context.getFindingCount()
                + " linked findings and "
                + context.getEvidenceCount()
                + " normalized evidence items to identify the "
                + "confirmed scope and probable cause.";
    }

    private String buildSeverityActionStep(
            RiskSeverity severity
    ) {
        return switch (severity) {
            case LOW ->
                    "Schedule the confirmed correction through the "
                            + "normal maintenance process and ensure "
                            + "that the issue does not accumulate into "
                            + "a broader reliability or security risk.";

            case MEDIUM ->
                    "Prioritize the confirmed correction within the "
                            + "next controlled maintenance window and "
                            + "assign an accountable owner.";

            case HIGH ->
                    "Treat the confirmed condition as a high-priority "
                            + "remediation item, limit unnecessary "
                            + "exposure where practical, and complete "
                            + "the corrective change under controlled "
                            + "change management.";

            case CRITICAL ->
                    "Begin immediate controlled containment of the "
                            + "confirmed exposure, notify the responsible "
                            + "technical owner, and prioritize a tested "
                            + "corrective change with rollback readiness.";
        };
    }

    private String buildFindingVerificationStep(
            RiskRemediationRecommendationContext context
    ) {
        if (context.getFindingCount() == 0) {
            return "Confirm that the new monitoring run contains "
                    + "sufficient findings and evidence to support "
                    + "the remediation conclusion.";
        }

        return "Confirm that the "
                + context.getFindingCount()
                + " original linked findings are no longer reproduced, "
                + "have materially reduced impact, or have a documented "
                + "and approved residual-risk decision.";
    }

    private String formatSteps(
            List<String> steps
    ) {
        StringBuilder formattedSteps =
                new StringBuilder();

        for (int index = 0;
             index < steps.size();
             index++) {

            if (index > 0) {
                formattedSteps.append('\n');
            }

            formattedSteps
                    .append(index + 1)
                    .append(". ")
                    .append(steps.get(index));
        }

        return formattedSteps.toString();
    }
}