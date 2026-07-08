package com.cigabyte.sitesentinel.scheduling;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "monitoring_schedules",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_monitoring_schedules_website", columnNames = "website_id")
        }
)
public class MonitoringSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MonitoringScheduleStatus status = MonitoringScheduleStatus.DISABLED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MonitoringScheduleFrequency frequency = MonitoringScheduleFrequency.DAILY;

    @Column(name = "next_run_at")
    private OffsetDateTime nextRunAt;

    @Column(name = "last_triggered_at")
    private OffsetDateTime lastTriggeredAt;

    @Column(name = "last_monitoring_run_id")
    private UUID lastMonitoringRunId;

    @Column(name = "last_failure_reason", columnDefinition = "TEXT")
    private String lastFailureReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected MonitoringSchedule() {
    }

    public MonitoringSchedule(UUID websiteId) {
        this.websiteId = websiteId;
        this.status = MonitoringScheduleStatus.DISABLED;
        this.frequency = MonitoringScheduleFrequency.DAILY;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = MonitoringScheduleStatus.DISABLED;
        }

        if (this.frequency == null) {
            this.frequency = MonitoringScheduleFrequency.DAILY;
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

    public MonitoringScheduleStatus getStatus() {
        return status;
    }

    public MonitoringScheduleFrequency getFrequency() {
        return frequency;
    }

    public OffsetDateTime getNextRunAt() {
        return nextRunAt;
    }

    public OffsetDateTime getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public UUID getLastMonitoringRunId() {
        return lastMonitoringRunId;
    }

    public String getLastFailureReason() {
        return lastFailureReason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isEnabled() {
        return status == MonitoringScheduleStatus.ENABLED;
    }

    public void enableDaily(OffsetDateTime nextRunAt) {
        this.status = MonitoringScheduleStatus.ENABLED;
        this.frequency = MonitoringScheduleFrequency.DAILY;
        this.nextRunAt = nextRunAt;
        this.lastFailureReason = null;
    }

    public void disable() {
        this.status = MonitoringScheduleStatus.DISABLED;
        this.nextRunAt = null;
    }

    public void markTriggered(UUID monitoringRunId, OffsetDateTime triggeredAt, OffsetDateTime nextRunAt) {
        this.lastMonitoringRunId = monitoringRunId;
        this.lastTriggeredAt = triggeredAt;
        this.nextRunAt = nextRunAt;
        this.lastFailureReason = null;
    }

    public void recordFailure(String failureReason) {
        this.lastFailureReason = failureReason;
    }
}