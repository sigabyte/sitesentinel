package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import com.cigabyte.sitesentinel.risk.RiskSeverity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class RiskRemediationRuleBasedFallbackGenerator {

    private static final RiskRemediationFallbackRuleVersion
            CURRENT_VERSION =
            RiskRemediationFallbackRuleVersion.V3;

    public RiskRemediationRuleBasedFallbackResult generate(
            RiskRemediationRecommendationContext context
    ) {
        return generate(
                context,
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    public RiskRemediationRuleBasedFallbackResult generate(
            RiskRemediationRecommendationContext context,
            SiteSentinelReportLanguage reportLanguage
    ) {
        RiskRemediationRecommendationContext requiredContext =
                Objects.requireNonNull(
                        context,
                        "Risk remediation recommendation context is required."
                );

        SiteSentinelReportLanguage requiredReportLanguage =
                Objects.requireNonNull(
                        reportLanguage,
                        "Recommendation report language is required."
                );

        RiskRemediationRecommendationContent content =
                switch (requiredReportLanguage) {
                    case ENGLISH ->
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

                    case TURKISH ->
                            buildTurkishContent(
                                    requiredContext
                            );
                };

        return new RiskRemediationRuleBasedFallbackResult(
                CURRENT_VERSION.getVersion(),
                content
        );
    }

    private RiskRemediationRecommendationContent
    buildTurkishContent(
            RiskRemediationRecommendationContext context
    ) {
        return new RiskRemediationRecommendationContent(
                context.getRiskType()
                        + " riskini inceleyin ve giderin",
                buildTurkishSummary(
                        context
                ),
                formatSteps(
                        buildTurkishRemediationSteps(
                                context
                        )
                ),
                formatSteps(
                        buildTurkishVerificationSteps(
                                context
                        )
                )
        );
    }

    private String buildTurkishSummary(
            RiskRemediationRecommendationContext context
    ) {
        return """
            Kalıcı kanıtlar, %s türünde %s önem seviyesine sahip bir riski doğrular. Bu değerlendirme %d bağlantılı bulgu ve %d normalize edilmiş kanıt öğesiyle desteklenmektedir. Koşul giderilmezse web sitesinin güvenlik, erişilebilirlik, içerik kalitesi veya güvenilirlik kontrollerini olumsuz etkileyebilir. Mevcut kanıtlar bir saldırının, sistem ele geçirilmesinin, veri ihlalinin, güvenlik olayının veya istismarın gerçekleştiğini doğrulamaz. Düzeltici değişiklik uygulanmadan önce etkilenen kontrolü ve temel nedeni doğrulayın. Bu tavsiye niteliğindeki açıklama yeni bir risk oluşturmaz ve mevcut risk, güven, önem seviyesi veya trust score değerlerini değiştirmez.
            """.formatted(
                context.getRiskType(),
                context.getSeverity().name(),
                context.getFindingCount(),
                context.getEvidenceCount()
        ).strip();
    }

    private List<String> buildTurkishRemediationSteps(
            RiskRemediationRecommendationContext context
    ) {
        return List.of(
                context.getRiskType()
                        + " ile ilişkili kalıcı bulguları "
                        + "ve kanıt bağlantılarını inceleyin.",
                "Etkilenen yapılandırma veya içerik kontrolünün "
                        + "temel nedenini doğrulayın.",
                "Geri alma hazırlığını koruyarak kontrollü bir "
                        + "değişiklik uygulayın.",
                "Değişikliğin kapsamını ve uygulama sonucunu "
                        + "operasyonel kayıtlarla belgeleyin."
        );
    }

    private List<String> buildTurkishVerificationSteps(
            RiskRemediationRecommendationContext context
    ) {
        return List.of(
                "SiteSentinel monitoring kontrolünü yeniden çalıştırın.",
                context.getRiskType()
                        + " koşulunun artık üretilmediğini doğrulayın.",
                "İlgili bulgu ve normalize edilmiş kanıt sonuçlarını "
                        + "yeniden inceleyin.",
                "Değişikliğin ilgisiz web sitesi davranışlarında "
                        + "regresyon oluşturmadığını doğrulayın."
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
        RiskExplanation explanation =
                resolveRiskExplanation(
                        context.getRiskType()
                );

        return """
            The persisted evidence confirms a %s risk with %s severity, supported by %d linked findings and %d normalized evidence items. %s If unresolved, %s The evidence does not confirm that an attack, compromise, data breach, incident, or exploitation occurred. Confirm the affected control and root cause before applying a corrective change. This advisory explanation does not create a new risk or modify any risk, confidence, severity, or trust score.
            """.formatted(
                context.getRiskType(),
                context.getSeverity().name(),
                context.getFindingCount(),
                context.getEvidenceCount(),
                explanation.meaning(),
                explanation.potentialImpact()
        ).strip();
    }

    private RiskExplanation resolveRiskExplanation(
            String riskType
    ) {
        return switch (riskType) {
            case "WEBSITE_REACHABILITY_RISK" ->
                    new RiskExplanation(
                            "The homepage could not be fetched "
                                    + "successfully, meaning the monitoring "
                                    + "run could not establish normal "
                                    + "website reachability.",
                            "the condition may prevent users and monitoring "
                                    + "systems from reaching the website or "
                                    + "may conceal the current state of its "
                                    + "public content and controls."
                    );

            case "WEBSITE_AVAILABILITY_RISK" ->
                    new RiskExplanation(
                            "The homepage returned an HTTP error status, "
                                    + "meaning the requested public resource "
                                    + "was not delivered successfully.",
                            "the condition may interrupt user access, reduce "
                                    + "service availability, and prevent "
                                    + "dependent monitoring or business "
                                    + "processes from completing normally."
                    );

            case "TRANSPORT_SECURITY_RISK" ->
                    new RiskExplanation(
                            "The homepage final URL is not served over HTTPS, "
                                    + "meaning transport protection was not "
                                    + "confirmed for the final destination.",
                            "traffic may be exposed to interception or "
                                    + "modification risks while travelling "
                                    + "between a user and the website."
                    );

            case "BROWSER_SECURITY_POLICY_RISK" ->
                    new RiskExplanation(
                            "A Content Security Policy was not observed, "
                                    + "meaning the browser was not given an "
                                    + "explicit policy restricting approved "
                                    + "content sources.",
                            "the absence of this defense-in-depth control may "
                                    + "increase exposure to browser-based "
                                    + "content injection and script execution "
                                    + "risks if another weakness is present."
                    );

            case "TRANSPORT_SECURITY_POLICY_RISK" ->
                    new RiskExplanation(
                            "A Strict Transport Security policy was not "
                                    + "observed, meaning compatible browsers "
                                    + "were not instructed to enforce future "
                                    + "HTTPS-only access.",
                            "users may remain more exposed to protocol "
                                    + "downgrade or insecure first-connection "
                                    + "risks."
                    );

            case "CLICKJACKING_PROTECTION_RISK" ->
                    new RiskExplanation(
                            "Explicit framing protection was not observed, "
                                    + "meaning the page may be permitted to "
                                    + "load inside another site's frame.",
                            "the condition may increase exposure to deceptive "
                                    + "interface overlays or clickjacking if "
                                    + "the page contains actionable controls."
                    );

            case "CONTENT_SNIFFING_PROTECTION_RISK" ->
                    new RiskExplanation(
                            "Protection against MIME type sniffing was not "
                                    + "observed, meaning browsers may attempt "
                                    + "to infer a resource type instead of "
                                    + "strictly following its declared type.",
                            "misclassified content may be interpreted in an "
                                    + "unsafe way under certain browser and "
                                    + "content conditions."
                    );

            case "REFERRER_PRIVACY_POLICY_RISK" ->
                    new RiskExplanation(
                            "A policy controlling outbound referrer "
                                    + "information was not observed.",
                            "navigation requests may disclose more source URL "
                                    + "or path information than intended to "
                                    + "external destinations."
                    );

            case "CONTENT_QUALITY_RISK" ->
                    new RiskExplanation(
                            "A usable page title was not observed, meaning "
                                    + "the homepage lacks a basic descriptive "
                                    + "signal used by users, browsers, and "
                                    + "assistive technologies.",
                            "the page may be harder to identify in browser "
                                    + "tabs, bookmarks, search results, and "
                                    + "accessibility workflows."
                    );

            case "SEARCH_PRESENTATION_RISK" ->
                    new RiskExplanation(
                            "A usable meta description was not observed, "
                                    + "meaning the site does not provide a "
                                    + "preferred descriptive summary for "
                                    + "search presentation.",
                            "search platforms may generate less relevant or "
                                    + "less consistent result descriptions, "
                                    + "which may reduce clarity and "
                                    + "engagement."
                    );

            case "CANONICALIZATION_RISK" ->
                    new RiskExplanation(
                            "A canonical URL was not observed, meaning the "
                                    + "homepage does not explicitly identify "
                                    + "its preferred URL representation.",
                            "duplicate or alternate URLs may be indexed or "
                                    + "evaluated inconsistently, potentially "
                                    + "fragmenting search signals."
                    );

            case "ASSESSMENT_DATA_QUALITY_RISK" ->
                    new RiskExplanation(
                            "The HTTP status evidence could not be interpreted "
                                    + "reliably, meaning the assessment could "
                                    + "not establish a dependable response "
                                    + "status conclusion.",
                            "the potential impact is reduced assessment "
                                    + "confidence, and availability or response "
                                    + "conditions may remain unresolved until "
                                    + "valid evidence is collected."
                    );

            default ->
                    new RiskExplanation(
                            "The persisted findings produced the "
                                    + riskType
                                    + " classification, but no dedicated "
                                    + "risk-specific explanation is available "
                                    + "for this future or unknown risk type.",
                            "the potential impact cannot be stated more "
                                    + "specifically without adding unsupported "
                                    + "facts; the linked findings and normalized "
                                    + "evidence should be reviewed before action."
                    );
        };
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

    private record RiskExplanation(
            String meaning,
            String potentialImpact
    ) {
    }
}