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