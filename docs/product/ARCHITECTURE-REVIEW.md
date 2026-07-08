# SiteSentinel Architecture Review-1

## Sprint

Sprint 1 Closure

## Result

APPROVED

## Product Owner

Approved

## Architecture Status

Core Assessment Lifecycle Implemented

## Implementation Status

Sprint 0 established the approved architecture baseline.

Sprint 1 implemented the first working version of the SiteSentinel core assessment lifecycle.

The implemented lifecycle is:

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

## Sprint 1 Completion Notes

Sprint 1 completed the following architecture-aligned implementation areas:

- Website registration baseline
- Monitoring run execution lifecycle
- Real HTTP evidence collection
- Evidence normalization
- Finding generation
- Risk evaluation
- Trust assessment generation
- Traceability across evidence, findings, risks, and trust assessments
- Scanner configuration
- Scanner target safety guardrails
- Dashboard and detail page visibility
- Assessment outcome clarity

## Preserved Architecture Boundaries

- Evidence Collection Engine collects evidence only.
- Evidence Analysis Engine normalizes evidence and produces findings.
- Risk Evaluation Engine produces risks from findings.
- Trust Evaluation Engine produces trust assessments from risks.
- Raw evidence does not directly produce risk or trust decisions.

## Ready for Sprint 2

The project is ready to move to Sprint 2.

Sprint 2 should extend the implemented lifecycle without replacing the Sprint 1 foundation.

# SiteSentinel Architecture Review-2

## Sprint

Sprint 2 Closure

## Result

APPROVED

## Product Owner

Approved

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented

## Implementation Status

Sprint 0 established the approved architecture baseline.

Sprint 1 implemented the first working version of the SiteSentinel core assessment lifecycle.

Sprint 2 implemented the explainable traceability review layer across the persisted assessment lifecycle.

The implemented lifecycle is:

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

The implemented traceability review layer exposes:

CollectedEvidence
↓
NormalizedEvidence

Finding
↓
Source CollectedEvidence
↓
NormalizedEvidence

Risk
↓
Source Findings
↓
Source CollectedEvidence
↓
NormalizedEvidence

TrustAssessment
↓
Source Risks
↓
Source Findings
↓
Source CollectedEvidence
↓
NormalizedEvidence

## Sprint 2 Completion Notes

Sprint 2 completed the following architecture-aligned implementation areas:

- Finding detail traceability
- Normalized evidence detail traceability
- Collected evidence detail traceability
- Risk detail traceability
- Trust assessment detail traceability
- Traceability navigation between lifecycle outputs
- Monitoring run traceability summary dashboard
- Overall traceability QA status
- Coverage status labels for lifecycle traceability paths
- Same-page traceability review anchors

## Preserved Architecture Boundaries

- Evidence Collection Engine collects evidence only.
- Evidence Analysis Engine normalizes evidence and produces findings.
- Risk Evaluation Engine produces risks from findings.
- Trust Evaluation Engine produces trust assessments from risks.
- Traceability views expose persisted relationships only.
- Traceability views do not reinterpret evidence, findings, risks, or trust assessments.
- Raw evidence does not directly produce risk or trust decisions.

## Ready for Sprint 3

The project is ready to move to Sprint 3.

Sprint 3 should extend the implemented lifecycle and traceability foundation without replacing the 
Sprint 1 or Sprint 2 architecture baseline.

---

## Sprint 3 Architecture Review — Assessment History & Change Comparison Baseline

### Review Status

Approved.

Sprint 3 is complete.

### Scope Reviewed

Sprint 3 introduced a read-only historical comparison layer for completed monitoring runs.

The implemented comparison layer compares:

- Current completed monitoring run.
- Previous completed monitoring run for the same website.
- Trust assessment output.
- Finding output grouped by finding type.
- Risk output grouped by risk type.

### Architecture Assessment

The implementation is consistent with the SiteSentinel architecture.

Sprint 3 correctly builds on:

- Sprint 1 lifecycle execution.
- Sprint 2 traceability review layer.

The comparison layer does not replace or duplicate lifecycle responsibilities.

### Approved Architecture Boundaries

The Sprint 3 comparison layer is approved as a read-oriented layer.

It may:

- Read monitoring runs.
- Read trust assessments.
- Read findings.
- Read risks.
- Compare persisted outputs.
- Display comparison summaries.
- Link back to traceability pages.

It must not:

- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify historical assessment outputs.
- Reinterpret raw evidence outside the approved lifecycle engines.

### Baseline Selection Rule

Only completed monitoring runs are valid comparison candidates.

Pending, running, failed, or incomplete monitoring runs must not be used as the previous 
comparison baseline.

### UI Review

The following UI surfaces are approved:

- Full monitoring run comparison page.
- Monitoring run detail link to comparison page.
- Website detail latest comparison summary.
- Traceability links from comparison output back to existing detail pages.

### Result

Sprint 3 is approved and may be marked complete.

