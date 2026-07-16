package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications/delivery/settings")
public class NotificationDeliverySettingsController {

    private final TelegramDeliveryReadinessService readinessService;

    private final NotificationDeliveryProviderCheckService providerCheckService;

    private final TelegramProviderHealthCheckService healthCheckService;

    public NotificationDeliverySettingsController(
            TelegramDeliveryReadinessService readinessService,
            NotificationDeliveryProviderCheckService providerCheckService,
            TelegramProviderHealthCheckService healthCheckService
    ) {
        this.readinessService = readinessService;
        this.providerCheckService = providerCheckService;
        this.healthCheckService = healthCheckService;
    }

    @GetMapping
    public String settings(Model model) {
        NotificationDeliveryProviderStatus providerStatus =
                readinessService.evaluate();

        NotificationDeliveryProviderCheck latestCheck =
                providerCheckService
                        .findLatest(NotificationDeliveryChannel.TELEGRAM)
                        .orElse(null);

        NotificationDeliverySettingsView settingsView =
                new NotificationDeliverySettingsView(
                        providerStatus,
                        latestCheck,
                        providerCheckService.findRecent(
                                NotificationDeliveryChannel.TELEGRAM
                        )
                );

        model.addAttribute("settingsView", settingsView);

        return "notifications/delivery-settings";
    }

    @PostMapping("/telegram/health-check")
    public String runTelegramHealthCheck(
            RedirectAttributes redirectAttributes
    ) {
        TelegramProviderHealthCheckResult result =
                healthCheckService.performHealthCheck();

        redirectAttributes.addFlashAttribute(
                "healthCheckStatus",
                result.getStatus()
        );

        redirectAttributes.addFlashAttribute(
                "healthCheckMessage",
                result.getMessage()
        );

        if (result.getHttpStatusCode() != null) {
            redirectAttributes.addFlashAttribute(
                    "healthCheckHttpStatusCode",
                    result.getHttpStatusCode()
            );
        }

        return "redirect:/notifications/delivery/settings";
    }
}