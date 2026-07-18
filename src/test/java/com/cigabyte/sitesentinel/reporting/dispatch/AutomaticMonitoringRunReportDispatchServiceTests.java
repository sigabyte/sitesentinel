package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDeliveryProperties;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryService;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryStatus;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactResolutionService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AutomaticMonitoringRunReportDispatchServiceTests {

    private final TelegramDeliveryProperties properties =
            new TelegramDeliveryProperties();

    private final MonitoringRunPdfArtifactResolutionService
            artifactResolutionService =
            mock(
                    MonitoringRunPdfArtifactResolutionService.class
            );

    private final MonitoringRunReportDispatchAttemptService
            dispatchAttemptService =
            mock(
                    MonitoringRunReportDispatchAttemptService.class
            );

    private final TelegramDocumentDeliveryService
            documentDeliveryService =
            mock(
                    TelegramDocumentDeliveryService.class
            );

    private final AutomaticMonitoringRunReportDispatchService
            dispatchService =
            new AutomaticMonitoringRunReportDispatchService(
                    properties,
                    artifactResolutionService,
                    dispatchAttemptService,
                    documentDeliveryService
            );

    @Test
    void dispatchCompletedRunShortCircuitsWhenAutomaticDispatchIsDisabled() {
        properties.setAutomaticPdfDispatchEnabled(
                false
        );

        TelegramDocumentDeliveryResult result =
                dispatchService.dispatchCompletedRun(
                        completedMonitoringRun()
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.DISABLED,
                result.getStatus()
        );

        assertFalse(
                result.isDeliveryAttempted()
        );

        verifyNoInteractions(
                artifactResolutionService,
                dispatchAttemptService,
                documentDeliveryService
        );
    }

    @Test
    void dispatchCompletedRunShortCircuitsWhenTelegramProviderIsDisabled() {
        configureReadyProperties();

        properties.setEnabled(false);

        TelegramDocumentDeliveryResult result =
                dispatchService.dispatchCompletedRun(
                        completedMonitoringRun()
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.DISABLED,
                result.getStatus()
        );

        assertFalse(
                result.isDeliveryAttempted()
        );

        verifyNoInteractions(
                artifactResolutionService,
                dispatchAttemptService,
                documentDeliveryService
        );
    }

    @Test
    void dispatchCompletedRunShortCircuitsWhenConfigurationIsMissing() {
        configureReadyProperties();

        properties.setBotToken("");

        TelegramDocumentDeliveryResult result =
                dispatchService.dispatchCompletedRun(
                        completedMonitoringRun()
                );

        assertEquals(
                TelegramDocumentDeliveryStatus
                        .CONFIGURATION_MISSING,
                result.getStatus()
        );

        assertFalse(
                result.isDeliveryAttempted()
        );

        verifyNoInteractions(
                artifactResolutionService,
                dispatchAttemptService,
                documentDeliveryService
        );
    }

    @Test
    void dispatchCompletedRunRejectsNonCompletedRunBeforeSideEffects() {
        configureReadyProperties();

        MonitoringRun monitoringRun =
                mock(
                        MonitoringRun.class
                );

        when(
                monitoringRun.getId()
        ).thenReturn(
                UUID.randomUUID()
        );

        when(
                monitoringRun.getWebsiteId()
        ).thenReturn(
                UUID.randomUUID()
        );

        when(
                monitoringRun.getStatus()
        ).thenReturn(
                MonitoringRunStatus.FAILED
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> dispatchService.dispatchCompletedRun(
                        monitoringRun
                )
        );

        verifyNoInteractions(
                documentDeliveryService
        );
    }

    @Test
    void dispatchCompletedRunPersistsSentResultAfterSuccessfulDelivery() {
        configureReadyProperties();

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        UUID attemptId =
                UUID.randomUUID();

        byte[] artifactBytes =
                "%PDF-controlled-dispatch-test"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        MonitoringRun monitoringRun =
                completedMonitoringRun(
                        websiteId,
                        monitoringRunId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        artifactBytes
                );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                pendingAttempt(
                        attemptId
                );

        TelegramDocumentDeliveryResult deliveryResult =
                TelegramDocumentDeliveryResult.success(
                        2468L,
                        "Telegram document was sent successfully.",
                        "Telegram Bot API accepted the "
                                + "sendDocument request. "
                                + "HTTP status=200."
                );

        when(
                artifactResolutionService
                        .resolveCurrentVersion(
                                websiteId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        when(
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRunId,
                                artifactId
                        )
        ).thenReturn(
                pendingAttempt
        );

        when(
                documentDeliveryService.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        artifactBytes,
                        expectedCaption(
                                monitoringRunId
                        )
                )
        ).thenReturn(
                deliveryResult
        );

        TelegramDocumentDeliveryResult actualResult =
                dispatchService.dispatchCompletedRun(
                        monitoringRun
                );

        assertSame(
                deliveryResult,
                actualResult
        );

        assertTrue(
                actualResult.isSuccessful()
        );

        verify(
                dispatchAttemptService
        ).markSent(
                attemptId,
                monitoringRunId,
                2468L,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );

        verify(
                dispatchAttemptService,
                never()
        ).markFailed(
                attemptId,
                monitoringRunId,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );
    }

    @Test
    void dispatchCompletedRunPersistsFailedDeliveryResult() {
        configureReadyProperties();

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        UUID attemptId =
                UUID.randomUUID();

        byte[] artifactBytes =
                "%PDF-controlled-failure-test"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        MonitoringRun monitoringRun =
                completedMonitoringRun(
                        websiteId,
                        monitoringRunId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        artifactBytes
                );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                pendingAttempt(
                        attemptId
                );

        TelegramDocumentDeliveryResult deliveryResult =
                TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Telegram Bot API returned HTTP status=429."
                );

        when(
                artifactResolutionService
                        .resolveCurrentVersion(
                                websiteId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        when(
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRunId,
                                artifactId
                        )
        ).thenReturn(
                pendingAttempt
        );

        when(
                documentDeliveryService.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        artifactBytes,
                        expectedCaption(
                                monitoringRunId
                        )
                )
        ).thenReturn(
                deliveryResult
        );

        TelegramDocumentDeliveryResult actualResult =
                dispatchService.dispatchCompletedRun(
                        monitoringRun
                );

        assertSame(
                deliveryResult,
                actualResult
        );

        assertEquals(
                TelegramDocumentDeliveryStatus.FAILED,
                actualResult.getStatus()
        );

        verify(
                dispatchAttemptService
        ).markFailed(
                attemptId,
                monitoringRunId,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );

        verify(
                dispatchAttemptService,
                never()
        ).markSent(
                attemptId,
                monitoringRunId,
                null,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );
    }

    @Test
    void dispatchCompletedRunConvertsUnexpectedDeliveryExceptionSafely() {
        configureReadyProperties();

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        UUID attemptId =
                UUID.randomUUID();

        byte[] artifactBytes =
                "%PDF-controlled-exception-test"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        MonitoringRun monitoringRun =
                completedMonitoringRun(
                        websiteId,
                        monitoringRunId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        artifactBytes
                );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                pendingAttempt(
                        attemptId
                );

        when(
                artifactResolutionService
                        .resolveCurrentVersion(
                                websiteId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        when(
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRunId,
                                artifactId
                        )
        ).thenReturn(
                pendingAttempt
        );

        when(
                documentDeliveryService.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        artifactBytes,
                        expectedCaption(
                                monitoringRunId
                        )
                )
        ).thenThrow(
                new IllegalStateException(
                        "Sensitive controlled exception detail."
                )
        );

        TelegramDocumentDeliveryResult result =
                dispatchService.dispatchCompletedRun(
                        monitoringRun
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.FAILED,
                result.getStatus()
        );

        assertTrue(
                result.isDeliveryAttempted()
        );

        assertFalse(
                result.getTechnicalDetail().contains(
                        "Sensitive controlled"
                )
        );

        assertTrue(
                result.getTechnicalDetail().contains(
                        "IllegalStateException"
                )
        );

        verify(
                dispatchAttemptService
        ).markFailed(
                attemptId,
                monitoringRunId,
                result.getResultMessage(),
                result.getTechnicalDetail()
        );
    }

    @Test
    void duplicateAutomaticAttemptStopsBeforeTelegramDelivery() {
        configureReadyProperties();

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        MonitoringRun monitoringRun =
                completedMonitoringRun(
                        websiteId,
                        monitoringRunId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        "%PDF-duplicate-test".getBytes(
                                StandardCharsets.US_ASCII
                        )
                );

        when(
                artifactResolutionService
                        .resolveCurrentVersion(
                                websiteId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        when(
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRunId,
                                artifactId
                        )
        ).thenThrow(
                new IllegalStateException(
                        "Automatic attempt already exists."
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> dispatchService.dispatchCompletedRun(
                        monitoringRun
                )
        );

        verifyNoInteractions(
                documentDeliveryService
        );
    }

    private void configureReadyProperties() {
        properties.setAutomaticPdfDispatchEnabled(
                true
        );

        properties.setEnabled(
                true
        );

        properties.setBotToken(
                "test-bot-token"
        );

        properties.setChatId(
                "test-chat-id"
        );

        properties.setApiBaseUrl(
                "https://api.telegram.org"
        );
    }

    private MonitoringRun completedMonitoringRun() {
        return completedMonitoringRun(
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private MonitoringRun completedMonitoringRun(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        MonitoringRun monitoringRun =
                mock(
                        MonitoringRun.class
                );

        when(
                monitoringRun.getWebsiteId()
        ).thenReturn(
                websiteId
        );

        when(
                monitoringRun.getId()
        ).thenReturn(
                monitoringRunId
        );

        when(
                monitoringRun.getStatus()
        ).thenReturn(
                MonitoringRunStatus.COMPLETED
        );

        return monitoringRun;
    }

    private MonitoringRunPdfArtifact artifact(
            UUID artifactId,
            UUID monitoringRunId,
            byte[] artifactBytes
    ) {
        MonitoringRunPdfArtifact artifact =
                mock(
                        MonitoringRunPdfArtifact.class
                );

        when(
                artifact.getId()
        ).thenReturn(
                artifactId
        );

        when(
                artifact.getMonitoringRunId()
        ).thenReturn(
                monitoringRunId
        );

        when(
                artifact.getFileName()
        ).thenReturn(
                "monitoring-report.pdf"
        );

        when(
                artifact.getContentType()
        ).thenReturn(
                "application/pdf"
        );

        when(
                artifact.getArtifactBytes()
        ).thenReturn(
                artifactBytes
        );

        when(
                artifact.getReportVersion()
        ).thenReturn(
                "v1"
        );

        return artifact;
    }

    private MonitoringRunReportDispatchAttempt pendingAttempt(
            UUID attemptId
    ) {
        MonitoringRunReportDispatchAttempt attempt =
                mock(
                        MonitoringRunReportDispatchAttempt.class
                );

        when(
                attempt.getId()
        ).thenReturn(
                attemptId
        );

        return attempt;
    }

    private String expectedCaption(
            UUID monitoringRunId
    ) {
        return "SiteSentinel monitoring run report"
                + "\nMonitoring run ID: "
                + monitoringRunId
                + "\nReport version: v1";
    }
}