The project is ready to move toward scheduled monitoring, reporting, or notification 
features in later sprints.


# SiteSentinel Architecture Review-4

## Sprint

Sprint 4 Closure

## Result

APPROVED

## Product Owner

Approved

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented

## Implementation Status

Sprint 4 introduced controlled scheduled monitoring for SiteSentinel.

The scheduled monitoring baseline allows a website to have a recurring monitoring schedule while preserving the existing assessment lifecycle, traceability layer, and comparison baseline.

The approved scheduled execution path is:

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

## Sprint 4 Completion Notes

Sprint 4 completed the following architecture-aligned implementation areas:

- Website-level monitoring schedule configuration.
- Daily scheduled monitoring frequency.
- Schedule enable and disable controls.
- Schedule status visibility.
- Next run timestamp visibility.
- Last triggered timestamp visibility.
- Latest scheduled monitoring run visibility.
- Manual versus scheduled monitoring run distinction.
- Monitoring schedule reference on scheduled monitoring runs.
- Safe scheduled monitoring worker.
- Due schedule detection.
- Scheduled execution through MonitoringExecutionService.
- Overlap prevention for active monitoring runs.
- Stale active run recovery.
- Scheduled monitoring QA and closure documentation.

## Preserved Architecture Boundaries

Sprint 4 preserved the existing lifecycle boundary.

The scheduled monitoring worker may:

- Read enabled monitoring schedules.
- Detect schedules due for execution.
- Check whether a website has an active monitoring run.
- Recover stale active monitoring runs.
- Trigger scheduled execution through MonitoringExecutionService.
- Update schedule metadata after execution.

The scheduled monitoring worker must not:

- Collect evidence directly.
- Normalize evidence directly.
- Generate findings directly.
- Evaluate risks directly.
- Generate trust assessments directly.
- Perform assessment comparison directly.
- Modify lifecycle output outside the approved monitoring execution path.

## Safety Rules

Sprint 4 introduced the following safety rules:

- A scheduled run must not start if the same website has a pending or running monitoring run.
- Stale pending or running monitoring runs may be marked as failed after the configured timeout.
- Scheduled monitoring must always enter the existing lifecycle through MonitoringExecutionService.
- Scheduled monitoring must preserve lifecycle persistence and traceability generation.
- Scheduled monitoring must preserve comparison eligibility rules.

## Deferred Items

Sprint 4 intentionally deferred:

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

## Result

Sprint 4 is approved and may be marked complete.

The project is ready to move to Sprint 5.

Recommended Sprint 5 scope:

Monitoring Run Report Baseline

# SiteSentinel Architecture Review-5

## Sprint

Sprint 5 Closure

## Result

APPROVED

## Product Owner

Approved

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented

## Implementation Status

Sprint 5 introduced a read-only monitoring run report baseline.

The report baseline gives users a browser-based report view for an existing monitoring run while preserving the existing lifecycle and traceability boundaries.

The approved report data path is:

MonitoringRun
↓
Existing Persisted Lifecycle Output
↓
MonitoringRunReportService
↓
MonitoringRunReportView
↓
Browser-Based Report Template

## Sprint 5 Completion Notes

Sprint 5 completed the following architecture-aligned implementation areas:

- Monitoring run report read model.
- Monitoring run report status classification.
- Lifecycle output count summary.
- Trust assessment summary.
- Finding summary.
- Risk summary.
- Stage-level traceability summary.
- Assessment comparison summary.
- Report controller.
- Browser-based report template.
- Report navigation from monitoring run detail.
- Report navigation from website detail.
- Report navigation from comparison detail.
- Full report mode for completed runs.
- Limited report mode for pending, running, and failed runs.

## Preserved Architecture Boundaries

Sprint 5 preserved the existing SiteSentinel architecture boundaries.

The report layer may:

- Read website metadata.
- Read monitoring run metadata.
- Read lifecycle output counts.
- Read findings.
- Read risks.
- Read trust assessments.
- Read comparison summaries.
- Display traceability availability.
- Link to existing traceability detail pages.

The report layer must not:

- Execute monitoring runs.
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
- Persist assessment comparison output.

## Report Boundary Decision

Sprint 5 intentionally introduced browser-based reporting before export or notification features.

This preserves product clarity:

- First, generate reliable lifecycle output.
- Then, expose traceability.
- Then, compare historical assessments.
- Then, schedule recurring scans.
- Then, present the result as a readable report.

Export, delivery, and notification features remain future work.

## Deferred Items

Sprint 5 intentionally deferred:

- PDF report export.
- CSV export.
- Email notifications.
- WhatsApp notifications.
- Slack notifications.
- Webhooks.
- AI-written report summaries.
- Report approval workflow.
- Report versioning.
- Authentication.
- User access control.
- Advanced scanner signals.

## Result

Sprint 5 is approved and may be marked complete.

The project is ready to move to Sprint 6.

Recommended Sprint 6 scope:

Notification Event Baseline