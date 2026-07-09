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

---

## Sprint 3 Closure — Assessment History & Change Comparison Baseline

### Status

Sprint 3 is complete.

### Completed Scope

Sprint 3 introduced the first historical comparison baseline for SiteSentinel.

The system can now compare a completed monitoring run against the previous completed monitoring run for the same website.

### Implemented Blocks

- Block 3A — Documentation alignment.
- Block 3B — Monitoring run history access.
- Block 3C — Assessment comparison read model.
- Block 3D — Monitoring run comparison page.
- Block 3E — Website detail latest comparison summary.
- Block 3F — QA and closure documentation.

### Implemented Capabilities

Sprint 3 added:

- Completed monitoring run history access.
- Latest completed run lookup per website.
- Previous completed run lookup per current completed run.
- Read-only assessment comparison model.
- Comparison status handling.
- Trust status comparison.
- Trust score delta calculation.
- Trust score direction classification.
- Finding type comparison.
- Risk type comparison.
- New, resolved, and unchanged change classification.
- Full monitoring run comparison page.
- Comparison links from monitoring run detail.
- Latest comparison summary on website detail.
- Links from comparison output back to existing traceability pages.

### Comparison Statuses

The Sprint 3 comparison layer supports:

- `AVAILABLE`
- `CURRENT_RUN_NOT_COMPLETED`
- `NO_PREVIOUS_COMPLETED_RUN`

### Change Statuses

Finding and risk comparison items support:

- `NEW`
- `RESOLVED`
- `UNCHANGED`

### Architecture Boundary Preserved

Sprint 3 remains a read-oriented comparison layer.

The comparison layer reads existing persisted lifecycle outputs only:

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

Sprint 3 does not:

- Collect new evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Produce trust assessments.
- Modify trust scores.
- Reinterpret raw evidence.
- Replace the Sprint 1 lifecycle.
- Replace the Sprint 2 traceability layer.

### Baseline Selection Rule

Only completed monitoring runs are eligible for comparison.

Failed, pending, or running monitoring runs are not used as the previous comparison baseline.

### Manual QA Checklist

Verified behaviours:

- Website detail page remains accessible.
- Monitoring run detail page remains accessible.
- Full comparison page is accessible from monitoring run detail.
- Latest comparison summary appears on website detail.
- Websites without completed runs show comparison unavailable state.
- Websites with one completed run show no previous completed run state.
- Websites with two or more completed runs show comparison summary.
- Trust status comparison is displayed.
- Trust score delta is displayed.
- Finding changes are grouped by finding type.
- Risk changes are grouped by risk type.
- Current and previous traceability links remain available.
- Existing Sprint 2 traceability pages remain linked and accessible.

### Outcome

Sprint 3 establishes the historical assessment comparison foundation required before scheduled monitoring, notification, and reporting features.

The product can now answer:

- What changed since the previous completed scan?
- Did trust status change?
- Did trust score improve, decline, or remain unchanged?
- Which finding types are new?
- Which finding types are resolved?
- Which finding types are unchanged?
- Which risk types are new?
- Which risk types are resolved?
- Which risk types are unchanged?

### Deferred Items

The following items remain deferred:

- Scheduled recurring scans.
- Report export.
- CSV export.
- PDF export.
- Authentication and user access control.
- Notification engine.
- Advanced scanner signals.
- External reputation integrations.
- AI-assisted analysis.


---

## Sprint 4 Opening — Scheduled Monitoring & Recurring Scan Baseline

### Status

Sprint 4 is open.

### Objective

Sprint 4 introduces the first scheduled monitoring baseline for SiteSentinel.

The goal is to allow websites to be monitored on a recurring schedule while preserving the existing Sprint 1 lifecycle, Sprint 2 traceability layer, and Sprint 3 comparison baseline.

Sprint 4 should not create a new assessment engine.

It should introduce a scheduling layer that safely triggers the existing monitoring lifecycle.

### Product Goal

Sprint 4 should allow the system to answer:

- Which websites have scheduled monitoring enabled?
- When is the next scheduled run expected?
- When was the schedule last triggered?
- Was a monitoring run started manually or by schedule?
- Did the scheduled run complete using the existing lifecycle?
- Did the completed scheduled run become eligible for Sprint 3 comparison?
- Was overlapping execution prevented for the same website?

### Architecture Boundary

Sprint 4 must remain a scheduling and orchestration layer.

The scheduled monitoring layer may:

