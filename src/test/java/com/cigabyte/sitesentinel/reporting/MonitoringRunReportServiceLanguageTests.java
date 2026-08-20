package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonService;
import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendation;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendationService;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.trust.TrustAssessmentService;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitoringRunReportServiceLanguageTests {

    private final WebsiteService websiteService =
            mock(WebsiteService.class);

    private final MonitoringRunService monitoringRunService =
            mock(MonitoringRunService.class);

    private final EvidenceService evidenceService =
            mock(EvidenceService.class);

    private final FindingService findingService =
            mock(FindingService.class);

    private final RiskService riskService =
            mock(RiskService.class);

    private final RiskRemediationRecommendationService
            recommendationService =
            mock(
                    RiskRemediationRecommendationService.class
            );

    private final TrustAssessmentService trustAssessmentService =
            mock(TrustAssessmentService.class);

    private final AssessmentComparisonService
            assessmentComparisonService =
            mock(AssessmentComparisonService.class);

    private final MonitoringRunReportService reportService =
            new MonitoringRunReportService(
                    websiteService,
                    monitoringRunService,
                    evidenceService,
                    findingService,
                    riskService,
                    recommendationService,
                    trustAssessmentService,
                    assessmentComparisonService
            );

    @Test
    void turkishReportContainsOnlyTurkishRecommendations() {
        UUID websiteId = UUID.randomUUID();
        UUID monitoringRunId = UUID.randomUUID();

        Website website =
                mock(Website.class);

        MonitoringRun monitoringRun =
                mock(MonitoringRun.class);

        RiskRemediationRecommendation
                englishRecommendation =
                mock(
                        RiskRemediationRecommendation.class
                );

        RiskRemediationRecommendation
                turkishRecommendation =
                mock(
                        RiskRemediationRecommendation.class
                );

        when(
                monitoringRun.getId()
        ).thenReturn(monitoringRunId);

        when(
                website.getId()
        ).thenReturn(websiteId);

        when(
                websiteService.findById(
                        websiteId
                )
        ).thenReturn(website);

        when(
                monitoringRunService
                        .findByIdAndWebsiteId(
                                monitoringRunId,
                                websiteId
                        )
        ).thenReturn(monitoringRun);

        when(
                findingService.findByMonitoringRunId(
                        monitoringRunId
                )
        ).thenReturn(List.of());

        when(
                riskService.findByMonitoringRunId(
                        monitoringRunId
                )
        ).thenReturn(List.of());

        when(
                recommendationService
                        .findByMonitoringRunId(
                                monitoringRunId
                        )
        ).thenReturn(
                List.of(
                        englishRecommendation,
                        turkishRecommendation
                )
        );

        when(
                englishRecommendation.getReportLanguage()
        ).thenReturn(
                SiteSentinelReportLanguage.ENGLISH
        );

        when(
                turkishRecommendation.getReportLanguage()
        ).thenReturn(
                SiteSentinelReportLanguage.TURKISH
        );

        MonitoringRunReportView reportView =
                reportService.buildReport(
                        websiteId,
                        monitoringRunId,
                        SiteSentinelReportLanguage.TURKISH
                );

        assertEquals(
                SiteSentinelReportLanguage.TURKISH,
                reportView.getReportLanguage()
        );

        assertEquals(
                1,
                reportView.getRecommendations().size()
        );

        assertSame(
                turkishRecommendation,
                reportView.getRecommendations().get(0)
        );
    }
}