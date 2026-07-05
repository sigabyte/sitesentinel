package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MonitoringRunService {

    private final MonitoringRunRepository monitoringRunRepository;
    private final WebsiteRepository websiteRepository;

    public MonitoringRunService(
            MonitoringRunRepository monitoringRunRepository,
            WebsiteRepository websiteRepository
    ) {
        this.monitoringRunRepository = monitoringRunRepository;
        this.websiteRepository = websiteRepository;
    }

    @Transactional(readOnly = true)
    public List<MonitoringRun> findByWebsiteId(UUID websiteId) {
        return monitoringRunRepository.findByWebsiteIdOrderByCreatedAtDesc(websiteId);
    }

    @Transactional
    public MonitoringRun createPendingRun(UUID websiteId) {
        boolean websiteExists = websiteRepository.existsById(websiteId);

        if (!websiteExists) {
            throw new IllegalArgumentException("Website not found: " + websiteId);
        }

        MonitoringRun monitoringRun = new MonitoringRun(websiteId);
        return monitoringRunRepository.save(monitoringRun);
    }
}