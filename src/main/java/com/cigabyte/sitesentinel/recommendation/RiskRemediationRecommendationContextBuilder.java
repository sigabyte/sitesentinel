package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.evidence.NormalizedEvidence;
import com.cigabyte.sitesentinel.evidence.NormalizedEvidenceRepository;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.finding.FindingEvidence;
import com.cigabyte.sitesentinel.finding.FindingEvidenceRepository;
import com.cigabyte.sitesentinel.finding.FindingRepository;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskFinding;
import com.cigabyte.sitesentinel.risk.RiskFindingRepository;
import com.cigabyte.sitesentinel.risk.RiskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class RiskRemediationRecommendationContextBuilder {

    private final RiskRepository riskRepository;
    private final RiskFindingRepository riskFindingRepository;
    private final FindingRepository findingRepository;
    private final FindingEvidenceRepository
            findingEvidenceRepository;
    private final NormalizedEvidenceRepository
            normalizedEvidenceRepository;

    private final RiskRemediationRecommendationContextSanitizer
            contextSanitizer;

    public RiskRemediationRecommendationContextBuilder(
            RiskRepository riskRepository,
            RiskFindingRepository riskFindingRepository,
            FindingRepository findingRepository,
            FindingEvidenceRepository findingEvidenceRepository,
            NormalizedEvidenceRepository normalizedEvidenceRepository,
            RiskRemediationRecommendationContextSanitizer
                    contextSanitizer
    ) {
        this.riskRepository = riskRepository;
        this.riskFindingRepository =
                riskFindingRepository;
        this.findingRepository = findingRepository;
        this.findingEvidenceRepository =
                findingEvidenceRepository;
        this.normalizedEvidenceRepository =
                normalizedEvidenceRepository;
        this.contextSanitizer = contextSanitizer;
    }

    @Transactional(readOnly = true)
    public RiskRemediationRecommendationContext build(
            UUID monitoringRunId,
            UUID riskId
    ) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        UUID requiredRiskId =
                requireId(
                        riskId,
                        "Risk ID"
                );

        Risk risk = riskRepository.findById(
                        requiredRiskId
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Risk not found: "
                                        + requiredRiskId
                        )
                );

        if (!requiredMonitoringRunId.equals(
                risk.getMonitoringRunId()
        )) {
            throw new IllegalArgumentException(
                    "Risk does not belong to monitoring run: "
                            + requiredRiskId
            );
        }

        List<RiskRemediationRecommendationFindingContext>
                findingContexts =
                buildFindingContexts(risk);

        return RiskRemediationRecommendationContext.create(
                requiredMonitoringRunId,
                requiredRiskId,
                contextSanitizer.sanitizeRiskType(
                        risk.getRiskType()
                ),
                Objects.requireNonNull(
                        risk.getSeverity(),
                        "Risk severity is required."
                ),
                requireScore(
                        risk.getRiskScore(),
                        "Risk score"
                ),
                requireScore(
                        risk.getConfidenceScore(),
                        "Risk confidence score"
                ),
                contextSanitizer.sanitizeRiskRationale(
                        risk.getRationale()
                ),
                findingContexts
        );
    }

    private List<RiskRemediationRecommendationFindingContext>
    buildFindingContexts(Risk risk) {
        List<RiskFinding> riskFindingLinks =
                riskFindingRepository
                        .findByRiskIdOrderByCreatedAtAscIdAsc(
                                risk.getId()
                        );

        if (riskFindingLinks.isEmpty()) {
            return List.of();
        }

        List<UUID> findingIds =
                riskFindingLinks.stream()
                        .map(RiskFinding::getFindingId)
                        .toList();

        List<Finding> findings =
                findingRepository
                        .findByIdInAndMonitoringRunIdAndWebsiteIdOrderByFindingTypeAscCreatedAtAscIdAsc(
                                findingIds,
                                risk.getMonitoringRunId(),
                                risk.getWebsiteId()
                        );

        if (findings.size() != findingIds.size()) {
            throw new IllegalStateException(
                    "Risk finding traceability is incomplete for risk: "
                            + risk.getId()
            );
        }

        List<RiskRemediationRecommendationFindingContext>
                contexts = new ArrayList<>();

        for (Finding finding : findings) {
            contexts.add(
                    buildFindingContext(
                            risk,
                            finding
                    )
            );
        }

        return List.copyOf(contexts);
    }

    private RiskRemediationRecommendationFindingContext
    buildFindingContext(
            Risk risk,
            Finding finding
    ) {
        List<FindingEvidence> evidenceLinks =
                findingEvidenceRepository
                        .findByFindingIdOrderByCreatedAtAscIdAsc(
                                finding.getId()
                        );

        List<RiskRemediationRecommendationEvidenceContext>
                evidenceContexts = new ArrayList<>();

        for (FindingEvidence evidenceLink :
                evidenceLinks) {

            List<NormalizedEvidence>
                    normalizedEvidenceItems =
                    normalizedEvidenceRepository
                            .findByCollectedEvidenceIdAndMonitoringRunIdAndWebsiteIdOrderByNormalizedTypeAscCreatedAtAscIdAsc(
                                    evidenceLink
                                            .getCollectedEvidenceId(),
                                    risk.getMonitoringRunId(),
                                    risk.getWebsiteId()
                            );

            for (NormalizedEvidence normalizedEvidence :
                    normalizedEvidenceItems) {

                evidenceContexts.add(
                        new RiskRemediationRecommendationEvidenceContext(
                                normalizedEvidence.getId(),
                                normalizedEvidence
                                        .getCollectedEvidenceId(),
                                contextSanitizer
                                        .sanitizeNormalizedEvidenceType(
                                                normalizedEvidence
                                                        .getNormalizedType()
                                        ),
                                contextSanitizer
                                        .sanitizeNormalizedEvidenceValue(
                                                normalizedEvidence
                                                        .getNormalizedValue()
                                        )
                        )
                );
            }
        }

        return new RiskRemediationRecommendationFindingContext(
                finding.getId(),
                contextSanitizer.sanitizeFindingType(
                        finding.getFindingType()
                ),
                contextSanitizer.sanitizeFindingTitle(
                        finding.getTitle()
                ),
                contextSanitizer
                        .sanitizeFindingDescription(
                                finding.getDescription()
                        ),
                requireScore(
                        finding.getConfidenceScore(),
                        "Finding confidence score"
                ),
                evidenceContexts
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

    private int requireScore(
            Integer value,
            String fieldName
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be between 0 and 100."
            );
        }

        return value;
    }
}