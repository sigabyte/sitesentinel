package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryStatus;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactRepository;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfVersion;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MonitoringRunReportDispatchIntegrationTests {

    private static final String BOT_TOKEN =
            "controlled-integration-bot-token";

    private static final String CHAT_ID =
            "controlled-integration-chat-id";

    private static final AtomicReference<StubResponse>
            STUB_RESPONSE =
            new AtomicReference<>();

    private static final List<RecordedRequest>
            RECORDED_REQUESTS =
            new CopyOnWriteArrayList<>();

    private static final HttpServer TELEGRAM_STUB_SERVER =
            startTelegramStubServer();

    private final WebsiteRepository websiteRepository;

    private final MonitoringRunRepository monitoringRunRepository;

    private final MonitoringRunPdfArtifactRepository
            pdfArtifactRepository;

    private final MonitoringRunReportDispatchAttemptRepository
            dispatchAttemptRepository;

    private final AutomaticMonitoringRunReportDispatchService
            automaticDispatchService;

    private final ManualMonitoringRunReportRetryService
            manualRetryService;

    private final List<UUID> createdWebsiteIds =
            new ArrayList<>();

    @Autowired
    MonitoringRunReportDispatchIntegrationTests(
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository,
            MonitoringRunPdfArtifactRepository pdfArtifactRepository,
            MonitoringRunReportDispatchAttemptRepository
                    dispatchAttemptRepository,
            AutomaticMonitoringRunReportDispatchService
                    automaticDispatchService,
            ManualMonitoringRunReportRetryService manualRetryService
    ) {
        this.websiteRepository =
                websiteRepository;

        this.monitoringRunRepository =
                monitoringRunRepository;

        this.pdfArtifactRepository =
                pdfArtifactRepository;

        this.dispatchAttemptRepository =
                dispatchAttemptRepository;

        this.automaticDispatchService =
                automaticDispatchService;

        this.manualRetryService =
                manualRetryService;
    }

    @DynamicPropertySource
    static void telegramProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "sitesentinel.scheduler.enabled",
                () -> "false"
        );

        registry.add(
                "sitesentinel.notification.delivery.telegram.enabled",
                () -> "true"
        );

        registry.add(
                "sitesentinel.notification.delivery.telegram."
                        + "automatic-pdf-dispatch-enabled",
                () -> "true"
        );

        registry.add(
                "sitesentinel.notification.delivery.telegram.bot-token",
                () -> BOT_TOKEN
        );

        registry.add(
                "sitesentinel.notification.delivery.telegram.chat-id",
                () -> CHAT_ID
        );

        registry.add(
                "sitesentinel.notification.delivery.telegram.api-base-url",
                MonitoringRunReportDispatchIntegrationTests
                        ::telegramStubBaseUrl
        );

        registry.add(
                "sitesentinel.notification.delivery.telegram."
                        + "connect-timeout-seconds",
                () -> "2"
        );

        registry.add(
                "sitesentinel.notification.delivery.telegram."
                        + "request-timeout-seconds",
                () -> "5"
        );
    }

    @BeforeEach
    void resetTelegramStub() {
        RECORDED_REQUESTS.clear();

        STUB_RESPONSE.set(
                successfulTelegramResponse(
                        2468L
                )
        );
    }

    @AfterEach
    void deleteCreatedWebsites() {
        for (UUID websiteId : createdWebsiteIds) {
            if (websiteRepository.existsById(
                    websiteId
            )) {
                websiteRepository.deleteById(
                        websiteId
                );
            }
        }

        websiteRepository.flush();
        createdWebsiteIds.clear();
    }

    @AfterAll
    static void stopTelegramStubServer() {
        TELEGRAM_STUB_SERVER.stop(
                0
        );
    }

    @Test
    void automaticDispatchGeneratesPdfSendsMultipartAndPersistsSentAttempt() {
        Website website =
                persistWebsite(
                        "Automatic Dispatch Integration"
                );

        MonitoringRun monitoringRun =
                persistCompletedRun(
                        website
                );

        TelegramDocumentDeliveryResult result =
                automaticDispatchService
                        .dispatchCompletedRun(
                                monitoringRun
                        );

        assertEquals(
                TelegramDocumentDeliveryStatus.SENT,
                result.getStatus()
        );

        assertTrue(
                result.isDeliveryAttempted()
        );

        assertTrue(
                result.isSuccessful()
        );

        assertEquals(
                Long.valueOf(2468L),
                result.getTelegramMessageId()
        );

        MonitoringRunPdfArtifact artifact =
                pdfArtifactRepository
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRun.getId(),
                                MonitoringRunPdfVersion.V1
                                        .getValue()
                        )
                        .orElseThrow();

        assertNotNull(
                artifact.getId()
        );

        assertEquals(
                monitoringRun.getId(),
                artifact.getMonitoringRunId()
        );

        assertEquals(
                1,
                pdfArtifactRepository
                        .countByMonitoringRunId(
                                monitoringRun.getId()
                        )
        );

        List<MonitoringRunReportDispatchAttempt> attempts =
                dispatchAttemptRepository
                        .findByMonitoringRunIdOrderByAttemptedAtDescCreatedAtDesc(
                                monitoringRun.getId()
                        );

        assertEquals(
                1,
                attempts.size()
        );

        MonitoringRunReportDispatchAttempt attempt =
                attempts.get(0);

        assertEquals(
                MonitoringRunReportDispatchType.AUTOMATIC,
                attempt.getDispatchType()
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.SENT,
                attempt.getStatus()
        );

        assertEquals(
                1,
                attempt.getAttemptNumber()
        );

        assertEquals(
                artifact.getId(),
                attempt.getPdfArtifactId()
        );

        assertEquals(
                Long.valueOf(2468L),
                attempt.getTelegramMessageId()
        );

        assertNotNull(
                attempt.getCompletedAt()
        );

        assertEquals(
                1,
                RECORDED_REQUESTS.size()
        );

        RecordedRequest request =
                RECORDED_REQUESTS.get(0);

        assertEquals(
                "POST",
                request.method()
        );

        assertEquals(
                "/bot"
                        + BOT_TOKEN
                        + "/sendDocument",
                request.path()
        );

        assertNotNull(
                request.contentType()
        );

        assertTrue(
                request.contentType().startsWith(
                        "multipart/form-data; boundary="
                )
        );

        String multipartText =
                new String(
                        request.bodyBytes(),
                        StandardCharsets.ISO_8859_1
                );

        assertTrue(
                multipartText.contains(
                        "name=\"chat_id\"\r\n\r\n"
                                + CHAT_ID
                                + "\r\n"
                )
        );

        assertTrue(
                multipartText.contains(
                        "filename=\""
                                + artifact.getFileName()
                                + "\""
                )
        );

        assertTrue(
                multipartText.contains(
                        "Content-Type: application/pdf"
                )
        );

        assertTrue(
                multipartText.contains(
                        "Monitoring run ID: "
                                + monitoringRun.getId()
                )
        );

        assertTrue(
                containsSubsequence(
                        request.bodyBytes(),
                        artifact.getArtifactBytes()
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> automaticDispatchService
                        .dispatchCompletedRun(
                                monitoringRun
                        )
        );

        assertEquals(
                1,
                RECORDED_REQUESTS.size()
        );

        assertEquals(
                1,
                dispatchAttemptRepository
                        .countByMonitoringRunId(
                                monitoringRun.getId()
                        )
        );
    }

    @Test
    void failedAutomaticDispatchCanBeRetriedWithSameArtifact() {
        STUB_RESPONSE.set(
                failedTelegramResponse()
        );

        Website website =
                persistWebsite(
                        "Manual Retry Integration"
                );

        MonitoringRun monitoringRun =
                persistCompletedRun(
                        website
                );

        TelegramDocumentDeliveryResult automaticResult =
                automaticDispatchService
                        .dispatchCompletedRun(
                                monitoringRun
                        );

        assertEquals(
                TelegramDocumentDeliveryStatus.FAILED,
                automaticResult.getStatus()
        );

        MonitoringRunPdfArtifact artifact =
                pdfArtifactRepository
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRun.getId(),
                                MonitoringRunPdfVersion.V1
                                        .getValue()
                        )
                        .orElseThrow();

        List<MonitoringRunReportDispatchAttempt>
                failedAttempts =
                dispatchAttemptRepository
                        .findByPdfArtifactIdAndChannelOrderByAttemptNumberAsc(
                                artifact.getId(),
                                NotificationDeliveryChannel.TELEGRAM
                        );

        assertEquals(
                1,
                failedAttempts.size()
        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                failedAttempts.get(0);

        assertEquals(
                MonitoringRunReportDispatchStatus.FAILED,
                failedAttempt.getStatus()
        );

        assertEquals(
                MonitoringRunReportDispatchType.AUTOMATIC,
                failedAttempt.getDispatchType()
        );

        STUB_RESPONSE.set(
                successfulTelegramResponse(
                        97531L
                )
        );

        TelegramDocumentDeliveryResult retryResult =
                manualRetryService.retryFailedAttempt(
                        monitoringRun.getId(),
                        failedAttempt.getId()
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.SENT,
                retryResult.getStatus()
        );

        assertTrue(
                retryResult.isSuccessful()
        );

        assertEquals(
                Long.valueOf(97531L),
                retryResult.getTelegramMessageId()
        );

        assertEquals(
                1,
                pdfArtifactRepository
                        .countByMonitoringRunId(
                                monitoringRun.getId()
                        )
        );

        List<MonitoringRunReportDispatchAttempt> attempts =
                dispatchAttemptRepository
                        .findByPdfArtifactIdAndChannelOrderByAttemptNumberAsc(
                                artifact.getId(),
                                NotificationDeliveryChannel.TELEGRAM
                        );

        assertEquals(
                2,
                attempts.size()
        );

        MonitoringRunReportDispatchAttempt automaticAttempt =
                attempts.get(0);

        MonitoringRunReportDispatchAttempt manualRetryAttempt =
                attempts.get(1);

        assertEquals(
                MonitoringRunReportDispatchStatus.FAILED,
                automaticAttempt.getStatus()
        );

        assertEquals(
                MonitoringRunReportDispatchType.MANUAL_RETRY,
                manualRetryAttempt.getDispatchType()
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.SENT,
                manualRetryAttempt.getStatus()
        );

        assertEquals(
                2,
                manualRetryAttempt.getAttemptNumber()
        );

        assertEquals(
                automaticAttempt.getId(),
                manualRetryAttempt.getRetryOfAttemptId()
        );

        assertEquals(
                artifact.getId(),
                manualRetryAttempt.getPdfArtifactId()
        );

        assertEquals(
                Long.valueOf(97531L),
                manualRetryAttempt.getTelegramMessageId()
        );

        assertEquals(
                2,
                RECORDED_REQUESTS.size()
        );

        RecordedRequest automaticRequest =
                RECORDED_REQUESTS.get(0);

        RecordedRequest retryRequest =
                RECORDED_REQUESTS.get(1);

        assertTrue(
                containsSubsequence(
                        automaticRequest.bodyBytes(),
                        artifact.getArtifactBytes()
                )
        );

        assertTrue(
                containsSubsequence(
                        retryRequest.bodyBytes(),
                        artifact.getArtifactBytes()
                )
        );

        String retryMultipartText =
                new String(
                        retryRequest.bodyBytes(),
                        StandardCharsets.ISO_8859_1
                );

        assertTrue(
                retryMultipartText.contains(
                        "Dispatch type: manual retry"
                )
        );

        assertTrue(
                retryMultipartText.contains(
                        "Attempt number: 2"
                )
        );
    }

    private Website persistWebsite(
            String name
    ) {
        String uniqueValue =
                UUID.randomUUID().toString();

        Website website =
                websiteRepository.saveAndFlush(
                        new Website(
                                name,
                                "dispatch-integration-"
                                        + uniqueValue
                                        + ".example.test"
                        )
                );

        createdWebsiteIds.add(
                website.getId()
        );

        return website;
    }

    private MonitoringRun persistCompletedRun(
            Website website
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(
                        website.getId()
                );

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return monitoringRunRepository.saveAndFlush(
                monitoringRun
        );
    }

    private static HttpServer startTelegramStubServer() {
        try {
            HttpServer server =
                    HttpServer.create(
                            new InetSocketAddress(
                                    "127.0.0.1",
                                    0
                            ),
                            0
                    );

            server.createContext(
                    "/",
                    MonitoringRunReportDispatchIntegrationTests
                            ::handleTelegramRequest
            );

            server.start();

            return server;

        } catch (IOException exception) {
            throw new ExceptionInInitializerError(
                    exception
            );
        }
    }

    private static void handleTelegramRequest(
            HttpExchange exchange
    ) throws IOException {
        byte[] requestBytes =
                exchange.getRequestBody()
                        .readAllBytes();

        RECORDED_REQUESTS.add(
                new RecordedRequest(
                        exchange.getRequestMethod(),
                        exchange.getRequestURI().getPath(),
                        exchange.getRequestHeaders()
                                .getFirst(
                                        "Content-Type"
                                ),
                        requestBytes
                )
        );

        StubResponse configuredResponse =
                STUB_RESPONSE.get();

        StubResponse response =
                configuredResponse == null
                        ? new StubResponse(
                        500,
                        """
                        {
                          "ok": false,
                          "description": "Stub response missing"
                        }
                        """
                )
                        : configuredResponse;

        byte[] responseBytes =
                response.body()
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );

        exchange.sendResponseHeaders(
                response.statusCode(),
                responseBytes.length
        );

        exchange.getResponseBody()
                .write(
                        responseBytes
                );

        exchange.close();
    }

    private static String telegramStubBaseUrl() {
        return "http://127.0.0.1:"
                + TELEGRAM_STUB_SERVER
                .getAddress()
                .getPort();
    }

    private static StubResponse successfulTelegramResponse(
            long messageId
    ) {
        return new StubResponse(
                200,
                """
                {
                  "ok": true,
                  "result": {
                    "message_id": %d
                  }
                }
                """.formatted(
                        messageId
                )
        );
    }

    private static StubResponse failedTelegramResponse() {
        return new StubResponse(
                429,
                """
                {
                  "ok": false,
                  "error_code": 429,
                  "description": "Controlled rate-limit response"
                }
                """
        );
    }

    private static boolean containsSubsequence(
            byte[] source,
            byte[] target
    ) {
        if (target.length == 0) {
            return true;
        }

        for (int sourceIndex = 0;
             sourceIndex <= source.length - target.length;
             sourceIndex++) {

            boolean matches = true;

            for (int targetIndex = 0;
                 targetIndex < target.length;
                 targetIndex++) {

                if (source[sourceIndex + targetIndex]
                        != target[targetIndex]) {

                    matches = false;
                    break;
                }
            }

            if (matches) {
                return true;
            }
        }

        return false;
    }

    private record StubResponse(
            int statusCode,
            String body
    ) {
    }

    private record RecordedRequest(
            String method,
            String path,
            String contentType,
            byte[] bodyBytes
    ) {
    }
}