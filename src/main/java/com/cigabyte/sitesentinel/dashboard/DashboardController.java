package com.cigabyte.sitesentinel.dashboard;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import com.cigabyte.sitesentinel.trust.TrustAssessmentRepository;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import com.cigabyte.sitesentinel.website.WebsiteStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final WebsiteRepository websiteRepository;
    private final MonitoringRunRepository monitoringRunRepository;
    private final TrustAssessmentRepository trustAssessmentRepository;

    public DashboardController(
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository,
            TrustAssessmentRepository trustAssessmentRepository
    ) {
        this.websiteRepository = websiteRepository;
        this.monitoringRunRepository = monitoringRunRepository;
        this.trustAssessmentRepository = trustAssessmentRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        long totalWebsites = websiteRepository.count();
        long activeWebsites = websiteRepository.findAll()
                .stream()
                .filter(website -> website.getStatus() == WebsiteStatus.ACTIVE)
                .count();

        long totalMonitoringRuns = monitoringRunRepository.count();
        long pendingMonitoringRuns = monitoringRunRepository.countByStatus(MonitoringRunStatus.PENDING);
        long runningMonitoringRuns = monitoringRunRepository.countByStatus(MonitoringRunStatus.RUNNING);
        long completedMonitoringRuns = monitoringRunRepository.countByStatus(MonitoringRunStatus.COMPLETED);
        long failedMonitoringRuns = monitoringRunRepository.countByStatus(MonitoringRunStatus.FAILED);

        model.addAttribute("totalWebsites", totalWebsites);
        model.addAttribute("activeWebsites", activeWebsites);

        model.addAttribute("totalMonitoringRuns", totalMonitoringRuns);
        model.addAttribute("pendingMonitoringRuns", pendingMonitoringRuns);
        model.addAttribute("runningMonitoringRuns", runningMonitoringRuns);
        model.addAttribute("completedMonitoringRuns", completedMonitoringRuns);
        model.addAttribute("failedMonitoringRuns", failedMonitoringRuns);

        model.addAttribute("latestMonitoringRuns", monitoringRunRepository.findTop10ByOrderByCreatedAtDesc());
        model.addAttribute("latestTrustAssessments", trustAssessmentRepository.findTop10ByOrderByCreatedAtDesc());

        return "dashboard/index";
    }
}