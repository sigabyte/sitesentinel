package com.cigabyte.sitesentinel.notification;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonService;
import com.cigabyte.sitesentinel.comparison.AssessmentComparisonSummary;
import com.cigabyte.sitesentinel.comparison.ComparisonChangeStatus;
import com.cigabyte.sitesentinel.comparison.RiskComparisonItem;
import com.cigabyte.sitesentinel.comparison.TrustComparisonSummary;
import com.cigabyte.sitesentinel.comparison.TrustScoreDirection;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import com.cigabyte.sitesentinel.risk.RiskSeverity;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.trust.TrustAssessmentRepository;
import com.cigabyte.sitesentinel.trust.TrustStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationEventGenerationService {

    private final NotificationEventService notificationEventService;
    private final TrustAssessmentRepository trustAssessmentRepository;
    private final AssessmentComparisonService assessmentComparisonService;

    public NotificationEventGenerationService(
            NotificationEventService notificationEventService,
            TrustAssessmentRepository trustAssessmentRepository,
            AssessmentComparisonService assessmentComparisonService
    ) {
        this.notificationEventService = notificationEventService;
        this.trustAssessmentRepository = trustAssessmentRepository;
        this.assessmentComparisonService = assessmentComparisonService;
    }

    @Transactional
    public List<NotificationEvent> generateForRun(MonitoringRun monitoringRun) {
        if (monitoringRun == null) {
            return List.of();
        }

        if (monitoringRun.getStatus() == MonitoringRunStatus.FAILED) {
            return generateForFailedRun(monitoringRun);
        }

        if (monitoringRun.getStatus() == MonitoringRunStatus.COMPLETED) {
            return generateForCompletedRun(monitoringRun);
        }

        return List.of();
    }

    private List<NotificationEvent> generateForFailedRun(MonitoringRun monitoringRun) {
        List<NotificationEvent> generatedEvents = new ArrayList<>();

        generatedEvents.add(notificationEventService.createIfAbsent(
                new NotificationEventCreateRequest(
                        monitoringRun.getWebsiteId(),
                        monitoringRun.getId(),
                        NotificationEventType.MONITORING_RUN_FAILED,
                        NotificationEventSeverity.HIGH,
                        "Monitoring run failed",
                        "A monitoring run failed. Reason: " + safeText(
                                monitoringRun.getFailureReason(),
                                "No failure reason was recorded."
                        ),
                        failedRunDeduplicationKey(monitoringRun)
                )
        ));

        return generatedEvents;
    }

    private List<NotificationEvent> generateForCompletedRun(MonitoringRun monitoringRun) {
        List<NotificationEvent> generatedEvents = new ArrayList<>();

        Optional<TrustAssessment> currentTrustAssessment =
                trustAssessmentRepository.findFirstByMonitoringRunIdOrderByCreatedAtDesc(
                        monitoringRun.getId()
                );

        currentTrustAssessment.ifPresent(trustAssessment ->
                generateHighRiskTrustAssessmentEvent(monitoringRun, trustAssessment, generatedEvents)
        );

        AssessmentComparisonSummary comparisonSummary = assessmentComparisonService.compare(
                monitoringRun.getWebsiteId(),
                monitoringRun.getId()
        );

        if (!comparisonSummary.isAvailable()) {
            return generatedEvents;
        }

        generateTrustStatusChangedEvent(monitoringRun, comparisonSummary, generatedEvents);
        generateTrustScoreDeclinedEvent(monitoringRun, comparisonSummary, generatedEvents);
        generateNewRiskTypeDetectedEvents(monitoringRun, comparisonSummary, generatedEvents);

        return generatedEvents;
    }

    private void generateHighRiskTrustAssessmentEvent(
            MonitoringRun monitoringRun,
            TrustAssessment trustAssessment,
            List<NotificationEvent> generatedEvents
    ) {
        if (trustAssessment.getTrustStatus() != TrustStatus.HIGH_RISK) {
            return;
        }

        generatedEvents.add(notificationEventService.createIfAbsent(
                new NotificationEventCreateRequest(
                        monitoringRun.getWebsiteId(),
                        monitoringRun.getId(),
                        NotificationEventType.HIGH_RISK_TRUST_ASSESSMENT,
                        NotificationEventSeverity.CRITICAL,
                        "High-risk trust assessment detected",
                        "The latest trust assessment is HIGH_RISK. Trust score: "
                                + trustAssessment.getTrustScore()
                                + ". Confidence score: "
                                + trustAssessment.getConfidenceScore()
                                + ".",
                        highRiskTrustDeduplicationKey(monitoringRun)
                )
        ));
    }

    private void generateTrustStatusChangedEvent(
            MonitoringRun monitoringRun,
            AssessmentComparisonSummary comparisonSummary,
            List<NotificationEvent> generatedEvents
    ) {
        TrustComparisonSummary trustComparison = comparisonSummary.getTrustComparison();

        if (trustComparison == null || !trustComparison.isAvailable()) {
            return;
        }

        if (!trustComparison.isStatusChanged()) {
            return;
        }

        TrustStatus previousStatus = trustComparison.getPreviousTrustStatus();
        TrustStatus currentStatus = trustComparison.getCurrentTrustStatus();

        generatedEvents.add(notificationEventService.createIfAbsent(
                new NotificationEventCreateRequest(
                        monitoringRun.getWebsiteId(),
                        monitoringRun.getId(),
                        NotificationEventType.TRUST_STATUS_CHANGED,
                        severityForTrustStatus(currentStatus),
                        "Trust status changed",
                        "Trust status changed from "
                                + previousStatus
                                + " to "
                                + currentStatus
                                + ".",
                        trustStatusChangedDeduplicationKey(
                                monitoringRun,
                                previousStatus,
                                currentStatus
                        )
                )
        ));
    }

    private void generateTrustScoreDeclinedEvent(
            MonitoringRun monitoringRun,
            AssessmentComparisonSummary comparisonSummary,
            List<NotificationEvent> generatedEvents
    ) {
        TrustComparisonSummary trustComparison = comparisonSummary.getTrustComparison();

        if (trustComparison == null || !trustComparison.isAvailable()) {
            return;
        }

        if (trustComparison.getScoreDirection() != TrustScoreDirection.DECLINED) {
            return;
        }

        generatedEvents.add(notificationEventService.createIfAbsent(
                new NotificationEventCreateRequest(
                        monitoringRun.getWebsiteId(),
                        monitoringRun.getId(),
                        NotificationEventType.TRUST_SCORE_DECLINED,
                        severityForTrustScoreDelta(trustComparison.getScoreDelta()),
                        "Trust score declined",
                        "Trust score declined from "
                                + trustComparison.getPreviousTrustScore()
                                + " to "
                                + trustComparison.getCurrentTrustScore()
                                + ". Delta: "
                                + trustComparison.getScoreDelta()
                                + ".",
                        trustScoreDeclinedDeduplicationKey(monitoringRun)
                )
        ));
    }

    private void generateNewRiskTypeDetectedEvents(
            MonitoringRun monitoringRun,
            AssessmentComparisonSummary comparisonSummary,
            List<NotificationEvent> generatedEvents
    ) {
        for (RiskComparisonItem riskChange : comparisonSummary.getRiskChanges()) {
            if (riskChange.getChangeStatus() != ComparisonChangeStatus.NEW) {
                continue;
            }

            String riskType = safeText(riskChange.getRiskType(), "UNKNOWN_RISK_TYPE");

            generatedEvents.add(notificationEventService.createIfAbsent(
                    new NotificationEventCreateRequest(
                            monitoringRun.getWebsiteId(),
                            monitoringRun.getId(),
                            NotificationEventType.NEW_RISK_TYPE_DETECTED,
                            severityForRiskSeverity(riskChange.getCurrentSeverity()),
                            "New risk type detected",
                            "A new risk type was detected: "
                                    + riskType
                                    + ". Current count: "
                                    + riskChange.getCurrentCount()
                                    + ". Severity: "
                                    + safeText(
                                    riskChange.getCurrentSeverity() == null
                                            ? null
                                            : riskChange.getCurrentSeverity().name(),
                                    "UNKNOWN"
                            )
                                    + ". Risk score: "
                                    + safeText(
                                    riskChange.getCurrentRiskScore() == null
                                            ? null
                                            : riskChange.getCurrentRiskScore().toString(),
                                    "UNKNOWN"
                            )
                                    + ".",
                            newRiskTypeDeduplicationKey(monitoringRun, riskType)
                    )
            ));
        }
    }

    private NotificationEventSeverity severityForTrustStatus(TrustStatus trustStatus) {
        if (trustStatus == TrustStatus.HIGH_RISK) {
            return NotificationEventSeverity.CRITICAL;
        }

        if (trustStatus == TrustStatus.NEEDS_ATTENTION) {
            return NotificationEventSeverity.WARNING;
        }

        if (trustStatus == TrustStatus.TRUSTED) {
            return NotificationEventSeverity.INFO;
        }

        return NotificationEventSeverity.WARNING;
    }

    private NotificationEventSeverity severityForTrustScoreDelta(Integer scoreDelta) {
        if (scoreDelta == null) {
            return NotificationEventSeverity.WARNING;
        }

        if (scoreDelta <= -30) {
            return NotificationEventSeverity.HIGH;
        }

        if (scoreDelta <= -10) {
            return NotificationEventSeverity.WARNING;
        }

        return NotificationEventSeverity.INFO;
    }

    private NotificationEventSeverity severityForRiskSeverity(RiskSeverity riskSeverity) {
        if (riskSeverity == RiskSeverity.CRITICAL) {
            return NotificationEventSeverity.CRITICAL;
        }

        if (riskSeverity == RiskSeverity.HIGH) {
            return NotificationEventSeverity.HIGH;
        }

        if (riskSeverity == RiskSeverity.MEDIUM) {
            return NotificationEventSeverity.WARNING;
        }

        return NotificationEventSeverity.INFO;
    }

    private String failedRunDeduplicationKey(MonitoringRun monitoringRun) {
        return "notification:"
                + monitoringRun.getWebsiteId()
                + ":"
                + NotificationEventType.MONITORING_RUN_FAILED
                + ":"
                + normalizeKeyPart(monitoringRun.getFailureReason());
    }

    private String highRiskTrustDeduplicationKey(MonitoringRun monitoringRun) {
        return "notification:"
                + monitoringRun.getWebsiteId()
                + ":"
                + NotificationEventType.HIGH_RISK_TRUST_ASSESSMENT;
    }

    private String trustStatusChangedDeduplicationKey(
            MonitoringRun monitoringRun,
            TrustStatus previousStatus,
            TrustStatus currentStatus
    ) {
        return "notification:"
                + monitoringRun.getWebsiteId()
                + ":"
                + NotificationEventType.TRUST_STATUS_CHANGED
                + ":"
                + previousStatus
                + ":to:"
                + currentStatus
                + ":run:"
                + monitoringRun.getId();
    }

    private String trustScoreDeclinedDeduplicationKey(MonitoringRun monitoringRun) {
        return "notification:"
                + monitoringRun.getWebsiteId()
                + ":"
                + NotificationEventType.TRUST_SCORE_DECLINED
                + ":run:"
                + monitoringRun.getId();
    }

    private String newRiskTypeDeduplicationKey(
            MonitoringRun monitoringRun,
            String riskType
    ) {
        return "notification:"
                + monitoringRun.getWebsiteId()
                + ":"
                + NotificationEventType.NEW_RISK_TYPE_DETECTED
                + ":"
                + normalizeKeyPart(riskType)
                + ":run:"
                + monitoringRun.getId();
    }

    private String normalizeKeyPart(String value) {
        String normalized = safeText(value, "unknown")
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9_-]+", "-");

        if (normalized.length() > 80) {
            return normalized.substring(0, 80);
        }

        return normalized;
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }
}