- Store schedule configuration per website.
- Enable scheduled monitoring for a website.
- Disable scheduled monitoring for a website.
- Identify due schedules.
- Trigger a monitoring run through the existing monitoring execution service.
- Mark a scheduled run as manual or scheduled.
- Recalculate the next scheduled run time.
- Prevent overlapping monitoring runs for the same website.

The scheduled monitoring layer must not:

- Collect evidence directly.
- Normalize evidence directly.
- Generate findings directly.
- Evaluate risks directly.
- Produce trust assessments directly.
- Compare assessments directly.
- Replace the Sprint 1 monitoring lifecycle.
- Replace the Sprint 2 traceability layer.
- Replace the Sprint 3 comparison layer.
- Send notifications.
- Generate reports.
- Export CSV or PDF output.

### Required Execution Boundary

Scheduled monitoring should follow this execution path:

Website Schedule
↓
Scheduler
↓
MonitoringExecutionService
↓
Existing Sprint 1 Lifecycle
↓
Existing Sprint 2 Traceability
↓
Existing Sprint 3 Comparison Eligibility

The scheduler should only decide when a scan is due.

The existing lifecycle should still decide how evidence, findings, risks, and trust assessments are created.

### Planned Implementation Blocks

- Block 4A — Documentation alignment.
- Block 4B — Scheduling domain model and database baseline.
- Block 4C — Website-level schedule enable and disable controls.
- Block 4D — Safe scheduled execution worker.
- Block 4E — Scheduled run visibility in UI.
- Block 4F — Sprint 4 QA and closure documentation.

### Initial Scheduling Scope

Sprint 4 should start with a controlled baseline.

Supported in Sprint 4:

- One schedule per website.
- Enable or disable schedule.
- Daily frequency.
- Next run timestamp.
- Last triggered timestamp.
- Last scheduled monitoring run reference.
- Manual run versus scheduled run distinction.
- Overlap prevention for the same website.

Not supported in Sprint 4:

- Custom cron expressions.
- Multiple schedules per website.
- Email notifications.
- WhatsApp notifications.
- Slack notifications.
- Webhook notifications.
- PDF reports.
- CSV exports.
- Retry dashboard.
- Distributed locking.
- Multi-node scheduler coordination.
- User permissions.
- Advanced failure analytics.

### Data Model Direction

Sprint 4 should introduce a dedicated schedule model.

Expected baseline entity:

MonitoringSchedule

Expected relationship:

Website
↓
MonitoringSchedule
↓
MonitoringRun

MonitoringRun should also record whether it was triggered manually or by schedule.

### Safety Rule

A scheduled run must not start if the same website already has a pending or running monitoring run.

This prevents overlapping scans and protects the integrity of lifecycle output.

### Deferred Items

The following items remain deferred until after the Sprint 4 scheduled monitoring baseline:

- Email notification.
- WhatsApp notification.
- Slack notification.
- Webhook notification.
- PDF report generation.
- CSV export.
- Custom cron expression support.
- Retry policy dashboard.
- Distributed locking.
- Multi-node scheduling.
- Authentication and user access control.
- Advanced scanner signals.
- External reputation integrations.
- AI-assisted analysis.


---

## Sprint 4 Closure — Scheduled Monitoring & Recurring Scan Baseline

### Status

Sprint 4 is complete.

### Completed Scope

Sprint 4 introduced the scheduled monitoring baseline for SiteSentinel.

The implementation added website-level recurring monitoring configuration while preserving the existing assessment lifecycle, traceability layer, and comparison baseline.

### Implemented Capabilities

Sprint 4 completed the following capabilities:

- One monitoring schedule per website.
- Daily scheduled monitoring frequency.
- Website-level schedule enable action.
- Website-level schedule disable action.
- Schedule status visibility.
- Next scheduled run visibility.
- Last triggered timestamp visibility.
- Latest scheduled monitoring run visibility.
- Manual versus scheduled monitoring run distinction.
- Scheduled run trigger metadata.
- Schedule-to-run reference through monitoring schedule ID.
- Safe scheduled execution worker.
- Due schedule detection.
- Scheduled run execution through the existing MonitoringExecutionService.
- Overlap prevention for the same website.
- Stale active run recovery for old pending or running runs.
- Scheduled monitoring UI visibility in website detail.
- Trigger type visibility in monitoring run list.
- Trigger type visibility in monitoring run detail.

### Architecture Preserved

Sprint 4 preserved the existing lifecycle boundary.

Scheduled monitoring follows this path:

Website
↓
MonitoringSchedule
↓
ScheduledMonitoringWorker
↓
MonitoringExecutionService
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
Assessment Comparison Eligibility

