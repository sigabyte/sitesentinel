package com.cigabyte.sitesentinel.risk;

import com.cigabyte.sitesentinel.evidence.CollectedEvidence;
import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.evidence.NormalizedEvidence;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.finding.FindingEvidence;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs/{runId}/risks")
public class RiskController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final RiskService riskService;
    private final FindingService findingService;
    private final EvidenceService evidenceService;

    public RiskController(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            RiskService riskService,
            FindingService findingService,
            EvidenceService evidenceService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.riskService = riskService;
        this.findingService = findingService;
        this.evidenceService = evidenceService;
    }

    @GetMapping("/{riskId}")
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            @PathVariable UUID riskId,
            Model model
    ) {
        Website website = websiteService.findById(websiteId);
        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(runId, websiteId);

        Risk risk = riskService.findByIdAndMonitoringRunIdAndWebsiteId(
                riskId,
                runId,
                websiteId
        );

        List<RiskFinding> riskFindingLinks = riskService.findFindingLinks(riskId);

        List<UUID> findingIds = riskFindingLinks.stream()
                .map(RiskFinding::getFindingId)
                .toList();

        List<Finding> sourceFindings = findingService.findByIdsAndMonitoringRunIdAndWebsiteId(
                websiteId,
                runId,
                findingIds
        );

        Map<UUID, List<FindingEvidence>> findingEvidenceLinksByFindingId = new LinkedHashMap<>();
        Map<UUID, List<CollectedEvidence>> sourceEvidenceByFindingId = new LinkedHashMap<>();
        Map<UUID, List<NormalizedEvidence>> normalizedEvidenceByCollectedEvidenceId = new LinkedHashMap<>();

        for (Finding finding : sourceFindings) {
            List<FindingEvidence> findingEvidenceLinks = findingService.findEvidenceLinks(finding.getId());

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

        model.addAttribute("website", website);
        model.addAttribute("monitoringRun", monitoringRun);
        model.addAttribute("risk", risk);
        model.addAttribute("riskFindingLinks", riskFindingLinks);
        model.addAttribute("sourceFindings", sourceFindings);
        model.addAttribute("findingEvidenceLinksByFindingId", findingEvidenceLinksByFindingId);
        model.addAttribute("sourceEvidenceByFindingId", sourceEvidenceByFindingId);
        model.addAttribute("normalizedEvidenceByCollectedEvidenceId", normalizedEvidenceByCollectedEvidenceId);

        return "risks/detail";
    }
}