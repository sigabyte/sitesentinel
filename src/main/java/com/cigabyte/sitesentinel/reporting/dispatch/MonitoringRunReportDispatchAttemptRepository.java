package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitoringRunReportDispatchAttemptRepository
        extends JpaRepository<
        MonitoringRunReportDispatchAttempt,
        UUID
        > {

    Optional<MonitoringRunReportDispatchAttempt>
    findByIdAndMonitoringRunId(
            UUID id,
            UUID monitoringRunId
    );

    List<MonitoringRunReportDispatchAttempt>
    findByMonitoringRunIdOrderByAttemptedAtDescCreatedAtDesc(
            UUID monitoringRunId
    );

    List<MonitoringRunReportDispatchAttempt>
    findByPdfArtifactIdAndChannelOrderByAttemptNumberAsc(
            UUID pdfArtifactId,
            NotificationDeliveryChannel channel
    );

    Optional<MonitoringRunReportDispatchAttempt>
    findFirstByPdfArtifactIdAndChannelOrderByAttemptNumberDescCreatedAtDesc(
            UUID pdfArtifactId,
            NotificationDeliveryChannel channel
    );

    boolean
    existsByMonitoringRunIdAndPdfArtifactIdAndChannelAndDispatchType(
            UUID monitoringRunId,
            UUID pdfArtifactId,
            NotificationDeliveryChannel channel,
            MonitoringRunReportDispatchType dispatchType
    );

    long countByMonitoringRunId(
            UUID monitoringRunId
    );
}