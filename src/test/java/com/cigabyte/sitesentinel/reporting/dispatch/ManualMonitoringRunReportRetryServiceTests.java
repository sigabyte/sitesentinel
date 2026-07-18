package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.notification.delivery.TelegramDeliveryProperties;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryService;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryStatus;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactService;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ManualMonitoringRunReportRetryServiceTests {

    private final TelegramDeliveryProperties properties =
            new TelegramDeliveryProperties();

    private final MonitoringRunReportDispatchAttemptService
            dispatchAttemptService =
            mock(
                    MonitoringRunReportDispatchAttemptService.class
            );

    private final MonitoringRunPdfArtifactService
            pdfArtifactService =
            mock(
                    MonitoringRunPdfArtifactService.class
            );

    private final TelegramDocumentDeliveryService
            documentDeliveryService =
            mock(
                    TelegramDocumentDeliveryService.class
            );

    private final ManualMonitoringRunReportRetryService
            retryService =
            new ManualMonitoringRunReportRetryService(
                    properties,
                    dispatchAttemptService,
                    pdfArtifactService,
                    documentDeliveryService
            );

    @Test
    void retryFailedAttemptShortCircuitsWhenTelegramProviderIsDisabled() {
        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempt(
                        UUID.randomUUID()
                );

        when(
                dispatchAttemptService.findAttempt(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                failedAttempt
        );

        TelegramDocumentDeliveryResult result =
                retryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.DISABLED,
                result.getStatus()
        );

        assertFalse(
                result.isDeliveryAttempted()
        );

        verify(
                dispatchAttemptService
        ).findAttempt(
                failedAttemptId,
                monitoringRunId
        );

        verify(
                dispatchAttemptService,
                never()
        ).startManualRetry(
                any(),
                any()
        );

        verifyNoInteractions(
                pdfArtifactService,
                documentDeliveryService
        );
    }

    @Test
    void retryFailedAttemptShortCircuitsWhenConfigurationIsMissing() {
        properties.setEnabled(
                true
        );

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempt(
                        UUID.randomUUID()
                );

        when(
                dispatchAttemptService.findAttempt(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                failedAttempt
        );

        TelegramDocumentDeliveryResult result =
                retryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                );

        assertEquals(
                TelegramDocumentDeliveryStatus
                        .CONFIGURATION_MISSING,
                result.getStatus()
        );

        assertFalse(
                result.isDeliveryAttempted()
        );

        verify(
                dispatchAttemptService
        ).findAttempt(
                failedAttemptId,
                monitoringRunId
        );

        verify(
                dispatchAttemptService,
                never()
        ).startManualRetry(
                any(),
                any()
        );

        verifyNoInteractions(
                pdfArtifactService,
                documentDeliveryService
        );
    }

    @Test
    void retryFailedAttemptSendsSameArtifactAndPersistsSentResult() {
        configureReadyProperties();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        UUID retryAttemptId =
                UUID.randomUUID();

        byte[] artifactBytes =
                "%PDF-controlled-manual-retry"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempt(
                        artifactId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        artifactBytes
                );

        MonitoringRunReportDispatchAttempt retryAttempt =
                retryAttempt(
                        retryAttemptId,
                        2
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
                dispatchAttemptService.findAttempt(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                failedAttempt
        );

        when(
                pdfArtifactService
                        .findByIdAndMonitoringRunId(
                                artifactId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        when(
                dispatchAttemptService.startManualRetry(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                retryAttempt
        );

        when(
                documentDeliveryService.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        artifactBytes,
                        expectedCaption(
                                monitoringRunId,
                                2
                        )
                )
        ).thenReturn(
                deliveryResult
        );

        TelegramDocumentDeliveryResult actualResult =
                retryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                );

        assertSame(
                deliveryResult,
                actualResult
        );

        assertTrue(
                actualResult.isSuccessful()
        );

        InOrder executionOrder =
                inOrder(
                        dispatchAttemptService,
                        pdfArtifactService,
                        documentDeliveryService
                );

        executionOrder.verify(
                dispatchAttemptService
        ).findAttempt(
                failedAttemptId,
                monitoringRunId
        );

        executionOrder.verify(
                pdfArtifactService
        ).findByIdAndMonitoringRunId(
                artifactId,
                monitoringRunId
        );

        executionOrder.verify(
                pdfArtifactService
        ).validateIntegrity(
                artifact
        );

        executionOrder.verify(
                dispatchAttemptService
        ).startManualRetry(
                failedAttemptId,
                monitoringRunId
        );

        executionOrder.verify(
                documentDeliveryService
        ).deliver(
                "monitoring-report.pdf",
                "application/pdf",
                artifactBytes,
                expectedCaption(
                        monitoringRunId,
                        2
                )
        );

        executionOrder.verify(
                dispatchAttemptService
        ).markSent(
                retryAttemptId,
                monitoringRunId,
                2468L,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );

        verify(
                dispatchAttemptService,
                never()
        ).markFailed(
                any(),
                any(),
                anyString(),
                anyString()
        );
    }

    @Test
    void retryFailedAttemptPersistsFailedDeliveryResult() {
        configureReadyProperties();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        UUID retryAttemptId =
                UUID.randomUUID();

        byte[] artifactBytes =
                "%PDF-controlled-manual-failure"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempt(
                        artifactId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        artifactBytes
                );

        MonitoringRunReportDispatchAttempt retryAttempt =
                retryAttempt(
                        retryAttemptId,
                        2
                );

        TelegramDocumentDeliveryResult deliveryResult =
                TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Telegram Bot API returned HTTP status=429."
                );

        prepareRetry(
                monitoringRunId,
                failedAttemptId,
                artifactId,
                failedAttempt,
                artifact,
                retryAttempt
        );

        when(
                documentDeliveryService.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        artifactBytes,
                        expectedCaption(
                                monitoringRunId,
                                2
                        )
                )
        ).thenReturn(
                deliveryResult
        );

        TelegramDocumentDeliveryResult actualResult =
                retryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
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
                retryAttemptId,
                monitoringRunId,
                deliveryResult.getResultMessage(),
                deliveryResult.getTechnicalDetail()
        );

        verify(
                dispatchAttemptService,
                never()
        ).markSent(
                any(),
                any(),
                any(),
                anyString(),
                anyString()
        );
    }

    @Test
    void retryFailedAttemptConvertsUnexpectedDeliveryExceptionSafely() {
        configureReadyProperties();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        UUID retryAttemptId =
                UUID.randomUUID();

        byte[] artifactBytes =
                "%PDF-controlled-manual-exception"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempt(
                        artifactId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        artifactBytes
                );

        MonitoringRunReportDispatchAttempt retryAttempt =
                retryAttempt(
                        retryAttemptId,
                        2
                );

        prepareRetry(
                monitoringRunId,
                failedAttemptId,
                artifactId,
                failedAttempt,
                artifact,
                retryAttempt
        );

        when(
                documentDeliveryService.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        artifactBytes,
                        expectedCaption(
                                monitoringRunId,
                                2
                        )
                )
        ).thenThrow(
                new IllegalStateException(
                        "Sensitive controlled provider detail."
                )
        );

        TelegramDocumentDeliveryResult result =
                retryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
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
                retryAttemptId,
                monitoringRunId,
                result.getResultMessage(),
                result.getTechnicalDetail()
        );
    }

    @Test
    void retryFailedAttemptStopsBeforeRetryCreationWhenArtifactIntegrityFails() {
        configureReadyProperties();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempt(
                        artifactId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        "%PDF-corrupted-artifact"
                                .getBytes(
                                        StandardCharsets.US_ASCII
                                )
                );

        when(
                dispatchAttemptService.findAttempt(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                failedAttempt
        );

        when(
                pdfArtifactService
                        .findByIdAndMonitoringRunId(
                                artifactId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        doThrow(
                new IllegalArgumentException(
                        "Controlled artifact integrity failure."
                )
        ).when(
                pdfArtifactService
        ).validateIntegrity(
                artifact
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> retryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                )
        );

        verify(
                dispatchAttemptService,
                never()
        ).startManualRetry(
                any(),
                any()
        );

        verifyNoInteractions(
                documentDeliveryService
        );
    }

    @Test
    void retryCreationFailureStopsBeforeTelegramDelivery() {
        configureReadyProperties();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempt(
                        artifactId
                );

        MonitoringRunPdfArtifact artifact =
                artifact(
                        artifactId,
                        monitoringRunId,
                        "%PDF-controlled-retry-rejection"
                                .getBytes(
                                        StandardCharsets.US_ASCII
                                )
                );

        when(
                dispatchAttemptService.findAttempt(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                failedAttempt
        );

        when(
                pdfArtifactService
                        .findByIdAndMonitoringRunId(
                                artifactId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        when(
                dispatchAttemptService.startManualRetry(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenThrow(
                new IllegalStateException(
                        "Only the latest failed attempt can be retried."
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> retryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                )
        );

        verify(
                pdfArtifactService
        ).validateIntegrity(
                artifact
        );

        verifyNoInteractions(
                documentDeliveryService
        );
    }

    private void prepareRetry(
            UUID monitoringRunId,
            UUID failedAttemptId,
            UUID artifactId,
            MonitoringRunReportDispatchAttempt failedAttempt,
            MonitoringRunPdfArtifact artifact,
            MonitoringRunReportDispatchAttempt retryAttempt
    ) {
        when(
                dispatchAttemptService.findAttempt(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                failedAttempt
        );

        when(
                pdfArtifactService
                        .findByIdAndMonitoringRunId(
                                artifactId,
                                monitoringRunId
                        )
        ).thenReturn(
                artifact
        );

        when(
                dispatchAttemptService.startManualRetry(
                        failedAttemptId,
                        monitoringRunId
                )
        ).thenReturn(
                retryAttempt
        );
    }

    private void configureReadyProperties() {
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

    private MonitoringRunReportDispatchAttempt failedAttempt(
            UUID artifactId
    ) {
        MonitoringRunReportDispatchAttempt attempt =
                mock(
                        MonitoringRunReportDispatchAttempt.class
                );

        when(
                attempt.getPdfArtifactId()
        ).thenReturn(
                artifactId
        );

        return attempt;
    }

    private MonitoringRunReportDispatchAttempt retryAttempt(
            UUID retryAttemptId,
            int attemptNumber
    ) {
        MonitoringRunReportDispatchAttempt attempt =
                mock(
                        MonitoringRunReportDispatchAttempt.class
                );

        when(
                attempt.getId()
        ).thenReturn(
                retryAttemptId
        );

        when(
                attempt.getAttemptNumber()
        ).thenReturn(
                attemptNumber
        );

        return attempt;
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

    private String expectedCaption(
            UUID monitoringRunId,
            int attemptNumber
    ) {
        return "SiteSentinel monitoring run report"
                + "\nMonitoring run ID: "
                + monitoringRunId
                + "\nReport version: v1"
                + "\nDispatch type: manual retry"
                + "\nAttempt number: "
                + attemptNumber;
    }
}