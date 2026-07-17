package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonSummary;
import com.cigabyte.sitesentinel.comparison.TrustComparisonSummary;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendation;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportCounts;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportRiskRecommendationView;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportTraceabilitySummary;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportView;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.website.Website;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class PdfBoxMonitoringRunPdfRenderer
        implements MonitoringRunPdfRenderer {

    private static final DateTimeFormatter
            DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static final byte[] PDF_HEADER =
            "%PDF-".getBytes(
                    StandardCharsets.US_ASCII
            );

    @Override
    public MonitoringRunPdfVersion getReportVersion() {
        return MonitoringRunPdfVersion.V1;
    }

    @Override
    public byte[] render(
            MonitoringRunReportView reportView
    ) {
        MonitoringRunReportView requiredReportView =
                Objects.requireNonNull(
                        reportView,
                        "Monitoring run report view is required."
                );

        validateReportBoundary(
                requiredReportView
        );

        try (
                PDDocument document =
                        new PDDocument();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {
            try (
                    MonitoringRunPdfDocumentLayout layout =
                            new MonitoringRunPdfDocumentLayout(
                                    document
                            )
            ) {
                writeReport(
                        layout,
                        requiredReportView
                );
            }

            document.save(outputStream);

            byte[] renderedBytes =
                    outputStream.toByteArray();

            validateRenderedPdf(
                    renderedBytes
            );

            return renderedBytes;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Monitoring run PDF rendering failed.",
                    exception
            );
        }
    }

    private void validateReportBoundary(
            MonitoringRunReportView reportView
    ) {
        Website website =
                Objects.requireNonNull(
                        reportView.getWebsite(),
                        "PDF report website is required."
                );

        MonitoringRun monitoringRun =
                Objects.requireNonNull(
                        reportView.getMonitoringRun(),
                        "PDF report monitoring run is required."
                );

        if (!reportView.isFullReport()
                || !reportView.isCompletedRun()) {

            throw new IllegalArgumentException(
                    "A full PDF report may only be rendered "
                            + "for a completed monitoring run."
            );
        }

        if (website.getId() == null) {
            throw new IllegalArgumentException(
                    "Persisted website ID is required "
                            + "for PDF rendering."
            );
        }

        if (monitoringRun.getId() == null) {
            throw new IllegalArgumentException(
                    "Persisted monitoring run ID is required "
                            + "for PDF rendering."
            );
        }

        if (!website.getId().equals(
                monitoringRun.getWebsiteId()
        )) {
            throw new IllegalArgumentException(
                    "Monitoring run does not belong "
                            + "to the PDF report website."
            );
        }
    }

    private void writeReport(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRunReportView reportView
    ) throws IOException {
        layout.writeDocumentTitle(
                reportView.getReportTitle()
        );

        writeReportMetadata(
                layout,
                reportView
        );

        writeWebsiteSection(
                layout,
                reportView.getWebsite()
        );

        writeMonitoringRunSection(
                layout,
                reportView.getMonitoringRun()
        );

        writeLifecycleCountsSection(
                layout,
                reportView.getCounts()
        );

        writeTrustAssessmentSection(
                layout,
                reportView.getLatestTrustAssessment()
        );

        writeTraceabilitySection(
                layout,
                reportView.getTraceabilitySummary()
        );

        writeComparisonSection(
                layout,
                reportView.getComparison()
        );

        writeFindingsSection(
                layout,
                reportView
        );

        writeRisksSection(
                layout,
                reportView
        );

        writeRecommendationsSection(
                layout,
                reportView
        );

        writeReportBoundarySection(
                layout
        );
    }

    private void writeReportMetadata(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRunReportView reportView
    ) throws IOException {
        layout.writeSectionHeading(
                "Report Baseline"
        );

        layout.writeKeyValue(
                "PDF Report Version",
                getReportVersion().getValue()
        );

        layout.writeKeyValue(
                "Report Status",
                reportView.getReportStatus()
        );

        layout.writeKeyValue(
                "Run Status",
                reportView.getMonitoringRun()
                        .getStatus()
        );

        layout.writeKeyValue(
                "Trigger Type",
                reportView.getMonitoringRun()
                        .getTriggerType()
        );

        layout.writeKeyValue(
                "Comparison",
                reportView.hasComparison()
                        ? "AVAILABLE"
                        : "UNAVAILABLE"
        );

        layout.writeParagraph(
                "This full PDF report reads existing persisted "
                        + "monitoring lifecycle output only."
        );
    }

    private void writeWebsiteSection(
            MonitoringRunPdfDocumentLayout layout,
            Website website
    ) throws IOException {
        layout.writeSectionHeading(
                "Website"
        );

        layout.writeKeyValue(
                "Website ID",
                website.getId()
        );

        layout.writeKeyValue(
                "Name",
                website.getName()
        );

        layout.writeKeyValue(
                "Domain",
                website.getDomain()
        );

        layout.writeKeyValue(
                "Status",
                website.getStatus()
        );

        layout.writeKeyValue(
                "Created At",
                formatDateTime(
                        website.getCreatedAt()
                )
        );
    }

    private void writeMonitoringRunSection(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRun monitoringRun
    ) throws IOException {
        layout.writeSectionHeading(
                "Monitoring Run"
        );

        layout.writeKeyValue(
                "Run ID",
                monitoringRun.getId()
        );

        layout.writeKeyValue(
                "Website ID",
                monitoringRun.getWebsiteId()
        );

        layout.writeKeyValue(
                "Status",
                monitoringRun.getStatus()
        );

        layout.writeKeyValue(
                "Trigger Type",
                monitoringRun.getTriggerType()
        );

        layout.writeKeyValue(
                "Monitoring Schedule ID",
                monitoringRun.getMonitoringScheduleId()
        );

        layout.writeKeyValue(
                "Started At",
                formatDateTime(
                        monitoringRun.getStartedAt()
                )
        );

        layout.writeKeyValue(
                "Completed At",
                formatDateTime(
                        monitoringRun.getCompletedAt()
                )
        );

        layout.writeKeyValue(
                "Created At",
                formatDateTime(
                        monitoringRun.getCreatedAt()
                )
        );
    }

    private void writeLifecycleCountsSection(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRunReportCounts counts
    ) throws IOException {
        layout.writeSectionHeading(
                "Lifecycle Output Counts"
        );

        layout.writeKeyValue(
                "Collected Evidence",
                counts.getCollectedEvidenceCount()
        );

        layout.writeKeyValue(
                "Normalized Evidence",
                counts.getNormalizedEvidenceCount()
        );

        layout.writeKeyValue(
                "Findings",
                counts.getFindingCount()
        );

        layout.writeKeyValue(
                "Risks",
                counts.getRiskCount()
        );

        layout.writeKeyValue(
                "Remediation Recommendations",
                counts.getRecommendationCount()
        );

        layout.writeKeyValue(
                "Trust Assessments",
                counts.getTrustAssessmentCount()
        );

        layout.writeParagraph(
                "Counts are read from persisted lifecycle output. "
                        + "PDF rendering does not create or modify "
                        + "assessment data."
        );
    }

    private void writeTrustAssessmentSection(
            MonitoringRunPdfDocumentLayout layout,
            TrustAssessment trustAssessment
    ) throws IOException {
        layout.writeSectionHeading(
                "Trust Assessment"
        );

        if (trustAssessment == null) {
            layout.writeParagraph(
                    "No trust assessment was produced "
                            + "for this monitoring run."
            );

            return;
        }

        layout.writeKeyValue(
                "Trust Assessment ID",
                trustAssessment.getId()
        );

        layout.writeKeyValue(
                "Trust Status",
                trustAssessment.getTrustStatus()
        );

        layout.writeKeyValue(
                "Trust Score",
                trustAssessment.getTrustScore()
        );

        layout.writeKeyValue(
                "Confidence Score",
                trustAssessment.getConfidenceScore()
        );

        layout.writeKeyValue(
                "Created At",
                formatDateTime(
                        trustAssessment.getCreatedAt()
                )
        );

        layout.writeSubsectionHeading(
                "Summary"
        );

        layout.writeParagraph(
                trustAssessment.getSummary()
        );
    }

    private void writeTraceabilitySection(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRunReportTraceabilitySummary
                    traceabilitySummary
    ) throws IOException {
        layout.writeSectionHeading(
                "Traceability Summary"
        );

        layout.writeKeyValue(
                "Collected Evidence Available",
                yesNo(
                        traceabilitySummary
                                .isCollectedEvidenceAvailable()
                )
        );

        layout.writeKeyValue(
                "Normalized Evidence Available",
                yesNo(
                        traceabilitySummary
                                .isNormalizedEvidenceAvailable()
                )
        );

        layout.writeKeyValue(
                "Findings Available",
                yesNo(
                        traceabilitySummary
                                .isFindingsAvailable()
                )
        );

        layout.writeKeyValue(
                "Risks Available",
                yesNo(
                        traceabilitySummary
                                .isRisksAvailable()
                )
        );

        layout.writeKeyValue(
                "Recommendations Available",
                yesNo(
                        traceabilitySummary
                                .isRecommendationsAvailable()
                )
        );

        layout.writeKeyValue(
                "Trust Assessments Available",
                yesNo(
                        traceabilitySummary
                                .isTrustAssessmentsAvailable()
                )
        );

        layout.writeSubsectionHeading(
                "Reviewability"
        );

        layout.writeKeyValue(
                "Evidence to Finding",
                reviewability(
                        traceabilitySummary
                                .isEvidenceToFindingReviewable()
                )
        );

        layout.writeKeyValue(
                "Finding to Risk",
                reviewability(
                        traceabilitySummary
                                .isFindingToRiskReviewable()
                )
        );

        layout.writeKeyValue(
                "Risk to Advisory Recommendation",
                reviewability(
                        traceabilitySummary
                                .isRiskToRecommendationReviewable()
                )
        );

        layout.writeKeyValue(
                "Risk to Trust",
                reviewability(
                        traceabilitySummary
                                .isRiskToTrustReviewable()
                )
        );

        layout.writeKeyValue(
                "End-to-End Lifecycle Output",
                reviewability(
                        traceabilitySummary
                                .isEndToEndReviewable()
                )
        );
    }

    private void writeComparisonSection(
            MonitoringRunPdfDocumentLayout layout,
            AssessmentComparisonSummary comparison
    ) throws IOException {
        layout.writeSectionHeading(
                "Assessment Comparison"
        );

        if (comparison == null
                || !comparison.isAvailable()) {

            layout.writeKeyValue(
                    "Status",
                    comparison == null
                            ? "UNAVAILABLE"
                            : comparison.getStatus()
            );

            layout.writeParagraph(
                    "Comparison requires the current run to be "
                            + "completed and a previous completed run "
                            + "to exist for the same website."
            );

            return;
        }

        layout.writeKeyValue(
                "Status",
                comparison.getStatus()
        );

        layout.writeKeyValue(
                "Previous Completed Run ID",
                comparison.getPreviousRun() == null
                        ? null
                        : comparison.getPreviousRun()
                        .getId()
        );

        layout.writeKeyValue(
                "New Finding Types",
                comparison.getNewFindingCount()
        );

        layout.writeKeyValue(
                "Resolved Finding Types",
                comparison.getResolvedFindingCount()
        );

        layout.writeKeyValue(
                "Unchanged Finding Types",
                comparison.getUnchangedFindingCount()
        );

        layout.writeKeyValue(
                "New Risk Types",
                comparison.getNewRiskCount()
        );

        layout.writeKeyValue(
                "Resolved Risk Types",
                comparison.getResolvedRiskCount()
        );

        layout.writeKeyValue(
                "Unchanged Risk Types",
                comparison.getUnchangedRiskCount()
        );

        TrustComparisonSummary trustComparison =
                comparison.getTrustComparison();

        layout.writeSubsectionHeading(
                "Trust Comparison"
        );

        if (trustComparison == null
                || !trustComparison.isAvailable()) {

            layout.writeParagraph(
                    "Trust comparison is not available."
            );

            return;
        }

        layout.writeKeyValue(
                "Current Trust Status",
                trustComparison.getCurrentTrustStatus()
        );

        layout.writeKeyValue(
                "Previous Trust Status",
                trustComparison.getPreviousTrustStatus()
        );

        layout.writeKeyValue(
                "Current Trust Score",
                trustComparison.getCurrentTrustScore()
        );

        layout.writeKeyValue(
                "Previous Trust Score",
                trustComparison.getPreviousTrustScore()
        );

        layout.writeKeyValue(
                "Score Delta",
                trustComparison.getScoreDelta()
        );

        layout.writeKeyValue(
                "Score Direction",
                trustComparison.getScoreDirection()
        );

        layout.writeKeyValue(
                "Trust Status Changed",
                yesNo(
                        trustComparison.isStatusChanged()
                )
        );
    }

    private void writeFindingsSection(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRunReportView reportView
    ) throws IOException {
        layout.writeSectionHeading(
                "Findings"
        );

        if (!reportView.hasFindings()) {
            layout.writeParagraph(
                    "No findings were produced "
                            + "for this monitoring run."
            );

            return;
        }

        int findingNumber = 0;

        for (Finding finding :
                reportView.getFindings()) {

            findingNumber++;

            layout.writeSubsectionHeading(
                    "Finding "
                            + findingNumber
                            + " - "
                            + finding.getTitle()
            );

            layout.writeKeyValue(
                    "Finding ID",
                    finding.getId()
            );

            layout.writeKeyValue(
                    "Finding Type",
                    finding.getFindingType()
            );

            layout.writeKeyValue(
                    "Confidence Score",
                    finding.getConfidenceScore()
            );

            layout.writeKeyValue(
                    "Created At",
                    formatDateTime(
                            finding.getCreatedAt()
                    )
            );

            layout.writeParagraph(
                    finding.getDescription()
            );
        }
    }

    private void writeRisksSection(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRunReportView reportView
    ) throws IOException {
        layout.writeSectionHeading(
                "Risks"
        );

        if (!reportView.hasRisks()) {
            layout.writeParagraph(
                    "No risks were produced "
                            + "for this monitoring run."
            );

            return;
        }

        int riskNumber = 0;

        for (Risk risk :
                reportView.getRisks()) {

            riskNumber++;

            layout.writeSubsectionHeading(
                    "Risk "
                            + riskNumber
                            + " - "
                            + risk.getRiskType()
            );

            layout.writeKeyValue(
                    "Risk ID",
                    risk.getId()
            );

            layout.writeKeyValue(
                    "Severity",
                    risk.getSeverity()
            );

            layout.writeKeyValue(
                    "Risk Score",
                    risk.getRiskScore()
            );

            layout.writeKeyValue(
                    "Confidence Score",
                    risk.getConfidenceScore()
            );

            layout.writeKeyValue(
                    "Created At",
                    formatDateTime(
                            risk.getCreatedAt()
                    )
            );

            layout.writeSubsectionHeading(
                    "Rationale"
            );

            layout.writeParagraph(
                    risk.getRationale()
            );
        }
    }

    private void writeRecommendationsSection(
            MonitoringRunPdfDocumentLayout layout,
            MonitoringRunReportView reportView
    ) throws IOException {
        layout.writeSectionHeading(
                "Advisory Remediation Recommendations"
        );

        layout.writeParagraph(
                "Recommendations are advisory persisted output. "
                        + "They do not modify risk severity, risk score, "
                        + "confidence score or trust score."
        );

        if (reportView
                .getRiskRecommendationViews()
                .isEmpty()) {

            layout.writeParagraph(
                    "No risks or remediation recommendations "
                            + "are available for this monitoring run."
            );

            return;
        }

        int recommendationNumber = 0;

        for (
                MonitoringRunReportRiskRecommendationView item :
                reportView.getRiskRecommendationViews()
        ) {
            recommendationNumber++;

            Risk risk = item.getRisk();

            layout.writeSubsectionHeading(
                    "Recommendation Context "
                            + recommendationNumber
                            + " - "
                            + risk.getRiskType()
            );

            layout.writeKeyValue(
                    "Risk ID",
                    risk.getId()
            );

            layout.writeKeyValue(
                    "Risk Severity",
                    risk.getSeverity()
            );

            layout.writeKeyValue(
                    "Risk Score",
                    risk.getRiskScore()
            );

            layout.writeKeyValue(
                    "Recommendation Available",
                    yesNo(
                            item.isRecommendationAvailable()
                    )
            );

            if (!item.isRecommendationAvailable()) {
                layout.writeParagraph(
                        "No persisted remediation recommendation "
                                + "is available for this risk."
                );

                continue;
            }

            writeRecommendation(
                    layout,
                    item.getLatestRecommendation()
            );
        }
    }

    private void writeRecommendation(
            MonitoringRunPdfDocumentLayout layout,
            RiskRemediationRecommendation recommendation
    ) throws IOException {
        layout.writeSubsectionHeading(
                recommendation.getTitle()
        );

        layout.writeSubsectionHeading(
                "Summary"
        );

        layout.writeParagraph(
                recommendation.getSummary()
        );

        layout.writeSubsectionHeading(
                "Remediation Steps"
        );

        layout.writeParagraph(
                recommendation.getRemediationSteps()
        );

        layout.writeSubsectionHeading(
                "Verification Steps"
        );

        layout.writeParagraph(
                recommendation.getVerificationSteps()
        );

        layout.writeSubsectionHeading(
                "Recommendation Audit Metadata"
        );

        layout.writeKeyValue(
                "Recommendation ID",
                recommendation.getId()
        );

        layout.writeKeyValue(
                "Source",
                recommendation.getSource()
        );

        layout.writeKeyValue(
                "Fallback Reason",
                recommendation.getFallbackReason()
        );

        layout.writeKeyValue(
                "Validation Status",
                recommendation.getValidationStatus()
        );

        layout.writeKeyValue(
                "Advisory",
                yesNo(
                        recommendation.isAdvisory()
                )
        );

        layout.writeKeyValue(
                "Provider",
                recommendation.getProviderName() == null
                        ? "Not used"
                        : recommendation.getProviderName()
        );

        layout.writeKeyValue(
                "Model",
                recommendation.getModelName() == null
                        ? "Not used"
                        : recommendation.getModelName()
        );

        layout.writeKeyValue(
                "Prompt Version",
                recommendation.getPromptVersion()
        );

        layout.writeKeyValue(
                "Fallback Rule Version",
                recommendation.getFallbackRuleVersion() == null
                        ? "Not applicable"
                        : recommendation
                        .getFallbackRuleVersion()
        );

        layout.writeKeyValue(
                "Context Fingerprint",
                recommendation.getContextFingerprint()
        );

        layout.writeKeyValue(
                "Linked Findings",
                recommendation.getContextFindingCount()
        );

        layout.writeKeyValue(
                "Normalized Evidence Items",
                recommendation.getContextEvidenceCount()
        );

        layout.writeKeyValue(
                "Generated At",
                formatDateTime(
                        recommendation.getGeneratedAt()
                )
        );

        layout.writeKeyValue(
                "Persisted At",
                formatDateTime(
                        recommendation.getCreatedAt()
                )
        );
    }

    private void writeReportBoundarySection(
            MonitoringRunPdfDocumentLayout layout
    ) throws IOException {
        layout.writeSectionHeading(
                "Report Boundary"
        );

        layout.writeParagraph(
                "This PDF is a read-only representation of persisted "
                        + "monitoring lifecycle output."
        );

        layout.writeBullet(
                "It does not collect or normalize evidence."
        );

        layout.writeBullet(
                "It does not generate findings or evaluate risks."
        );

        layout.writeBullet(
                "It does not calculate trust assessments."
        );

        layout.writeBullet(
                "It does not generate remediation recommendations."
        );

        layout.writeBullet(
                "It does not call an AI provider."
        );

        layout.writeBullet(
                "It does not modify comparison output."
        );

        layout.writeBullet(
                "It does not dispatch data through Telegram."
        );
    }

    private void validateRenderedPdf(
            byte[] renderedBytes
    ) {
        if (renderedBytes == null
                || renderedBytes.length < PDF_HEADER.length) {

            throw new IllegalStateException(
                    "PDF renderer produced empty or incomplete content."
            );
        }

        for (int index = 0;
             index < PDF_HEADER.length;
             index++) {

            if (renderedBytes[index]
                    != PDF_HEADER[index]) {

                throw new IllegalStateException(
                        "PDF renderer produced content "
                                + "without a valid PDF header."
                );
            }
        }
    }

    private String formatDateTime(
            OffsetDateTime value
    ) {
        if (value == null) {
            return "Not available";
        }

        return DATE_TIME_FORMATTER.format(
                value
        );
    }

    private String yesNo(
            boolean value
    ) {
        return value ? "YES" : "NO";
    }

    private String reviewability(
            boolean value
    ) {
        return value
                ? "REVIEWABLE"
                : "LIMITED";
    }
}