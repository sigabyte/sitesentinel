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
@RequestMapping("/websites/{websiteId}/monitoring-runs/{runId}/normalized-evidence")
public class NormalizedEvidenceController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final EvidenceService evidenceService;
    private final FindingService findingService;

    public NormalizedEvidenceController(
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

    @GetMapping("/{normalizedEvidenceId}")
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            @PathVariable UUID normalizedEvidenceId,
            Model model
    ) {
        Website website = websiteService.findById(websiteId);
        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(runId, websiteId);

        NormalizedEvidence normalizedEvidence = evidenceService.findNormalizedEvidenceById(
                websiteId,
                runId,
                normalizedEvidenceId
        );

        CollectedEvidence sourceEvidence = evidenceService.findCollectedEvidenceById(
                websiteId,
                runId,
                normalizedEvidence.getCollectedEvidenceId()
        );

        List<NormalizedEvidence> normalizedEvidenceFromSameSource =
                evidenceService.findNormalizedEvidenceForCollectedEvidence(
                        websiteId,
                        runId,
                        sourceEvidence.getId()
                );

        List<FindingEvidence> findingEvidenceLinks =
                findingService.findEvidenceLinksByCollectedEvidenceId(sourceEvidence.getId());

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
        model.addAttribute("normalizedEvidence", normalizedEvidence);
        model.addAttribute("sourceEvidence", sourceEvidence);
        model.addAttribute("normalizedEvidenceFromSameSource", normalizedEvidenceFromSameSource);
        model.addAttribute("findingEvidenceLinks", findingEvidenceLinks);
        model.addAttribute("relatedFindings", relatedFindings);

        return "evidence/normalized-detail";
    }
}