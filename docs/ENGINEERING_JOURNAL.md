## Engineering implementation notes and Sprint closure history.

## Sprint 1 Closure — Core Assessment Lifecycle Implementation

### Status

Sprint 1 implementation is complete.

The core SiteSentinel assessment lifecycle has been implemented from website registration through real HTTP evidence collection, evidence analysis, risk evaluation, and trust assessment.

### Implemented Scope

- Website registration and website detail baseline
- Monitoring run execution lifecycle
- Real HTTP evidence collection engine
- Homepage, robots.txt, and sitemap.xml scan coverage
- Scanner execution outcome evidence
- Scanner configuration through application properties
- Scanner safety guardrails for local, private, loopback, special-use, and internal targets
- Manual redirect validation before following redirect targets
- Collected evidence persistence
- Normalized evidence generation
- Rule-based finding generation from collected evidence
- Rule-based risk evaluation from findings
- Rule-based trust assessment from risks
- Traceability across evidence, findings, risks, and trust assessments
- Dashboard execution visibility
- Website lifecycle summary counts
- Monitoring run lifecycle summary
- Assessment output ordering and evidence grouping
- Duplicate assessment output prevention
- Assessment outcome clarity
- Shared application stylesheet for readable assessment output

### Implemented Lifecycle

Website
↓
MonitoringRun
↓
HTTP Evidence Collection
↓
CollectedEvidence
↓
NormalizedEvidence
↓
Finding
↓
Risk
↓
TrustAssessment

### Preserved Architecture Boundaries

The Sprint 1 implementation preserves the SSAS responsibility boundaries:

- Evidence Collection Engine collects raw evidence only.
- Evidence Analysis Engine normalizes evidence and produces findings.
- Risk Evaluation Engine produces risk records from findings.
- Trust Evaluation Engine produces trust assessments from risks.
- Risk and trust decisions are not produced directly from raw collected evidence.

### Manual QA Checklist

The following flows were manually verified during Sprint 1 closure:

- Dashboard loads successfully.
- Website registration works for public domains.
- Duplicate domains are rejected.
- Local, private, loopback, and internal targets are rejected.
- Public website scan creates monitoring runs.
- HTTP evidence is collected from real website resources.
- Evidence is normalized.
- Findings are generated from real collected evidence.
- Risks are generated from findings.
- Trust assessments are generated from risks.
- Scanner configuration can skip robots.txt and sitemap.xml.
- Failed scans expose failure reason and assessment outcome.
- Monitoring run detail shows lifecycle output in readable order.
- Collected evidence is grouped by source type.
- Long raw evidence values remain readable in the UI.

### Deferred Items

The following items are intentionally deferred to future sprints:

- Authentication and user accounts
- Scheduled recurring scans
- Report export
- Finding detail traceability pages
- Risk detail traceability pages
- Trust assessment detail pages
- Historical scan comparison
- Advanced scanner signals
- External reputation integrations
- Notification engine
- AI-assisted analysis
- Database-level uniqueness constraints for idempotent outputs
- Automated service and integration tests for the scanner lifecycle

## Sprint 2 Block 2A — Finding Detail Traceability

### Status

Completed.

### Implemented Scope

- Added finding detail traceability page under monitoring run context.
- Added navigation from monitoring run findings table to finding detail page.
- Added evidence link counts for findings on monitoring run detail.
- Added repository and service methods to safely load findings within website and monitoring run boundaries.
- Added source collected evidence visibility for each finding.
- Added UI support for reviewing why a finding was produced.

### Traceability Coverage

The UI now supports the following navigation path:

Website
↓
MonitoringRun
↓
Finding
↓
Source CollectedEvidence

### Preserved Architecture Boundaries

- Evidence Collection Engine remains responsible only for collecting raw evidence.
- Evidence Analysis Engine remains responsible for producing normalized evidence and findings.
- Finding detail traceability only exposes existing relationships.
- No evidence generation logic was changed.
- No risk evaluation logic was changed.
- No trust assessment logic was changed.
- No new database migration was introduced.

---

## Sprint 2 Block 2B — Normalized Evidence Detail Traceability

### Status

Completed.

### Implemented Scope

