package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.website.WebsiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cigabyte.sitesentinel.finding.FindingService;

import java.util.UUID;

@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs")
public class MonitoringRunController {

    private final MonitoringRunService monitoringRunService;
    private final WebsiteService websiteService;
    private final EvidenceService evidenceService;
    private final FindingService findingService;

    public MonitoringRunController(
            MonitoringRunService monitoringRunService,
            WebsiteService websiteService,
            EvidenceService evidenceService,
            FindingService findingService
    ) {
        this.monitoringRunService = monitoringRunService;
        this.websiteService = websiteService;
        this.evidenceService = evidenceService;
        this.findingService = findingService;
    }

    @PostMapping
    public String create(@PathVariable UUID websiteId) {
        monitoringRunService.createPendingRun(websiteId);
        return "redirect:/websites/" + websiteId;
    }

    @GetMapping("/{runId}")
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            Model model
    ) {
        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(runId, websiteId);

        model.addAttribute("website", websiteService.findById(websiteId));
        model.addAttribute("monitoringRun", monitoringRun);
        model.addAttribute("collectedEvidence", evidenceService.findCollectedEvidence(runId));
        model.addAttribute("normalizedEvidence", evidenceService.findNormalizedEvidence(runId));
        model.addAttribute("findings", findingService.findByMonitoringRunId(runId));

        return "monitoring-runs/detail";
    }
}