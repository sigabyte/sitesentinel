package com.cigabyte.sitesentinel.comparison;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs/{runId}/comparison")
public class AssessmentComparisonController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final AssessmentComparisonService assessmentComparisonService;

    public AssessmentComparisonController(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            AssessmentComparisonService assessmentComparisonService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.assessmentComparisonService = assessmentComparisonService;
    }

    @GetMapping
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            Model model
    ) {
        Website website = websiteService.findById(websiteId);

        MonitoringRun currentRun = monitoringRunService.findByIdAndWebsiteId(
                runId,
                websiteId
        );

        AssessmentComparisonSummary comparison = assessmentComparisonService.compare(
                websiteId,
                runId
        );

        model.addAttribute("website", website);
        model.addAttribute("currentRun", currentRun);
        model.addAttribute("comparison", comparison);

        return "comparison/detail";
    }
}