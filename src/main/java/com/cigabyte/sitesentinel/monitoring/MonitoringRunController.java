package com.cigabyte.sitesentinel.monitoring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs")
public class MonitoringRunController {

    private final MonitoringRunService monitoringRunService;

    public MonitoringRunController(MonitoringRunService monitoringRunService) {
        this.monitoringRunService = monitoringRunService;
    }

    @PostMapping
    public String create(@PathVariable UUID websiteId) {
        monitoringRunService.createPendingRun(websiteId);
        return "redirect:/websites/" + websiteId;
    }
}