package com.cigabyte.sitesentinel.scheduling;

import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonitoringScheduleService {

    private final MonitoringScheduleRepository monitoringScheduleRepository;
    private final WebsiteRepository websiteRepository;

    public MonitoringScheduleService(
            MonitoringScheduleRepository monitoringScheduleRepository,
            WebsiteRepository websiteRepository
    ) {
        this.monitoringScheduleRepository = monitoringScheduleRepository;
        this.websiteRepository = websiteRepository;
    }

    @Transactional(readOnly = true)
    public Optional<MonitoringSchedule> findByWebsiteId(UUID websiteId) {
        return monitoringScheduleRepository.findByWebsiteId(websiteId);
    }

    @Transactional
    public MonitoringSchedule findOrCreateForWebsite(UUID websiteId) {
        validateWebsiteExists(websiteId);

        return monitoringScheduleRepository.findByWebsiteId(websiteId)
                .orElseGet(() -> monitoringScheduleRepository.save(new MonitoringSchedule(websiteId)));
    }

    @Transactional
    public MonitoringSchedule enableDailySchedule(UUID websiteId, OffsetDateTime nextRunAt) {
        MonitoringSchedule schedule = findOrCreateForWebsite(websiteId);
        schedule.enableDaily(nextRunAt);

        return monitoringScheduleRepository.save(schedule);
    }

    @Transactional
    public MonitoringSchedule disableSchedule(UUID websiteId) {
        MonitoringSchedule schedule = findOrCreateForWebsite(websiteId);
        schedule.disable();

        return monitoringScheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<MonitoringSchedule> findDueSchedules(OffsetDateTime now) {
        return monitoringScheduleRepository.findByStatusAndNextRunAtLessThanEqualOrderByNextRunAtAsc(
                MonitoringScheduleStatus.ENABLED,
                now
        );
    }

    @Transactional
    public MonitoringSchedule markTriggered(
            UUID scheduleId,
            UUID monitoringRunId,
            OffsetDateTime triggeredAt,
            OffsetDateTime nextRunAt
    ) {
        MonitoringSchedule schedule = monitoringScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring schedule not found: " + scheduleId));

        schedule.markTriggered(monitoringRunId, triggeredAt, nextRunAt);

        return monitoringScheduleRepository.save(schedule);
    }

    @Transactional
    public MonitoringSchedule recordFailure(UUID scheduleId, String failureReason) {
        MonitoringSchedule schedule = monitoringScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring schedule not found: " + scheduleId));

        schedule.recordFailure(failureReason);

        return monitoringScheduleRepository.save(schedule);
    }

    private void validateWebsiteExists(UUID websiteId) {
        boolean websiteExists = websiteRepository.existsById(websiteId);

        if (!websiteExists) {
            throw new IllegalArgumentException("Website not found: " + websiteId);
        }
    }
}