- Added normalized evidence detail traceability page under monitoring run context.
- Added navigation from monitoring run normalized evidence table to normalized evidence detail page.
- Linked normalized evidence to its source collected evidence.
- Displayed normalized evidence records produced from the same collected evidence source.
- Displayed findings related through the same collected evidence source.
- Added repository and service methods to safely load normalized evidence, collected evidence, and related findings within website and monitoring run boundaries.

### Traceability Coverage

The UI now supports the following navigation path:

Website
↓
MonitoringRun
↓
NormalizedEvidence
↓
Source CollectedEvidence
↓
Related Findings

### Preserved Architecture Boundaries

- Normalized evidence detail does not create, modify, or reinterpret evidence.
- Existing normalized evidence and finding relationships are only exposed through UI traceability.
- Evidence Collection Engine remains unchanged.
- Evidence Analysis Engine remains unchanged.
- Risk and trust assessment logic remains unchanged.
- No new database migration was introduced.

---

## Sprint 2 Block 2C — Traceability Navigation Hardening

### Status

Completed.

### Implemented Scope

- Added collected evidence detail traceability page.
- Added navigation from collected evidence to normalized evidence.
- Added navigation from collected evidence to related findings.
- Added navigation from finding detail source evidence to normalized evidence.
- Added navigation from finding detail source evidence to collected evidence detail.
- Added navigation from normalized evidence detail back to collected evidence detail.
- Added monitoring run detail links from collected evidence rows to collected evidence traceability.

### Traceability Coverage

The UI now supports the following navigation paths:

Website
↓
MonitoringRun
↓
CollectedEvidence
↓
NormalizedEvidence
↓
Related Findings

Finding
↓
Source CollectedEvidence
↓
NormalizedEvidence

NormalizedEvidence
↓
Source CollectedEvidence
↓
Related Findings

### Preserved Architecture Boundaries

- Evidence Collection Engine remains responsible only for raw evidence collection.
- Evidence Analysis Engine remains responsible for normalized evidence and findings.
- Traceability navigation does not create, modify, or reinterpret assessment outputs.
- No new database table or migration was introduced.
- No risk or trust evaluation logic was changed.

## Sprint 2 Block 2D — Risk Detail Traceability

### Status

Completed.

### Implemented Scope

- Added risk detail traceability page under monitoring run context.
- Added navigation from monitoring run risks table to risk detail page.
- Linked each risk to its source findings through the risk_finding table.
- Displayed source collected evidence behind each finding linked to a risk.
- Displayed normalized evidence produced from each collected evidence item.
- Added repository and service methods to safely load risks within website and monitoring run boundaries.

### Traceability Coverage

The UI now supports the following navigation path:

Website
↓
MonitoringRun
↓
Risk
↓
Source Findings
↓
Source CollectedEvidence
↓
NormalizedEvidence

### Preserved Architecture Boundaries

- Risk Evaluation Engine remains responsible for producing risks.
- Risk detail traceability only exposes existing risk-to-finding relationships.
- Finding-to-evidence and evidence-to-normalized-evidence relationships are reused.
- No risk generation logic was changed.
- No trust assessment logic was changed.
- No database migration was introduced.

## Sprint 2 Block 2E — Trust Assessment Detail Traceability

### Status

Completed.

### Implemented Scope

- Added trust assessment detail traceability page under monitoring run context.
- Added navigation from latest trust assessment summary to trust assessment detail.
- Added navigation from trust assessments table to trust assessment detail.
- Linked each trust assessment to its source risks through the trust_assessment_risk table.
- Displayed source findings behind each risk linked to a trust assessment.
- Displayed source collected evidence behind each finding.
- Displayed normalized evidence produced from each collected evidence item.
- Added repository and service methods to safely load trust assessments and risks within website and monitoring run boundaries.

### Traceability Coverage

The UI now supports the following navigation path:

Website
↓
MonitoringRun
↓
TrustAssessment
↓
Source Risks
↓
Source Findings
↓
Source CollectedEvidence
↓
NormalizedEvidence

### Preserved Architecture Boundaries

- Trust Evaluation Engine remains responsible for producing trust assessments.
- Trust assessment detail traceability only exposes existing trust-to-risk relationships.
- Risk-to-finding, finding-to-evidence, and evidence-to-normalized-evidence relationships are reused.
- No trust scoring logic was changed.
- No risk evaluation logic was changed.
- No evidence analysis logic was changed.
- No database migration was introduced.

