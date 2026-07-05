package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.website.WebsiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.trust.TrustAssessmentService;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs")
public class MonitoringRunController {

    private final MonitoringRunService monitoringRunService;
    private final WebsiteService websiteService;
    private final EvidenceService evidenceService;
    private final FindingService findingService;
    private final RiskService riskService;
    private final TrustAssessmentService trustAssessmentService;
    private final MonitoringExecutionService monitoringExecutionService;

    public MonitoringRunController(
            MonitoringRunService monitoringRunService,
            MonitoringExecutionService monitoringExecutionService,
            WebsiteService websiteService,
            EvidenceService evidenceService,
            FindingService findingService,
            RiskService riskService,
            TrustAssessmentService trustAssessmentService
    ) {
        this.monitoringRunService = monitoringRunService;
        this.monitoringExecutionService = monitoringExecutionService;
        this.websiteService = websiteService;
        this.evidenceService = evidenceService;
        this.findingService = findingService;
        this.riskService = riskService;
        this.trustAssessmentService = trustAssessmentService;
    }

    @PostMapping
    public String create(@PathVariable UUID websiteId) {
        MonitoringRun monitoringRun = monitoringExecutionService.execute(websiteId);
        return "redirect:/websites/" + websiteId + "/monitoring-runs/" + monitoringRun.getId();
    }

    @GetMapping("/{runId}")
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            Model model
    ) {
        MonitoringRun monitoringRun = monitoringRunService.findByIdAndWebsiteId(runId, websiteId);
        List<TrustAssessment> trustAssessments = trustAssessmentService.findByMonitoringRunId(runId);

        TrustAssessment latestTrustAssessment = trustAssessments.isEmpty()
                ? null
                : trustAssessments.get(0);

        model.addAttribute("website", websiteService.findById(websiteId));
        model.addAttribute("monitoringRun", monitoringRun);

        model.addAttribute("collectedEvidence", evidenceService.findCollectedEvidence(runId));
        model.addAttribute("normalizedEvidence", evidenceService.findNormalizedEvidence(runId));
        model.addAttribute("findings", findingService.findByMonitoringRunId(runId));
        model.addAttribute("risks", riskService.findByMonitoringRunId(runId));
        model.addAttribute("trustAssessments", trustAssessments);

        model.addAttribute("latestTrustAssessment", latestTrustAssessment);
        model.addAttribute("collectedEvidenceCount", evidenceService.countCollectedEvidence(runId));
        model.addAttribute("normalizedEvidenceCount", evidenceService.countNormalizedEvidence(runId));
        model.addAttribute("findingCount", findingService.countByMonitoringRunId(runId));
        model.addAttribute("riskCount", riskService.countByMonitoringRunId(runId));
        model.addAttribute("trustAssessmentCount", trustAssessmentService.countByMonitoringRunId(runId));

        return "monitoring-runs/detail";
    }
}