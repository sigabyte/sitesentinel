package com.cigabyte.sitesentinel.finding;

import com.cigabyte.sitesentinel.evidence.EvidenceService;
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
@RequestMapping("/websites/{websiteId}/monitoring-runs/{runId}/findings")
public class FindingController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final FindingService findingService;
    private final EvidenceService evidenceService;

    public FindingController(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            FindingService findingService,
            EvidenceService evidenceService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.findingService = findingService;
        this.evidenceService = evidenceService;
    }

    @GetMapping("/{findingId}")
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            @PathVariable UUID findingId,
            Model model
    ) {
        Website website = websiteService.findById(websiteId);
        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(runId, websiteId);
        Finding finding = findingService.findByIdAndMonitoringRunIdAndWebsiteId(
                findingId,
                runId,
                websiteId
        );

        List<FindingEvidence> evidenceLinks = findingService.findEvidenceLinks(findingId);
        List<UUID> collectedEvidenceIds = evidenceLinks.stream()
                .map(FindingEvidence::getCollectedEvidenceId)
                .toList();

        model.addAttribute("website", website);
        model.addAttribute("monitoringRun", monitoringRun);
        model.addAttribute("finding", finding);
        model.addAttribute("evidenceLinks", evidenceLinks);
        model.addAttribute("sourceEvidence", evidenceService.findCollectedEvidenceByIds(
                websiteId,
                runId,
                collectedEvidenceIds
        ));

        return "findings/detail";
    }
}