The scheduled monitoring worker does not collect evidence directly.

The scheduled monitoring worker does not analyze evidence directly.

The scheduled monitoring worker does not generate findings directly.

The scheduled monitoring worker does not evaluate risks directly.

The scheduled monitoring worker does not produce trust assessments directly.

The scheduled monitoring worker does not perform assessment comparison directly.

### Data Model Added

Sprint 4 added the following scheduling baseline:

MonitoringSchedule

Fields include:

- Website reference.
- Schedule status.
- Schedule frequency.
- Next run timestamp.
- Last triggered timestamp.
- Last monitoring run reference.
- Last failure reason.
- Created timestamp.
- Updated timestamp.

Sprint 4 also extended MonitoringRun with:

- Trigger type.
- Monitoring schedule reference.

### Trigger Types

Monitoring runs can now be identified as:

- MANUAL
- SCHEDULED

Manual runs are started by user action.

Scheduled runs are started by the scheduled monitoring worker.

### Safety Rules

Sprint 4 introduced the following scheduled execution safety rules:

- A scheduled run must not start if the same website has an active pending or running monitoring run.
- A stale pending or running monitoring run may be recovered as failed when it exceeds the configured stale active run timeout.
- Scheduled execution must always enter the existing monitoring lifecycle through MonitoringExecutionService.
- Scheduled execution must not bypass lifecycle persistence.
- Scheduled execution must not bypass traceability generation.
- Scheduled execution must not bypass comparison eligibility.

### Stale Active Run Recovery

Sprint 4 added stale active run recovery to prevent old pending or running runs from blocking scheduled monitoring forever.

The configured baseline timeout is:

60 minutes

If an active run is older than the configured timeout, the scheduler marks it as failed with a recovery reason before attempting scheduled execution again.

This protects the system from development interruptions, process shutdowns, and unfinished monitoring runs.

### QA Results

The following Sprint 4 QA checks were completed:

- Schedule can be enabled from website detail.
- Schedule can be disabled from website detail.
- Enabled schedule receives a next run timestamp.
- Scheduler detects due schedules.
- Scheduler does not start duplicate runs for the same website.
- Scheduler blocks execution when a real active run exists.
- Scheduler recovers stale pending runs.
- Scheduled execution creates a monitoring run.
- Scheduled run uses trigger type SCHEDULED.
- Manual run uses trigger type MANUAL.
- Scheduled run stores monitoring schedule ID.
- Schedule stores last monitoring run ID.
- Schedule stores last triggered timestamp.
- Schedule recalculates next run timestamp after execution.
- Website detail displays scheduled monitoring status.
- Website detail displays latest scheduled run.
- Monitoring run list displays trigger type.
- Monitoring run detail displays trigger type.
- Monitoring run detail displays schedule ID for scheduled runs.
- Scheduled run output remains compatible with lifecycle, traceability, and comparison views.

### Known Limitations

Sprint 4 intentionally does not include:

- Custom cron expressions.
- Multiple schedules per website.
- Email notifications.
- WhatsApp notifications.
- Slack notifications.
- Webhook notifications.
- PDF report generation.
- CSV export.
- Retry dashboard.
- Distributed locking.
- Multi-node scheduler coordination.
- Authentication.
- User access control.
- Advanced failure analytics.
- AI-assisted analysis.

### Closure Decision

Sprint 4 is accepted as the scheduled monitoring baseline.

Future work can build on this baseline without changing the Sprint 1 lifecycle, 
Sprint 2 traceability layer, or Sprint 3 comparison baseline.


---

## Sprint 5 Opening — Monitoring Run Report Baseline

### Status

Sprint 5 is open.

### Selected Scope

Sprint 5 will introduce a read-only monitoring run report baseline.

The report baseline should make existing monitoring output easier to review without changing the assessment lifecycle or generating new assessment data.

### Product Goal

Sprint 5 should allow a user to open a browser-based report for a monitoring run and understand:

- Which website was assessed.
- When the run started and completed.
- Whether the run was manual or scheduled.
- Whether the run completed successfully or failed.
- What trust assessment was produced.
- What findings were produced.
- What risks were produced.
- Whether comparison data is available.
- What changed compared with the previous completed run.
- Whether traceability is available for the run output.

### Architecture Boundary

Sprint 5 is a reporting and presentation layer only.

The report layer may:

- Read monitoring runs.
- Read websites.
- Read collected evidence.
- Read normalized evidence.
- Read findings.
- Read risks.
- Read trust assessments.
- Read comparison summaries.
- Display traceability availability.
- Link to existing traceability detail pages.

