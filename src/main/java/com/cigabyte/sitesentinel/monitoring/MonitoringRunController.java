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
import java.util.stream.Collectors;
import java.util.Map;

import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.evidence.CollectedEvidence;
import com.cigabyte.sitesentinel.evidence.NormalizedEvidence;
import com.cigabyte.sitesentinel.risk.Risk;


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

        List<CollectedEvidence> collectedEvidence = evidenceService.findCollectedEvidence(runId);
        List<NormalizedEvidence> normalizedEvidence = evidenceService.findNormalizedEvidence(runId);

        model.addAttribute("collectedEvidence", collectedEvidence);
        model.addAttribute("normalizedEvidence", normalizedEvidence);

        List<Finding> findings = findingService.findByMonitoringRunId(runId);

        Map<UUID, Long> findingEvidenceCounts = findings.stream()
                .collect(Collectors.toMap(
                        finding -> finding.getId(),
                        finding -> findingService.countEvidenceLinks(finding.getId())
                ));

        model.addAttribute("findings", findings);
        model.addAttribute("findingEvidenceCounts", findingEvidenceCounts);

        List<Risk> risks = riskService.findByMonitoringRunId(runId);

        model.addAttribute("risks", risks);

        Map<UUID, Long> riskFindingCounts = risks.stream()
                .collect(Collectors.toMap(
                        risk -> risk.getId(),
                        risk -> riskService.countFindingLinks(risk.getId())
                ));

        model.addAttribute("riskFindingCounts", riskFindingCounts);

        model.addAttribute("trustAssessments", trustAssessments);

        Map<UUID, Long> trustAssessmentRiskCounts = trustAssessments.stream()
                .collect(Collectors.toMap(
                        assessment -> assessment.getId(),
                        assessment -> trustAssessmentService.countRiskLinks(assessment.getId())
                ));

        model.addAttribute("trustAssessmentRiskCounts", trustAssessmentRiskCounts);

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

        long collectedEvidenceWithNormalizedCount = normalizedEvidence.stream()
                .map(NormalizedEvidence::getCollectedEvidenceId)
                .distinct()
                .count();

        long findingsWithEvidenceCount = findings.stream()
                .filter(finding -> findingEvidenceCounts.getOrDefault(finding.getId(), 0L) > 0)
                .count();

        long risksWithFindingCount = risks.stream()
                .filter(risk -> riskFindingCounts.getOrDefault(risk.getId(), 0L) > 0)
                .count();

        long trustAssessmentsWithRiskCount = trustAssessments.stream()
                .filter(assessment -> trustAssessmentRiskCounts.getOrDefault(assessment.getId(), 0L) > 0)
                .count();

        model.addAttribute("collectedEvidenceWithNormalizedCount", collectedEvidenceWithNormalizedCount);
        model.addAttribute("findingsWithEvidenceCount", findingsWithEvidenceCount);
        model.addAttribute("risksWithFindingCount", risksWithFindingCount);
        model.addAttribute("trustAssessmentsWithRiskCount", trustAssessmentsWithRiskCount);

        String evidenceToNormalizedCoverageStatus = resolveCoverageStatus(
                collectedEvidenceCount,
                collectedEvidenceWithNormalizedCount
        );

        String findingToEvidenceCoverageStatus = resolveCoverageStatus(
                findingCount,
                findingsWithEvidenceCount
        );

        String riskToFindingCoverageStatus = resolveCoverageStatus(
                riskCount,
                risksWithFindingCount
        );

        String trustToRiskCoverageStatus = resolveCoverageStatus(
                trustAssessmentCount,
                trustAssessmentsWithRiskCount
        );

        String overallTraceabilityStatus = resolveOverallTraceabilityStatus(
                evidenceToNormalizedCoverageStatus,
                findingToEvidenceCoverageStatus,
                riskToFindingCoverageStatus,
                trustToRiskCoverageStatus
        );

        model.addAttribute("evidenceToNormalizedCoverageStatus", evidenceToNormalizedCoverageStatus);
        model.addAttribute("findingToEvidenceCoverageStatus", findingToEvidenceCoverageStatus);
        model.addAttribute("riskToFindingCoverageStatus", riskToFindingCoverageStatus);
        model.addAttribute("trustToRiskCoverageStatus", trustToRiskCoverageStatus);

        model.addAttribute("overallTraceabilityStatus", overallTraceabilityStatus);
        model.addAttribute(
                "overallTraceabilityStatusDescription",
                resolveOverallTraceabilityStatusDescription(overallTraceabilityStatus)
        );

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

    private String resolveCoverageStatus(long sourceCount, long linkedSourceCount) {
        if (sourceCount == 0) {
            return "NO_SOURCE_DATA";
        }

        if (linkedSourceCount == 0) {
            return "MISSING";
        }

        if (linkedSourceCount < sourceCount) {
            return "PARTIAL";
        }

        return "AVAILABLE";
    }

    private String resolveOverallTraceabilityStatus(
            String evidenceToNormalizedCoverageStatus,
            String findingToEvidenceCoverageStatus,
            String riskToFindingCoverageStatus,
            String trustToRiskCoverageStatus
    ) {
        List<String> coverageStatuses = List.of(
                evidenceToNormalizedCoverageStatus,
                findingToEvidenceCoverageStatus,
                riskToFindingCoverageStatus,
                trustToRiskCoverageStatus
        );

        boolean allHaveNoSourceData = coverageStatuses.stream()
                .allMatch("NO_SOURCE_DATA"::equals);

        if (allHaveNoSourceData) {
            return "NO_TRACEABILITY_SOURCE_DATA";
        }

        if (coverageStatuses.contains("MISSING")) {
            return "TRACEABILITY_MISSING_LINKS";
        }

        if (coverageStatuses.contains("PARTIAL")) {
            return "TRACEABILITY_PARTIAL";
        }

        return "TRACEABILITY_AVAILABLE";
    }

    private String resolveOverallTraceabilityStatusDescription(String overallTraceabilityStatus) {
        if ("TRACEABILITY_AVAILABLE".equals(overallTraceabilityStatus)) {
            return "Run output has usable traceability links across the monitored lifecycle.";
        }

        if ("TRACEABILITY_PARTIAL".equals(overallTraceabilityStatus)) {
            return "Some lifecycle outputs have traceability links, but not every source record is linked downstream.";
        }

        if ("TRACEABILITY_MISSING_LINKS".equals(overallTraceabilityStatus)) {
            return "One or more lifecycle stages produced records without downstream traceability links.";
        }

        if ("NO_TRACEABILITY_SOURCE_DATA".equals(overallTraceabilityStatus)) {
            return "No source records exist yet for traceability evaluation.";
        }

        return "Traceability status could not be determined.";
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