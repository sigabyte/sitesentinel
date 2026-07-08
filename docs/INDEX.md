# SiteSentinel Documentation Index

This index lists the active documentation set for the SiteSentinel project.

## Current Implementation Status

Sprint 5 is complete.

Sprint 6 is not open yet.

The current completed implementation scope is:

Monitoring Run Report Baseline

The recommended next sprint scope is:

Notification Event Baseline

The current implementation provides a working core assessment lifecycle with an explainable traceability review layer, a historical comparison baseline, and a controlled scheduled monitoring baseline:

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
↓
Assessment Comparison

The scheduled monitoring baseline follows this execution path:

Website
↓
MonitoringSchedule
↓
ScheduledMonitoringWorker
↓
MonitoringExecutionService
↓
MonitoringRun
↓
Existing Assessment Lifecycle

## Implemented Baseline Through Sprint 4

- Website registration
- Website detail view
- Monitoring run execution
- Real HTTP evidence collection
- Homepage scan
- robots.txt scan
- sitemap.xml scan
- Scanner execution outcome evidence
- Scanner configuration through application properties
- Scanner safety guardrails
- Manual redirect target validation
- Collected evidence persistence
- Normalized evidence generation
- Rule-based finding generation
- Rule-based risk evaluation
- Rule-based trust assessment
- Evidence-to-finding traceability
- Finding-to-risk traceability
- Risk-to-trust-assessment traceability
- Dashboard execution visibility
- Website lifecycle summary
- Monitoring run lifecycle summary
- Assessment output ordering and evidence grouping
- Duplicate assessment output prevention
- Assessment outcome clarity
- Shared application stylesheet
- Finding detail traceability
- Normalized evidence detail traceability
- Collected evidence detail traceability
- Risk detail traceability
- Trust assessment detail traceability
- Traceability navigation between lifecycle outputs
- Monitoring run traceability summary dashboard
- Overall traceability QA status
- Coverage status labels for lifecycle traceability paths
- Completed monitoring run history access
- Latest completed run lookup per website
- Previous completed run lookup per current completed run
- Assessment comparison read model
- Trust status comparison
- Trust score delta calculation
- Trust score direction classification
- Finding type change comparison
- Risk type change comparison
- New, resolved, and unchanged change classification
- Monitoring run comparison page
- Website detail latest comparison summary
- Comparison links back to traceability detail pages
- Website-level monitoring schedule configuration
- Daily scheduled monitoring frequency
- Schedule enable and disable controls
- Safe scheduled monitoring worker
- Due schedule detection
- Overlap prevention for active monitoring runs
- Stale active run recovery
- Manual versus scheduled run trigger visibility
- Latest scheduled run visibility
- Schedule-to-run reference tracking
- Monitoring run report read model
- Monitoring run report status classification
- Full report mode for completed monitoring runs
- Limited report mode for pending, running, and failed monitoring runs
- Lifecycle output count summary
- Trust assessment summary
- Finding summary
- Risk summary
- Stage-level traceability summary
- Assessment comparison summary in report view
- Manual versus scheduled trigger visibility in report view
- Monitoring schedule reference visibility in report view
- Failure reason visibility in report view
- Browser-based monitoring run report page
- Report navigation from monitoring run detail
- Report navigation from website detail
- Report navigation from latest scheduled run area
- Report navigation from latest assessment comparison area
- Report navigation from comparison detail

## Latest Completed Sprint

### Sprint 5 — Monitoring Run Report Baseline

Sprint 5 is complete.

Sprint 5 introduced a browser-based, read-only monitoring run report baseline.

Sprint 5 added:

- Monitoring run report read model.
- Monitoring run report service.
- Monitoring run report controller.
- Browser-based report template.
- Full report mode for completed monitoring runs.
- Limited report mode for pending, running, and failed monitoring runs.
- Lifecycle output counts.
- Trust assessment summary.
- Finding summary.
- Risk summary.
- Stage-level traceability summary.
- Assessment comparison summary.
- Manual versus scheduled trigger visibility.
- Monitoring schedule reference visibility.
- Failure reason visibility.
- Report links from monitoring run detail.
- Report links from website detail.
- Report links from latest scheduled run area.
- Report links from latest assessment comparison area.
- Report links from comparison detail.