The report layer must not:

- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify monitoring runs.
- Modify evidence.
- Modify findings.
- Modify risks.
- Modify trust assessments.
- Modify historical comparison output.

### Initial Sprint 5 Candidate Blocks

Sprint 5 should proceed in controlled blocks:

- Block 5A — Documentation and stale UI cleanup.
- Block 5B — Monitoring run report read model baseline.
- Block 5C — Monitoring run report service.
- Block 5D — Monitoring run report controller.
- Block 5E — Browser-based monitoring run report template.
- Block 5F — Report navigation links.
- Block 5G — QA and sprint closure documentation.

### Explicitly Deferred From Sprint 5

The following items remain deferred:

- PDF export.
- CSV export.
- Email delivery.
- WhatsApp delivery.
- Slack delivery.
- Webhook delivery.
- AI-assisted report writing.
- New scanner signals.
- External reputation integrations.
- Authentication and user access control.
- Report versioning.
- Report approval workflow.

### Sprint 5 Decision

Sprint 5 will start with a browser-based read-only report.

The first implementation goal is clarity, traceability preservation, and lifecycle-safe reporting.

---

## Sprint 5 Closure — Monitoring Run Report Baseline

### Status

Sprint 5 is complete.

### Completed Scope

Sprint 5 introduced a browser-based, read-only monitoring run report baseline.

The report baseline summarizes existing persisted lifecycle output without changing the assessment lifecycle, traceability layer, comparison layer, or scheduled monitoring baseline.

### Completed Blocks

Sprint 5 completed the following blocks:

- Block 5A — Documentation and stale UI cleanup.
- Block 5B — Monitoring run report read model baseline.
- Block 5C — Monitoring run report service.
- Block 5D — Monitoring run report controller.
- Block 5E — Browser-based monitoring run report template.
- Block 5F — Report navigation links.
- Block 5G — QA and sprint closure documentation.

### Implemented Report Capabilities

Sprint 5 added:

- Monitoring run report read model.
- Report status classification.
- Full report mode for completed monitoring runs.
- Limited report mode for pending, running, or failed monitoring runs.
- Lifecycle output counts.
- Trust assessment summary.
- Finding summary.
- Risk summary.
- Stage-level traceability summary.
- Assessment comparison summary.
- Manual versus scheduled trigger visibility.
- Monitoring schedule reference visibility for scheduled runs.
- Failure reason visibility for failed runs.
- Report route per monitoring run.
- Browser-based report template.
- Report links from monitoring run detail.
- Report links from website detail.
- Report links from latest scheduled run area.
- Report links from latest assessment comparison area.
- Report links from comparison detail.

### Architecture Boundary

Sprint 5 remained a reporting and presentation layer only.

The report layer reads existing persisted output from:

- Websites.
- Monitoring runs.
- Collected evidence counts.
- Normalized evidence counts.
- Findings.
- Risks.
- Trust assessments.
- Assessment comparison summaries.

The report layer does not:

- Start monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify monitoring runs.
- Modify evidence.
- Modify findings.
- Modify risks.
- Modify trust assessments.
- Persist comparison output.

### QA Result

Sprint 5 QA passed.

Validated behavior:

- Completed monitoring run reports open as full reports.
- Scheduled monitoring run reports show scheduled trigger metadata.
- Manual monitoring run reports show manual trigger metadata.
- Report links from monitoring run detail work.
- Report links from website detail work.
- Report links from comparison detail work.
- Report links back to traceability pages work.
- Report output remains read-only.
- Existing lifecycle execution remains unchanged.
- Existing scheduled monitoring behavior remains unchanged.
- Existing comparison behavior remains unchanged.

### Deferred Items

Sprint 5 intentionally deferred:

- PDF export.
- CSV export.
- Email delivery.
- WhatsApp delivery.
- Slack delivery.
- Webhook delivery.
- AI-assisted report writing.
- Report approval workflow.
- Report versioning.
- Authentication.
- User access control.
- Advanced scanner signals.

### Closure Decision

Sprint 5 is approved and complete.

The project can move to Sprint 6.

---

## Sprint 6 Opening — Notification Event Baseline

### Status

Sprint 6 is opened.

### Approved Sprint 6 Scope

Sprint 6 will introduce the Notification Event Baseline.

The goal of Sprint 6 is to persist important SiteSentinel platform events as in-application notification events and make them visible in the user interface.

Sprint 6 is not a notification delivery sprint.

### Why Sprint 6 Exists

The platform can now:

