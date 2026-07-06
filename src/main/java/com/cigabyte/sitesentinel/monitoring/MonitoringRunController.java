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

        model.addAttribute("homepageEvidence", evidenceService.findHomepageEvidence(runId));
        model.addAttribute("robotsTxtEvidence", evidenceService.findRobotsTxtEvidence(runId));
        model.addAttribute("sitemapXmlEvidence", evidenceService.findSitemapXmlEvidence(runId));
        model.addAttribute("otherCollectedEvidence", evidenceService.findOtherCollectedEvidence(runId));

        long collectedEvidenceCount = evidenceService.countCollectedEvidence(runId);
        long normalizedEvidenceCount = evidenceService.countNormalizedEvidence(runId);
        long findingCount = findingService.countByMonitoringRunId(runId);
        long riskCount = riskService.countByMonitoringRunId(runId);
        long trustAssessmentCount = trustAssessmentService.countByMonitoringRunId(runId);

        model.addAttribute("collectedEvidenceCount", collectedEvidenceCount);
        model.addAttribute("normalizedEvidenceCount", normalizedEvidenceCount);
        model.addAttribute("findingCount", findingCount);
        model.addAttribute("riskCount", riskCount);
        model.addAttribute("trustAssessmentCount", trustAssessmentCount);

        model.addAttribute(
                "assessmentOutcome",
                resolveAssessmentOutcome(
                        monitoringRun,
                        collectedEvidenceCount,
                        normalizedEvidenceCount,
                        findingCount,
                        riskCount,
                        trustAssessmentCount
                )
        );

        model.addAttribute(
                "assessmentOutcomeDescription",
                resolveAssessmentOutcomeDescription(
                        monitoringRun,
                        collectedEvidenceCount,
                        normalizedEvidenceCount,
                        findingCount,
                        riskCount,
                        trustAssessmentCount
                )
        );
        return "monitoring-runs/detail";
    }

    private String resolveAssessmentOutcome(
            MonitoringRun monitoringRun,
            long collectedEvidenceCount,
            long normalizedEvidenceCount,
            long findingCount,
            long riskCount,
            long trustAssessmentCount
    ) {
        if (monitoringRun.getStatus() == MonitoringRunStatus.FAILED && trustAssessmentCount == 0) {
            if (collectedEvidenceCount > 0 || normalizedEvidenceCount > 0 || findingCount > 0 || riskCount > 0) {
                return "FAILED_WITH_PARTIAL_OUTPUT";
            }

            return "SCAN_FAILED";
        }

        if (trustAssessmentCount > 0) {
            return "TRUST_ASSESSED";
        }

        if (riskCount > 0) {
            return "RISKS_IDENTIFIED_NO_TRUST_ASSESSMENT";
        }

        if (findingCount > 0) {
            return "FINDINGS_IDENTIFIED_NO_RISK";
        }

        if (normalizedEvidenceCount > 0 || collectedEvidenceCount > 0) {
            return "EVIDENCE_COLLECTED";
        }

        if (monitoringRun.getStatus() == MonitoringRunStatus.COMPLETED) {
            return "COMPLETED_NO_OUTPUT";
        }

        return "NOT_STARTED";
    }

    private String resolveAssessmentOutcomeDescription(
            MonitoringRun monitoringRun,
            long collectedEvidenceCount,
            long normalizedEvidenceCount,
            long findingCount,
            long riskCount,
            long trustAssessmentCount
    ) {
        String outcome = resolveAssessmentOutcome(
                monitoringRun,
                collectedEvidenceCount,
                normalizedEvidenceCount,
                findingCount,
                riskCount,
                trustAssessmentCount
        );

        if ("TRUST_ASSESSED".equals(outcome)) {
            return "The monitoring run completed the full lifecycle and produced a trust assessment.";
        }

        if ("FAILED_WITH_PARTIAL_OUTPUT".equals(outcome)) {
            return "The monitoring run failed, but partial assessment output was recorded before failure.";
        }

        if ("SCAN_FAILED".equals(outcome)) {
            return "The monitoring run failed before usable assessment output could be produced.";
        }

        if ("RISKS_IDENTIFIED_NO_TRUST_ASSESSMENT".equals(outcome)) {
            return "Risk records were produced, but no final trust assessment was created.";
        }

        if ("FINDINGS_IDENTIFIED_NO_RISK".equals(outcome)) {
            return "Findings were produced, but no risk records were created.";
        }

        if ("EVIDENCE_COLLECTED".equals(outcome)) {
            return "Evidence was collected, but the lifecycle did not produce findings, risks or a trust assessment.";
        }

        if ("COMPLETED_NO_OUTPUT".equals(outcome)) {
            return "The run completed technically, but no assessment output was produced.";
        }

        return "The monitoring run has not produced assessment output yet.";
    }
}