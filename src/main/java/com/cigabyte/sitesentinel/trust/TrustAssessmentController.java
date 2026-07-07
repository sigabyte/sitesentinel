package com.cigabyte.sitesentinel.trust;

import com.cigabyte.sitesentinel.evidence.CollectedEvidence;
import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.evidence.NormalizedEvidence;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.finding.FindingEvidence;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskFinding;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs/{runId}/trust-assessments")
public class TrustAssessmentController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final TrustAssessmentService trustAssessmentService;
    private final RiskService riskService;
    private final FindingService findingService;
    private final EvidenceService evidenceService;

    public TrustAssessmentController(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            TrustAssessmentService trustAssessmentService,
            RiskService riskService,
            FindingService findingService,
            EvidenceService evidenceService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.trustAssessmentService = trustAssessmentService;
        this.riskService = riskService;
        this.findingService = findingService;
        this.evidenceService = evidenceService;
    }

    @GetMapping("/{trustAssessmentId}")
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            @PathVariable UUID trustAssessmentId,
            Model model
    ) {
        Website website = websiteService.findById(websiteId);
        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(runId, websiteId);

        TrustAssessment trustAssessment = trustAssessmentService.findByIdAndMonitoringRunIdAndWebsiteId(
                trustAssessmentId,
                runId,
                websiteId
        );

        List<TrustAssessmentRisk> trustAssessmentRiskLinks =
                trustAssessmentService.findRiskLinks(trustAssessmentId);

        List<UUID> riskIds = trustAssessmentRiskLinks.stream()
                .map(TrustAssessmentRisk::getRiskId)
                .toList();

        List<Risk> sourceRisks = riskService.findByIdsAndMonitoringRunIdAndWebsiteId(
                websiteId,
                runId,
                riskIds
        );

        Map<UUID, List<RiskFinding>> riskFindingLinksByRiskId = new LinkedHashMap<>();
        Map<UUID, List<Finding>> sourceFindingsByRiskId = new LinkedHashMap<>();
        Map<UUID, List<FindingEvidence>> findingEvidenceLinksByFindingId = new LinkedHashMap<>();
        Map<UUID, List<CollectedEvidence>> sourceEvidenceByFindingId = new LinkedHashMap<>();
        Map<UUID, List<NormalizedEvidence>> normalizedEvidenceByCollectedEvidenceId = new LinkedHashMap<>();

        for (Risk risk : sourceRisks) {
            List<RiskFinding> riskFindingLinks = riskService.findFindingLinks(risk.getId());

            riskFindingLinksByRiskId.put(
                    risk.getId(),
                    riskFindingLinks
            );

            List<UUID> findingIds = riskFindingLinks.stream()
                    .map(RiskFinding::getFindingId)
                    .toList();

            List<Finding> sourceFindings = findingService.findByIdsAndMonitoringRunIdAndWebsiteId(
                    websiteId,
                    runId,
                    findingIds
            );

            sourceFindingsByRiskId.put(
                    risk.getId(),
                    sourceFindings
            );

            for (Finding finding : sourceFindings) {
                List<FindingEvidence> findingEvidenceLinks =
                        findingService.findEvidenceLinks(finding.getId());

                findingEvidenceLinksByFindingId.put(
                        finding.getId(),
                        findingEvidenceLinks
                );

                List<UUID> collectedEvidenceIds = findingEvidenceLinks.stream()
                        .map(FindingEvidence::getCollectedEvidenceId)
                        .toList();

                List<CollectedEvidence> sourceEvidence = evidenceService.findCollectedEvidenceByIds(
                        websiteId,
                        runId,
                        collectedEvidenceIds
                );

                sourceEvidenceByFindingId.put(
                        finding.getId(),
                        sourceEvidence
                );

                for (CollectedEvidence collectedEvidence : sourceEvidence) {
                    normalizedEvidenceByCollectedEvidenceId.put(
                            collectedEvidence.getId(),
                            evidenceService.findNormalizedEvidenceForCollectedEvidence(
                                    websiteId,
                                    runId,
                                    collectedEvidence.getId()
                            )
                    );
                }
            }
        }

        model.addAttribute("website", website);
        model.addAttribute("monitoringRun", monitoringRun);
        model.addAttribute("trustAssessment", trustAssessment);
        model.addAttribute("trustAssessmentRiskLinks", trustAssessmentRiskLinks);
        model.addAttribute("sourceRisks", sourceRisks);
        model.addAttribute("riskFindingLinksByRiskId", riskFindingLinksByRiskId);
        model.addAttribute("sourceFindingsByRiskId", sourceFindingsByRiskId);
        model.addAttribute("findingEvidenceLinksByFindingId", findingEvidenceLinksByFindingId);
        model.addAttribute("sourceEvidenceByFindingId", sourceEvidenceByFindingId);
        model.addAttribute("normalizedEvidenceByCollectedEvidenceId", normalizedEvidenceByCollectedEvidenceId);

        return "trust/detail";
    }
}