- Execute monitoring runs.
- Preserve lifecycle output.
- Expose traceability.
- Compare completed assessments.
- Execute scheduled monitoring.
- Present monitoring run reports.

The next product need is to make important monitoring outcomes visible as structured notification events.

Notification events should help users notice important changes without reading every run detail manually.

### Approved Notification Event Baseline

Sprint 6 may add:

- Notification event persistence.
- Notification event domain model.
- Notification event repository.
- Notification event service.
- Notification event generation rules.
- Notification events linked to websites.
- Notification events linked to monitoring runs.
- Notification event visibility on dashboard.
- Notification event visibility on website detail.
- Notification event visibility on monitoring run detail or report pages.
- Basic deduplication to avoid repeated event spam from scheduled monitoring.

### Initial Notification Event Types

Sprint 6 may introduce baseline events for:

- Monitoring run failed.
- High-risk trust assessment detected.
- Trust status changed.
- Trust score declined.
- New risk type detected.

The initial rule set should remain small and explainable.

### Architecture Boundary

The notification event layer may:

- Read monitoring run metadata.
- Read website metadata.
- Read trust assessment output.
- Read risk output.
- Read finding output.
- Read assessment comparison output.
- Persist notification event records.
- Display notification event records in the UI.

The notification event layer must not:

- Start monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify monitoring runs.
- Modify evidence.
- Modify findings.
- Modify risks.
- Modify trust assessments.
- Generate reports.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.
- Generate AI-written notification text.

### Explicitly Deferred From Sprint 6

The following items remain deferred:

- Email notification delivery.
- WhatsApp notification delivery.
- Slack notification delivery.
- Webhook delivery.
- Notification recipient preferences.
- User-specific notification routing.
- Authentication and user access control.
- Notification retry policies.
- Delivery status tracking.
- AI-generated notification summaries.
- Advanced notification policy engine.
- PDF export.
- CSV export.

### Initial Sprint 6 Candidate Blocks

Sprint 6 should proceed in controlled blocks:

- Block 6A — Documentation and notification boundary approval.
- Block 6B — Notification event persistence baseline.
- Block 6C — Notification event service baseline.
- Block 6D — Notification event generation rules.
- Block 6E — Monitoring lifecycle integration.
- Block 6F — Notification event UI visibility.
- Block 6G — QA and sprint closure documentation.

### Sprint 6 Decision

Sprint 6 will start with persisted in-application notification events.

The first implementation goal is visibility, traceability preservation, and lifecycle-safe 
notification event generation.

---

## Sprint 6 Closure — Notification Event Baseline

### Status

Sprint 6 is complete.

### Completed Scope

Sprint 6 introduced the Notification Event Baseline.

The platform can now persist important monitoring outcomes as structured in-application notification events and display them in the user interface.

Sprint 6 did not introduce external notification delivery.

### Completed Blocks

Sprint 6 completed the following blocks:

- Block 6A — Documentation and notification boundary approval.
- Block 6B — Notification event persistence baseline.
- Block 6C — Notification event service baseline.
- Block 6D — Notification event generation rules.
- Block 6E — Monitoring lifecycle integration.
- Block 6F — Notification event UI visibility.
- Block 6G — QA and sprint closure documentation.

### Implemented Capabilities

Sprint 6 added:

- Notification event database table.
- Notification event domain model.
- Notification event type enum.
- Notification event severity enum.
- Notification event status enum.
- Notification event repository.
- Notification event create request model.
- Notification event service.
- Notification event create-if-absent behavior.
- Notification event deduplication key support.
- Notification read/unread state model.
- Notification event generation service.
- Monitoring run failed notification rule.
- High-risk trust assessment notification rule.
- Trust status changed notification rule.
- Trust score declined notification rule.
- New risk type detected notification rule.
- Lifecycle-safe notification generation after completed monitoring runs.
- Lifecycle-safe notification generation after failed monitoring runs.
- Dashboard notification event visibility.
- Website detail notification event visibility.
- Monitoring run detail notification event visibility.
- Monitoring run report notification event visibility.
- Basic notification event styling.

### Implemented Notification Event Types

Sprint 6 implemented the following notification event types:

- MONITORING_RUN_FAILED
- HIGH_RISK_TRUST_ASSESSMENT
- TRUST_STATUS_CHANGED
- TRUST_SCORE_DECLINED
- NEW_RISK_TYPE_DETECTED

### Architecture Boundary Preserved

The notification event layer reads existing persisted lifecycle output and comparison output.

The notification event layer does not:

