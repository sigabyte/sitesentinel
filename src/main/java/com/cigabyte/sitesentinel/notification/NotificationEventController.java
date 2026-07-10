package com.cigabyte.sitesentinel.notification;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/notifications")
public class NotificationEventController {

    private final NotificationEventService notificationEventService;
    private final NotificationDeliveryAttemptService notificationDeliveryAttemptService;

    public NotificationEventController(
            NotificationEventService notificationEventService,
            NotificationDeliveryAttemptService notificationDeliveryAttemptService
    ) {
        this.notificationEventService = notificationEventService;
        this.notificationDeliveryAttemptService = notificationDeliveryAttemptService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) UUID websiteId,
            @RequestParam(required = false) UUID monitoringRunId,
            @RequestParam(required = false) NotificationEventStatus status,
            @RequestParam(required = false) NotificationEventSeverity severity,
            Model model
    ) {
        model.addAttribute("notificationEvents", notificationEventService.findManagedEvents(
                websiteId,
                monitoringRunId,
                status,
                severity
        ));

        model.addAttribute("selectedWebsiteId", websiteId);
        model.addAttribute("selectedMonitoringRunId", monitoringRunId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSeverity", severity);
        model.addAttribute("statuses", NotificationEventStatus.values());
        model.addAttribute("severities", NotificationEventSeverity.values());

        return "notifications/list";
    }

    @GetMapping("/{notificationEventId}")
    public String detail(
            @PathVariable UUID notificationEventId,
            Model model
    ) {
        model.addAttribute("notificationEvent", notificationEventService.findEvent(notificationEventId));
        model.addAttribute("statuses", NotificationEventStatus.values());
        model.addAttribute("severities", NotificationEventSeverity.values());
        model.addAttribute("deliveryChannels", NotificationDeliveryChannel.values());
        model.addAttribute("deliveryAttempts", notificationDeliveryAttemptService.findAttemptsForEvent(notificationEventId));
        model.addAttribute("deliveryAttemptCount", notificationDeliveryAttemptService.countAttemptsForEvent(notificationEventId));

        return "notifications/detail";
    }

    @PostMapping("/{notificationEventId}/mark-read")
    public String markRead(@PathVariable UUID notificationEventId) {
        notificationEventService.markRead(notificationEventId);

        return "redirect:/notifications/" + notificationEventId;
    }

    @PostMapping("/{notificationEventId}/mark-unread")
    public String markUnread(@PathVariable UUID notificationEventId) {
        notificationEventService.markUnread(notificationEventId);

        return "redirect:/notifications/" + notificationEventId;
    }

    @PostMapping("/{notificationEventId}/delivery-attempts/simulate-success")
    public String recordSimulatedDeliverySuccess(
            @PathVariable UUID notificationEventId,
            @RequestParam NotificationDeliveryChannel channel
    ) {
        notificationDeliveryAttemptService.recordSimulatedSuccess(notificationEventId, channel);

        return "redirect:/notifications/" + notificationEventId;
    }

    @PostMapping("/{notificationEventId}/delivery-attempts/simulate-failure")
    public String recordSimulatedDeliveryFailure(
            @PathVariable UUID notificationEventId,
            @RequestParam NotificationDeliveryChannel channel
    ) {
        notificationDeliveryAttemptService.recordSimulatedFailure(notificationEventId, channel);

        return "redirect:/notifications/" + notificationEventId;
    }

    @PostMapping("/{notificationEventId}/delivery-attempts/skip")
    public String recordSkippedDeliveryAttempt(
            @PathVariable UUID notificationEventId,
            @RequestParam NotificationDeliveryChannel channel
    ) {
        notificationDeliveryAttemptService.recordSkipped(notificationEventId, channel);

        return "redirect:/notifications/" + notificationEventId;
    }

    @PostMapping("/{notificationEventId}/delivery-attempts/telegram-test")
    public String recordRealTelegramTestDelivery(
            @PathVariable UUID notificationEventId
    ) {
        notificationDeliveryAttemptService.recordRealTelegramTestDelivery(notificationEventId);

        return "redirect:/notifications/" + notificationEventId;
    }
}