package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class MonitoringRunReportDispatchAttemptService {

    private final MonitoringRunReportDispatchAttemptRepository
            dispatchAttemptRepository;

    private final MonitoringRunPdfArtifactService
            pdfArtifactService;

    public MonitoringRunReportDispatchAttemptService(
            MonitoringRunReportDispatchAttemptRepository
                    dispatchAttemptRepository,
            MonitoringRunPdfArtifactService pdfArtifactService
    ) {
        this.dispatchAttemptRepository =
                dispatchAttemptRepository;

        this.pdfArtifactService =
                pdfArtifactService;
    }

    @Transactional
    public MonitoringRunReportDispatchAttempt
    startAutomaticAttempt(
            UUID monitoringRunId,
            UUID pdfArtifactId
    ) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        UUID requiredPdfArtifactId =
                requireId(
                        pdfArtifactId,
                        "PDF artifact ID"
                );

        pdfArtifactService.findByIdAndMonitoringRunId(
                requiredPdfArtifactId,
                requiredMonitoringRunId
        );

        boolean automaticAttemptAlreadyExists =
                dispatchAttemptRepository
                        .existsByMonitoringRunIdAndPdfArtifactIdAndChannelAndDispatchType(
                                requiredMonitoringRunId,
                                requiredPdfArtifactId,
                                NotificationDeliveryChannel.TELEGRAM,
                                MonitoringRunReportDispatchType.AUTOMATIC
                        );

        if (automaticAttemptAlreadyExists) {
            throw new IllegalStateException(
                    "An automatic Telegram report dispatch "
                            + "attempt already exists for "
                            + "monitoringRunId="
                            + requiredMonitoringRunId
                            + " and pdfArtifactId="
                            + requiredPdfArtifactId
                            + "."
            );
        }

        MonitoringRunReportDispatchAttempt attempt =
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                requiredMonitoringRunId,
                                requiredPdfArtifactId,
                                nowUtc()
                        );

        return dispatchAttemptRepository.saveAndFlush(
                attempt
        );
    }

    @Transactional
    public MonitoringRunReportDispatchAttempt
    startManualRetry(
            UUID failedAttemptId,
            UUID monitoringRunId
    ) {
        MonitoringRunReportDispatchAttempt previousAttempt =
                findRequiredAttempt(
                        failedAttemptId,
                        monitoringRunId
                );

        if (previousAttempt.getStatus()
                != MonitoringRunReportDispatchStatus.FAILED) {

            throw new IllegalStateException(
                    "Only a failed report dispatch attempt "
                            + "can be retried."
            );
        }

        MonitoringRunReportDispatchAttempt latestAttempt =
                dispatchAttemptRepository
                        .findFirstByPdfArtifactIdAndChannelOrderByAttemptNumberDescCreatedAtDesc(
                                previousAttempt.getPdfArtifactId(),
                                NotificationDeliveryChannel.TELEGRAM
                        )
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Latest Telegram report dispatch "
                                                + "attempt could not be resolved."
                                )
                        );

        if (!previousAttempt.getId().equals(
                latestAttempt.getId()
        )) {
            throw new IllegalStateException(
                    "Only the latest report dispatch attempt "
                            + "for the PDF artifact can be retried."
            );
        }

        Integer previousAttemptNumber =
                previousAttempt.getAttemptNumber();

        if (previousAttemptNumber == null
                || previousAttemptNumber
                == Integer.MAX_VALUE) {

            throw new IllegalStateException(
                    "The next report dispatch attempt number "
                            + "cannot be allocated."
            );
        }

        MonitoringRunReportDispatchAttempt retryAttempt =
                MonitoringRunReportDispatchAttempt
                        .manualRetry(
                                previousAttempt.getMonitoringRunId(),
                                previousAttempt.getPdfArtifactId(),
                                previousAttemptNumber + 1,
                                previousAttempt.getId(),
                                nowUtc()
                        );

        return dispatchAttemptRepository.saveAndFlush(
                retryAttempt
        );
    }

    @Transactional
    public MonitoringRunReportDispatchAttempt markSent(
            UUID attemptId,
            UUID monitoringRunId,
            Long telegramMessageId,
            String resultMessage,
            String technicalDetail
    ) {
        MonitoringRunReportDispatchAttempt attempt =
                findRequiredAttempt(
                        attemptId,
                        monitoringRunId
                );

        attempt.markSent(
                telegramMessageId,
                resultMessage,
                technicalDetail,
                nowUtc()
        );

        return dispatchAttemptRepository.saveAndFlush(
                attempt
        );
    }

    @Transactional
    public MonitoringRunReportDispatchAttempt markFailed(
            UUID attemptId,
            UUID monitoringRunId,
            String resultMessage,
            String technicalDetail
    ) {
        MonitoringRunReportDispatchAttempt attempt =
                findRequiredAttempt(
                        attemptId,
                        monitoringRunId
                );

        attempt.markFailed(
                resultMessage,
                technicalDetail,
                nowUtc()
        );

        return dispatchAttemptRepository.saveAndFlush(
                attempt
        );
    }

    @Transactional(readOnly = true)
    public List<MonitoringRunReportDispatchAttempt>
    findAttemptsForMonitoringRun(
            UUID monitoringRunId
    ) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return dispatchAttemptRepository
                .findByMonitoringRunIdOrderByAttemptedAtDescCreatedAtDesc(
                        requiredMonitoringRunId
                );
    }

    @Transactional(readOnly = true)
    public MonitoringRunReportDispatchAttempt findAttempt(
            UUID attemptId,
            UUID monitoringRunId
    ) {
        return findRequiredAttempt(
                attemptId,
                monitoringRunId
        );
    }

    private MonitoringRunReportDispatchAttempt
    findRequiredAttempt(
            UUID attemptId,
            UUID monitoringRunId
    ) {
        UUID requiredAttemptId =
                requireId(
                        attemptId,
                        "Report dispatch attempt ID"
                );

        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return dispatchAttemptRepository
                .findByIdAndMonitoringRunId(
                        requiredAttemptId,
                        requiredMonitoringRunId
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Monitoring run report dispatch "
                                        + "attempt not found: "
                                        + requiredAttemptId
                        )
                );
    }

    private OffsetDateTime nowUtc() {
        return OffsetDateTime.now(
                ZoneOffset.UTC
        );
    }

    private UUID requireId(
            UUID value,
            String fieldName
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value;
    }
}