- Start monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify monitoring runs.
- Modify evidence.
- Modify findings.
- Modify risks.
- Modify trust assessments.
- Modify comparison output.
- Generate monitoring reports.
- Deliver external notifications.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.
- Generate AI-written notification text.

### Lifecycle Safety

Notification event generation is called after monitoring runs are marked completed or failed.

Notification generation failures are isolated from the monitoring lifecycle.

If notification generation fails, the monitoring run result remains completed or failed according to the core lifecycle result.

### QA Notes

The expected QA result is:

- Existing monitoring lifecycle remains stable.
- Manual monitoring runs continue to complete or fail as before.
- Scheduled monitoring continues to execute as before.
- Notification event generation does not break monitoring execution.
- Notification events are visible in the dashboard.
- Notification events are visible on website detail pages.
- Notification events are visible on monitoring run detail pages.
- Notification events are visible on monitoring run report pages.
- Empty notification states are displayed when no events exist.
- External notification delivery is not present.

### Deferred Items

The following remain deferred:

- Notification management page.
- Notification detail page.
- Mark as read/unread UI controls.
- Notification filtering by severity.
- Notification filtering by status.
- Email notification delivery.
- WhatsApp notification delivery.
- Slack notification delivery.
- Webhook delivery.
- Notification recipient preferences.
- User-specific notification routing.
- Delivery retry policies.
- Delivery status tracking.
- Advanced notification policy engine.
- AI-generated notification summaries.

### Sprint 6 Decision

Sprint 6 is complete.

The platform now has a persisted, in-application notification event baseline.

The recommended next sprint scope is Sprint 7 — Notification Management Baseline.

---

## Sprint 7 Opening — Notification Management Baseline

### Status

Sprint 7 is open.

### Sprint Goal

Sprint 7 introduces the Notification Management Baseline.

Sprint 6 created persisted in-application notification events.

Sprint 7 makes those notification events manageable from the UI.

The purpose of Sprint 7 is to allow the user to review, filter, inspect, and update the read/unread state of existing notification events.

Sprint 7 is not a notification delivery sprint.

### Why Sprint 7 Exists

The platform can now:

- Execute monitoring runs.
- Preserve lifecycle output.
- Expose traceability.
- Compare completed assessments.
- Execute scheduled monitoring.
- Present monitoring run reports.
- Generate persisted notification events.

The next product need is to make notification events operationally usable.

Notification management should help the user answer:

- Which notification events exist?
- Which notification events are unread?
- Which notification events are critical, high, warning, or informational?
- Which website or monitoring run produced a notification?
- What exactly does a notification event mean?
- Has the notification already been reviewed?

### Approved Notification Management Baseline

Sprint 7 may add:

- Notification event list page.
- Notification event detail page.
- Notification filtering by status.
- Notification filtering by severity.
- Notification filtering by status and severity together.
- Notification read action.
- Notification unread action.
- Dashboard navigation to notification management.
- Website-level navigation to notification management.
- Monitoring run and report navigation to notification event detail pages.
- Empty-state UI for notification management pages.

### Architecture Boundary

The notification management layer may:

- Read notification event records.
- Read website references linked to notification events.
- Read monitoring run references linked to notification events.
- Filter notification events by status.
- Filter notification events by severity.
- Display notification event detail.
- Update notification event status between READ and UNREAD.
- Link back to website detail pages.
- Link back to monitoring run detail pages.
- Link back to monitoring run report pages.

The notification management layer must not:

- Start monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify monitoring runs.
- Modify evidence.
- Modify normalized evidence.
- Modify findings.
- Modify risks.
- Modify trust assessments.
- Modify comparison output.
- Generate monitoring reports.
- Generate new notification event rules.
- Change notification event generation logic.
- Deliver external notifications.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.
- Manage notification recipients.
- Manage user preferences.
- Generate AI-written notification summaries.

### Explicitly Deferred From Sprint 7

The following items remain deferred:

- Email notification delivery.
- WhatsApp notification delivery.
- Slack notification delivery.
- Webhook delivery.
- Notification recipient preferences.
- User-specific notification routing.
- Notification subscriptions.
- Delivery retry policies.
- Delivery status tracking.
- Advanced notification policy engine.
- AI-generated notification summaries.
- Authentication and user access control.
- PDF export.
- CSV export.

### Sprint 7 Candidate Blocks

Sprint 7 should proceed in controlled blocks:

- Block 7A — Documentation and notification management boundary opening.
- Block 7B — Notification management query baseline.
- Block 7C — Notification management controller.
- Block 7D — Notification event list page.
- Block 7E — Notification event detail page.
- Block 7F — Notification management navigation integration.
- Block 7G — QA and sprint closure documentation.

