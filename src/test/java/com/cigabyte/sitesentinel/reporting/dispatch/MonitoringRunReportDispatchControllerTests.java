package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MonitoringRunReportDispatchControllerTests {

    private ManualMonitoringRunReportRetryService
            manualRetryService;

    private MonitoringRunService
            monitoringRunService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        manualRetryService =
                mock(
                        ManualMonitoringRunReportRetryService.class
                );

        monitoringRunService =
                mock(
                        MonitoringRunService.class
                );

        MonitoringRunReportDispatchController controller =
                new MonitoringRunReportDispatchController(
                        manualRetryService,
                        monitoringRunService
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .build();
    }

    @Test
    void successfulRetryRedirectsWithSuccessFlash()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        when(
                monitoringRunService.findByIdAndWebsiteId(
                        monitoringRunId,
                        websiteId
                )
        ).thenReturn(
                mock(MonitoringRun.class)
        );

        when(
                manualRetryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                )
        ).thenReturn(
                TelegramDocumentDeliveryResult.success(
                        2468L,
                        "Telegram document was sent successfully.",
                        "Telegram Bot API accepted the document."
                )
        );

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/dispatch-attempts"
                                        + "/{attemptId}/retry",
                                websiteId,
                                monitoringRunId,
                                failedAttemptId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                reportUrl(
                                        websiteId,
                                        monitoringRunId
                                )
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchStatus",
                                "SUCCESS"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchMessage",
                                "Telegram PDF report was sent successfully."
                        )
                );

        InOrder executionOrder =
                inOrder(
                        monitoringRunService,
                        manualRetryService
                );

        executionOrder.verify(
                monitoringRunService
        ).findByIdAndWebsiteId(
                monitoringRunId,
                websiteId
        );

        executionOrder.verify(
                manualRetryService
        ).retryFailedAttempt(
                monitoringRunId,
                failedAttemptId
        );
    }

    @Test
    void failedDeliveryRedirectsWithSafeFailureFlash()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        prepareOwnedRun(
                websiteId,
                monitoringRunId
        );

        when(
                manualRetryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                )
        ).thenReturn(
                TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Sensitive provider failure detail."
                )
        );

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/dispatch-attempts"
                                        + "/{attemptId}/retry",
                                websiteId,
                                monitoringRunId,
                                failedAttemptId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                reportUrl(
                                        websiteId,
                                        monitoringRunId
                                )
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchStatus",
                                "FAILURE"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchMessage",
                                "Telegram PDF report retry was attempted, "
                                        + "but delivery was not successful. "
                                        + "Review the dispatch history "
                                        + "for details."
                        )
                );
    }

    @Test
    void disabledTelegramRedirectsWithUnavailableFlash()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        prepareOwnedRun(
                websiteId,
                monitoringRunId
        );

        when(
                manualRetryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                )
        ).thenReturn(
                TelegramDocumentDeliveryResult.disabled(
                        "Telegram document delivery is disabled.",
                        "Telegram provider was not called."
                )
        );

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/dispatch-attempts"
                                        + "/{attemptId}/retry",
                                websiteId,
                                monitoringRunId,
                                failedAttemptId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchStatus",
                                "UNAVAILABLE"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchMessage",
                                "Telegram PDF report retry was not started "
                                        + "because Telegram delivery "
                                        + "is disabled."
                        )
                );
    }

    @Test
    void missingConfigurationRedirectsWithUnavailableFlash()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        prepareOwnedRun(
                websiteId,
                monitoringRunId
        );

        when(
                manualRetryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                )
        ).thenReturn(
                TelegramDocumentDeliveryResult
                        .configurationMissing(
                                "Telegram configuration is missing.",
                                "Telegram provider was not called."
                        )
        );

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/dispatch-attempts"
                                        + "/{attemptId}/retry",
                                websiteId,
                                monitoringRunId,
                                failedAttemptId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchStatus",
                                "UNAVAILABLE"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchMessage",
                                "Telegram PDF report retry was not started "
                                        + "because Telegram configuration "
                                        + "is incomplete."
                        )
                );
    }

    @Test
    void websiteOwnershipFailureStopsBeforeRetryService()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        when(
                monitoringRunService.findByIdAndWebsiteId(
                        monitoringRunId,
                        websiteId
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "Sensitive ownership detail."
                )
        );

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/dispatch-attempts"
                                        + "/{attemptId}/retry",
                                websiteId,
                                monitoringRunId,
                                failedAttemptId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                reportUrl(
                                        websiteId,
                                        monitoringRunId
                                )
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchStatus",
                                "FAILURE"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchMessage",
                                "Telegram PDF report retry was not started. "
                                        + "The dispatch attempt may not be "
                                        + "retryable or may not belong to "
                                        + "this monitoring run."
                        )
                );

        verifyNoInteractions(
                manualRetryService
        );
    }

    @Test
    void retryExceptionRedirectsWithoutExposingTechnicalDetail()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID failedAttemptId =
                UUID.randomUUID();

        prepareOwnedRun(
                websiteId,
                monitoringRunId
        );

        when(
                manualRetryService.retryFailedAttempt(
                        monitoringRunId,
                        failedAttemptId
                )
        ).thenThrow(
                new IllegalStateException(
                        "Sensitive internal retry detail."
                )
        );

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/dispatch-attempts"
                                        + "/{attemptId}/retry",
                                websiteId,
                                monitoringRunId,
                                failedAttemptId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchStatus",
                                "FAILURE"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "reportDispatchMessage",
                                "Telegram PDF report retry was not started. "
                                        + "The dispatch attempt may not be "
                                        + "retryable or may not belong to "
                                        + "this monitoring run."
                        )
                );

        verify(
                manualRetryService
        ).retryFailedAttempt(
                monitoringRunId,
                failedAttemptId
        );
    }

    private void prepareOwnedRun(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        when(
                monitoringRunService.findByIdAndWebsiteId(
                        monitoringRunId,
                        websiteId
                )
        ).thenReturn(
                mock(MonitoringRun.class)
        );
    }

    private String reportUrl(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        return "/websites/"
                + websiteId
                + "/monitoring-runs/"
                + monitoringRunId
                + "/report";
    }
}