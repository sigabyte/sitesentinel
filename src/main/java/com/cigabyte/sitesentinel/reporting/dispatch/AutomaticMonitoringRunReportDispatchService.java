package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDeliveryProperties;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryService;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactResolutionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AutomaticMonitoringRunReportDispatchService {

    private final TelegramDeliveryProperties
            telegramDeliveryProperties;

    private final MonitoringRunPdfArtifactResolutionService
            artifactResolutionService;

    private final MonitoringRunReportDispatchAttemptService
            dispatchAttemptService;

    private final TelegramDocumentDeliveryService
            documentDeliveryService;

    public AutomaticMonitoringRunReportDispatchService(
            TelegramDeliveryProperties telegramDeliveryProperties,
            MonitoringRunPdfArtifactResolutionService
                    artifactResolutionService,
            MonitoringRunReportDispatchAttemptService
                    dispatchAttemptService,
            TelegramDocumentDeliveryService documentDeliveryService
    ) {
        this.telegramDeliveryProperties =
                telegramDeliveryProperties;

        this.artifactResolutionService =
                artifactResolutionService;

        this.dispatchAttemptService =
                dispatchAttemptService;

        this.documentDeliveryService =
                documentDeliveryService;
    }

    public TelegramDocumentDeliveryResult dispatchCompletedRun(
            MonitoringRun monitoringRun
    ) {
        MonitoringRun requiredMonitoringRun =
                requireCompletedRun(
                        monitoringRun
                );

        if (!telegramDeliveryProperties
                .isAutomaticPdfDispatchEnabled()) {

            return TelegramDocumentDeliveryResult.disabled(
                    "Automatic Telegram PDF dispatch is disabled.",
                    "PDF artifact resolution, dispatch-attempt "
                            + "persistence, and Telegram Bot API "
                            + "delivery were not performed."
            );
        }

        if (!telegramDeliveryProperties.isEnabled()) {
            return TelegramDocumentDeliveryResult.disabled(
                    "Telegram document delivery is disabled.",
                    "Automatic PDF dispatch was enabled, but "
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
                            "PDF artifact resolution and dispatch-attempt "
                                    + "persistence were not performed "
                                    + "because Telegram configuration "
                                    + "is incomplete."
                    );
        }

        UUID monitoringRunId =
                requiredMonitoringRun.getId();

        MonitoringRunPdfArtifact artifact =
                artifactResolutionService
                        .resolveCurrentVersion(
                                requiredMonitoringRun.getWebsiteId(),
                                monitoringRunId
                        );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRunId,
                                artifact.getId()
                        );

        TelegramDocumentDeliveryResult deliveryResult =
                deliverSafely(
                        artifact,
                        monitoringRunId
                );

        if (deliveryResult.isSuccessful()) {
            dispatchAttemptService.markSent(
                    pendingAttempt.getId(),
                    monitoringRunId,
                    deliveryResult.getTelegramMessageId(),
                    deliveryResult.getResultMessage(),
                    deliveryResult.getTechnicalDetail()
            );

            return deliveryResult;
        }

        dispatchAttemptService.markFailed(
                pendingAttempt.getId(),
                monitoringRunId,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );

        return deliveryResult;
    }

    private TelegramDocumentDeliveryResult deliverSafely(
            MonitoringRunPdfArtifact artifact,
            UUID monitoringRunId
    ) {
        try {
            TelegramDocumentDeliveryResult result =
                    documentDeliveryService.deliver(
                            artifact.getFileName(),
                            artifact.getContentType(),
                            artifact.getArtifactBytes(),
                            buildCaption(
                                    monitoringRunId,
                                    artifact
                            )
                    );

            if (result == null) {
                return TelegramDocumentDeliveryResult.failure(
                        "Automatic Telegram report dispatch failed.",
                        "Telegram document delivery service "
                                + "returned no result."
                );
            }

            return result;

        } catch (RuntimeException exception) {
            return TelegramDocumentDeliveryResult.failure(
                    "Automatic Telegram report dispatch failed.",
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
            MonitoringRunPdfArtifact artifact
    ) {
        return "SiteSentinel monitoring run report"
                + "\nMonitoring run ID: "
                + monitoringRunId
                + "\nReport version: "
                + artifact.getReportVersion();
    }

    private MonitoringRun requireCompletedRun(
            MonitoringRun monitoringRun
    ) {
        if (monitoringRun == null) {
            throw new IllegalArgumentException(
                    "Monitoring run is required."
            );
        }

        if (monitoringRun.getId() == null) {
            throw new IllegalArgumentException(
                    "Monitoring run ID is required."
            );
        }

        if (monitoringRun.getWebsiteId() == null) {
            throw new IllegalArgumentException(
                    "Monitoring run website ID is required."
            );
        }

        if (monitoringRun.getStatus()
                != MonitoringRunStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Automatic Telegram PDF dispatch "
                            + "requires a completed monitoring run."
            );
        }

        return monitoringRun;
    }
}