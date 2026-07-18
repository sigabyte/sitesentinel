package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping(
        "/websites/{websiteId}/monitoring-runs/{runId}"
                + "/report/dispatch-attempts"
)
public class MonitoringRunReportDispatchController {

    private final ManualMonitoringRunReportRetryService
            manualRetryService;

    private final MonitoringRunService
            monitoringRunService;

    public MonitoringRunReportDispatchController(
            ManualMonitoringRunReportRetryService
                    manualRetryService,
            MonitoringRunService monitoringRunService
    ) {
        this.manualRetryService =
                manualRetryService;

        this.monitoringRunService =
                monitoringRunService;
    }

    @PostMapping("/{attemptId}/retry")
    public String retryFailedAttempt(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            @PathVariable UUID attemptId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            monitoringRunService.findByIdAndWebsiteId(
                    runId,
                    websiteId
            );

            TelegramDocumentDeliveryResult result =
                    manualRetryService.retryFailedAttempt(
                            runId,
                            attemptId
                    );

            applyRetryResult(
                    result,
                    redirectAttributes
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "reportDispatchStatus",
                    "FAILURE"
            );

            redirectAttributes.addFlashAttribute(
                    "reportDispatchMessage",
                    "Telegram PDF report retry was not started. "
                            + "The dispatch attempt may not be retryable "
                            + "or may not belong to this monitoring run."
            );
        }

        return reportRedirect(
                websiteId,
                runId
        );
    }

    private void applyRetryResult(
            TelegramDocumentDeliveryResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result == null) {
            redirectAttributes.addFlashAttribute(
                    "reportDispatchStatus",
                    "FAILURE"
            );

            redirectAttributes.addFlashAttribute(
                    "reportDispatchMessage",
                    "Telegram PDF report retry did not return "
                            + "a delivery result."
            );

            return;
        }

        TelegramDocumentDeliveryStatus status =
                result.getStatus();

        if (status
                == TelegramDocumentDeliveryStatus.SENT) {

            redirectAttributes.addFlashAttribute(
                    "reportDispatchStatus",
                    "SUCCESS"
            );

            redirectAttributes.addFlashAttribute(
                    "reportDispatchMessage",
                    "Telegram PDF report was sent successfully."
            );

            return;
        }

        if (status
                == TelegramDocumentDeliveryStatus.DISABLED) {

            redirectAttributes.addFlashAttribute(
                    "reportDispatchStatus",
                    "UNAVAILABLE"
            );

            redirectAttributes.addFlashAttribute(
                    "reportDispatchMessage",
                    "Telegram PDF report retry was not started "
                            + "because Telegram delivery is disabled."
            );

            return;
        }

        if (status
                == TelegramDocumentDeliveryStatus
                .CONFIGURATION_MISSING) {

            redirectAttributes.addFlashAttribute(
                    "reportDispatchStatus",
                    "UNAVAILABLE"
            );

            redirectAttributes.addFlashAttribute(
                    "reportDispatchMessage",
                    "Telegram PDF report retry was not started "
                            + "because Telegram configuration "
                            + "is incomplete."
            );

            return;
        }

        redirectAttributes.addFlashAttribute(
                "reportDispatchStatus",
                "FAILURE"
        );

        redirectAttributes.addFlashAttribute(
                "reportDispatchMessage",
                "Telegram PDF report retry was attempted, "
                        + "but delivery was not successful. "
                        + "Review the dispatch history for details."
        );
    }

    private String reportRedirect(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        return "redirect:/websites/"
                + websiteId
                + "/monitoring-runs/"
                + monitoringRunId
                + "/report";
    }
}