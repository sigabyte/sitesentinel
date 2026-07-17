package com.cigabyte.sitesentinel.reporting.pdf;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitoringRunPdfArtifactRepository
        extends JpaRepository<MonitoringRunPdfArtifact, UUID> {

    Optional<MonitoringRunPdfArtifact>
    findByMonitoringRunIdAndReportVersion(
            UUID monitoringRunId,
            String reportVersion
    );

    boolean existsByMonitoringRunIdAndReportVersion(
            UUID monitoringRunId,
            String reportVersion
    );

    List<MonitoringRunPdfArtifact>
    findByMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
            UUID monitoringRunId
    );

    Optional<MonitoringRunPdfArtifact>
    findByIdAndMonitoringRunId(
            UUID id,
            UUID monitoringRunId
    );

    long countByMonitoringRunId(
            UUID monitoringRunId
    );
}