package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.notification.delivery.TelegramDeliveryProperties;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryService;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ManualMonitoringRunReportRetryService {

    private final TelegramDeliveryProperties
            telegramDeliveryProperties;

    private final MonitoringRunReportDispatchAttemptService
            dispatchAttemptService;

    private final MonitoringRunPdfArtifactService
            pdfArtifactService;

    private final TelegramDocumentDeliveryService
            documentDeliveryService;

    public ManualMonitoringRunReportRetryService(
            TelegramDeliveryProperties telegramDeliveryProperties,
            MonitoringRunReportDispatchAttemptService
                    dispatchAttemptService,
            MonitoringRunPdfArtifactService pdfArtifactService,
            TelegramDocumentDeliveryService documentDeliveryService
    ) {
        this.telegramDeliveryProperties =
                telegramDeliveryProperties;

        this.dispatchAttemptService =
                dispatchAttemptService;

        this.pdfArtifactService =
                pdfArtifactService;

        this.documentDeliveryService =
                documentDeliveryService;
    }

    public TelegramDocumentDeliveryResult retryFailedAttempt(
            UUID monitoringRunId,
            UUID failedAttemptId
    ) {
        MonitoringRunReportDispatchAttempt failedAttempt =
                dispatchAttemptService.findAttempt(
                        failedAttemptId,
                        monitoringRunId
                );

        if (!telegramDeliveryProperties.isEnabled()) {
            return TelegramDocumentDeliveryResult.disabled(
                    "Telegram document delivery is disabled.",
                    "Manual report retry was not started because "
                            + "the Telegram delivery provider "
                            + "is disabled."
            );
        }

        if (!telegramDeliveryProperties
                .hasRequiredConfiguration()) {

            return TelegramDocumentDeliveryResult
                    .configurationMissing(
                            "Telegram document delivery "
                                    + "configuration is missing.",
                            "Manual report retry was not started "
                                    + "because Telegram configuration "
                                    + "is incomplete."
                    );
        }

        MonitoringRunPdfArtifact artifact =
                pdfArtifactService
                        .findByIdAndMonitoringRunId(
                                failedAttempt.getPdfArtifactId(),
                                monitoringRunId
                        );

        pdfArtifactService.validateIntegrity(
                artifact
        );

        MonitoringRunReportDispatchAttempt retryAttempt =
                dispatchAttemptService.startManualRetry(
                        failedAttemptId,
                        monitoringRunId
                );

        TelegramDocumentDeliveryResult deliveryResult =
                deliverSafely(
                        artifact,
                        monitoringRunId,
                        retryAttempt.getAttemptNumber()
                );

        if (deliveryResult.isSuccessful()) {
            dispatchAttemptService.markSent(
                    retryAttempt.getId(),
                    monitoringRunId,
                    deliveryResult.getTelegramMessageId(),
                    deliveryResult.getResultMessage(),
                    deliveryResult.getTechnicalDetail()
            );

            return deliveryResult;
        }

        dispatchAttemptService.markFailed(
                retryAttempt.getId(),
                monitoringRunId,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );

        return deliveryResult;
    }

    private TelegramDocumentDeliveryResult deliverSafely(
            MonitoringRunPdfArtifact artifact,
            UUID monitoringRunId,
            Integer attemptNumber
    ) {
        try {
            TelegramDocumentDeliveryResult result =
                    documentDeliveryService.deliver(
                            artifact.getFileName(),
                            artifact.getContentType(),
                            artifact.getArtifactBytes(),
                            buildCaption(
                                    monitoringRunId,
                                    artifact,
                                    attemptNumber
                            )
                    );

            if (result == null) {
                return TelegramDocumentDeliveryResult.failure(
                        "Manual Telegram report retry failed.",
                        "Telegram document delivery service "
                                + "returned no result."
                );
            }

            return result;

        } catch (RuntimeException exception) {
            return TelegramDocumentDeliveryResult.failure(
                    "Manual Telegram report retry failed.",
                    "Telegram document delivery service failed "
                            + "before returning a result. "
                            + "Failure type="
                            + exception.getClass().getSimpleName()
                            + "."
            );
        }
    }

    private String buildCaption(
            UUID monitoringRunId,
            MonitoringRunPdfArtifact artifact,
            Integer attemptNumber
    ) {
        return "SiteSentinel monitoring run report"
                + "\nMonitoring run ID: "
                + monitoringRunId
                + "\nReport version: "
                + artifact.getReportVersion()
                + "\nDispatch type: manual retry"
                + "\nAttempt number: "
                + attemptNumber;
    }
}