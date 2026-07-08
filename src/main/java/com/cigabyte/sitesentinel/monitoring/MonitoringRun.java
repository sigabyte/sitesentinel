package com.cigabyte.sitesentinel.monitoring;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "monitoring_runs")
public class MonitoringRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MonitoringRunStatus status = MonitoringRunStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 30)
    private MonitoringRunTriggerType triggerType = MonitoringRunTriggerType.MANUAL;

    @Column(name = "monitoring_schedule_id")
    private UUID monitoringScheduleId;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected MonitoringRun() {
    }

    public MonitoringRun(UUID websiteId) {
        this(websiteId, MonitoringRunTriggerType.MANUAL, null);
    }

    public MonitoringRun(
            UUID websiteId,
            MonitoringRunTriggerType triggerType,
            UUID monitoringScheduleId
    ) {
        this.websiteId = websiteId;
        this.status = MonitoringRunStatus.PENDING;
        this.triggerType = triggerType == null ? MonitoringRunTriggerType.MANUAL : triggerType;
        this.monitoringScheduleId = monitoringScheduleId;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = MonitoringRunStatus.PENDING;
        }

        if (this.triggerType == null) {
            this.triggerType = MonitoringRunTriggerType.MANUAL;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getWebsiteId() {
        return websiteId;
    }

    public MonitoringRunStatus getStatus() {
        return status;
    }

    public MonitoringRunTriggerType getTriggerType() {
        return triggerType;
    }

    public UUID getMonitoringScheduleId() {
        return monitoringScheduleId;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void markRunning() {
        this.status = MonitoringRunStatus.RUNNING;
        this.startedAt = OffsetDateTime.now();
        this.failureReason = null;
    }

    public void markCompleted() {
        this.status = MonitoringRunStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void markFailed(String failureReason) {
        this.status = MonitoringRunStatus.FAILED;
        this.completedAt = OffsetDateTime.now();
        this.failureReason = failureReason;
    }
}