### Sprint 7 Decision

Sprint 7 starts with in-application notification management.

The first implementation goal is reviewability, filtering, and read/unread management for existing notification events.

External notification delivery remains outside the Sprint 7 boundary.

---

## Sprint 7 Closure — Notification Management Baseline

### Status

Sprint 7 is complete.

### Completed Scope

Sprint 7 implemented the Notification Management Baseline.

The platform can now manage persisted in-application notification events created by the Sprint 6 notification event baseline.

### Completed Capabilities

Sprint 7 added:

- Notification event management list page.
- Notification event detail page.
- Status-based filtering.
- Severity-based filtering.
- Combined status and severity filtering.
- Website-context notification filtering.
- Monitoring-run-context notification filtering.
- Mark notification as read action.
- Mark notification as unread action.
- Dashboard navigation to notification management.
- Website detail navigation to website-filtered notification management.
- Monitoring run detail navigation to run-filtered notification management.
- Monitoring run report navigation to run-filtered notification management.
- Notification event detail links from existing notification visibility tables.
- Empty-state handling for notification management views.

### Implemented Files

Sprint 7 added or updated:

- `src/main/java/com/cigabyte/sitesentinel/notification/NotificationEventRepository.java`
- `src/main/java/com/cigabyte/sitesentinel/notification/NotificationEventService.java`
- `src/main/java/com/cigabyte/sitesentinel/notification/NotificationEventController.java`
- `src/main/resources/templates/notifications/list.html`
- `src/main/resources/templates/notifications/detail.html`
- `src/main/resources/templates/dashboard/index.html`
- `src/main/resources/templates/websites/detail.html`
- `src/main/resources/templates/monitoring-runs/detail.html`
- `src/main/resources/templates/reports/monitoring-run-report.html`

### Preserved Architecture Boundaries

Sprint 7 preserved the core SiteSentinel architecture.

Sprint 7 did not:

- Start monitoring runs from notification management.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify monitoring lifecycle output.
- Modify comparison output.
- Modify monitoring report generation.
- Change notification event generation rules.
- Add external notification delivery.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.
- Add user-specific notification preferences.
- Add recipient management.
- Add notification subscriptions.
- Add AI-generated notification summaries.

### Validation Performed

Sprint 7 validation covered:

- Application compilation.
- Dashboard notification management navigation.
- Notification management list page.
- Empty-state behavior.
- Status filter.
- Severity filter.
- Combined status and severity filter.
- Website-context filter.
- Monitoring-run-context filter.
- Notification detail page.
- Website link from notification detail.
- Monitoring run link from notification detail.
- Monitoring run report link from notification detail.
- Mark as read action.
- Mark as unread action.
- Dashboard unread notification count refresh.
- Notification detail links from dashboard.
- Notification detail links from website detail.
- Notification detail links from monitoring run detail.
- Notification detail links from monitoring run report.

### Result

Sprint 7 completed the in-application Notification Management Baseline.

The notification event layer is now reviewable, filterable, inspectable, and operationally manageable from the UI.

External notification delivery remains deferred.

---

## Sprint 8 Opening — Notification Delivery Readiness Baseline

### Status

Sprint 8 is open.

### Sprint Goal

Sprint 8 will introduce the Notification Delivery Readiness Baseline.

The goal is to prepare SiteSentinel for future external notification delivery without sending real external notifications yet.

Sprint 8 will model and record delivery attempts for existing notification events.

### Planned Scope

Sprint 8 may add:

- Notification delivery channel model.
- Notification delivery attempt status model.
- Notification delivery attempt persistence.
- Delivery attempt repository.
- Delivery attempt service.
- Simulated delivery attempt recording.
- Delivery attempt visibility from notification detail pages.
- Delivery readiness documentation.

### Intended Data Path

Sprint 8 will extend the notification layer with the following readiness path:

NotificationEvent  
↓  
NotificationDeliveryAttempt  
↓  
NotificationDeliveryAttemptRepository  
↓  
NotificationDeliveryAttemptService  
↓  
Notification Detail UI

### Preserved Architecture Boundaries

Sprint 8 must preserve the existing SiteSentinel monitoring lifecycle.

Sprint 8 must not:

- Start monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify comparison output.
- Modify monitoring run report output.
- Change notification event generation rules.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.
- Call external delivery APIs.
- Add recipient management.
- Add user-specific notification preferences.
- Add notification subscription rules.
- Add delivery retry scheduling.
- Add AI-generated notification summaries.

### External Delivery Boundary

