package com.cigabyte.sitesentinel.engine.risk;

import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.risk.RiskSeverity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleBasedRiskEvaluationEngine implements RiskEvaluationEngine {

    private final FindingService findingService;
    private final RiskService riskService;

    public RuleBasedRiskEvaluationEngine(
            FindingService findingService,
            RiskService riskService
    ) {
        this.findingService = findingService;
        this.riskService = riskService;
    }

    @Override
    public void evaluate(UUID monitoringRunId) {
        List<Finding> findings = findingService.findByMonitoringRunId(monitoringRunId);

        for (Finding finding : findings) {
            createRiskIfApplicable(finding);
        }
    }

    private void createRiskIfApplicable(Finding finding) {
        RiskRule riskRule = resolveRiskRule(finding.getFindingType());

        if (riskRule == null) {
            return;
        }

        riskService.recordRisk(
                finding.getWebsiteId(),
                finding.getMonitoringRunId(),
                riskRule.riskType(),
                riskRule.severity(),
                riskRule.riskScore(),
                finding.getConfidenceScore(),
                buildRationale(finding, riskRule),
                finding.getId()
        );
    }

    private RiskRule resolveRiskRule(String findingType) {
        if ("HOMEPAGE_HTTP_ERROR_STATUS".equals(findingType)) {
            return new RiskRule(
                    "WEBSITE_AVAILABILITY_RISK",
                    RiskSeverity.HIGH,
                    85,
                    "The homepage returned an HTTP error status."
            );
        }

        if ("HOMEPAGE_FINAL_URL_NOT_HTTPS".equals(findingType)) {
            return new RiskRule(
                    "TRANSPORT_SECURITY_RISK",
                    RiskSeverity.HIGH,
                    80,
                    "The homepage final URL is not served over HTTPS."
            );
        }

        if ("MISSING_CONTENT_SECURITY_POLICY_HEADER".equals(findingType)) {
            return new RiskRule(
                    "BROWSER_SECURITY_POLICY_RISK",
                    RiskSeverity.HIGH,
                    75,
                    "The homepage is missing a Content-Security-Policy header."
            );
        }

        if ("MISSING_HSTS_HEADER".equals(findingType)) {
            return new RiskRule(
                    "TRANSPORT_SECURITY_POLICY_RISK",
                    RiskSeverity.MEDIUM,
                    65,
                    "The homepage is missing a Strict-Transport-Security header."
            );
        }

        if ("MISSING_X_FRAME_OPTIONS_HEADER".equals(findingType)) {
            return new RiskRule(
                    "CLICKJACKING_PROTECTION_RISK",
                    RiskSeverity.MEDIUM,
                    55,
                    "The homepage is missing an X-Frame-Options header."
            );
        }

        if ("MISSING_X_CONTENT_TYPE_OPTIONS_HEADER".equals(findingType)) {
            return new RiskRule(
                    "CONTENT_SNIFFING_PROTECTION_RISK",
                    RiskSeverity.MEDIUM,
                    50,
                    "The homepage is missing an X-Content-Type-Options header."
            );
        }

        if ("MISSING_REFERRER_POLICY_HEADER".equals(findingType)) {
            return new RiskRule(
                    "REFERRER_PRIVACY_POLICY_RISK",
                    RiskSeverity.LOW,
                    35,
                    "The homepage is missing a Referrer-Policy header."
            );
        }

        if ("MISSING_PAGE_TITLE".equals(findingType)) {
            return new RiskRule(
                    "CONTENT_QUALITY_RISK",
                    RiskSeverity.LOW,
                    30,
                    "The homepage does not expose a usable page title."
            );
        }

        if ("MISSING_META_DESCRIPTION".equals(findingType)) {
            return new RiskRule(
                    "SEARCH_PRESENTATION_RISK",
                    RiskSeverity.LOW,
                    25,
                    "The homepage does not expose a usable meta description."
            );
        }

        if ("MISSING_CANONICAL_URL".equals(findingType)) {
            return new RiskRule(
                    "CANONICALIZATION_RISK",
                    RiskSeverity.LOW,
                    20,
                    "The homepage does not expose a canonical URL."
            );
        }

        if ("INVALID_HTTP_STATUS_EVIDENCE".equals(findingType)) {
            return new RiskRule(
                    "ASSESSMENT_DATA_QUALITY_RISK",
                    RiskSeverity.MEDIUM,
                    45,
                    "The HTTP status evidence could not be interpreted reliably."
            );
        }

        return null;
    }

    private String buildRationale(Finding finding, RiskRule riskRule) {
        return riskRule.rationale()
                + " Source finding: "
                + finding.getTitle()
                + " "
                + finding.getDescription();
    }

    private record RiskRule(
            String riskType,
            RiskSeverity severity,
            Integer riskScore,
            String rationale
    ) {
    }
}