Sprint 5 did not add:

- PDF export.
- CSV export.
- Email notifications.
- WhatsApp notifications.
- Slack notifications.
- Webhook notifications.
- AI-written report summaries.
- Report approval workflow.
- Report versioning.
- Authentication or user access control.

## Completed Sprint

### Sprint 4 — Scheduled Monitoring & Recurring Scan Baseline

Sprint 4 is complete.

Sprint 4 introduced controlled scheduled monitoring while preserving the existing lifecycle, traceability, and comparison boundaries.

Sprint 4 added:

- Schedule configuration per website.
- Manual enable and disable controls for scheduled monitoring.
- Safe recurring scan execution.
- Prevention of overlapping monitoring runs for the same website.
- Stale active run recovery.
- Manual versus scheduled run visibility.
- Latest scheduled run visibility.
- Scheduled monitoring QA and closure documentation.

Sprint 4 did not add:

- Notification delivery.
- Report generation.
- CSV export.
- PDF export.
- Custom cron expression support.
- Multi-node scheduler coordination.
- Authentication or user access control.


### Sprint 3 — Assessment History & Change Comparison Baseline

Sprint 3 is complete.

Sprint 3 added a read-only comparison layer that compares a completed monitoring run against the previous completed monitoring run for the same website.

The comparison layer answers:

- What changed since the previous completed run?
- Did the trust status change?
- Did the trust score improve, decline, or stay the same?
- Which finding types are new?
- Which finding types are resolved?
- Which finding types are unchanged?
- Which risk types are new?
- Which risk types are resolved?
- Which risk types are unchanged?

Sprint 3 preserved the Sprint 1 lifecycle and Sprint 2 traceability boundaries.

It compares existing persisted lifecycle outputs only.  
It does not create new findings, risks, trust assessments, or evidence.

## Main Project Documents

### Architecture

- `docs/architecture/specifications/SSAS-v1.0.md` — SiteSentinel Software Architecture Specification.
- `docs/architecture/decisions/ADR-0001-product-positioning.md` — Product positioning decision.
- `docs/architecture/decisions/ADR-0002-agentless-v1.md` — V1 agentless architecture decision.
- `docs/architecture/decisions/ADR-0003-trust-engine-centered-architecture.md` — Trust-engine-centered architecture decision.
- `docs/architecture/decisions/ADR-0004-ai-analysis-boundary.md` — AI analysis boundary decision.

### Product

- `docs/product/PRODUCT-VISION.md` — Product vision.
- `docs/product/ENGINEERING-CONSTITUTION.md` — Engineering constitution.
- `docs/product/ENGINEERING-OS.md` — Engineering operating system.
- `docs/product/project-governance.md` — Project governance.
- `docs/product/ARCHITECTURE-REVIEW.md` — Architecture review and sprint readiness status.

### Engineering Log

- `docs/ENGINEERING_JOURNAL.md` — Engineering implementation notes and sprint closure history.

### Backlog

- `docs/product/backlog/FUTURE-BACKLOG.md` — Deferred items and future candidate work.

### Meetings

- `docs/meetings/Meeting-2026-07-04.md` — Meeting notes.

## Current Baseline

SiteSentinel now has a functional real-scan baseline, an explainable traceability review layer, a historical assessment comparison baseline, a controlled scheduled monitoring baseline, and a browser-based monitoring run report baseline.

The system can register a public website, execute a real HTTP scan, persist collected evidence, normalize evidence, generate findings, evaluate risks, produce a trust assessment, expose traceability across the persisted lifecycle, compare completed assessments against previous completed assessments, and execute recurring scheduled scans through the existing monitoring lifecycle.

## Next Phase

Recommended next scope:

Sprint 6 — Notification Event Baseline

Sprint 6 should build on the completed scheduled monitoring and report baselines by introducing an internal notification event baseline.

Recommended Sprint 6 candidate blocks:

- Notification event read/write model.
- Notification event creation after scheduled monitoring completion.
- Notification event creation after scheduled monitoring failure.
- Notification event list page.
- Notification event detail page.
- Notification event status lifecycle.
- Notification event QA and sprint closure documentation.

Sprint 6 should not introduce real email, WhatsApp, Slack, or webhook delivery yet.