Sprint 8 is not a real delivery sprint.

Email, WhatsApp, Slack, webhook, and other outbound delivery mechanisms remain deferred.

Sprint 8 may record simulated delivery attempts only.

### Sprint 8 Candidate Blocks

Sprint 8 is planned as:

- Block 8A — Documentation opening.
- Block 8B — Notification delivery attempt database baseline.
- Block 8C — Notification delivery attempt domain model.
- Block 8D — Notification delivery attempt service.
- Block 8E — Manual simulated delivery attempt action.
- Block 8F — Delivery attempt visibility on notification detail.
- Block 8G — QA and sprint closure documentation.

### Sprint 8 Decision

Sprint 8 starts with delivery readiness, not real delivery.

The first implementation goal is to model delivery attempts safely and make them visible from notification management.

External notification delivery remains outside the Sprint 8 boundary.

---

## Sprint 8 Closure — Notification Delivery Readiness Baseline

### Status

Sprint 8 is complete.

### Result

Notification Delivery Readiness Baseline has been implemented.

Sprint 8 introduced delivery attempt modeling, persistence, simulated delivery actions, 
and delivery attempt visibility for notification events.

### Implemented Scope

Sprint 8 implemented:

- Notification delivery channel enum.
- Notification delivery attempt status enum.
- Notification delivery attempt entity.
- Notification delivery attempt repository.
- Notification delivery attempt service.
- Notification delivery attempt database table.
- Manual simulated successful delivery attempt action.
- Manual simulated failed delivery attempt action.
- Manual skipped delivery attempt action.
- Delivery attempt count on notification detail.
- Delivery attempt history on notification detail.
- TELEGRAM as a modeled delivery channel.
- Flyway correction migration for TELEGRAM channel constraint.

### Database Changes

Sprint 8 added:

- `notification_delivery_attempts`

The table records delivery attempt readiness entries linked to existing notification events.

Important migration note:

- `V9__create_notification_delivery_attempts_table.sql` created the delivery attempt table.
- `V10__allow_telegram_notification_delivery_channel.sql` exists as an empty local repair-alignment migration 
because Flyway had already recorded version 10 with checksum 0 during local development.
- `V11__allow_telegram_notification_delivery_channel.sql` is the effective migration that updates the delivery 
channel check constraint to allow `TELEGRAM`.

### Implemented Data Path

Sprint 8 implemented the following readiness path:

NotificationEvent  
↓  
NotificationDeliveryAttempt  
↓  
NotificationDeliveryAttemptRepository  
↓  
NotificationDeliveryAttemptService  
↓  
NotificationEventController  
↓  
Notification Detail UI

### Supported Modeled Delivery Channels

Sprint 8 supports the following modeled delivery channels:

- EMAIL
- WHATSAPP
- SLACK
- WEBHOOK
- IN_APP
- TELEGRAM

### Supported Attempt Statuses

Sprint 8 supports the following delivery attempt statuses:

- PENDING
- SIMULATED_SUCCESS
- SIMULATED_FAILURE
- SKIPPED

### TELEGRAM Decision

TELEGRAM was added as a modeled notification delivery channel during Sprint 8.

Sprint 8 does not send Telegram messages.

Telegram Bot API integration remains deferred to a future delivery sprint.

### Preserved Architecture Boundaries

Sprint 8 preserved the monitoring lifecycle.

Sprint 8 did not change:

- Website monitoring execution.
- Scheduled monitoring execution.
- Evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment generation.
- Comparison output.
- Monitoring run report output.
- Notification event generation rules.

### External Delivery Boundary

Sprint 8 did not implement real outbound notification delivery.

Sprint 8 does not call:

- SMTP providers.
- WhatsApp APIs.
- Telegram Bot API.
- Slack APIs.
- Webhook URLs.
- External delivery APIs.

All delivery actions in Sprint 8 are simulated internal readiness records only.

### QA Result

Sprint 8 QA passed.

Verified behavior:

- Notification detail page opens successfully.
- Delivery readiness action is visible.
- Delivery channel dropdown includes TELEGRAM.
- Simulated success attempts are recorded.
- Simulated failure attempts are recorded.
- Skipped attempts are recorded.
- Delivery attempt count updates correctly.
- Delivery attempt history displays recorded attempts.
- Delivery attempt history is ordered from newest to oldest.
- Existing notification management actions still work.
- No real external notification delivery occurs.

### Sprint 8 Closure Decision

Sprint 8 is complete.

The system is now ready for a future real delivery sprint, but external delivery remains 
intentionally deferred.