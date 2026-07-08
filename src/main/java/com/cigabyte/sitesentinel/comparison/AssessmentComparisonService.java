package com.cigabyte.sitesentinel.comparison;

import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.trust.TrustAssessmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssessmentComparisonService {

    private final MonitoringRunService monitoringRunService;
    private final FindingService findingService;
    private final RiskService riskService;
    private final TrustAssessmentService trustAssessmentService;

    public AssessmentComparisonService(
            MonitoringRunService monitoringRunService,
            FindingService findingService,
            RiskService riskService,
            TrustAssessmentService trustAssessmentService
    ) {
        this.monitoringRunService = monitoringRunService;
        this.findingService = findingService;
        this.riskService = riskService;
        this.trustAssessmentService = trustAssessmentService;
    }

    @Transactional(readOnly = true)
    public AssessmentComparisonSummary compare(UUID websiteId, UUID currentRunId) {
        MonitoringRun currentRun = monitoringRunService.findByIdAndWebsiteId(
                currentRunId,
                websiteId
        );

        if (currentRun.getStatus() != MonitoringRunStatus.COMPLETED) {
            return AssessmentComparisonSummary.unavailable(
                    ComparisonStatus.CURRENT_RUN_NOT_COMPLETED,
                    currentRun
            );
        }

        Optional<MonitoringRun> previousRunOptional =
                monitoringRunService.findPreviousCompletedRun(currentRun);

        if (previousRunOptional.isEmpty()) {
            return AssessmentComparisonSummary.unavailable(
                    ComparisonStatus.NO_PREVIOUS_COMPLETED_RUN,
                    currentRun
            );
        }

        MonitoringRun previousRun = previousRunOptional.get();

        TrustComparisonSummary trustComparison = TrustComparisonSummary.compare(
                findLatestTrustAssessment(currentRun.getId()),
                findLatestTrustAssessment(previousRun.getId())
        );

        List<FindingComparisonItem> findingChanges = compareFindings(
                findingService.findByMonitoringRunId(currentRun.getId()),
                findingService.findByMonitoringRunId(previousRun.getId())
        );

        List<RiskComparisonItem> riskChanges = compareRisks(
                riskService.findByMonitoringRunId(currentRun.getId()),
                riskService.findByMonitoringRunId(previousRun.getId())
        );

        return AssessmentComparisonSummary.available(
                currentRun,
                previousRun,
                trustComparison,
                findingChanges,
                riskChanges
        );
    }

    private TrustAssessment findLatestTrustAssessment(UUID monitoringRunId) {
        return trustAssessmentService.findByMonitoringRunId(monitoringRunId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private List<FindingComparisonItem> compareFindings(
            List<Finding> currentFindings,
            List<Finding> previousFindings
    ) {
        Map<String, List<Finding>> currentByType = groupFindingsByType(currentFindings);
        Map<String, List<Finding>> previousByType = groupFindingsByType(previousFindings);

        TreeSet<String> allTypes = new TreeSet<>();
        allTypes.addAll(currentByType.keySet());
        allTypes.addAll(previousByType.keySet());

        List<FindingComparisonItem> items = new ArrayList<>();

        for (String findingType : allTypes) {
            List<Finding> currentItems = currentByType.getOrDefault(findingType, List.of());
            List<Finding> previousItems = previousByType.getOrDefault(findingType, List.of());

            items.add(new FindingComparisonItem(
                    findingType,
                    resolveChangeStatus(currentItems, previousItems),
                    currentItems.size(),
                    previousItems.size(),
                    firstOrNull(currentItems),
                    firstOrNull(previousItems)
            ));
        }

        return items;
    }

    private List<RiskComparisonItem> compareRisks(
            List<Risk> currentRisks,
            List<Risk> previousRisks
    ) {
        Map<String, List<Risk>> currentByType = groupRisksByType(currentRisks);
        Map<String, List<Risk>> previousByType = groupRisksByType(previousRisks);

        TreeSet<String> allTypes = new TreeSet<>();
        allTypes.addAll(currentByType.keySet());
        allTypes.addAll(previousByType.keySet());

        List<RiskComparisonItem> items = new ArrayList<>();

        for (String riskType : allTypes) {
            List<Risk> currentItems = currentByType.getOrDefault(riskType, List.of());
            List<Risk> previousItems = previousByType.getOrDefault(riskType, List.of());

            items.add(new RiskComparisonItem(
                    riskType,
                    resolveChangeStatus(currentItems, previousItems),
                    currentItems.size(),
                    previousItems.size(),
                    firstOrNull(currentItems),
                    firstOrNull(previousItems)
            ));
        }

        return items;
    }

    private Map<String, List<Finding>> groupFindingsByType(List<Finding> findings) {
        if (findings == null || findings.isEmpty()) {
            return Map.of();
        }

        return findings.stream()
                .filter(finding -> finding.getFindingType() != null)
                .collect(Collectors.groupingBy(
                        Finding::getFindingType,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    private Map<String, List<Risk>> groupRisksByType(List<Risk> risks) {
        if (risks == null || risks.isEmpty()) {
            return Map.of();
        }

        return risks.stream()
                .filter(risk -> risk.getRiskType() != null)
                .collect(Collectors.groupingBy(
                        Risk::getRiskType,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    private ComparisonChangeStatus resolveChangeStatus(
            List<?> currentItems,
            List<?> previousItems
    ) {
        boolean existsInCurrent = currentItems != null && !currentItems.isEmpty();
        boolean existedPreviously = previousItems != null && !previousItems.isEmpty();

        if (existsInCurrent && !existedPreviously) {
            return ComparisonChangeStatus.NEW;
        }

        if (!existsInCurrent && existedPreviously) {
            return ComparisonChangeStatus.RESOLVED;
        }

        return ComparisonChangeStatus.UNCHANGED;
    }

    private <T> T firstOrNull(List<T> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.get(0);
    }
}