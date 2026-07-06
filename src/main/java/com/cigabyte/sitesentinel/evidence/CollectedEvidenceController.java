package com.cigabyte.sitesentinel.evidence;

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

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs/{runId}/collected-evidence")
public class CollectedEvidenceController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final EvidenceService evidenceService;
    private final FindingService findingService;

    public CollectedEvidenceController(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            EvidenceService evidenceService,
            FindingService findingService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.evidenceService = evidenceService;
        this.findingService = findingService;
    }

    @GetMapping("/{collectedEvidenceId}")
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            @PathVariable UUID collectedEvidenceId,
            Model model
    ) {
        Website website = websiteService.findById(websiteId);
        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(runId, websiteId);

        CollectedEvidence collectedEvidence = evidenceService.findCollectedEvidenceById(
                websiteId,
                runId,
                collectedEvidenceId
        );

        List<NormalizedEvidence> normalizedEvidence =
                evidenceService.findNormalizedEvidenceForCollectedEvidence(
                        websiteId,
                        runId,
                        collectedEvidenceId
                );

        List<FindingEvidence> findingEvidenceLinks =
                findingService.findEvidenceLinksByCollectedEvidenceId(collectedEvidenceId);

        List<UUID> findingIds = findingEvidenceLinks.stream()
                .map(FindingEvidence::getFindingId)
                .toList();

        List<Finding> relatedFindings = findingService.findByIdsAndMonitoringRunIdAndWebsiteId(
                websiteId,
                runId,
                findingIds
        );

        model.addAttribute("website", website);
        model.addAttribute("monitoringRun", monitoringRun);
        model.addAttribute("collectedEvidence", collectedEvidence);
        model.addAttribute("normalizedEvidence", normalizedEvidence);
        model.addAttribute("findingEvidenceLinks", findingEvidenceLinks);
        model.addAttribute("relatedFindings", relatedFindings);

        return "evidence/collected-detail";
    }
}