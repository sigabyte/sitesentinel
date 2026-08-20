package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonService;
import com.cigabyte.sitesentinel.comparison.AssessmentComparisonSummary;
import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendation;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendationService;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.trust.TrustAssessmentService;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class MonitoringRunReportService {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final EvidenceService evidenceService;
    private final FindingService findingService;
    private final RiskService riskService;

    private final RiskRemediationRecommendationService
            recommendationService;

    private final TrustAssessmentService
            trustAssessmentService;

    private final AssessmentComparisonService
            assessmentComparisonService;

    public MonitoringRunReportService(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            EvidenceService evidenceService,
            FindingService findingService,
            RiskService riskService,
            RiskRemediationRecommendationService
                    recommendationService,
            TrustAssessmentService trustAssessmentService,
            AssessmentComparisonService
                    assessmentComparisonService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.evidenceService = evidenceService;
        this.findingService = findingService;
        this.riskService = riskService;
        this.recommendationService =
                recommendationService;
        this.trustAssessmentService =
                trustAssessmentService;
        this.assessmentComparisonService =
                assessmentComparisonService;
    }

    @Transactional(readOnly = true)
    public MonitoringRunReportView buildReport(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        return buildReport(
                websiteId,
                monitoringRunId,
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    @Transactional(readOnly = true)
    public MonitoringRunReportView buildReport(
            UUID websiteId,
            UUID monitoringRunId,
            SiteSentinelReportLanguage reportLanguage
    ) {
        SiteSentinelReportLanguage requiredReportLanguage =
                Objects.requireNonNull(
                        reportLanguage,
                        "Report language is required."
                );

        Website website =
                websiteService.findById(
                        websiteId
                );

        MonitoringRun monitoringRun =
                monitoringRunService
                        .findByIdAndWebsiteId(
                                monitoringRunId,
                                websiteId
                        );

        List<RiskRemediationRecommendation>
                allRecommendations =
                recommendationService
                        .findByMonitoringRunId(
                                monitoringRun.getId()
                        );

        List<RiskRemediationRecommendation>
                recommendations =
                filterRecommendationsByLanguage(
                        allRecommendations,
                        requiredReportLanguage
                );

        MonitoringRunReportCounts counts =
                buildCounts(
                        monitoringRun.getId(),
                        recommendations.size()
                );

        MonitoringRunReportTraceabilitySummary
                traceabilitySummary =
                MonitoringRunReportTraceabilitySummary
                        .fromCounts(counts);

        TrustAssessment latestTrustAssessment =
                findLatestTrustAssessment(
                        monitoringRun.getId()
                );

        List<Finding> findings =
                findingService
                        .findByMonitoringRunId(
                                monitoringRun.getId()
                        );

        List<Risk> risks =
                riskService
                        .findByMonitoringRunId(
                                monitoringRun.getId()
                        );

        List<MonitoringRunReportRiskRecommendationView>
                riskRecommendationViews =
                buildRiskRecommendationViews(
                        risks,
                        recommendations
                );

        AssessmentComparisonSummary comparison =
                assessmentComparisonService.compare(
                        website.getId(),
                        monitoringRun.getId()
                );

        return new MonitoringRunReportView(
                website,
                monitoringRun,
                counts,
                traceabilitySummary,
                latestTrustAssessment,
                findings,
                risks,
                recommendations,
                riskRecommendationViews,
                comparison,
                requiredReportLanguage
        );
    }

    private List<RiskRemediationRecommendation>
    filterRecommendationsByLanguage(
            List<RiskRemediationRecommendation>
                    recommendations,
            SiteSentinelReportLanguage reportLanguage
    ) {
        if (recommendations == null
                || recommendations.isEmpty()) {

            return List.of();
        }

        return recommendations.stream()
                .filter(Objects::nonNull)
                .filter(
                        recommendation ->
                                recommendation
                                        .getReportLanguage()
                                        == reportLanguage
                )
                .toList();
    }

    private List<MonitoringRunReportRiskRecommendationView>
    buildRiskRecommendationViews(
            List<Risk> risks,
            List<RiskRemediationRecommendation>
                    recommendations
    ) {
        if (risks == null || risks.isEmpty()) {
            return List.of();
        }

        Map<UUID, RiskRemediationRecommendation>
                latestRecommendationByRiskId =
                new HashMap<>();

        if (recommendations != null) {
            for (
                    RiskRemediationRecommendation
                            recommendation
                    : recommendations
            ) {
                latestRecommendationByRiskId.put(
                        recommendation.getRiskId(),
                        recommendation
                );
            }
        }

        return risks.stream()
                .map(
                        risk ->
                                new MonitoringRunReportRiskRecommendationView(
                                        risk,
                                        latestRecommendationByRiskId
                                                .get(risk.getId())
                                )
                )
                .toList();
    }

    private MonitoringRunReportCounts buildCounts(
            UUID monitoringRunId,
            long recommendationCount
    ) {
        return new MonitoringRunReportCounts(
                evidenceService.countCollectedEvidence(
                        monitoringRunId
                ),
                evidenceService.countNormalizedEvidence(
                        monitoringRunId
                ),
                findingService.countByMonitoringRunId(
                        monitoringRunId
                ),
                riskService.countByMonitoringRunId(
                        monitoringRunId
                ),
                recommendationCount,
                trustAssessmentService
                        .countByMonitoringRunId(
                                monitoringRunId
                        )
        );
    }

    private TrustAssessment findLatestTrustAssessment(
            UUID monitoringRunId
    ) {
        return trustAssessmentService
                .findByMonitoringRunId(
                        monitoringRunId
                )
                .stream()
                .findFirst()
                .orElse(null);
    }
}