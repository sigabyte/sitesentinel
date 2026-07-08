package com.cigabyte.sitesentinel.scheduling;

import com.cigabyte.sitesentinel.monitoring.MonitoringExecutionService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScheduledMonitoringWorker {

    private static final Logger log = LoggerFactory.getLogger(ScheduledMonitoringWorker.class);

    private final MonitoringScheduleService monitoringScheduleService;
    private final MonitoringRunService monitoringRunService;
    private final MonitoringExecutionService monitoringExecutionService;
    private final boolean schedulerEnabled;
    private final long staleActiveRunTimeoutMinutes;


    public ScheduledMonitoringWorker(
            MonitoringScheduleService monitoringScheduleService,
            MonitoringRunService monitoringRunService,
            MonitoringExecutionService monitoringExecutionService,
            @Value("${sitesentinel.scheduler.enabled:true}") boolean schedulerEnabled,
            @Value("${sitesentinel.scheduler.stale-active-run-timeout-minutes:60}") long staleActiveRunTimeoutMinutes
    ) {
        this.monitoringScheduleService = monitoringScheduleService;
        this.monitoringRunService = monitoringRunService;
        this.monitoringExecutionService = monitoringExecutionService;
        this.schedulerEnabled = schedulerEnabled;
        this.staleActiveRunTimeoutMinutes = staleActiveRunTimeoutMinutes;
    }

    @Scheduled(
            initialDelayString = "${sitesentinel.scheduler.initial-delay-ms:15000}",
            fixedDelayString = "${sitesentinel.scheduler.fixed-delay-ms:30000}"
    )
    public void executeDueSchedules() {
        if (!schedulerEnabled) {
            log.info("Scheduled monitoring worker skipped because scheduler is disabled.");
            return;
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        log.debug("Scheduled monitoring worker tick. now={}", now);

        List<MonitoringSchedule> dueSchedules = monitoringScheduleService.findDueSchedules(now);

        log.debug("Scheduled monitoring due schedule count={}", dueSchedules.size());

        for (MonitoringSchedule schedule : dueSchedules) {
            executeDueSchedule(schedule, now);
        }
    }

    private void executeDueSchedule(MonitoringSchedule schedule, OffsetDateTime now) {
        log.info(
                "Evaluating scheduled monitoring. scheduleId={}, websiteId={}, status={}, nextRunAt={}",
                schedule.getId(),
                schedule.getWebsiteId(),
                schedule.getStatus(),
                schedule.getNextRunAt()
        );

        if (!isDue(schedule, now)) {
            log.info(
                    "Scheduled monitoring skipped because schedule is not due. scheduleId={}, nextRunAt={}, now={}",
                    schedule.getId(),
                    schedule.getNextRunAt(),
                    now
            );
            return;
        }

        OffsetDateTime staleBefore = now.minusMinutes(staleActiveRunTimeoutMinutes);

        List<MonitoringRun> recoveredRuns = monitoringRunService.recoverStaleActiveRunsForWebsite(
                schedule.getWebsiteId(),
                staleBefore,
                "Stale active monitoring run recovered by scheduled monitoring safety guard."
        );

        for (MonitoringRun recoveredRun : recoveredRuns) {
            log.info(
                    "Recovered stale active monitoring run before scheduled execution. scheduleId={}, websiteId={}, recoveredRunId={}, recoveredRunStatus={}, recoveredRunCreatedAt={}, recoveredRunStartedAt={}, staleBefore={}",
                    schedule.getId(),
                    schedule.getWebsiteId(),
                    recoveredRun.getId(),
                    recoveredRun.getStatus(),
                    recoveredRun.getCreatedAt(),
                    recoveredRun.getStartedAt(),
                    staleBefore
            );
        }

        List<MonitoringRun> activeRuns = monitoringRunService.findActiveRunsForWebsite(schedule.getWebsiteId());

        if (!activeRuns.isEmpty()) {
            for (MonitoringRun activeRun : activeRuns) {
                log.info(
                        "Scheduled monitoring skipped because website already has active run. scheduleId={}, websiteId={}, activeRunId={}, activeRunStatus={}, activeRunCreatedAt={}, activeRunStartedAt={}",
                        schedule.getId(),
                        schedule.getWebsiteId(),
                        activeRun.getId(),
                        activeRun.getStatus(),
                        activeRun.getCreatedAt(),
                        activeRun.getStartedAt()
                );
            }

            return;
        }

        try {
            log.info(
                    "Starting scheduled monitoring run. scheduleId={}, websiteId={}",
                    schedule.getId(),
                    schedule.getWebsiteId()
            );

            MonitoringRun monitoringRun = monitoringExecutionService.executeScheduled(
                    schedule.getWebsiteId(),
                    schedule.getId()
            );

            OffsetDateTime triggeredAt = monitoringRun.getStartedAt() == null
                    ? now
                    : monitoringRun.getStartedAt();

            OffsetDateTime nextRunAt = calculateNextRunAt(schedule, triggeredAt);

            monitoringScheduleService.markTriggered(
                    schedule.getId(),
                    monitoringRun.getId(),
                    triggeredAt,
                    nextRunAt
            );

            log.info(
                    "Scheduled monitoring run finished. scheduleId={}, runId={}, status={}, nextRunAt={}",
                    schedule.getId(),
                    monitoringRun.getId(),
                    monitoringRun.getStatus(),
                    nextRunAt
            );

            if (monitoringRun.getStatus() == MonitoringRunStatus.FAILED) {
                monitoringScheduleService.recordFailure(
                        schedule.getId(),
                        monitoringRun.getFailureReason()
                );

                log.info(
                        "Scheduled monitoring recorded failed run. scheduleId={}, runId={}, reason={}",
                        schedule.getId(),
                        monitoringRun.getId(),
                        monitoringRun.getFailureReason()
                );
            }
        } catch (RuntimeException exception) {
            String failureReason = safeFailureReason(exception);

            monitoringScheduleService.recordFailure(
                    schedule.getId(),
                    failureReason
            );

            log.error(
                    "Scheduled monitoring execution failed. scheduleId={}, websiteId={}, reason={}",
                    schedule.getId(),
                    schedule.getWebsiteId(),
                    failureReason,
                    exception
            );
        }
    }

    private boolean isDue(MonitoringSchedule schedule, OffsetDateTime now) {
        if (schedule == null) {
            return false;
        }

        if (!schedule.isEnabled()) {
            return false;
        }

        if (schedule.getNextRunAt() == null) {
            return false;
        }

        return !schedule.getNextRunAt().isAfter(now);
    }

    private OffsetDateTime calculateNextRunAt(
            MonitoringSchedule schedule,
            OffsetDateTime triggeredAt
    ) {
        if (schedule.getFrequency() == MonitoringScheduleFrequency.DAILY) {
            return triggeredAt.plusDays(1);
        }

        return triggeredAt.plusDays(1);
    }

    private String safeFailureReason(RuntimeException exception) {
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }

        if (message.length() > 2000) {
            return message.substring(0, 2000);
        }

        return message;
    }
}