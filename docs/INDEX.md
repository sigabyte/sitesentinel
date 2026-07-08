# SiteSentinel Documentation Index

This index lists the active documentation set for the SiteSentinel project.

## Current Implementation Status

Sprint 2 is complete.

Sprint 3 is now open with the following scope:

Assessment History & Change Comparison Baseline

The current implementation provides a working core assessment lifecycle with an explainable traceability review layer:

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

## Implemented Baseline Through Sprint 2

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

## Active Sprint

### Sprint 3 — Assessment History & Change Comparison Baseline

Sprint 3 should build on the Sprint 1 lifecycle foundation and Sprint 2 traceability layer without replacing either.

The goal of Sprint 3 is to introduce a baseline for comparing a completed monitoring run against the previous completed monitoring run for the same website.

Sprint 3 should answer:

- What changed since the previous completed run?
- Did the trust status change?
- Did the trust score improve or decline?
- Which finding types are new?
- Which finding types are resolved?
- Which finding types are unchanged?
- Which risk types are new?
- Which risk types are resolved?
- Which risk types are unchanged?

Sprint 3 must remain read-oriented.

It should compare existing persisted lifecycle outputs.  
It must not create new findings, risks, or trust assessments.

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

SiteSentinel now has a functional real-scan baseline and an explainable traceability review layer.

The system can register a public website, execute a real HTTP scan, persist collected evidence, normalize evidence, generate findings, evaluate risks, produce a trust assessment, and expose traceability across the persisted lifecycle.

## Next Phase

Sprint 3 should implement the first historical comparison baseline.

Recommended Sprint 3 implementation blocks:

- Block 3A — Documentation alignment
- Block 3B — Monitoring run history access
- Block 3C — Comparison read model
- Block 3D — Monitoring run comparison page
- Block 3E — Website detail latest comparison summary
- Block 3F — Sprint 3 QA and closure documentation