package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonService;
import com.cigabyte.sitesentinel.comparison.AssessmentComparisonSummary;
import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.trust.TrustAssessmentService;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MonitoringRunReportService {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final EvidenceService evidenceService;
    private final FindingService findingService;
    private final RiskService riskService;
    private final TrustAssessmentService trustAssessmentService;
    private final AssessmentComparisonService assessmentComparisonService;

    public MonitoringRunReportService(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            EvidenceService evidenceService,
            FindingService findingService,
            RiskService riskService,
            TrustAssessmentService trustAssessmentService,
            AssessmentComparisonService assessmentComparisonService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.evidenceService = evidenceService;
        this.findingService = findingService;
        this.riskService = riskService;
        this.trustAssessmentService = trustAssessmentService;
        this.assessmentComparisonService = assessmentComparisonService;
    }

    @Transactional(readOnly = true)
    public MonitoringRunReportView buildReport(UUID websiteId, UUID monitoringRunId) {
        Website website = websiteService.findById(websiteId);

        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(
                monitoringRunId,
                websiteId
        );

        MonitoringRunReportCounts counts = buildCounts(monitoringRun.getId());

        MonitoringRunReportTraceabilitySummary traceabilitySummary =
                MonitoringRunReportTraceabilitySummary.fromCounts(counts);

        TrustAssessment latestTrustAssessment = findLatestTrustAssessment(monitoringRun.getId());

        List<Finding> findings = findingService.findByMonitoringRunId(monitoringRun.getId());

        List<Risk> risks = riskService.findByMonitoringRunId(monitoringRun.getId());

        AssessmentComparisonSummary comparison = assessmentComparisonService.compare(
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
                comparison
        );
    }

    private MonitoringRunReportCounts buildCounts(UUID monitoringRunId) {
        return new MonitoringRunReportCounts(
                evidenceService.countCollectedEvidence(monitoringRunId),
                evidenceService.countNormalizedEvidence(monitoringRunId),
                findingService.countByMonitoringRunId(monitoringRunId),
                riskService.countByMonitoringRunId(monitoringRunId),
                trustAssessmentService.countByMonitoringRunId(monitoringRunId)
        );
    }

    private TrustAssessment findLatestTrustAssessment(UUID monitoringRunId) {
        return trustAssessmentService.findByMonitoringRunId(monitoringRunId)
                .stream()
                .findFirst()
                .orElse(null);
    }
}