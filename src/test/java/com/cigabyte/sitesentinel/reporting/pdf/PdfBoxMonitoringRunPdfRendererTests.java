package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonSummary;
import com.cigabyte.sitesentinel.comparison.ComparisonStatus;
import com.cigabyte.sitesentinel.comparison.TrustComparisonSummary;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationFallbackReason;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendation;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendationContent;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportCounts;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportRiskRecommendationView;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportTraceabilitySummary;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportView;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskSeverity;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.trust.TrustStatus;
import com.cigabyte.sitesentinel.website.Website;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfBoxMonitoringRunPdfRendererTests {

    private static final OffsetDateTime BASE_TIME =
            OffsetDateTime.of(
                    2026,
                    7,
                    17,
                    10,
                    0,
                    0,
                    0,
                    ZoneOffset.UTC
            );

    private final PdfBoxMonitoringRunPdfRenderer renderer =
            new PdfBoxMonitoringRunPdfRenderer();

    @Test
    void completedReportRendersValidPdf() throws IOException {
        MonitoringRunReportView reportView =
                createMinimalCompletedReportView(
                        "Renderer Test Website"
                );

        byte[] renderedBytes =
                renderer.render(reportView);

        assertEquals(
                MonitoringRunPdfVersion.V1,
                renderer.getReportVersion()
        );

        assertTrue(
                renderedBytes.length > 100
        );

        assertArrayEquals(
                "%PDF-".getBytes(
                        StandardCharsets.US_ASCII
                ),
                Arrays.copyOf(
                        renderedBytes,
                        5
                )
        );

        try (
                PDDocument document =
                        Loader.loadPDF(
                                renderedBytes
                        )
        ) {
            assertTrue(
                    document.getNumberOfPages() >= 1
            );

            String text =
                    new PDFTextStripper()
                            .getText(document);

            assertTrue(
                    text.contains(
                            "Monitoring Run Report"
                    )
            );

            assertTrue(
                    text.contains(
                            "Renderer Test Website"
                    )
            );

            assertTrue(
                    text.contains(
                            "Report Boundary"
                    )
            );

            assertTrue(
                    text.contains(
                            "SiteSentinel | Page 1"
                    )
            );
        }
    }

    @Test
    void fullReportIncludesAssessmentAndRecommendationContent()
            throws IOException {

        MonitoringRunReportView reportView =
                createRichCompletedReportView();

        byte[] renderedBytes =
                renderer.render(reportView);

        String extractedText =
                extractText(renderedBytes);

        assertTrue(
                extractedText.contains(
                        "Lifecycle Output Counts"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Trust Assessment"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Assessment Comparison"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Findings"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Missing Security Header"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Risks"
                )
        );

        assertTrue(
                extractedText.contains(
                        "SECURITY_HEADER_RISK"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Advisory Remediation Recommendations"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Add the required security header"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Recommendation Audit Metadata"
                )
        );

        assertTrue(
                extractedText.contains(
                        "PROVIDER_UNAVAILABLE"
                )
        );

        assertTrue(
                extractedText.contains(
                        "risk-remediation-v1"
                )
        );

        assertTrue(
                extractedText.contains(
                        "risk-remediation-fallback-v1"
                )
        );

        assertTrue(
                extractedText.contains(
                        "a".repeat(64)
                )
        );
    }

    @Test
    void longReportCreatesMultiplePagesAndFooters()
            throws IOException {

        MonitoringRunReportView reportView =
                createLongCompletedReportView();

        byte[] renderedBytes =
                renderer.render(reportView);

        try (
                PDDocument document =
                        Loader.loadPDF(
                                renderedBytes
                        )
        ) {
            assertTrue(
                    document.getNumberOfPages() > 1
            );

            String text =
                    new PDFTextStripper()
                            .getText(document);

            assertTrue(
                    text.contains(
                            "SiteSentinel | Page 1"
                    )
            );

            assertTrue(
                    text.contains(
                            "SiteSentinel | Page 2"
                    )
            );

            assertTrue(
                    text.contains(
                            "Finding 40"
                    )
            );
        }
    }

    @Test
    void unsupportedUnicodeDoesNotBreakRendering()
            throws IOException {

        MonitoringRunReportView reportView =
                createMinimalCompletedReportView(
                        "Unicode 🔒 Website — Güvenlik"
                );

        byte[] renderedBytes =
                renderer.render(reportView);

        String extractedText =
                extractText(renderedBytes);

        assertTrue(
                extractedText.contains(
                        "Unicode"
                )
        );

        assertTrue(
                extractedText.contains(
                        "Website"
                )
        );

        assertArrayEquals(
                "%PDF-".getBytes(
                        StandardCharsets.US_ASCII
                ),
                Arrays.copyOf(
                        renderedBytes,
                        5
                )
        );
    }

    @Test
    void pendingMonitoringRunIsRejected() {
        UUID websiteId =
                UUID.randomUUID();

        Website website =
                createWebsite(
                        websiteId,
                        "Pending Report Website"
                );

        MonitoringRun pendingRun =
                createPendingRun(
                        websiteId
                );

        MonitoringRunReportView reportView =
                createReportView(
                        website,
                        pendingRun,
                        new MonitoringRunReportCounts(
                                0,
                                0,
                                0,
                                0,
                                0,
                                0
                        ),
                        null,
                        List.of(),
                        List.of(),
                        List.of(),
                        null
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> renderer.render(
                        reportView
                )
        );
    }

    @Test
    void nullAndWebsiteOwnershipMismatchAreRejected() {
        assertThrows(
                NullPointerException.class,
                () -> renderer.render(null)
        );

        UUID websiteId =
                UUID.randomUUID();

        UUID differentWebsiteId =
                UUID.randomUUID();

        Website website =
                createWebsite(
                        websiteId,
                        "Ownership Test Website"
                );

        MonitoringRun completedRun =
                createCompletedRun(
                        differentWebsiteId
                );

        MonitoringRunReportView reportView =
                createReportView(
                        website,
                        completedRun,
                        new MonitoringRunReportCounts(
                                0,
                                0,
                                0,
                                0,
                                0,
                                0
                        ),
                        null,
                        List.of(),
                        List.of(),
                        List.of(),
                        null
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> renderer.render(
                        reportView
                )
        );
    }

    private MonitoringRunReportView
    createMinimalCompletedReportView(
            String websiteName
    ) {
        UUID websiteId =
                UUID.randomUUID();

        Website website =
                createWebsite(
                        websiteId,
                        websiteName
                );

        MonitoringRun monitoringRun =
                createCompletedRun(
                        websiteId
                );

        MonitoringRunReportCounts counts =
                new MonitoringRunReportCounts(
                        0,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        return createReportView(
                website,
                monitoringRun,
                counts,
                null,
                List.of(),
                List.of(),
                List.of(),
                AssessmentComparisonSummary
                        .unavailable(
                                ComparisonStatus
                                        .NO_PREVIOUS_COMPLETED_RUN,
                                monitoringRun
                        )
        );
    }

    private MonitoringRunReportView
    createRichCompletedReportView() {
        UUID websiteId =
                UUID.randomUUID();

        Website website =
                createWebsite(
                        websiteId,
                        "Full PDF Report Website"
                );

        MonitoringRun monitoringRun =
                createCompletedRun(
                        websiteId
                );

        MonitoringRun previousRun =
                createCompletedRun(
                        websiteId
                );

        Finding finding =
                new Finding(
                        websiteId,
                        monitoringRun.getId(),
                        "SECURITY_HEADER",
                        "Missing Security Header",
                        "The expected security header was not "
                                + "present in normalized monitoring evidence.",
                        94
                );

        setEntityMetadata(
                finding,
                UUID.randomUUID()
        );

        Risk risk =
                new Risk(
                        websiteId,
                        monitoringRun.getId(),
                        "SECURITY_HEADER_RISK",
                        RiskSeverity.HIGH,
                        82,
                        91,
                        "The missing security header increases "
                                + "the website exposure baseline."
                );

        setEntityMetadata(
                risk,
                UUID.randomUUID()
        );

        TrustAssessment trustAssessment =
                new TrustAssessment(
                        websiteId,
                        monitoringRun.getId(),
                        TrustStatus.NEEDS_ATTENTION,
                        63,
                        90,
                        "The website requires remediation before "
                                + "its trust status can improve."
                );

        setEntityMetadata(
                trustAssessment,
                UUID.randomUUID()
        );

        TrustAssessment previousTrustAssessment =
                new TrustAssessment(
                        websiteId,
                        previousRun.getId(),
                        TrustStatus.TRUSTED,
                        88,
                        92,
                        "The previous completed monitoring run "
                                + "had a stronger trust result."
                );

        setEntityMetadata(
                previousTrustAssessment,
                UUID.randomUUID()
        );

        RiskRemediationRecommendationContent content =
                new RiskRemediationRecommendationContent(
                        "Add the required security header",
                        "Configure the web server to return the "
                                + "persisted missing security header.",
                        """
                        1. Review the current web server configuration.
                        2. Add the required response header.
                        3. Deploy the controlled configuration change.
                        """.strip(),
                        """
                        1. Run the monitoring process again.
                        2. Confirm the header appears in normalized evidence.
                        """.strip()
                );

        RiskRemediationRecommendation recommendation =
                RiskRemediationRecommendation
                        .ruleBasedFallback(
                                monitoringRun.getId(),
                                risk.getId(),
                                content,
                                RiskRemediationFallbackReason
                                        .PROVIDER_UNAVAILABLE,
                                null,
                                null,
                                "risk-remediation-v1",
                                "risk-remediation-fallback-v1",
                                "a".repeat(64),
                                1,
                                2,
                                BASE_TIME.plusMinutes(10)
                        );

        setEntityMetadata(
                recommendation,
                UUID.randomUUID()
        );

        MonitoringRunReportRiskRecommendationView
                recommendationView =
                new MonitoringRunReportRiskRecommendationView(
                        risk,
                        recommendation
                );

        MonitoringRunReportCounts counts =
                new MonitoringRunReportCounts(
                        4,
                        4,
                        1,
                        1,
                        1,
                        1
                );

        AssessmentComparisonSummary comparison =
                AssessmentComparisonSummary.available(
                        monitoringRun,
                        previousRun,
                        TrustComparisonSummary.compare(
                                trustAssessment,
                                previousTrustAssessment
                        ),
                        List.of(),
                        List.of()
                );

        return createReportView(
                website,
                monitoringRun,
                counts,
                trustAssessment,
                List.of(finding),
                List.of(risk),
                List.of(recommendationView),
                comparison
        );
    }

    private MonitoringRunReportView
    createLongCompletedReportView() {
        UUID websiteId =
                UUID.randomUUID();

        Website website =
                createWebsite(
                        websiteId,
                        "Multi Page PDF Website"
                );

        MonitoringRun monitoringRun =
                createCompletedRun(
                        websiteId
                );

        List<Finding> findings =
                new ArrayList<>();

        for (int index = 1;
             index <= 40;
             index++) {

            Finding finding =
                    new Finding(
                            websiteId,
                            monitoringRun.getId(),
                            "LONG_REPORT_FINDING_"
                                    + index,
                            "Finding "
                                    + index,
                            "This finding contains enough descriptive "
                                    + "content to exercise wrapping and "
                                    + "automatic page creation in the PDF "
                                    + "document layout foundation. "
                                    + "The content remains persisted-report "
                                    + "data and does not trigger assessment.",
                            80
                    );

            setEntityMetadata(
                    finding,
                    UUID.randomUUID()
            );

            findings.add(finding);
        }

        MonitoringRunReportCounts counts =
                new MonitoringRunReportCounts(
                        40,
                        40,
                        40,
                        0,
                        0,
                        0
                );

        return createReportView(
                website,
                monitoringRun,
                counts,
                null,
                findings,
                List.of(),
                List.of(),
                AssessmentComparisonSummary
                        .unavailable(
                                ComparisonStatus
                                        .NO_PREVIOUS_COMPLETED_RUN,
                                monitoringRun
                        )
        );
    }

    private MonitoringRunReportView createReportView(
            Website website,
            MonitoringRun monitoringRun,
            MonitoringRunReportCounts counts,
            TrustAssessment trustAssessment,
            List<Finding> findings,
            List<Risk> risks,
            List<MonitoringRunReportRiskRecommendationView>
                    recommendationViews,
            AssessmentComparisonSummary comparison
    ) {
        List<RiskRemediationRecommendation>
                recommendations =
                recommendationViews.stream()
                        .filter(
                                MonitoringRunReportRiskRecommendationView
                                        ::isRecommendationAvailable
                        )
                        .map(
                                MonitoringRunReportRiskRecommendationView
                                        ::getLatestRecommendation
                        )
                        .toList();

        return new MonitoringRunReportView(
                website,
                monitoringRun,
                counts,
                MonitoringRunReportTraceabilitySummary
                        .fromCounts(counts),
                trustAssessment,
                findings,
                risks,
                recommendations,
                recommendationViews,
                comparison
        );
    }

    private Website createWebsite(
            UUID websiteId,
            String name
    ) {
        Website website =
                new Website(
                        name,
                        "pdf-renderer.example.test"
                );

        ReflectionTestUtils.setField(
                website,
                "id",
                websiteId
        );

        ReflectionTestUtils.setField(
                website,
                "createdAt",
                BASE_TIME
        );

        ReflectionTestUtils.setField(
                website,
                "updatedAt",
                BASE_TIME
        );

        return website;
    }

    private MonitoringRun createCompletedRun(
            UUID websiteId
    ) {
        MonitoringRun monitoringRun =
                createPendingRun(
                        websiteId
                );

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return monitoringRun;
    }

    private MonitoringRun createPendingRun(
            UUID websiteId
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(
                        websiteId
                );

        ReflectionTestUtils.setField(
                monitoringRun,
                "id",
                UUID.randomUUID()
        );

        ReflectionTestUtils.setField(
                monitoringRun,
                "createdAt",
                BASE_TIME
        );

        ReflectionTestUtils.setField(
                monitoringRun,
                "updatedAt",
                BASE_TIME
        );

        return monitoringRun;
    }

    private void setEntityMetadata(
            Object entity,
            UUID id
    ) {
        ReflectionTestUtils.setField(
                entity,
                "id",
                id
        );

        ReflectionTestUtils.setField(
                entity,
                "createdAt",
                BASE_TIME
        );
    }

    private String extractText(
            byte[] renderedBytes
    ) throws IOException {
        try (
                PDDocument document =
                        Loader.loadPDF(
                                renderedBytes
                        )
        ) {
            return new PDFTextStripper()
                    .getText(document);
        }
    }
}