## Sprint 2 Block 2F — Traceability Summary Dashboard

### Status

Completed.

### Implemented Scope

- Added run-level traceability summary to monitoring run detail.
- Added coverage counts for collected evidence to normalized evidence.
- Added coverage counts for findings to source evidence.
- Added coverage counts for risks to source findings.
- Added coverage counts for trust assessments to source risks.
- Added standardized traceability coverage status labels.
- Added same-page navigation anchors for trust assessments, risks, findings, and normalized evidence.
- Added finding link counts to the monitoring run risks table.

### Traceability Coverage

The monitoring run detail page now summarizes the following paths:

CollectedEvidence
↓
NormalizedEvidence

Finding
↓
Source CollectedEvidence

Risk
↓
Source Finding

TrustAssessment
↓
Source Risk

### Coverage Status Values

- AVAILABLE: all records at the source level have at least one linked downstream record.
- PARTIAL: some source records have linked downstream records, but not all.
- MISSING: source records exist, but no linked downstream records exist.
- NO_SOURCE_DATA: no records exist at the source level.

### Preserved Architecture Boundaries

- Traceability summary reads existing persisted lifecycle outputs.
- No evidence collection logic was changed.
- No evidence analysis logic was changed.
- No risk evaluation logic was changed.
- No trust assessment logic was changed.
- No database migration was introduced.

## Sprint 2 Block 2G — Traceability QA & Consistency Hardening

### Status

Completed.

### Implemented Scope

- Added overall traceability status to monitoring run detail.
- Added overall traceability status description for QA visibility.
- Standardized coverage status rendering in the traceability summary table.
- Added visual distinction for AVAILABLE, PARTIAL, MISSING, and NO_SOURCE_DATA coverage states.
- Added QA note explaining that PARTIAL or MISSING coverage may be valid for records that are not expected to produce downstream lifecycle output.

### Overall Traceability Status Values

- TRACEABILITY_AVAILABLE: lifecycle output has usable traceability links across the monitored lifecycle.
- TRACEABILITY_PARTIAL: some lifecycle outputs are linked, but not all source records have downstream traceability.
- TRACEABILITY_MISSING_LINKS: one or more lifecycle stages produced records without downstream traceability links.
- NO_TRACEABILITY_SOURCE_DATA: no source records exist yet for traceability evaluation.

### Preserved Architecture Boundaries

- No evidence collection logic was changed.
- No evidence analysis logic was changed.
- No finding generation logic was changed.
- No risk evaluation logic was changed.
- No trust assessment logic was changed.
- No database migration was introduced.
- This block only improves traceability review clarity and QA consistency.

## Sprint 2 Closure — Explainable Traceability Layer

### Status

Sprint 2 implementation is complete.

Sprint 2 extended the Sprint 1 core assessment lifecycle with a full explainable traceability review layer.

### Implemented Scope

- Finding detail traceability.
- Normalized evidence detail traceability.
- Collected evidence detail traceability.
- Traceability navigation hardening between evidence, normalized evidence, findings, risks, and trust assessments.
- Risk detail traceability.
- Trust assessment detail traceability.
- Monitoring run traceability summary dashboard.
- Overall traceability QA status.
- Coverage status labels for lifecycle traceability paths.
- Same-page review anchors for monitoring run assessment outputs.

### Implemented Traceability Paths

Website
↓
MonitoringRun
↓
CollectedEvidence
↓
NormalizedEvidence

Website
↓
MonitoringRun
↓
Finding
↓
Source CollectedEvidence
↓
NormalizedEvidence

Website
↓
MonitoringRun
↓
Risk
↓
Source Findings
↓
Source CollectedEvidence
↓
NormalizedEvidence

Website
↓
MonitoringRun
↓
TrustAssessment
↓
Source Risks
↓
Source Findings
↓
Source CollectedEvidence
↓
NormalizedEvidence

### Run-Level Traceability Summary

Monitoring run detail now summarizes:

- Collected Evidence → Normalized Evidence
- Findings → Source Evidence
- Risks → Source Findings
- Trust Assessments → Source Risks

Supported coverage status values:

