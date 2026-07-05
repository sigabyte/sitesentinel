package com.cigabyte.sitesentinel.engine.trust;

import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.risk.RiskSeverity;
import com.cigabyte.sitesentinel.trust.TrustAssessmentService;
import com.cigabyte.sitesentinel.trust.TrustStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleBasedTrustEvaluationEngine implements TrustEvaluationEngine {

    private final RiskService riskService;
    private final TrustAssessmentService trustAssessmentService;

    public RuleBasedTrustEvaluationEngine(
            RiskService riskService,
            TrustAssessmentService trustAssessmentService
    ) {
        this.riskService = riskService;
        this.trustAssessmentService = trustAssessmentService;
    }

    @Override
    public void assess(UUID monitoringRunId) {
        List<Risk> risks = riskService.findByMonitoringRunId(monitoringRunId);

        if (risks.isEmpty()) {
            return;
        }

        Risk firstRisk = risks.get(0);

        TrustAssessmentResult result = evaluateRisks(risks);

        List<UUID> riskIds = risks.stream()
                .map(Risk::getId)
                .toList();

        trustAssessmentService.recordTrustAssessment(
                firstRisk.getWebsiteId(),
                monitoringRunId,
                result.trustStatus(),
                result.trustScore(),
                result.confidenceScore(),
                result.summary(),
                riskIds
        );
    }

    private TrustAssessmentResult evaluateRisks(List<Risk> risks) {
        int totalPenalty = 0;
        int totalConfidence = 0;

        int lowCount = 0;
        int mediumCount = 0;
        int highCount = 0;
        int criticalCount = 0;

        for (Risk risk : risks) {
            totalPenalty += penaltyFor(risk.getSeverity());
            totalConfidence += safeScore(risk.getConfidenceScore());

            if (risk.getSeverity() == RiskSeverity.LOW) {
                lowCount++;
            } else if (risk.getSeverity() == RiskSeverity.MEDIUM) {
                mediumCount++;
            } else if (risk.getSeverity() == RiskSeverity.HIGH) {
                highCount++;
            } else if (risk.getSeverity() == RiskSeverity.CRITICAL) {
                criticalCount++;
            }
        }

        int trustScore = Math.max(0, 100 - totalPenalty);
        int confidenceScore = totalConfidence / risks.size();
        TrustStatus trustStatus = resolveTrustStatus(
                trustScore,
                lowCount,
                mediumCount,
                highCount,
                criticalCount
        );

        String summary = buildSummary(
                risks.size(),
                lowCount,
                mediumCount,
                highCount,
                criticalCount,
                trustScore,
                trustStatus
        );

        return new TrustAssessmentResult(
                trustStatus,
                trustScore,
                confidenceScore,
                summary
        );
    }

    private TrustStatus resolveTrustStatus(
            int trustScore,
            int lowCount,
            int mediumCount,
            int highCount,
            int criticalCount
    ) {
        if (criticalCount > 0 || highCount > 0) {
            return TrustStatus.HIGH_RISK;
        }

        if (mediumCount > 0) {
            return TrustStatus.NEEDS_ATTENTION;
        }

        if (lowCount > 0 && trustScore < 90) {
            return TrustStatus.NEEDS_ATTENTION;
        }

        return TrustStatus.TRUSTED;
    }

    private int penaltyFor(RiskSeverity severity) {
        if (severity == RiskSeverity.CRITICAL) {
            return 45;
        }

        if (severity == RiskSeverity.HIGH) {
            return 30;
        }

        if (severity == RiskSeverity.MEDIUM) {
            return 15;
        }

        if (severity == RiskSeverity.LOW) {
            return 5;
        }

        return 0;
    }

    private int safeScore(Integer value) {
        if (value == null) {
            return 0;
        }

        if (value < 0) {
            return 0;
        }

        if (value > 100) {
            return 100;
        }

        return value;
    }

    private String buildSummary(
            int totalRisks,
            int lowCount,
            int mediumCount,
            int highCount,
            int criticalCount,
            int trustScore,
            TrustStatus trustStatus
    ) {
        return "Trust assessment completed from "
                + totalRisks
                + " risk record(s). Severity distribution: "
                + "LOW="
                + lowCount
                + ", MEDIUM="
                + mediumCount
                + ", HIGH="
                + highCount
                + ", CRITICAL="
                + criticalCount
                + ". Final trust status="
                + trustStatus
                + ", trust score="
                + trustScore
                + ".";
    }

    private record TrustAssessmentResult(
            TrustStatus trustStatus,
            Integer trustScore,
            Integer confidenceScore,
            String summary
    ) {
    }
}