package com.cigabyte.sitesentinel.engine.analysis;

import com.cigabyte.sitesentinel.evidence.CollectedEvidence;
import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.finding.FindingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class RuleBasedEvidenceAnalysisEngine implements EvidenceAnalysisEngine {

    private final EvidenceService evidenceService;
    private final FindingService findingService;

    public RuleBasedEvidenceAnalysisEngine(
            EvidenceService evidenceService,
            FindingService findingService
    ) {
        this.evidenceService = evidenceService;
        this.findingService = findingService;
    }

    @Override
    public void analyze(UUID monitoringRunId) {
        List<CollectedEvidence> collectedEvidence = evidenceService.findCollectedEvidence(monitoringRunId);

        for (CollectedEvidence evidence : collectedEvidence) {
            normalizeEvidence(evidence);
            createFindingIfApplicable(evidence);
        }
    }

    private void normalizeEvidence(CollectedEvidence evidence) {
        String evidenceType = evidence.getEvidenceType();
        String rawValue = safeValue(evidence.getRawValue());

        if ("FETCH_OUTCOME".equals(evidenceType)) {
            evidenceService.recordNormalizedEvidence(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    evidence.getId(),
                    "FETCH_OUTCOME",
                    normalizeFetchOutcome(rawValue)
            );
            return;
        }

        if ("HTTP_STATUS".equals(evidenceType)) {
            evidenceService.recordNormalizedEvidence(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    evidence.getId(),
                    "HTTP_STATUS_CLASS",
                    classifyHttpStatus(rawValue)
            );
            return;
        }

        if ("FINAL_URL".equals(evidenceType)) {
            evidenceService.recordNormalizedEvidence(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    evidence.getId(),
                    "URL_SCHEME",
                    classifyUrlScheme(rawValue)
            );
            return;
        }

        if (evidenceType.startsWith("HEADER_")) {
            evidenceService.recordNormalizedEvidence(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    evidence.getId(),
                    "HEADER_PRESENCE",
                    isMissing(rawValue) ? "MISSING" : "PRESENT"
            );
            return;
        }

        if ("PAGE_TITLE".equals(evidenceType)
                || "META_DESCRIPTION".equals(evidenceType)
                || "CANONICAL_URL".equals(evidenceType)) {
            evidenceService.recordNormalizedEvidence(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    evidence.getId(),
                    evidenceType + "_PRESENCE",
                    isMissing(rawValue) ? "MISSING" : "PRESENT"
            );
        }
    }

    private void createFindingIfApplicable(CollectedEvidence evidence) {
        if (!"HOMEPAGE".equals(evidence.getSourceType())) {
            return;
        }

        String evidenceType = evidence.getEvidenceType();
        String rawValue = safeValue(evidence.getRawValue());

        if ("FETCH_OUTCOME".equals(evidenceType)) {
            createHomepageFetchOutcomeFindingIfNeeded(evidence, rawValue);
            return;
        }

        if ("HTTP_STATUS".equals(evidenceType)) {
            createHttpStatusFindingIfNeeded(evidence, rawValue);
            return;
        }

        if ("FINAL_URL".equals(evidenceType)) {
            createFinalUrlFindingIfNeeded(evidence, rawValue);
            return;
        }

        if ("HEADER_STRICT_TRANSPORT_SECURITY".equals(evidenceType)) {
            createMissingHeaderFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_HSTS_HEADER",
                    "Strict-Transport-Security header is missing",
                    "The homepage response does not include the Strict-Transport-Security header."
            );
            return;
        }

        if ("HEADER_CONTENT_SECURITY_POLICY".equals(evidenceType)) {
            createMissingHeaderFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_CONTENT_SECURITY_POLICY_HEADER",
                    "Content-Security-Policy header is missing",
                    "The homepage response does not include the Content-Security-Policy header."
            );
            return;
        }

        if ("HEADER_X_FRAME_OPTIONS".equals(evidenceType)) {
            createMissingHeaderFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_X_FRAME_OPTIONS_HEADER",
                    "X-Frame-Options header is missing",
                    "The homepage response does not include the X-Frame-Options header."
            );
            return;
        }

        if ("HEADER_X_CONTENT_TYPE_OPTIONS".equals(evidenceType)) {
            createMissingHeaderFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_X_CONTENT_TYPE_OPTIONS_HEADER",
                    "X-Content-Type-Options header is missing",
                    "The homepage response does not include the X-Content-Type-Options header."
            );
            return;
        }

        if ("HEADER_REFERRER_POLICY".equals(evidenceType)) {
            createMissingHeaderFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_REFERRER_POLICY_HEADER",
                    "Referrer-Policy header is missing",
                    "The homepage response does not include the Referrer-Policy header."
            );
            return;
        }

        if ("PAGE_TITLE".equals(evidenceType)) {
            createMissingValueFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_PAGE_TITLE",
                    "Homepage page title is missing",
                    "The homepage HTML does not include a usable title element."
            );
            return;
        }

        if ("META_DESCRIPTION".equals(evidenceType)) {
            createMissingValueFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_META_DESCRIPTION",
                    "Homepage meta description is missing",
                    "The homepage HTML does not include a usable meta description."
            );
            return;
        }

        if ("CANONICAL_URL".equals(evidenceType)) {
            createMissingValueFindingIfNeeded(
                    evidence,
                    rawValue,
                    "MISSING_CANONICAL_URL",
                    "Homepage canonical URL is missing",
                    "The homepage HTML does not include a canonical URL."
            );
        }
    }

    private void createHttpStatusFindingIfNeeded(CollectedEvidence evidence, String rawValue) {
        Integer statusCode = parseInteger(rawValue);

        if (statusCode == null) {
            findingService.recordFinding(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    "INVALID_HTTP_STATUS_EVIDENCE",
                    "Homepage HTTP status could not be interpreted",
                    "The homepage HTTP status evidence value is not a valid integer: " + rawValue,
                    90,
                    evidence.getId()
            );
            return;
        }

        if (statusCode >= 400) {
            findingService.recordFinding(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    "HOMEPAGE_HTTP_ERROR_STATUS",
                    "Homepage returned HTTP error status",
                    "The homepage returned HTTP status " + statusCode + ".",
                    95,
                    evidence.getId()
            );
        }
    }

    private void createFinalUrlFindingIfNeeded(CollectedEvidence evidence, String rawValue) {
        String value = rawValue.toLowerCase(Locale.ROOT);

        if (value.startsWith("http://")) {
            findingService.recordFinding(
                    evidence.getWebsiteId(),
                    evidence.getMonitoringRunId(),
                    "HOMEPAGE_FINAL_URL_NOT_HTTPS",
                    "Homepage final URL is not HTTPS",
                    "The homepage final URL uses HTTP instead of HTTPS: " + rawValue,
                    95,
                    evidence.getId()
            );
        }
    }

    private void createMissingHeaderFindingIfNeeded(
            CollectedEvidence evidence,
            String rawValue,
            String findingType,
            String title,
            String description
    ) {
        if (!isMissing(rawValue)) {
            return;
        }

        findingService.recordFinding(
                evidence.getWebsiteId(),
                evidence.getMonitoringRunId(),
                findingType,
                title,
                description,
                90,
                evidence.getId()
        );
    }

    private void createMissingValueFindingIfNeeded(
            CollectedEvidence evidence,
            String rawValue,
            String findingType,
            String title,
            String description
    ) {
        if (!isMissing(rawValue)) {
            return;
        }

        findingService.recordFinding(
                evidence.getWebsiteId(),
                evidence.getMonitoringRunId(),
                findingType,
                title,
                description,
                90,
                evidence.getId()
        );
    }

    private String classifyHttpStatus(String rawValue) {
        Integer statusCode = parseInteger(rawValue);

        if (statusCode == null) {
            return "INVALID";
        }

        if (statusCode >= 100 && statusCode < 200) {
            return "INFORMATIONAL";
        }

        if (statusCode >= 200 && statusCode < 300) {
            return "SUCCESS";
        }

        if (statusCode >= 300 && statusCode < 400) {
            return "REDIRECTION";
        }

        if (statusCode >= 400 && statusCode < 500) {
            return "CLIENT_ERROR";
        }

        if (statusCode >= 500 && statusCode < 600) {
            return "SERVER_ERROR";
        }

        return "UNKNOWN";
    }

    private String classifyUrlScheme(String rawValue) {
        String value = safeValue(rawValue).toLowerCase(Locale.ROOT);

        if (value.startsWith("https://")) {
            return "HTTPS";
        }

        if (value.startsWith("http://")) {
            return "HTTP";
        }

        return "UNKNOWN";
    }

    private boolean isMissing(String value) {
        return value == null
                || value.isBlank()
                || "MISSING".equalsIgnoreCase(value.trim());
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(safeValue(value));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String safeValue(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String normalizeFetchOutcome(String rawValue) {
        String value = safeValue(rawValue).toUpperCase(Locale.ROOT);

        if ("SUCCESS".equals(value)) {
            return "SUCCESS";
        }

        if ("FAILED".equals(value)) {
            return "FAILED";
        }

        return "UNKNOWN";
    }

    private void createHomepageFetchOutcomeFindingIfNeeded(CollectedEvidence evidence, String rawValue) {
        if (!"FAILED".equalsIgnoreCase(rawValue)) {
            return;
        }

        findingService.recordFinding(
                evidence.getWebsiteId(),
                evidence.getMonitoringRunId(),
                "HOMEPAGE_FETCH_FAILED",
                "Homepage fetch failed",
                "The scanner could not fetch the homepage resource successfully.",
                95,
                evidence.getId()
        );
    }
}