- AVAILABLE
- PARTIAL
- MISSING
- NO_SOURCE_DATA

Supported overall traceability status values:

- TRACEABILITY_AVAILABLE
- TRACEABILITY_PARTIAL
- TRACEABILITY_MISSING_LINKS
- NO_TRACEABILITY_SOURCE_DATA

### Preserved Architecture Boundaries

- Evidence Collection Engine remains responsible only for collecting raw evidence.
- Evidence Analysis Engine remains responsible for normalized evidence and findings.
- Risk Evaluation Engine remains responsible for producing risks from findings.
- Trust Evaluation Engine remains responsible for producing trust assessments from risks.
- Traceability pages only expose persisted lifecycle relationships.
- Traceability UI does not reinterpret evidence, findings, risks, or trust assessments.
- No database migration was introduced.
- No scoring logic was changed.
- No engine responsibility boundary was changed.

### Manual QA Checklist

The following flows were manually verified during Sprint 2:

- Finding detail traceability opens from monitoring run detail.
- Finding detail shows linked source collected evidence.
- Normalized evidence detail opens from monitoring run detail.
- Normalized evidence detail shows source collected evidence and related findings.
- Collected evidence detail shows normalized evidence and related findings.
- Risk detail shows source findings and their evidence chain.
- Trust assessment detail shows source risks, findings, evidence, and normalized evidence.
- Monitoring run traceability summary shows lifecycle coverage counts.
- Overall traceability status appears correctly.
- Traceability navigation links preserve website and monitoring run context.
- Sprint 2 traceability pages compile and run successfully.

### Deferred Items

The following items remain deferred to future sprints:

- Historical scan comparison.
- Scheduled recurring scans.
- Report export.
- CSV export.
- PDF export.
- Authentication and user access control.
- Advanced scanner signals.
- External reputation integrations.
- Notification engine.
- Automated integration tests for the full traceability lifecycle.

## Next Phase

Sprint 3 should build on the Sprint 1 lifecycle foundation and Sprint 2 traceability layer without replacing either.

Recommended future candidates are recorded in:

`docs/product/backlog/FUTURE-BACKLOG.md`

---

## Sprint 3 Opening — Assessment History & Change Comparison Baseline

### Status

Sprint 3 is open.

### Approved Scope

Sprint 3 will introduce the first historical comparison baseline for SiteSentinel.

The goal is to compare a completed monitoring run against the previous completed monitoring run for the same website.

### Sprint 3 Objective

Sprint 3 should help the user understand what changed between two completed assessments of the same website.

The comparison baseline should answer:

- Whether a previous completed monitoring run exists.
- Which completed run is being used as the comparison baseline.
- Whether the trust status changed.
- Whether the trust score improved, declined, or stayed the same.
- Which finding types are new.
- Which finding types are resolved.
- Which finding types are unchanged.
- Which risk types are new.
- Which risk types are resolved.
- Which risk types are unchanged.

### Planned Implementation Blocks

- Block 3A — Documentation alignment.
- Block 3B — Monitoring run history access.
- Block 3C — Comparison read model.
- Block 3D — Monitoring run comparison page.
- Block 3E — Website detail latest comparison summary.
- Block 3F — Sprint 3 QA and closure documentation.

### Architecture Boundary

Sprint 3 must remain a read-oriented comparison layer.

The comparison layer must read existing persisted lifecycle outputs only:

Website
↓
MonitoringRun
↓
CollectedEvidence
↓
NormalizedEvidence
↓
Finding
↓
Risk
↓
TrustAssessment

The comparison layer must not:

- Collect new evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Produce trust assessments.
- Modify trust scores.
- Reinterpret raw evidence.
- Replace Sprint 1 lifecycle responsibilities.
- Replace Sprint 2 traceability responsibilities.

### Comparison Baseline Rule

Only completed monitoring runs should be used for comparison.

Failed, pending, or running monitoring runs should not be selected as the previous comparison baseline.

### Deferred Items

The following items remain deferred until after the Sprint 3 comparison baseline:

- Scheduled recurring scans.
- Report export.
- CSV export.
- PDF export.
- Authentication and user access control.
- Notification engine.
- Advanced scanner signals.
- External reputation integrations.
- AI-assisted analysis.