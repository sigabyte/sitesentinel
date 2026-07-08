package com.cigabyte.sitesentinel.scheduling;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Controller
@RequestMapping("/websites/{websiteId}/schedules")
public class MonitoringScheduleController {

    private final MonitoringScheduleService monitoringScheduleService;

    public MonitoringScheduleController(MonitoringScheduleService monitoringScheduleService) {
        this.monitoringScheduleService = monitoringScheduleService;
    }

    @PostMapping("/enable-daily")
    public String enableDaily(@PathVariable UUID websiteId) {
        monitoringScheduleService.enableDailySchedule(
                websiteId,
                OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(10)
        );

        return "redirect:/websites/" + websiteId;
    }

    @PostMapping("/disable")
    public String disable(@PathVariable UUID websiteId) {
        monitoringScheduleService.disableSchedule(websiteId);

        return "redirect:/websites/" + websiteId;
    }
}