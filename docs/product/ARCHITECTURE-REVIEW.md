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

The scheduled monitoring baseline allows a website to have a recurring monitoring schedule 
while preserving the existing assessment lifecycle, traceability layer, and comparison baseline.

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

The report baseline gives users a browser-based report view for an existing monitoring 
run while preserving the existing lifecycle and traceability boundaries.

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

---

# SiteSentinel Architecture Review-6

## Sprint

Sprint 6 Opening

## Proposed Scope

Notification Event Baseline

## Architecture Status Before Sprint 6

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented

## Approved Direction

Sprint 6 should introduce a persisted notification event layer.

The notification event layer should record important monitoring outcomes as structured in-application events.

The approved notification event data path is:

MonitoringRun
↓
Existing Persisted Lifecycle Output
↓
Existing Assessment Comparison Output
↓
NotificationEventGenerationService
↓
NotificationEvent
↓
Dashboard / Website Detail / Monitoring Run Detail Visibility

## Architecture Intent

Notification events should improve operational visibility without changing the assessment lifecycle.

The system should be able to answer:

- Did an important monitoring event happen?
- Which website is affected?
- Which monitoring run caused the event?
- What type of event occurred?
- How severe is the event?
- Has the same event already been recorded?

## Approved Notification Boundary

The notification event layer may:

- Read website metadata.
- Read monitoring run metadata.
- Read trust assessments.
- Read risks.
- Read findings.
- Read assessment comparisons.
- Persist notification event records.
- Display notification event records.

The notification event layer must not:

- Execute monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify lifecycle output.
- Modify comparison output.
- Generate monitoring reports.
- Deliver external notifications.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.

## Delivery Boundary Decision

Sprint 6 is not a delivery sprint.

Email, WhatsApp, Slack, and webhook notification delivery remain outside the Sprint 6 boundary.

This decision keeps the current sprint focused on:

- Event persistence.
- Event generation.
- Event deduplication.
- Event visibility.

Delivery can be added later after the event model is stable.

## Initial Event Rule Boundary

Sprint 6 should begin with a small rule set.

Approved baseline event rules:

- Monitoring run failed.
- High-risk trust assessment detected.
- Trust status changed.
- Trust score declined.
- New risk type detected.

Additional rules should be deferred until the first event model is validated.

## Deduplication Boundary

Sprint 6 should include basic notification event deduplication.

The deduplication goal is to prevent scheduled monitoring from creating repeated identical 
notifications for the same website, run, or assessment condition.

Advanced notification policy logic remains deferred.

## Deferred Items

Sprint 6 intentionally defers:

- Email delivery.
- WhatsApp delivery.
- Slack delivery.
- Webhooks.
- User notification preferences.
- Recipient management.
- Notification subscriptions.
- Delivery retries.
- Delivery failure tracking.
- Advanced notification policy engine.
- AI-generated notification text.
- Authentication.
- User access control.
- PDF export.
- CSV export.

## Result

Sprint 6 is approved to start with the Notification Event Baseline.

---

# SiteSentinel Architecture Review-6 Closure

## Sprint

Sprint 6 Closure

## Result

APPROVED

## Product Owner

Approved

## Final Scope

Notification Event Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented

## Implementation Status

Sprint 6 introduced a persisted in-application notification event layer.

The notification event layer records important monitoring outcomes as structured events without 
changing the existing assessment lifecycle.

The implemented notification event data path is:

MonitoringRun
↓
Existing Persisted Lifecycle Output
↓
Existing Assessment Comparison Output
↓
NotificationEventGenerationService
↓
NotificationEventService
↓
NotificationEvent
↓
Dashboard / Website Detail / Monitoring Run Detail / Monitoring Run Report Visibility

## Sprint 6 Completion Notes

Sprint 6 completed the following architecture-aligned implementation areas:

- Notification event persistence.
- Notification event domain model.
- Notification event repository.
- Notification event service.
- Notification event create-if-absent behavior.
- Notification event deduplication.
- Notification read/unread status model.
- Notification event generation rules.
- Failed monitoring run notification generation.
- High-risk trust assessment notification generation.
- Trust status changed notification generation.
- Trust score declined notification generation.
- New risk type detected notification generation.
- Lifecycle-safe notification generation after completed runs.
- Lifecycle-safe notification generation after failed runs.
- Dashboard notification visibility.
- Website detail notification visibility.
- Monitoring run detail notification visibility.
- Monitoring run report notification visibility.

## Preserved Architecture Boundaries

Sprint 6 preserved the existing SiteSentinel architecture boundaries.

The notification event layer may:

- Read website metadata.
- Read monitoring run metadata.
- Read trust assessments.
- Read comparison summaries.
- Read risk comparison output.
- Persist notification event records.
- Deduplicate notification events by deduplication key.
- Expose notification events to UI pages.

The notification event layer must not:

- Execute monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Modify lifecycle output.
- Modify comparison output.
- Generate monitoring reports.
- Deliver external notifications.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.

## Lifecycle Safety Review

Notification event generation is integrated after a monitoring run reaches a completed or failed state.

Notification event generation is intentionally isolated from monitoring lifecycle failure.

If notification event generation fails, the monitoring run result remains completed or 
failed according to the monitoring lifecycle result.

## Implemented Event Rules

Sprint 6 implemented baseline rules for:

- Monitoring run failed.
- High-risk trust assessment detected.
- Trust status changed.
- Trust score declined.
- New risk type detected.

## UI Review

Notification events are visible in:

- Dashboard.
- Website detail.
- Monitoring run detail.
- Monitoring run report.

## Deferred Architecture Items

The following remain intentionally deferred:

- Notification management page.
- Notification detail page.
- Mark as read/unread UI controls.
- Notification filtering by severity and status.
- Email notification delivery.
- WhatsApp notification delivery.
- Slack notification delivery.
- Webhook delivery.
- User notification preferences.
- Recipient management.
- Notification subscriptions.
- Delivery retries.
- Delivery failure tracking.
- Advanced notification policy engine.
- AI-generated notification text.
- Authentication.
- User access control.

## Result

Sprint 6 is approved and may be marked complete.

The project is ready to move to Sprint 7.

Recommended Sprint 7 scope:

Notification Management Baseline

---

# SiteSentinel Architecture Review-7 Opening

## Sprint

Sprint 7 Opening

## Result

APPROVED TO START

## Product Owner

Approved

## Proposed Scope

Notification Management Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented  
Notification Management Baseline In Progress

## Sprint 7 Architecture Intent

Sprint 7 extends the persisted notification event baseline with in-application notification management.

Sprint 7 does not change how notification events are generated.

Sprint 7 does not introduce external delivery.

Sprint 7 provides a user-facing management layer for already persisted notification events.

## Approved Data Path

The approved Sprint 7 data path is:

NotificationEvent
↓
NotificationEventRepository
↓
NotificationEventService
↓
NotificationEventController
↓
Notification Management UI

Optional navigation may link notification events back to:

Website
↓
MonitoringRun
↓
Monitoring Run Report

## Approved Sprint 7 Capabilities

Sprint 7 may implement:

- Notification event list page.
- Notification event detail page.
- Status-based filtering.
- Severity-based filtering.
- Combined status and severity filtering.
- Mark notification as read.
- Mark notification as unread.
- Dashboard navigation to notification management.
- Website detail navigation to notification management.
- Monitoring run detail navigation to notification event detail.
- Monitoring run report navigation to notification event detail.
- Empty-state handling for notification management views.

## Preserved Architecture Boundaries

The Sprint 7 notification management layer may:

- Read notification event records.
- Read notification event website references.
- Read notification event monitoring run references.
- Display notification event fields.
- Filter notification events.
- Update notification event status.
- Link to existing lifecycle and report pages.

The Sprint 7 notification management layer must not:

- Execute monitoring runs.
- Execute scheduled monitoring.
- Collect evidence.
- Normalize evidence.
- Generate findings.
- Evaluate risks.
- Generate trust assessments.
- Generate monitoring reports.
- Modify lifecycle output.
- Modify comparison output.
- Change notification generation rules.
- Generate notification events outside the existing generation service.
- Deliver external notifications.
- Send emails.
- Send WhatsApp messages.
- Send Slack messages.
- Send webhooks.
- Manage recipients.
- Manage subscriptions.
- Manage user-specific notification preferences.
- Generate AI-written notification summaries.

## External Delivery Boundary

Sprint 7 is not an external notification delivery sprint.

Email, WhatsApp, Slack, webhook, and other outbound delivery mechanisms remain outside the Sprint 7 boundary.

Delivery should be considered only after notification management is stable.

## Read/Unread Boundary

Sprint 7 may update only the notification event status field.

Allowed status transitions:

- UNREAD to READ.
- READ to UNREAD.

Sprint 7 must not use read/unread state to change monitoring results, trust assessments, 
risks, findings, evidence, reports, or comparison output.

## Result

Sprint 7 is approved to start with the Notification Management Baseline.

---

# SiteSentinel Architecture Review-7 Closure

## Sprint

Sprint 7 Closure

## Result

APPROVED AS COMPLETE

## Product Owner

Approved

## Completed Scope

Notification Management Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented  
Notification Management Baseline Implemented

## Sprint 7 Architecture Result

Sprint 7 completed the in-application notification management layer.

The platform now supports user-facing management of persisted notification events.

The notification management layer allows users to:

- List notification events.
- Filter notification events by status.
- Filter notification events by severity.
- Filter notification events by status and severity together.
- Filter notification events by website context.
- Filter notification events by monitoring run context.
- Inspect notification event detail.
- Mark notification events as read.
- Mark notification events as unread.
- Navigate from dashboard, website detail, monitoring run detail, and monitoring run report pages 
into notification management.

---

# SiteSentinel Architecture Review-8 Opening

## Sprint

Sprint 8 Opening

## Result

APPROVED TO START

## Product Owner

Approved

## Planned Scope

Notification Delivery Readiness Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented  
Notification Management Baseline Implemented  
Notification Delivery Readiness Baseline Opening

## Sprint 8 Architecture Goal

Sprint 8 will prepare the notification layer for future delivery capabilities.

The sprint will introduce delivery attempt modeling and simulated delivery attempt records.

Sprint 8 does not implement real external delivery.

## Approved Sprint 8 Data Path

The approved Sprint 8 data path is:

NotificationEvent  
↓  
NotificationDeliveryAttempt  
↓  
NotificationDeliveryAttemptRepository  
↓  
NotificationDeliveryAttemptService  
↓  
Notification Detail UI

## Approved Sprint 8 Components

Sprint 8 may introduce:

- Notification delivery channel enum.
- Notification delivery attempt status enum.
- Notification delivery attempt entity.
- Notification delivery attempt repository.
- Notification delivery attempt service.
- Manual simulated delivery attempt action.
- Delivery attempt history visibility on notification detail pages.

## Preserved Architecture Boundaries

Sprint 8 must preserve the existing SiteSentinel lifecycle.

Sprint 8 must not change:

- Website monitoring execution.
- Evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment generation.
- Comparison generation.
- Report generation.
- Scheduled monitoring execution.
- Notification event generation.

Sprint 8 may only extend notification management with delivery readiness records.

## External Delivery Boundary

Sprint 8 is not an outbound notification delivery sprint.

The following remain deferred:

- Email delivery.
- WhatsApp delivery.
- Slack delivery.
- Webhook delivery.
- External API delivery.
- Recipient management.
- User notification preferences.
- Notification subscription rules.
- Delivery retry scheduler.
- AI-generated notification summaries.

## Simulation Boundary

Sprint 8 may record simulated delivery attempts.

A simulated delivery attempt is an internal audit/readiness record only.

It must not call SMTP, WhatsApp APIs, Slack APIs, webhook URLs, or any other external service.

## Architecture Decision

Sprint 8 is approved to start as Notification Delivery Readiness Baseline.

The next implementation step is the delivery attempt database baseline.

---

# SiteSentinel Architecture Review-8 Closure

## Sprint

Sprint 8

## Result

APPROVED AND CLOSED

## Product Owner

Approved

## Implemented Scope

Notification Delivery Readiness Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented  
Notification Management Baseline Implemented  
Notification Delivery Readiness Baseline Implemented

## Architecture Result

Sprint 8 successfully extended the notification layer with delivery readiness capability.

The system can now record simulated delivery attempts for notification events without sending 
real external notifications.

## Implemented Components

Sprint 8 added:

- NotificationDeliveryChannel
- NotificationDeliveryAttemptStatus
- NotificationDeliveryAttempt
- NotificationDeliveryAttemptRepository
- NotificationDeliveryAttemptService
- notification_delivery_attempts table
- Manual simulated success action
- Manual simulated failure action
- Manual skipped attempt action
- Delivery attempt count visibility
- Delivery attempt history visibility

## Approved Delivery Channels

The following delivery channels are modeled:

- EMAIL
- WHATSAPP
- SLACK
- WEBHOOK
- IN_APP
- TELEGRAM

## Approved Attempt Statuses

The following attempt statuses are modeled:

- PENDING
- SIMULATED_SUCCESS
- SIMULATED_FAILURE
- SKIPPED

## TELEGRAM Architecture Decision

TELEGRAM is approved as a modeled delivery channel.

Sprint 8 does not implement Telegram Bot API integration.

Telegram message delivery remains a future outbound delivery concern.

## Implemented Data Path

The implemented Sprint 8 data path is:

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

## Preserved Boundaries

Sprint 8 preserved all existing lifecycle boundaries.

Sprint 8 did not modify:

- Monitoring execution.
- Scheduled monitoring execution.
- Evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment generation.
- Comparison generation.
- Report generation.
- Notification event generation.

## External Delivery Boundary

Sprint 8 is not an outbound delivery implementation.

The following remain deferred:

- Email delivery.
- WhatsApp delivery.
- Telegram delivery.
- Slack delivery.
- Webhook delivery.
- External delivery API calls.
- Recipient management.
- User notification preferences.
- Notification subscriptions.
- Delivery retry scheduling.
- Delivery provider configuration.

## Migration Note

Sprint 8 introduced delivery attempt persistence through Flyway migrations.

`V9__create_notification_delivery_attempts_table.sql` created the delivery attempt table.

`V11__allow_telegram_notification_delivery_channel.sql` updates the delivery channel check constraint 
to include TELEGRAM.

`V10__allow_telegram_notification_delivery_channel.sql` is retained as an empty local repair-alignment
migration because Flyway had already recorded version 10 during local development.

## Architecture Decision

Sprint 8 is approved and closed.

The notification layer is now delivery-ready at the internal audit/readiness level.

Real external delivery should be implemented only in a future sprint with explicit provider boundaries.

## Final Sprint 7 Data Path

The implemented Sprint 7 data path is:

NotificationEvent
↓
NotificationEventRepository
↓
NotificationEventService
↓
NotificationEventController
↓
Notification Management UI

The notification management UI links back to:

Website Detail  
Monitoring Run Detail  
Monitoring Run Report

## Preserved Architecture Boundaries

Sprint 7 preserved the existing monitoring lifecycle.

Sprint 7 did not change:

- Website monitoring execution.
- Evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment generation.
- Comparison generation.
- Report generation.
- Scheduled monitoring execution.
- Notification event generation.

Sprint 7 only introduced notification event review and status management.

## Read/Unread Status Boundary

Sprint 7 updates only the notification event status field.

Allowed transitions implemented:

- UNREAD to READ.
- READ to UNREAD.

These transitions do not modify monitoring runs, evidence, normalized evidence, findings, risks, 
trust assessments, comparison output, or report output.

## External Delivery Boundary

Sprint 7 did not implement external notification delivery.

The following remain deferred:

- Email delivery.
- WhatsApp delivery.
- Slack delivery.
- Webhook delivery.
- Recipient management.
- User notification preferences.
- Notification subscription rules.
- Delivery retries.
- Delivery status tracking.
- AI-generated notification summaries.

## Architecture Decision

Notification management is now approved as a completed baseline.

The recommended next architectural step is delivery readiness, not real delivery.

Sprint 8 may introduce delivery attempt modeling and simulated delivery records while 
preserving the no-external-delivery boundary.

---

# SiteSentinel Architecture Review-9 Opening

## Sprint

Sprint 9 Opening

## Result

APPROVED TO START

## Product Owner

Approved

## Planned Scope

Controlled Telegram Delivery Provider Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented  
Notification Management Baseline Implemented  
Notification Delivery Readiness Baseline Implemented  
Controlled Telegram Delivery Provider Baseline Opening

## Sprint 9 Architecture Goal

Sprint 9 introduces the first real external notification delivery provider boundary.

The provider selected for the first controlled delivery baseline is TELEGRAM.

Sprint 9 does not introduce automatic dispatch.

Sprint 9 does not turn notification events into automatically delivered outbound messages.

The goal is controlled manual Telegram test delivery for existing notification events.

## Approved Sprint 9 Data Path

The approved Sprint 9 data path is:

NotificationEvent  
↓  
NotificationDeliveryAttempt  
↓  
NotificationDeliveryAttemptService  
↓  
NotificationDeliveryProvider  
↓  
TelegramNotificationDeliveryProvider  
↓  
Telegram Bot API

## Provider Boundary Requirement

External delivery must be isolated behind a provider boundary.

Controllers must not call Telegram Bot API directly.

Notification management UI must not call Telegram Bot API directly.

Notification event generation must not call Telegram Bot API directly.

The provider boundary must protect the existing monitoring lifecycle from external delivery failures.

## Approved Sprint 9 Components

Sprint 9 may introduce:

- Telegram delivery configuration properties.
- Telegram delivery enabled/disabled switch.
- Telegram bot token configuration.
- Telegram chat id configuration.
- NotificationDeliveryProvider interface.
- TelegramNotificationDeliveryProvider implementation.
- Real Telegram test delivery action.
- Real delivery attempt status values.
- Provider response capture.
- Provider failure capture.
- Configuration-missing result handling.
- Disabled-provider result handling.

## Safety Boundary

Telegram delivery must be disabled by default.

When Telegram delivery is disabled, the system must not call Telegram Bot API.

When required Telegram configuration is missing, the system must not call Telegram Bot API.

Telegram delivery must require explicit user action during Sprint 9.

No automatic external delivery is approved in Sprint 9.

## Preserved Architecture Boundaries

Sprint 9 must preserve the existing SiteSentinel lifecycle.

Sprint 9 must not change:

- Website monitoring execution.
- Scheduled monitoring execution.
- Evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment generation.
- Assessment comparison generation.
- Monitoring run report generation.
- Notification event generation.
- Notification read/unread management.

## Explicitly Deferred

The following remain deferred:

- Automatic notification dispatch.
- Delivery after scheduled monitoring completion.
- Email delivery.
- WhatsApp delivery.
- Slack delivery.
- Webhook delivery.
- Retry scheduler.
- Escalation policies.
- Recipient management.
- User notification preferences.
- Notification subscriptions.
- Multi-recipient routing.
- AI-generated notification messages.

## Architecture Decision

Sprint 9 is approved to start as Controlled Telegram Delivery Provider Baseline.

The first implementation step is Telegram delivery configuration, disabled by default.

---

# SiteSentinel Architecture Review-9 Closure

## Sprint

Sprint 9

## Result

APPROVED AS COMPLETE

## Product Owner

Approved

## Completed Scope

Controlled Telegram Delivery Provider Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented  
Notification Management Baseline Implemented  
Notification Delivery Readiness Baseline Implemented  
Controlled Telegram Delivery Provider Baseline Implemented

## Sprint 9 Architecture Result

Sprint 9 successfully introduced the first external notification delivery provider boundary.

Telegram is now implemented as the controlled first-provider baseline.

The provider is isolated behind the notification delivery provider interface.

Controllers do not call Telegram Bot API directly.

Notification event generation does not call Telegram Bot API directly.

Scheduled monitoring does not call Telegram Bot API directly.

Telegram delivery is available only through explicit manual test action during Sprint 9.

## Implemented Data Path

The implemented Sprint 9 delivery path is:

NotificationEvent  
↓  
NotificationDeliveryAttemptService  
↓  
NotificationDeliveryProvider  
↓  
TelegramNotificationDeliveryProvider  
↓  
Telegram Bot API  
↓  
NotificationDeliveryAttempt

## Implemented Safety Boundary

Telegram delivery remains disabled by default.

When disabled, Telegram Bot API is not called.

When required Telegram configuration is missing, Telegram Bot API is not called.

Provider failures are captured as delivery attempt records.

External delivery failure does not interrupt:

- Monitoring execution.
- Scheduled monitoring execution.
- Notification event generation.
- Notification management UI.
- Monitoring run report generation.

## Implemented Provider Result Model

The implemented provider result model supports:

- SENT
- FAILED
- CONFIGURATION_MISSING
- DISABLED

These statuses preserve auditability for both real delivery attempts and safe no-call outcomes.

## Preserved Architecture Boundaries

Sprint 9 did not change:

- Website monitoring execution.
- Scheduled monitoring execution.
- Evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment generation.
- Assessment comparison generation.
- Monitoring run report generation.
- Notification event generation rules.
- Notification read/unread management.

## Deferred Architecture Items

The following remain deferred:

- Automatic notification dispatch.
- Delivery after scheduled monitoring completion.
- Email delivery provider.
- WhatsApp delivery provider.
- Slack delivery provider.
- Webhook delivery provider.
- Retry scheduler.
- Escalation policies.
- Recipient management.
- User notification preferences.
- Notification subscriptions.
- Multi-recipient routing.
- AI-generated notification messages.

## Architecture Decision

Sprint 9 is approved as complete.

The controlled Telegram provider baseline is accepted.

Future delivery automation must be introduced only after explicit delivery rules, 
recipient management, retry behavior, and configuration safety are designed.

---

# Architecture Review — Sprint 10

## Status

APPROVED

## Scope

Notification Delivery Operations Baseline

## Architecture Decision

Sprint 10 extends the notification delivery subsystem with operational visibility while preserving 
the existing notification delivery boundary.

## New Architectural Components

Sprint 10 introduced:

- Provider operational status model.
- Provider readiness evaluation.
- Provider health-check model.
- Provider health-check persistence.
- Notification delivery settings UI.

## Preserved Boundaries

Sprint 10 preserves the separation between:

- Notification events.
- Notification delivery attempts.
- Provider operational checks.
- Provider configuration readiness.

Provider health checks remain independent from notification delivery attempts.

## Accepted Constraints

The current architecture intentionally keeps:

- Telegram disabled by default.
- Manual-only Telegram delivery.
- Environment-based secret configuration.
- Secret-safe operational visibility.

## Known Architectural Limitation

Telegram connectivity verification currently returns a boolean result.

Future versions may introduce a richer provider connectivity model capable of distinguishing 
authentication failures, network failures, timeouts, and provider unavailability.

This limitation is accepted for the Sprint 10 baseline.

## Result

Sprint 10 architecture is approved.

The notification delivery subsystem now provides operational visibility without changing 
the existing notification lifecycle or delivery boundaries.

---

# SiteSentinel Architecture Review-11 Closure

## Sprint

Sprint 11 Closure

## Result

APPROVED AS COMPLETE

## Product Owner

Approved

## Completed Scope

Notification Provider Diagnostics and Safety Verification Baseline

## Architecture Status

Core Assessment Lifecycle Implemented  
Explainable Traceability Layer Implemented  
Assessment History & Change Comparison Baseline Implemented  
Scheduled Monitoring & Recurring Scan Baseline Implemented  
Monitoring Run Report Baseline Implemented  
Notification Event Baseline Implemented  
Notification Management Baseline Implemented  
Notification Delivery Readiness Baseline Implemented  
Controlled Telegram Delivery Provider Baseline Implemented  
Notification Delivery Operations Baseline Implemented  
Notification Provider Diagnostics and Safety Verification Baseline Implemented

## Architecture Decision

Sprint 11 replaces the Sprint 10 boolean Telegram connectivity baseline with a typed, testable,
secret-safe provider diagnostic architecture.

The implementation preserves the existing provider operations and manual delivery boundaries.

Sprint 11 does not introduce automatic notification delivery.

## Approved Telegram Client Boundary

Sprint 11 introduced a dedicated Telegram Bot API client boundary:

TelegramNotificationDeliveryProvider  
↓  
TelegramBotApiClient  
↓  
JdkTelegramBotApiClient  
↓  
Telegram Bot API

The responsibilities are separated as follows.

### TelegramNotificationDeliveryProvider

Responsible for:

- Provider readiness enforcement.
- Notification delivery business behavior.
- Telegram notification message construction.
- Delivery result classification.
- Connectivity response classification.
- Connectivity exception classification.
- Secret-safe diagnostic output.

### TelegramBotApiClient

Defines the Telegram HTTP communication contract:

- `getMe`
- `sendMessage`

### JdkTelegramBotApiClient

Responsible for:

- Telegram API endpoint construction.
- HTTP request construction.
- Form encoding.
- Connect timeout enforcement.
- Request timeout enforcement.
- HTTP response capture.
- Technical exception wrapping.
- Thread interruption restoration.

Controllers, notification event generation, monitoring execution, and scheduled monitoring do not call
Telegram Bot API directly.

## Typed Connectivity Architecture

Sprint 11 introduced:

- `TelegramConnectivityStatus`
- `TelegramConnectivityResult`

Approved connectivity statuses:

- HEALTHY
- AUTHENTICATION_FAILED
- TIMEOUT
- UNREACHABLE
- INVALID_RESPONSE
- INTERRUPTED
- FAILED

The typed result may include:

- Connectivity status.
- Secret-safe diagnostic message.
- Optional HTTP status code.

It must not include:

- Bot token.
- Chat ID.
- Full Telegram endpoint.
- Request body.
- Notification payload.
- Raw Telegram response body.
- Client exception message.
- Exception cause detail.

## Readiness and Connectivity Separation

Provider readiness and provider connectivity remain separate architectural concerns.

Readiness represents configuration state:

- DISABLED
- CONFIGURATION_MISSING
- READY

Connectivity represents the result of an external provider check:

- HEALTHY
- AUTHENTICATION_FAILED
- TIMEOUT
- UNREACHABLE
- INVALID_RESPONSE
- INTERRUPTED
- FAILED

A provider may be `READY` because required configuration is present while still failing connectivity.

`READY` must not be interpreted as `HEALTHY`.

`HEALTHY` requires a successful provider health check.

## Approved Health-Check Path

The completed Sprint 11 health-check path is:

Notification Delivery Settings UI  
↓  
TelegramProviderHealthCheckService  
↓  
TelegramNotificationDeliveryProvider.checkConnectivity()  
↓  
TelegramBotApiClient.getMe()  
↓  
TelegramConnectivityResult  
↓  
NotificationDeliveryProviderCheckService  
↓  
NotificationDeliveryProviderCheck

The Sprint 10 legacy boolean method:

`verifyConnection()`

has been removed.

## Provider Health-Check Persistence

Sprint 11 expanded provider-check persistence.

Approved provider-check statuses:

- HEALTHY
- DISABLED
- CONFIGURATION_MISSING
- AUTHENTICATION_FAILED
- TIMEOUT
- UNREACHABLE
- INVALID_RESPONSE
- INTERRUPTED
- FAILED

Sprint 11 added:

- Typed provider-check status persistence.
- Optional HTTP status persistence.
- Latest provider-check visibility.
- Recent provider-check history.
- Safe diagnostic visibility.

The provider-check model remains independent from notification delivery attempts.

A health check must not create:

- Notification events.
- Notification delivery attempts.
- Telegram messages.

## Database Changes

Sprint 11 added:

- `V14__expand_notification_delivery_provider_check_statuses.sql`
- `V15__add_http_status_code_to_notification_delivery_provider_checks.sql`

V14 expands the provider-check status constraint.

V15 adds nullable:

`http_status_code`

The approved HTTP status constraint is:

- NULL when no HTTP response exists.
- Otherwise between 100 and 599.

Examples:

- HEALTHY → normally 200
- AUTHENTICATION_FAILED → normally 401 or 403
- INVALID_RESPONSE → actual response status
- TIMEOUT → NULL
- UNREACHABLE → NULL
- INTERRUPTED → NULL
- DISABLED → NULL
- CONFIGURATION_MISSING → NULL

## Structured Provider Response Boundary

Telegram response success is determined through structured JSON parsing.

A response is successful only when:

- HTTP status is 2xx.
- Response body is valid JSON.
- JSON root is an object.
- Top-level `ok` field is boolean `true`.

The following are not successful responses:

- `ok=false`
- `"ok":"true"`
- Nested `ok=true`
- Text containing an embedded `"ok":true` marker
- Malformed JSON
- Empty response body
- Array-root JSON
- Non-2xx response with `ok=true`

Substring-based and regex-based success classification are no longer part of the approved architecture.

## Secret-Safety Boundary

Sprint 11 strengthened the provider diagnostic boundary.

Persisted and UI-visible diagnostics must not expose:

- Telegram bot token.
- Telegram chat ID.
- Full Telegram API URL.
- Request body.
- Notification content.
- Raw provider response body.
- Client exception message.
- Exception cause details.

Allowed operational metadata includes:

- Typed provider status.
- Controlled diagnostic message.
- Optional HTTP status code.
- Provider-check timestamp.

Provider secrets remain environment-based.

The application UI must not display or edit raw provider credentials.

## Message Boundary Decision

Telegram notification messages use a maximum application-level length of 3900 UTF-16 code units.

The truncation suffix is included within this limit.

The implementation must not split Unicode surrogate pairs.

Provider-check diagnostic messages use a maximum application-level length of 500 UTF-16 code units,
remaining within the database `VARCHAR(500)` boundary.

Diagnostic normalization includes:

- Safe default for null or blank values.
- Trimming surrounding whitespace.
- Unicode-safe truncation.
- Truncation suffix included within the database limit.

## Automated Verification Architecture

Sprint 11 established a provider-focused automated test baseline.

Coverage includes:

- Disabled provider short-circuit.
- Missing configuration short-circuit.
- Readiness evaluation.
- Disabled-by-default configuration.
- Manual-only delivery mode.
- Typed connectivity classification.
- Authentication failure.
- Timeout.
- DNS and network failure.
- Invalid provider response.
- Thread interruption.
- Generic provider failure.
- Health-check status mapping.
- Provider-check persistence.
- HTTP status propagation.
- Provider-check entity validation.
- Provider-check query ordering.
- Telegram `getMe` request construction.
- Telegram `sendMessage` request construction.
- Form encoding.
- Request timeout behavior.
- Interrupt status restoration.
- Structured JSON response parsing.
- Delivery result classification.
- Secret-safe diagnostics.
- Telegram message length boundaries.
- Unicode-safe truncation.

Final verification result:

- Tests run: 74
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven test: BUILD SUCCESS
- Maven compile: BUILD SUCCESS

## Manual QA Architecture Verification

The following operational states were manually verified:

- DISABLED
- CONFIGURATION_MISSING
- HEALTHY
- AUTHENTICATION_FAILED
- TIMEOUT
- UNREACHABLE
- INVALID_RESPONSE

Manual QA confirmed:

- Health checks do not send Telegram messages.
- Health checks do not create notification delivery attempts.
- Health checks do not create notification events.
- HTTP status is recorded only when an HTTP response exists.
- Raw provider responses are not persisted.
- Provider secrets are not displayed.
- Provider secrets are not included in diagnostic messages.
- Telegram remains disabled by default.
- Telegram delivery remains manual only.

## Preserved Architecture Boundaries

Sprint 11 did not change:

- Website monitoring execution.
- Scheduled monitoring execution.
- Evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment generation.
- Assessment comparison.
- Monitoring run reporting.
- Notification event generation.
- Notification read/unread management.
- Notification delivery attempt ownership.

Sprint 11 did not add:

- Automatic notification dispatch.
- Automatic delivery after monitoring completion.
- Recipient management.
- Notification subscriptions.
- User-specific routing.
- Retry scheduling.
- Delivery queue.
- Escalation rules.
- Additional external delivery providers.
- UI-based secret management.
- AI-generated notification messages.

## Resolved Sprint 10 Limitation

Sprint 10 accepted boolean Telegram connectivity verification as a baseline limitation.

Sprint 11 resolves that limitation through:

- Typed connectivity statuses.
- Typed connectivity results.
- Safe diagnostic messages.
- Optional HTTP status metadata.
- Automated classification tests.
- Operational UI visibility.

The historical Sprint 10 limitation remains documented as the state accepted at Sprint 10 closure.

## Deferred Architecture Items

The following remain deferred:

- Recipient domain model.
- Delivery destination ownership.
- Automatic dispatch rules.
- Delivery idempotency.
- Duplicate automatic delivery prevention.
- Retry and exponential backoff.
- Dead-letter handling.
- Delivery queue.
- Provider rate limiting.
- Provider latency metrics.
- Provider success-rate analytics.
- Circuit breaker.
- Provider failover.
- Multi-provider routing.
- Additional delivery providers.
- External secret manager integration.
- Authentication.
- Role-based access control.

## Architecture Result

Sprint 11 is approved and closed.

SiteSentinel now has a typed, testable, auditable, and secret-safe Telegram provider diagnostic architecture.

The provider remains disabled by default and manual only.

Automatic notification dispatch remains outside the approved architecture boundary.

# SiteSentinel Architecture Review-12

## Sprint

Sprint 12 Closure

## Result

APPROVED

## Product Owner

Approved

## Architecture Status

AI Remediation Recommendation Baseline Implemented

## Implementation Status

Sprint 12 introduced a persisted, evidence-grounded, validated, advisory remediation recommendation layer.

The approved core assessment lifecycle remains:

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

Sprint 12 extends the completed assessment lifecycle with a post-assessment advisory path:

Monitoring Run Marked COMPLETED  
↓  
Persisted Risks Loaded  
↓  
Linked Findings Loaded  
↓  
Linked Normalized Evidence Loaded  
↓  
Evidence-Safe Recommendation Context Built  
↓  
Versioned AI Request Created  
↓  
AI Provider Abstraction Evaluated  
↓  
Structured Output Validated  
↓  
AI Recommendation or Rule-Based Fallback Produced  
↓  
Validated Advisory Recommendation Persisted  
↓  
Risk Detail and Monitoring Run Report Read Models Updated

The recommendation layer does not participate in risk or trust calculation.

Risk and trust remain authoritative persisted assessment outputs.

Recommendations are downstream advisory artifacts.

## Architectural Objective

Sprint 12 established the minimum architecture required to generate remediation guidance without weakening the 
existing assessment, traceability, security, or monitoring lifecycle boundaries.

The architecture must ensure that recommendation generation:

- Uses existing persisted risks.
- Uses existing persisted findings.
- Uses existing persisted normalized evidence.
- Does not create new risks.
- Does not create new findings.
- Does not create new evidence.
- Does not change risk severity.
- Does not change risk score.
- Does not change confidence score.
- Does not change trust score.
- Does not expose secrets to AI providers.
- Does not persist invalid AI output.
- Does not cause a completed monitoring run to become failed.
- Falls back to deterministic advisory guidance when AI is unavailable or invalid.

## Recommendation Domain Boundary

Sprint 12 introduced the recommendation domain under:

`com.cigabyte.sitesentinel.recommendation`

The primary persisted entity is:

`RiskRemediationRecommendation`

A recommendation is associated with:

- One persisted monitoring run.
- One persisted risk.

The recommendation is not part of the `Risk` aggregate.

The recommendation does not own or mutate:

- Risk.
- Finding.
- Evidence.
- Trust assessment.
- Monitoring run assessment data.

The association is maintained through persisted identifiers:

- `monitoringRunId`
- `riskId`

Database foreign keys preserve referential integrity.

Application-level persistence validation additionally confirms that the selected risk belongs to the selected 
monitoring run.

## Recommendation Source Model

The architecture supports two recommendation sources:

- `AI`
- `RULE_BASED_FALLBACK`

Supported fallback classifications are:

- `NONE`
- `PROVIDER_UNAVAILABLE`
- `PROVIDER_FAILURE`
- `VALIDATION_FAILURE`

The approved state combinations are:

AI recommendation:

- Source is `AI`.
- Fallback reason is `NONE`.
- Provider name is required.
- Model name is required.
- Prompt version is required.
- Fallback rule version is absent.

Rule-based fallback recommendation:

- Source is `RULE_BASED_FALLBACK`.
- Fallback reason is not `NONE`.
- Prompt version is required.
- Fallback rule version is required.
- Attempted provider and model metadata are optional.

Only recommendations with:

- `validationStatus = VALID`
- `advisory = true`

may cross the persistence boundary.

## Database Architecture

Sprint 12 added:

`V16__create_risk_remediation_recommendations_table.sql`

The migration introduced:

- Recommendation primary key.
- Monitoring run foreign key.
- Risk foreign key.
- Source classification.
- Fallback classification.
- Validation status.
- Advisory flag.
- Recommendation content.
- Provider metadata.
- Prompt version metadata.
- Fallback rule version metadata.
- Context fingerprint.
- Context finding count.
- Context evidence count.
- Generation timestamp.
- Persistence timestamp.

Database constraints enforce:

- Approved source values.
- Approved fallback values.
- Validated-only persistence.
- Advisory-only persistence.
- Non-negative context counts.
- Exact 64-character context fingerprint length.
- Consistent AI metadata.
- Consistent fallback metadata.

Indexes support:

- Monitoring run recommendation retrieval.
- Risk recommendation retrieval.
- Source classification queries.
- Generation-time ordering.

No existing risk, finding, evidence, trust, monitoring, notification, provider-check, or delivery-attempt table 
was modified.

## Persistence Architecture

Recommendation persistence is owned by:

`RiskRemediationRecommendationService`

The service validates:

- Recommendation presence.
- Monitoring run identifier presence.
- Risk identifier presence.
- Validation status.
- Advisory status.
- Monitoring run existence.
- Risk existence.
- Risk-to-monitoring-run ownership.

The service is the approved application persistence boundary for generated recommendations.

The repository provides:

- Monitoring run recommendation history.
- Risk recommendation history.
- Latest recommendation lookup.
- Monitoring run recommendation count.

Repository ordering supports:

Monitoring run report use:

- `generatedAt ASC`
- `createdAt ASC`

Risk detail history use:

- `generatedAt DESC`
- `createdAt DESC`

Latest recommendation use:

- Newest `generatedAt`.
- Newest `createdAt` as the secondary ordering field.

## Evidence-Safe Context Architecture

Recommendation context construction is owned by:

`RiskRemediationRecommendationContextBuilder`

The approved persisted traceability path is:

Risk  
↓  
RiskFinding  
↓  
Finding  
↓  
FindingEvidence  
↓  
NormalizedEvidence

The context builder may read:

- Risk type.
- Risk severity.
- Risk score.
- Risk confidence score.
- Risk rationale.
- Finding type.
- Finding title.
- Finding description.
- Finding confidence score.
- Normalized evidence type.
- Normalized evidence value.

The context builder must not read:

- Raw collected evidence value.
- Evidence source URL.
- Provider credential.
- Telegram bot token.
- Telegram chat ID.
- Database password.
- Raw AI response.
- Provider exception message.

The recommendation context builder has no dependency on:

- `CollectedEvidence`
- `CollectedEvidenceRepository`

This dependency exclusion is an explicit architecture boundary.

## Deterministic Context Ordering

Context fingerprint reliability requires deterministic repository ordering.

Sprint 12 added ordered retrieval paths for:

- Risk-to-finding links.
- Findings.
- Finding-to-evidence links.
- Normalized evidence.

The canonical context order is stable for the same persisted dataset.

This prevents nondeterministic database row ordering from changing the context fingerprint.

## Secret-Safe Text Boundary

Recommendation context text passes through:

`RiskRemediationRecommendationContextSanitizer`

The sanitizer provides defense-in-depth detection and redaction for patterns representing:

- Private keys.
- Bearer credentials.
- Basic credentials.
- Telegram bot tokens.
- JWT values.
- Credentials embedded in HTTP URLs.
- API keys.
- Access tokens.
- Refresh tokens.
- Secrets.
- Passwords.
- Cookies.
- Session identifiers.
- Chat identifiers.

Text fields also use controlled length limits.

UTF-16 truncation:

- Includes the suffix within the maximum length.
- Avoids splitting Unicode surrogate pairs.

The primary secret-safety boundary remains exclusion of raw collected evidence and provider credentials.

Pattern-based sanitization is a secondary control and is not treated as a complete secret-detection system.

## Context Fingerprint Architecture

Each context receives a deterministic SHA-256 fingerprint.

The canonical fingerprint input includes:

- Monitoring run ID.
- Risk ID.
- Risk properties.
- Ordered finding properties.
- Ordered normalized evidence properties.
- Finding count.
- Evidence count.

The resulting fingerprint:

- Contains 64 lowercase hexadecimal characters.
- Is stable for the same ordered context.
- Changes when canonical context content changes.
- Is persisted as recommendation audit metadata.

Database identifiers participate in the fingerprint for traceability integrity.

Those identifiers are not included in the AI prompt payload.

The fingerprint is not used in:

- Risk scoring.
- Confidence scoring.
- Trust scoring.
- Recommendation prioritization.

## AI Provider Port

Sprint 12 introduced the provider-neutral port:

`RiskRemediationAiProvider`

The port exposes:

- Provider name.
- Model name.
- Local availability.
- Structured recommendation generation.

The port does not expose:

- Provider credentials.
- Raw HTTP request.
- Raw HTTP response.
- Raw response body.
- Exception message.
- Prompt persistence.

Provider result statuses are:

- `SUCCESS`
- `UNAVAILABLE`
- `FAILURE`

The architecture permits zero concrete provider beans.

When no provider is configured, the application remains operational and produces rule-based fallback recommendations.

No concrete production AI provider adapter was introduced in Sprint 12.

## Provider Selection Architecture

The generation service accepts an ordered list of provider implementations.

Selection behavior is:

- Select the first available provider.
- Skip providers reporting unavailable.
- Classify availability-check exceptions as provider-selection failure.
- Classify provider invocation exceptions as provider failure.
- Classify null provider results as provider failure.

Provider and model names are validated before use.

Provider metadata must:

- Be non-blank.
- Remain inside persistence length limits.
- Avoid sensitive-material patterns.

Unsafe provider metadata is rejected.

Unsafe metadata is not persisted.

## Prompt Versioning Architecture

Prompt construction is owned by:

`RiskRemediationPromptFactory`

Current versions are:

- Prompt: `risk-remediation-v1`
- Output schema: `risk-remediation-output-v1`
- Fallback rules: `risk-remediation-fallback-v1`

Prompt version, output schema version, and fallback rule version are independent architecture concepts.

They must be increased independently when their corresponding contracts change.

The prompt includes explicit boundaries requiring the provider to:

- Use only supplied context.
- Avoid creating new risks.
- Avoid creating findings or evidence.
- Avoid modifying assessment scores.
- Treat context as data rather than instructions.
- Avoid exposing sensitive material.
- Produce one advisory recommendation.
- Return only one JSON object.
- Avoid Markdown and commentary.

## AI Prompt Payload Boundary

The prompt payload may contain sanitized:

- Risk type.
- Severity.
- Risk score.
- Confidence score.
- Risk rationale.
- Finding count.
- Evidence count.
- Finding type.
- Finding title.
- Finding description.
- Finding confidence score.
- Normalized evidence type.
- Normalized evidence value.

The prompt payload excludes:

- Monitoring run ID.
- Risk ID.
- Finding ID.
- Normalized evidence ID.
- Collected evidence ID.
- Raw evidence.
- Source URL.
- Telegram credentials.
- Database credentials.
- AI provider credentials.

Prompt text is generated for provider invocation only.

Sprint 12 does not persist:

- System instruction.
- User instruction.
- Complete prompt.
- Serialized prompt context.

## Structured AI Output Boundary

AI output is represented by:

`RiskRemediationAiOutput`

The output contract contains:

- Schema version.
- Title.
- Summary.
- Remediation steps.
- Verification steps.
- Advisory flag.

This type represents untrusted provider output.

It intentionally permits invalid state so that invalid output can be classified by the validator instead of being 
confused with provider communication failure.

The AI output object is not a persistence entity.

It cannot be persisted directly.

## Recommendation Validation Architecture

Validation is owned by:

`RiskRemediationRecommendationValidator`

The validator checks:

- Output presence.
- Schema version presence.
- Exact schema version.
- Title presence.
- Title length.
- Summary presence.
- Summary length.
- Remediation step presence.
- Remediation step item presence.
- Remediation step length.
- Remediation step count.
- Verification step presence.
- Verification step item presence.
- Verification step length.
- Verification step count.
- Advisory flag presence.
- Advisory flag value.
- Sensitive-material patterns.
- Persistence content contract compatibility.

Validation returns:

- `VALID` with persistence-ready content.
- `INVALID` with typed issue codes.

Invalid validation results do not contain recommendation content.

Valid validation results do not contain validation issues.

Invalid AI output:

- Is not persisted.
- Is not partially persisted.
- Is not silently corrected.
- Is not stored for later display.
- Leads to deterministic fallback generation.

Sensitive AI output is rejected rather than redacted and accepted.

## Rule-Based Fallback Architecture

Rule-based fallback generation is owned by:

`RiskRemediationRuleBasedFallbackGenerator`

The fallback is:

- Deterministic.
- Versioned.
- Advisory.
- Provider-independent.
- Network-independent.
- Database-independent after context creation.

Fallback content uses:

- Risk type.
- Persisted severity.
- Finding count.
- Normalized evidence count.

Fallback content does not echo:

- Risk rationale.
- Finding description.
- Normalized evidence value.
- Raw evidence.
- Source URL.
- Provider response.
- Exception message.

Severity affects guidance language only.

Severity is not recalculated or modified.

The current fallback rule version is:

`risk-remediation-fallback-v1`

## Recommendation Generation Orchestration

Single-risk orchestration is owned by:

`RiskRemediationRecommendationGenerationService`

The service coordinates:

1. Context construction.
2. Prompt construction.
3. Provider selection.
4. Provider metadata validation.
5. Provider invocation.
6. Provider result classification.
7. Structured output validation.
8. AI or fallback recommendation creation.
9. Validated persistence.

The approved mapping is:

No provider:

- Source: `RULE_BASED_FALLBACK`
- Reason: `PROVIDER_UNAVAILABLE`

Provider unavailable:

- Source: `RULE_BASED_FALLBACK`
- Reason: `PROVIDER_UNAVAILABLE`

Provider failure or exception:

- Source: `RULE_BASED_FALLBACK`
- Reason: `PROVIDER_FAILURE`

Provider success with invalid output:

- Source: `RULE_BASED_FALLBACK`
- Reason: `VALIDATION_FAILURE`

Provider success with valid output:

- Source: `AI`
- Reason: `NONE`

Provider exception messages and invalid AI output are not persisted.

## Transaction Architecture

The recommendation generation orchestration service is not transaction-scoped.

AI provider invocation must not occur inside a long-running database transaction.

Persistence transactions remain short and are owned by:

`RiskRemediationRecommendationService.saveValidated(...)`

Run-level generation also does not create a transaction around all risks.

This design ensures:

- Network latency does not hold database transactions open.
- One failed risk does not roll back previously persisted recommendations.
- Remaining risks continue processing.
- Persistence remains isolated per recommendation.

## Monitoring Lifecycle Integration

Run-level generation is owned by:

`RiskRemediationRecommendationRunGenerationService`

Recommendation generation runs only for a persisted monitoring run with status:

`COMPLETED`

The approved execution order is:

Monitoring collection and assessment  
↓  
Trust assessment generation  
↓  
Monitoring run marked COMPLETED  
↓  
Recommendation generation  
↓  
Notification event generation

Recommendation generation does not run for failed monitoring runs.

A run-level recommendation exception is caught by the monitoring execution boundary.

The completed monitoring run remains completed.

Notification event generation is still attempted.

The recommendation subsystem cannot overwrite the main monitoring failure reason.

## Per-Risk Failure Isolation

Run-level generation processes persisted risks independently.

For each risk:

- Generation is attempted.
- Success increments the generated count.
- Runtime failure increments the failed count.
- Processing continues with the next risk.

The run-level result contains:

- Monitoring run ID.
- Risk count.
- Generated count.
- Failed count.

The result does not contain:

- Recommendation content.
- Prompt content.
- Evidence content.
- Exception message.
- Provider response.

## Risk Detail Presentation Boundary

Risk detail now includes:

- Latest persisted recommendation.
- Recommendation content.
- Recommendation audit metadata.
- Recommendation history.

Recommendation history is read-only and ordered newest first.

The view does not:

- Trigger recommendation generation.
- Call an AI provider.
- Modify a recommendation.
- Modify a risk.
- Modify trust output.

Recommendation content is rendered through escaped Thymeleaf text:

`th:text`

Unescaped rendering through:

`th:utext`

is not approved for recommendation content.

## Monitoring Run Report Architecture

The monitoring run report read model now includes:

- Recommendation count.
- Persisted recommendation collection.
- Latest recommendation per risk.
- Risk-to-recommendation view mapping.
- Recommendation availability.
- Risk-to-recommendation traceability.

The report includes one read-model item for each persisted risk.

A report item may contain:

- Risk with recommendation.
- Risk without recommendation.

This preserves compatibility with:

- Monitoring runs completed before Sprint 12.
- Isolated recommendation generation failures.

Opening a report is a read-only operation.

The report does not:

- Generate recommendations.
- Call an AI provider.
- Re-run monitoring.
- Mutate persisted assessment data.

## PDF Integration Readiness

The monitoring run report view now carries sufficient persisted recommendation information for a future PDF renderer.

The future PDF layer should consume the existing report read model instead of rebuilding recommendation context 
or invoking AI.

The approved future direction is:

Persisted Monitoring Run Report View  
↓  
PDF Renderer  
↓  
Versioned PDF Artifact  
↓  
Telegram Document Dispatch  
↓  
Persisted Dispatch Audit

Sprint 12 stops before PDF rendering.

No PDF renderer or document-delivery adapter was added.

## Audit Architecture

Persisted recommendation audit metadata includes:

- Monitoring run ID.
- Risk ID.
- Source.
- Fallback reason.
- Validation status.
- Advisory status.
- Provider name when safely available.
- Model name when safely available.
- Prompt version.
- Fallback rule version when applicable.
- Context fingerprint.
- Context finding count.
- Context evidence count.
- Generated timestamp.
- Created timestamp.

The recommendation audit model intentionally excludes:

- Full prompts.
- Raw AI responses.
- Invalid AI output.
- Provider response bodies.
- Exception messages.
- Credentials.
- Raw evidence.
- Source URLs.

This provides operational traceability without retaining high-risk provider or secret-bearing payloads.

## Automated Verification Architecture

Sprint 12 added 36 automated tests.

Unit-level coverage includes:

- Secret redaction.
- Private key redaction.
- UTF-16-safe truncation.
- Blank context rejection.
- Structured output acceptance.
- Schema mismatch rejection.
- Non-advisory output rejection.
- Sensitive output rejection.
- Null step rejection.
- Step-count enforcement.
- Deterministic fallback generation.
- Severity-aware fallback guidance.
- Free-text echo prevention.
- Deterministic fingerprint generation.
- Fingerprint change detection.
- Prompt versioning.
- Prompt identifier exclusion.

Orchestration coverage includes:

- No-provider fallback.
- Provider unavailable fallback.
- Provider failure fallback.
- Provider exception isolation.
- Valid AI persistence.
- Validation failure fallback.
- Unsafe provider metadata rejection.
- Per-risk failure isolation.
- Empty completed-run handling.
- Non-completed-run rejection.
- Monitoring lifecycle preservation.
- Failed-run recommendation exclusion.
- Recommendation-before-notification ordering.

Persistence integration coverage includes:

- Monitoring run ordering.
- Monitoring run filtering.
- Risk history ordering.
- Latest recommendation selection.
- Recommendation count isolation.
- AI audit metadata persistence.
- Fallback audit metadata persistence.
- Valid risk-to-run persistence.
- Mismatched risk-to-run rejection.

Final verification result:

- Tests run: 110
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven test: BUILD SUCCESS
- Maven compile: BUILD SUCCESS

## Preserved Architecture Boundaries

Sprint 12 preserves the following ownership:

- Evidence Collection Engine collects evidence.
- Evidence Analysis Engine normalizes evidence and produces findings.
- Risk Evaluation Engine produces risks.
- Trust Evaluation Engine produces trust assessments.
- Recommendation Context Builder produces evidence-safe advisory context.
- Prompt Factory produces versioned provider requests.
- AI Provider port owns provider communication abstraction.
- Recommendation Validator validates untrusted provider output.
- Rule-Based Fallback Generator produces deterministic fallback content.
- Recommendation Generation Service coordinates single-risk generation.
- Recommendation Run Generation Service coordinates completed-run processing.
- Recommendation Service owns validated persistence.
- Risk detail and report layers own read-only presentation.
- Notification Event Generation remains separate from recommendation generation.
- Notification Delivery remains separate from recommendation generation.

## Explicit Non-Responsibilities

The recommendation layer does not own:

- Website registration.
- Monitoring scheduling.
- HTTP evidence collection.
- Evidence normalization.
- Finding generation.
- Risk evaluation.
- Trust assessment.
- Notification event ownership.
- Notification delivery attempts.
- Telegram provider health checks.
- Telegram message delivery.
- PDF creation.
- Report dispatch.

## Accepted Sprint 12 Limitations

The following limitations are accepted at Sprint 12 closure:

- No concrete production AI provider is configured.
- Production recommendation output currently uses rule-based fallback when no provider exists.
- Provider selection uses the first available implementation.
- Provider-specific network timeout policy is not yet implemented.
- Provider-specific retry policy is not yet implemented.
- Provider rate-limit classification is not implemented.
- Recommendation generation is synchronous after monitoring completion.
- Recommendation generation may delay notification generation for runs containing many risks.
- Recommendation regeneration idempotency is not implemented.
- Repeated explicit generation could create additional history records.
- Recommendation supersession is not modeled.
- Recommendation approval is not modeled.
- Recommendation quality feedback is not modeled.
- Prompt injection controls rely on instruction boundaries, context sanitization, structured validation, 
and raw-evidence exclusion; they do not provide a formal guarantee against all adversarial content.
- Pattern-based sensitive-material detection cannot identify every possible secret format.
- Recommendation history retention limits are not implemented.
- Context construction may perform multiple repository reads per risk.
- Provider latency and token-usage metrics are not persisted.
- Raw AI output is intentionally unavailable for forensic replay.
- PDF report generation is not implemented.
- Telegram PDF dispatch is not implemented.

These limitations do not invalidate the Sprint 12 baseline.

They define future architecture work.

## Deferred Architecture Items

The following remain deferred:

- Concrete AI provider adapter.
- AI HTTP client boundary.
- Provider-specific request and response parser.
- AI credential configuration.
- External secret-manager integration.
- Provider timeout configuration.
- Provider retry and backoff.
- Provider rate-limit handling.
- Provider circuit breaker.
- Provider failover.
- Multi-provider routing.
- Provider usage metrics.
- Provider cost metrics.
- Prompt template administration.
- Prompt experiment management.
- Recommendation regeneration controls.
- Recommendation idempotency.
- Recommendation supersession.
- Recommendation approval workflow.
- Recommendation user feedback.
- Recommendation quality scoring.
- Recommendation retention policy.
- Asynchronous recommendation generation.
- Recommendation work queue.
- PDF report renderer.
- PDF artifact persistence.
- PDF versioning.
- PDF integrity fingerprint.
- PDF retention policy.
- Telegram document-upload client boundary.
- Automatic Telegram report dispatch.
- Dispatch persistence.
- Dispatch idempotency.
- Duplicate dispatch prevention.
- Dispatch retry and backoff.
- Dispatch failure recovery.
- Report destination ownership.
- Recipient management.
- Notification subscriptions.
- Authentication.
- Role-based access control.

## Architecture Decision

Sprint 12 is architecturally approved.

SiteSentinel now has a provider-neutral, evidence-grounded, secret-safe, validated, persisted, advisory remediation
recommendation foundation.

The recommendation layer remains downstream from risk and trust assessment.

It does not change assessment calculations.

The application remains operational without a concrete AI provider through deterministic rule-based fallback.

Provider and validation failures remain isolated from the completed monitoring run lifecycle.

Recommendation data is available through risk detail and monitoring run report read models.

The existing report read model is ready to support a future PDF renderer.

PDF generation, Telegram PDF dispatch, dispatch persistence, and dispatch audit remain outside the Sprint 12 
architecture boundary.

# SiteSentinel Architecture Review-13 Opening

- Sprint: Sprint 13 Opening
- Result: APPROVED TO START
- Planned Scope: Full Monitoring Run PDF Artifact Baseline
- PDF generation is limited to completed monitoring runs.
- The renderer will consume the existing `MonitoringRunReportView`.
- The artifact will be immutable and versioned.
- The artifact will carry a SHA-256 fingerprint.
- Duplicate artifacts for the same monitoring run and report version will be prevented.
- Manual generation and manual download will be supported.
- PDF generation failure will not change the monitoring run status.
- Automatic generation, Telegram document upload, and dispatch remain explicitly deferred.
- The V17 migration will be introduced in a subsequent implementation block.

Completed MonitoringRun
↓
MonitoringRunReportService
↓
MonitoringRunReportView
↓
MonitoringRunPdfRenderer
↓
MonitoringRunPdfArtifactService
↓
MonitoringRunPdfArtifact
↓
Manual PDF Download

---

# SiteSentinel Architecture Review-13 Closure

### Decision

APPROVED

### Architectural outcome

Sprint 13 introduced a versioned PDF artifact boundary without coupling
monitoring execution, recommendation generation, PDF rendering, persistence
or Telegram delivery.

The approved flow is:

`MonitoringRunReportService`
→ `MonitoringRunReportView`
→ `MonitoringRunPdfRenderer`
→ `MonitoringRunPdfArtifactGenerationService`
→ `MonitoringRunPdfArtifactService`
→ `MonitoringRunPdfArtifactRepository`

### Renderer boundary

`MonitoringRunPdfRenderer` consumes an already-built
`MonitoringRunReportView` and returns PDF binary content.

The renderer does not:

- query repositories;
- rerun monitoring;
- generate findings or risks;
- calculate trust;
- generate remediation recommendations;
- invoke an AI provider;
- persist artifacts;
- calculate filenames or fingerprints;
- dispatch through Telegram.

### Artifact model

`MonitoringRunPdfArtifact` is an immutable persisted artifact containing:

- artifact ID;
- monitoring run ID;
- report version;
- deterministic filename;
- PDF content type;
- binary content;
- byte size;
- SHA-256 fingerprint;
- generation timestamp;
- persistence timestamp.

### Persistence invariants

V17 and the application persistence boundary enforce:

- completed-run-only persistence;
- valid monitoring run ownership;
- one artifact per run and report version;
- supported report version;
- `application/pdf` content type;
- `%PDF-` binary header;
- positive binary size;
- exact binary-size metadata;
- lowercase 64-character SHA-256 format;
- deterministic path-safe `.pdf` filename.

The application service independently recalculates SHA-256 before persistence.
The database unique constraint remains authoritative under concurrent requests.

### Transaction decision

PDF rendering is intentionally performed outside the artifact write
transaction.

- Report construction uses its existing read-only transaction.
- Rendering does not retain an open database transaction.
- Artifact persistence uses its own controlled write transaction.
- Rendering failure cannot leave a partial database artifact.
- Persistence failure cannot alter monitoring lifecycle output.

### Web boundary

Manual generation and download are separated:

- POST generates and persists the artifact, then redirects.
- GET validates website-to-run and run-to-artifact ownership before download.
- Download responses use `application/pdf`, attachment disposition,
  `no-store` caching and `nosniff`.

### Security and trust assessment

APPROVED

- User-controlled website names are excluded from filenames.
- Raw renderer and persistence errors are not exposed to the browser.
- Artifact download requires two-stage ownership validation.
- Persisted artifact integrity is independently verifiable through SHA-256.
- PDF rendering is read-only and cannot alter the assessment baseline.
- Duplicate artifacts are blocked at both application and database levels.

### Verification baseline

- Compile: SUCCESS
- Tests: 143 PASSED
- Latest migration: V17
- Full integration chain: PASSED
- Visual PDF review: PASSED
- Downloaded SHA-256 comparison: MATCHED
- Duplicate artifact check: PASSED

### Deferred architecture

The following remain outside the approved Sprint 13 baseline:

- automatic post-completion PDF generation;
- Telegram document dispatch;
- dispatch persistence and audit;
- delivery idempotency;
- retry and queue architecture;
- asynchronous processing;
- recipient preferences;
- authorization;
- artifact retention automation.

# SiteSentinel Architecture Review-14 Opening

- Sprint: Sprint 14 Opening
- Result: APPROVED TO START
- Planned Scope: Automatic Telegram PDF Dispatch and Audit
- Automatic PDF dispatch will run only after a monitoring run reaches
  COMPLETED status.
- Recommendation generation will precede PDF generation and dispatch.
- The existing immutable and versioned V1 PDF artifact will be reused when
  available.
- A missing current-version PDF artifact may be generated before dispatch.
- PDF integrity will be revalidated before every automatic or manual
  delivery.
- Telegram document transport will remain separate from PDF rendering and
  persistence.
- Report dispatch will use a dedicated persistence and audit model.
- The existing notification-event delivery-attempt model will not be reused
  for PDF report dispatch.
- Automatic dispatch will require a separate default-disabled configuration
  flag.
- Telegram network failures will not change the authoritative monitoring run
  result.
- Duplicate automatic dispatch will be prevented at application and database
  levels.
- Manual retry will preserve the original failure and create a new attempt.
- Automatic retry scheduling, queues, multiple recipients and additional
  providers remain deferred.
- The V18 migration will be introduced during Sprint 14 implementation.

Completed MonitoringRun
↓
Recommendation Generation
↓
Existing-or-Generate PDF Artifact Resolution
↓
PDF Integrity Revalidation
↓
Automatic Dispatch Attempt
↓
Telegram Document Upload
↓
SENT or FAILED Dispatch Audit

---

# SiteSentinel Architecture Review-14 Closure

## Decision

APPROVED

## Architectural Outcome

Sprint 14 completed the V1 monitoring-to-report-to-Telegram delivery chain
without coupling Telegram transport to the authoritative monitoring,
assessment, recommendation or PDF-rendering lifecycle.

The approved automatic flow is:

`MonitoringExecutionService`
→ `RiskRemediationRecommendationRunGenerationService`
→ `AutomaticMonitoringRunReportDispatchService`
→ `MonitoringRunPdfArtifactResolutionService`
→ `MonitoringRunPdfArtifactService`
→ `MonitoringRunReportDispatchAttemptService`
→ `TelegramDocumentDeliveryService`
→ `TelegramBotApiClient`
→ Telegram Bot API

The approved manual retry flow is:

`MonitoringRunReportDispatchController`
→ `ManualMonitoringRunReportRetryService`
→ existing immutable PDF artifact
→ PDF integrity validation
→ new manual-retry dispatch attempt
→ Telegram document delivery
→ SENT or FAILED terminal persistence

## Telegram Transport Boundary

Telegram document upload is implemented through the following isolated
transport components:

- `TelegramDocumentUploadRequest`
- `TelegramMultipartBody`
- `TelegramBotApiClient`
- `JdkTelegramBotApiClient`
- `TelegramBotApiResponse`
- `TelegramDocumentDeliveryService`
- `TelegramDocumentDeliveryResult`
- `TelegramDocumentDeliveryStatus`

The transport boundary:

- supports binary-safe `multipart/form-data`;
- sends documents through Telegram `sendDocument`;
- uses the configured Telegram destination only;
- preserves the existing Telegram text-message transport;
- extracts successful Telegram `message_id` values;
- rejects incomplete successful responses;
- converts provider failures to controlled delivery results;
- does not expose bot tokens, chat IDs, raw provider responses or exception
  messages through application result contracts.

The transport layer does not:

- generate PDFs;
- query monitoring repositories;
- create dispatch attempts;
- modify monitoring results;
- select arbitrary Telegram destinations;
- persist Telegram credentials.

## Report Dispatch Domain Boundary

Sprint 14 introduced a dedicated report-dispatch aggregate:

`MonitoringRunReportDispatchAttempt`

This model remains separate from:

`NotificationDeliveryAttempt`

The separation is approved because notification-event message delivery and
immutable PDF report dispatch have different:

- ownership relationships;
- content models;
- idempotency requirements;
- retry semantics;
- provider-reference requirements;
- audit histories.

The report-dispatch lifecycle is:

`PENDING`
→ `SENT`

or:

`PENDING`
→ `FAILED`

Terminal attempts cannot transition again.

## Dispatch Types

The approved dispatch types are:

- `AUTOMATIC`
- `MANUAL_RETRY`

An automatic attempt:

- uses attempt number 1;
- does not reference a previous attempt;
- is unique for the monitoring run, PDF artifact and Telegram channel.

A manual retry:

- uses attempt number 2 or greater;
- references the immediately preceding attempt;
- can be created only from the latest FAILED attempt;
- creates a new append-only audit record;
- does not overwrite the previous attempt;
- does not generate a replacement PDF artifact.

## Persistence Architecture

Sprint 14 introduced:

`V18__create_monitoring_run_report_dispatch_attempts_table.sql`

V18 establishes:

- monitoring-run ownership;
- PDF-artifact ownership;
- Telegram-only channel enforcement;
- automatic and manual-retry type enforcement;
- PENDING, SENT and FAILED state enforcement;
- positive attempt-number enforcement;
- automatic attempt-number and lineage rules;
- manual retry lineage rules;
- retry self-reference prevention;
- completion timestamp consistency;
- terminal-state field consistency;
- positive Telegram message ID requirement for SENT attempts;
- absence of Telegram message ID for FAILED attempts;
- automatic dispatch uniqueness;
- attempt-sequence uniqueness;
- cross-run and cross-artifact retry prevention.

The V17 PDF artifact migration remains unchanged.

## PDF Artifact Resolution

`MonitoringRunPdfArtifactResolutionService` provides the approved
existing-or-generate boundary.

The resolution process:

1. Looks for the current V1 PDF artifact.
2. Reuses the existing artifact when present.
3. Generates and persists a V1 artifact when absent.
4. Requires the resolved artifact to be persisted.
5. Verifies monitoring-run ownership.
6. Verifies the current supported report version.
7. Revalidates binary size and SHA-256 integrity.

The resolution service does not regenerate or overwrite an existing V1
artifact.

## Integrity Boundary

PDF integrity is verified:

- when the artifact is initially persisted;
- when an existing artifact is resolved for automatic dispatch;
- before a manual retry attempt is created.

The integrity check verifies:

- binary content presence;
- `%PDF-` header;
- recorded binary size;
- actual binary size;
- SHA-256 fingerprint format;
- recalculated SHA-256 equality.

An artifact that fails integrity validation cannot enter Telegram delivery.

## Transaction Decision

The orchestrators are intentionally not transactional.

The approved automatic transaction sequence is:

Transaction 1:

- resolve or persist the PDF artifact;
- create and commit the PENDING automatic dispatch attempt.

Outside the dispatch write transaction:

- perform the Telegram HTTP request.

Transaction 2:

- reload the run-scoped attempt;
- persist SENT or FAILED terminal state.

The approved manual retry sequence follows the same pattern.

This architecture prevents:

- an open database transaction during provider network latency;
- Telegram failure from rolling back the monitoring run;
- provider failure from deleting the PDF artifact;
- terminal dispatch persistence from changing assessment output.

## Monitoring Completion Ordering

The approved completed-run sequence is:

`MonitoringRun COMPLETED`
→ recommendation generation
→ PDF artifact resolution
→ PDF integrity validation
→ automatic Telegram PDF dispatch
→ notification event generation

Automatic PDF dispatch is skipped when the recommendation-generation
subsystem fails as a whole.

Individual recommendation failures already represented by the recommendation
generation result do not change the completed monitoring lifecycle.

Telegram dispatch failure does not:

- mark the monitoring run FAILED;
- modify risks;
- modify findings;
- modify trust assessment;
- modify recommendations;
- suppress subsequent notification-event generation.

## Idempotency Decision

Automatic dispatch idempotency is enforced twice.

Application level:

- the persistence service checks whether an automatic attempt already exists.

Database level:

- V18 contains an authoritative partial unique index for automatic dispatch.

The database constraint remains the final concurrency boundary.

A second automatic dispatch attempt for the same run, artifact and Telegram
channel cannot be persisted or delivered.

## Manual Retry Decision

Manual retry is an explicit user action.

It:

- does not require the automatic PDF dispatch flag;
- requires the Telegram provider to be enabled and fully configured;
- requires an existing failed attempt;
- requires that failed attempt to be the latest attempt for the artifact;
- reuses the original immutable PDF;
- revalidates artifact integrity;
- persists a new PENDING attempt before delivery;
- completes the new attempt as SENT or FAILED;
- leaves all previous attempts unchanged.

Automatic retry scheduling remains deferred.

## Configuration Architecture

Telegram provider enablement and automatic PDF dispatch enablement are
separate controls:

`sitesentinel.notification.delivery.telegram.enabled`

`sitesentinel.notification.delivery.telegram.automatic-pdf-dispatch-enabled`

Automatic PDF dispatch is disabled by default.

Environment configuration is supported through:

`SITESENTINEL_TELEGRAM_ENABLED`

`SITESENTINEL_TELEGRAM_AUTOMATIC_PDF_DISPATCH_ENABLED`

`SITESENTINEL_TELEGRAM_BOT_TOKEN`

`SITESENTINEL_TELEGRAM_CHAT_ID`

Scheduler execution is independently controlled through:

`sitesentinel.scheduler.enabled=${SITESENTINEL_SCHEDULER_ENABLED:true}`

The scheduler remains enabled by default but can be disabled for controlled
verification without changing source configuration.

## Web Boundary

The report-dispatch web boundary provides:

- run-scoped dispatch history;
- latest attempt status;
- attempt numbering;
- dispatch type;
- completion timestamp;
- successful Telegram message ID;
- safe result and technical summary;
- manual retry only for the latest FAILED attempt.

The manual retry endpoint:

- uses POST;
- validates website-to-monitoring-run ownership;
- validates attempt-to-run ownership;
- does not accept a destination from the request;
- does not expose raw provider or exception details;
- redirects back to the full monitoring run report.

Historical dispatch attempts remain read-only.

## Security Review

APPROVED

Sprint 14 preserves the following security properties:

- Telegram credentials remain external configuration.
- Bot token and chat ID are not persisted in dispatch records.
- Arbitrary Telegram destination selection is not supported.
- Multipart metadata is validated before HTTP request construction.
- Document bytes are defensively copied.
- Unsafe filename separators and header-injection characters are rejected.
- Provider exception messages are not returned to controllers.
- Raw Telegram response bodies are not persisted.
- Monitoring-run, artifact and attempt ownership are enforced.
- Retry operations cannot cross monitoring-run or artifact boundaries.
- Controlled verification did not expose secrets in source or logs.

## Operational Review

APPROVED

The implementation supports:

- default-disabled automatic PDF dispatch;
- controlled provider readiness checks;
- real Telegram document delivery;
- persisted provider message reference;
- manual recovery from failed delivery;
- append-only audit history;
- scheduler disablement during controlled verification.

A successful automatic attempt suppresses manual retry because the latest
attempt is no longer FAILED.

## Verification Baseline

- Compile: SUCCESS
- Tests: 228 PASSED
- Latest migration: V18
- Telegram upload request validation: PASSED
- Binary-safe multipart body verification: PASSED
- JDK Telegram document HTTP transport: PASSED
- Telegram message ID extraction: PASSED
- Document delivery result classification: PASSED
- Secret-safe provider failure handling: PASSED
- Dispatch domain lifecycle tests: PASSED
- V18 database constraint tests: PASSED
- Automatic dispatch idempotency tests: PASSED
- Retry lineage tests: PASSED
- PDF pre-dispatch integrity validation: PASSED
- Monitoring completion ordering tests: PASSED
- Monitoring lifecycle failure-isolation tests: PASSED
- Manual retry orchestration tests: PASSED
- Controller ownership and safe-feedback tests: PASSED
- Report template contract tests: PASSED
- Controlled local Telegram stub integration: PASSED
- Real PDF generation in dispatch integration: PASSED
- Same-artifact manual retry integration: PASSED

Controlled real Telegram verification:

- Application startup: SUCCESS
- Telegram provider readiness: READY
- Telegram health check: HEALTHY
- Controlled monitoring run: COMPLETED
- Recommendation-before-dispatch ordering: VERIFIED
- Real PDF generation: VERIFIED
- Real Telegram PDF delivery: VERIFIED
- Telegram PDF received and opened: VERIFIED
- Automatic dispatch attempt: SENT
- Telegram message ID persistence: VERIFIED
- Report-page dispatch history: VERIFIED
- Manual retry suppression after success: VERIFIED
- Secrets in source or logs: NOT DETECTED

## Accepted Sprint 14 Limitations

The following limitations are accepted at Sprint 14 closure:

- Automatic dispatch runs synchronously after monitoring completion.
- Telegram latency may extend the post-completion request duration.
- No durable dispatch queue exists.
- No automatic retry scheduler exists.
- No exponential backoff exists.
- No provider rate-limit-specific retry policy exists.
- No dead-letter mechanism exists.
- Recovery of indefinitely PENDING attempts is not automated.
- Manual retry is available only through the report page.
- Dispatch history has no dedicated administration page.
- Only one configured Telegram destination is supported.
- Destination ownership and recipient preferences are not modeled.
- Recipient subscriptions are not implemented.
- Additional PDF delivery providers are not implemented.
- Dispatch operational metrics are not persisted.
- Dispatch alerts are not implemented.
- Authentication and role-based authorization are not implemented.
- PDF artifact retention and cleanup automation are not implemented.
- AI-generated unresolved-risk impact analysis remains deferred.

These limitations do not invalidate the Sprint 14 baseline.

They define future production-hardening work.

## Deferred Architecture Items

The following remain deferred:

- asynchronous PDF dispatch;
- durable dispatch queue;
- automatic retry scheduler;
- exponential retry backoff;
- provider rate-limit classification;
- retry-after handling;
- dead-letter processing;
- PENDING-attempt recovery;
- dispatch reconciliation worker;
- dispatch metrics;
- operational dispatch alerting;
- recipient ownership;
- multi-recipient delivery;
- notification subscriptions;
- destination preferences;
- additional document-delivery providers;
- artifact retention policy;
- artifact cleanup automation;
- authentication;
- role-based access control;
- recommendation approval;
- recommendation supersession;
- AI-generated unresolved-risk impact analysis.

## Architecture Decision

Sprint 14 is architecturally approved.

SiteSentinel now provides a complete V1 monitoring-to-report-to-Telegram
delivery architecture.

The system can generate evidence-grounded remediation recommendations,
resolve or generate an immutable integrity-validated PDF artifact,
automatically deliver that artifact through Telegram and persist an auditable
delivery outcome.

Telegram delivery remains downstream from monitoring, assessment and
recommendation generation.

Provider failure cannot alter the authoritative completed monitoring result.

Automatic duplicate delivery is blocked at application and database levels.

Failed delivery can be manually retried using the same immutable PDF while
preserving the complete dispatch history.

# SiteSentinel Architecture Review-15 Opening

- Sprint: Sprint 15 Opening
- Result: APPROVED TO START
- Planned Scope: Production OpenAI Recommendation Provider Baseline
- The existing provider-neutral recommendation architecture will be preserved.
- OpenAI will be implemented as the first concrete production AI provider.
- OpenAI-specific HTTP, request and response details will remain isolated from
  the recommendation domain.
- Provider enablement will be disabled by default.
- API credentials will remain external environment configuration.
- The model name will remain configuration-controlled.
- The OpenAI Responses API will be used.
- Recommendation output will use strict structured JSON.
- Existing recommendation validation will remain authoritative.
- Existing rule-based fallback generation will remain mandatory.
- Provider unavailability or failure will not fail a monitoring run.
- Raw provider responses and exception messages will not be persisted.
- Existing recommendation persistence will be reused.
- Existing PDF generation and Telegram dispatch boundaries will remain
  unchanged.
- No database migration is expected.
- Multi-provider routing, provider failover, automatic retry and recommendation
  lifecycle expansion remain deferred.

Approved planned flow:

Completed Monitoring Run
↓
Persisted Risk
↓
Evidence-Safe Recommendation Context
↓
Versioned Prompt
↓
Provider Selection
↓
OpenAI Provider Adapter
↓
OpenAI Responses API
↓
Strict Structured Output
↓
Existing Recommendation Validator
↓
AI Recommendation or Rule-Based Fallback
↓
Existing Recommendation Persistence
↓
Existing PDF Generation
↓
Existing Telegram PDF Dispatch

---

# SiteSentinel Architecture Review-15 Closure

## Decision

APPROVED

## Architectural Outcome

Sprint 15 completed the first concrete production AI provider implementation
without replacing or weakening the provider-neutral recommendation
architecture introduced in Sprint 12.

The approved real-AI flow is:

`RiskRemediationRecommendationRunGenerationService`
→ `RiskRemediationRecommendationGenerationService`
→ `RiskRemediationRecommendationContextBuilder`
→ `RiskRemediationPromptFactory`
→ `RiskRemediationAiProvider`
→ `OpenAiRiskRemediationAiProvider`
→ `OpenAiRecommendationApiClient`
→ `JdkOpenAiRecommendationApiClient`
→ OpenAI Responses API
→ `OpenAiRecommendationResponseParser`
→ `RiskRemediationAiOutput`
→ `RiskRemediationRecommendationValidator`
→ `RiskRemediationRecommendationService`

The approved fallback flow is:

Provider disabled, unavailable or failed
↓
`RiskRemediationAiProviderResult`
↓
`RiskRemediationRecommendationGenerationService`
↓
`RiskRemediationRuleBasedFallbackGenerator`
↓
Validated fallback recommendation
↓
Existing recommendation persistence

The existing downstream report flow remains:

Persisted recommendations
↓
Monitoring run report
↓
Immutable PDF artifact
↓
Automatic Telegram document dispatch
↓
Persisted dispatch audit

OpenAI transport remains downstream from authoritative monitoring, evidence,
finding, assessment and risk persistence.

## Provider-Neutral Architecture

APPROVED

The existing `RiskRemediationAiProvider.java` interface remains the
recommendation domain boundary.

`RiskRemediationRecommendationGenerationService.java` continues to depend on:

`List<RiskRemediationAiProvider>`

rather than a concrete OpenAI implementation.

This preserves:

- provider-neutral orchestration;
- optional provider availability;
- future provider extensibility;
- existing provider selection behavior;
- existing validation behavior;
- existing fallback behavior;
- isolation from external API details.

The concrete OpenAI adapter is discovered as a Spring bean through the existing
provider list.

No OpenAI-specific type is introduced into:

- monitoring execution;
- risk persistence;
- recommendation persistence;
- PDF generation;
- Telegram delivery;
- controllers;
- database entities.

OpenAI is therefore an infrastructure adapter rather than an authoritative
domain dependency.

## OpenAI Package Boundary

OpenAI-specific implementation is isolated under:

`com.cigabyte.sitesentinel.recommendation.openai`

The approved components are:

- `OpenAiRecommendationProperties.java`
- `OpenAiRecommendationApiClient.java`
- `OpenAiRecommendationApiStatus.java`
- `OpenAiRecommendationApiResult.java`
- `OpenAiRecommendationRequestBodyFactory.java`
- `OpenAiRecommendationResponseParser.java`
- `JdkOpenAiRecommendationApiClient.java`
- `OpenAiRiskRemediationAiProvider.java`

The package owns:

- provider configuration;
- Responses API request construction;
- HTTP transport;
- OpenAI response parsing;
- transport failure classification;
- mapping into the existing provider-neutral result.

The package does not own:

- evidence collection;
- finding generation;
- risk classification;
- severity determination;
- recommendation persistence;
- report rendering;
- Telegram delivery;
- retry orchestration;
- monitoring-run state transitions.

## Configuration Architecture

APPROVED

OpenAI configuration is represented by:

`OpenAiRecommendationProperties.java`

Configuration prefix:

`sitesentinel.recommendation.ai.openai`

Supported environment configuration:

- `SITESENTINEL_OPENAI_ENABLED`
- `SITESENTINEL_OPENAI_API_KEY`
- `SITESENTINEL_OPENAI_API_BASE_URL`
- `SITESENTINEL_OPENAI_MODEL`
- `SITESENTINEL_OPENAI_CONNECT_TIMEOUT_SECONDS`
- `SITESENTINEL_OPENAI_REQUEST_TIMEOUT_SECONDS`
- `SITESENTINEL_OPENAI_MAX_OUTPUT_TOKENS`

Approved safe defaults:

- provider enabled: `false`;
- API key: empty;
- API base URL: `https://api.openai.com/v1`;
- model: `gpt-5.6-terra`;
- connection timeout: 10 seconds;
- request timeout: 60 seconds;
- maximum output tokens: 2000.

Provider readiness requires:

- explicit enablement;
- non-blank API key;
- non-blank API base URL;
- non-blank model name.

Configuration sanitization:

- trims string values;
- restores approved defaults for blank API URL or model values;
- enforces positive timeout values;
- enforces a minimum maximum-output-token value;
- removes a trailing slash when constructing the operation endpoint.

The model name remains configuration-controlled and is not embedded in the
recommendation domain.

## Default-Disabled Safety Decision

APPROVED

External OpenAI communication is disabled by default.

The application does not call OpenAI unless:

`SITESENTINEL_OPENAI_ENABLED=true`

and all required provider configuration is available.

When the provider is disabled or incomplete:

- `OpenAiRiskRemediationAiProvider.isAvailable()` returns false;
- the OpenAI API client is not invoked;
- recommendation generation selects the existing rule-based fallback;
- the persisted fallback reason is `PROVIDER_UNAVAILABLE`;
- monitoring execution remains successful.

This prevents accidental provider use during:

- local development;
- automated tests;
- fresh deployments;
- incomplete configuration;
- environments without API billing;
- environments without approved external-AI access.

## Telegram Safe-Default Correction

APPROVED

Sprint 15 corrected a configuration inconsistency inherited from the Sprint 14
runtime properties.

The following properties now bind to disabled unless explicitly enabled:

`sitesentinel.notification.delivery.telegram.enabled`

`sitesentinel.notification.delivery.telegram.automatic-pdf-dispatch-enabled`

Approved environment defaults:

`SITESENTINEL_TELEGRAM_ENABLED=false`

`SITESENTINEL_TELEGRAM_AUTOMATIC_PDF_DISPATCH_ENABLED=false`

This aligns runtime property binding with the existing
`TelegramDeliveryProperties.java` safety defaults and the Sprint 14
architecture decision.

`TelegramDeliveryConfigurationBindingTests.java` protects this configuration
baseline.

## Request Construction Architecture

APPROVED

`OpenAiRecommendationRequestBodyFactory.java` converts the existing
provider-neutral `RiskRemediationAiRequest.java` into an OpenAI Responses API
request.

The approved request contains:

- configured model;
- system instruction;
- user instruction;
- bounded maximum output tokens;
- `store=false`;
- strict structured-output configuration.

The request body does not include:

- OpenAI API key;
- authorization header;
- recommendation context fingerprint;
- internal prompt-version metadata;
- database identifiers beyond text intentionally present in the sanitized
  provider request;
- raw database entities;
- raw website content outside the existing evidence-safe prompt boundary.

The existing prompt factory remains responsible for deciding what information
is sent to the provider.

The OpenAI request factory does not independently access repositories or
monitoring data.

## Structured Output Architecture

APPROVED

OpenAI output is constrained through a strict JSON Schema.

The required output fields are:

- `schemaVersion`
- `title`
- `summary`
- `remediationSteps`
- `verificationSteps`
- `advisory`

The schema enforces:

- object output;
- all expected fields required;
- no additional properties;
- expected schema version only;
- non-empty title;
- non-empty summary;
- at least one remediation step;
- at least one verification step;
- bounded string lengths;
- bounded list sizes;
- advisory value fixed to `true`.

Structured output reduces parsing ambiguity but does not replace application
validation.

`RiskRemediationRecommendationValidator.java` remains the authoritative
acceptance boundary.

A structurally parseable OpenAI response can still be rejected by the existing
validator and replaced with a rule-based fallback.

## Response Parsing Architecture

APPROVED

`OpenAiRecommendationResponseParser.java` accepts only a narrow successful
response shape.

A successful provider result requires:

- a 2xx HTTP response;
- OpenAI response status `completed`;
- no response-level error;
- an output message;
- exactly one `output_text` content item;
- non-blank structured output text;
- JSON convertible to `RiskRemediationAiOutput.java`.

The parser rejects:

- null or blank response bodies;
- malformed JSON;
- incomplete responses;
- failed responses;
- response-level errors;
- absent output;
- absent output text;
- multiple output-text items;
- unsupported output shapes;
- output that cannot be mapped to the typed AI output model.

A refusal content item is classified as:

`REQUEST_REJECTED`

The refusal message itself is not exposed outside the parser.

The parser does not return raw JSON.

## HTTP Transport Architecture

APPROVED

`JdkOpenAiRecommendationApiClient.java` implements the external transport with
Java `HttpClient`.

Approved operation:

`POST <normalized-api-base-url>/responses`

Approved headers:

- Bearer authorization;
- `Content-Type: application/json`;
- `Accept: application/json`.

Approved transport controls:

- configured connection timeout;
- configured request timeout;
- UTF-8 request and response handling;
- redirect following disabled;
- typed result returned instead of transport exception propagation.

Redirect following remains disabled to reduce the risk of authorization data
being forwarded to another destination.

The Spring injection constructor is explicitly marked because the class also
contains a package-private constructor used by isolated transport tests.

## Transport Result Architecture

APPROVED

OpenAI transport results are represented by:

- `OpenAiRecommendationApiStatus.java`
- `OpenAiRecommendationApiResult.java`

Supported transport statuses:

- `SUCCESS`
- `REQUEST_REJECTED`
- `AUTHENTICATION_FAILED`
- `RATE_LIMITED`
- `TIMEOUT`
- `PROVIDER_UNAVAILABLE`
- `INVALID_RESPONSE`
- `INTERRUPTED`
- `FAILURE`

HTTP classification:

- 2xx → response parser;
- 401 or 403 → `AUTHENTICATION_FAILED`;
- 429 → `RATE_LIMITED`;
- 500–599 → `PROVIDER_UNAVAILABLE`;
- other non-2xx → `REQUEST_REJECTED`.

Exception classification:

- HTTP timeout → `TIMEOUT`;
- thread interruption → `INTERRUPTED`;
- network I/O failure → `FAILURE`;
- invalid request construction → `FAILURE`;
- security-related request failure → `FAILURE`.

Thread interruption restores the thread interrupt flag.

A successful result requires:

- typed `RiskRemediationAiOutput`;
- valid 2xx HTTP status.

An unsuccessful result cannot carry AI output.

Transport results do not contain:

- API key;
- authorization header;
- raw response body;
- OpenAI error message;
- exception message;
- prompt content.

## Provider Adapter Decision

APPROVED

`OpenAiRiskRemediationAiProvider.java` maps the OpenAI transport layer into the
existing provider-neutral contract.

Approved mapping:

OpenAI transport `SUCCESS`
→ provider `SUCCESS`

OpenAI provider disabled or incomplete configuration
→ provider `UNAVAILABLE`

Any unsuccessful OpenAI transport result
→ provider `FAILURE`

Unexpected runtime exception
→ provider `FAILURE`

Null client result
→ provider `FAILURE`

This intentionally prevents OpenAI transport-specific status values from
leaking into the recommendation domain.

The existing recommendation architecture persists the broad fallback reason:

- `PROVIDER_UNAVAILABLE`;
- `PROVIDER_FAILURE`;
- `VALIDATION_FAILURE`.

Detailed authentication, rate-limit and timeout diagnostics are not persisted
in Sprint 15.

## Validation and Fallback Decision

APPROVED

AI output is advisory and cannot bypass
`RiskRemediationRecommendationValidator.java`.

The accepted success sequence is:

OpenAI transport success
↓
Typed AI output
↓
Existing recommendation validator
↓
Validated recommendation content
↓
AI recommendation persistence

If validation fails:

- AI output is rejected;
- rule-based recommendation is generated;
- fallback reason is `VALIDATION_FAILURE`;
- provider metadata may remain available for audit according to the existing
  recommendation-generation behavior;
- the invalid provider output is not persisted as recommendation content.

If the provider is unavailable:

- rule-based recommendation is generated;
- fallback reason is `PROVIDER_UNAVAILABLE`.

If the provider fails:

- rule-based recommendation is generated;
- fallback reason is `PROVIDER_FAILURE`.

Fallback generation remains mandatory rather than optional.

## Monitoring Lifecycle Isolation

APPROVED

Provider communication remains subordinate to the authoritative monitoring
lifecycle.

OpenAI does not:

- start monitoring runs;
- complete monitoring runs;
- fail monitoring runs;
- collect evidence;
- create findings;
- create risks;
- alter risk severity;
- alter risk status;
- alter trust evaluation;
- modify website state;
- modify comparison baselines.

Controlled provider-failure verification confirmed:

- monitoring run status remained `COMPLETED`;
- four risks remained persisted;
- four rule-based fallback recommendations were generated;
- fallback reason was `PROVIDER_FAILURE`;
- provider errors did not become monitoring failure reasons.

Provider-disabled verification confirmed:

- no OpenAI HTTP request was performed;
- monitoring run status remained `COMPLETED`;
- four risks produced four fallback recommendations;
- fallback reason was `PROVIDER_UNAVAILABLE`.

## Persistence Architecture

APPROVED

Sprint 15 reused the existing
`risk_remediation_recommendations` persistence model.

Existing audit fields already support:

- recommendation source;
- fallback reason;
- validation status;
- provider name;
- model name;
- prompt version;
- context fingerprint;
- finding count;
- evidence count;
- advisory flag;
- generation timestamp.

No new table or column was required.

Latest migration remains:

V18

No V19 migration was introduced.

Raw OpenAI requests and responses are not persisted.

API credentials are not persisted.

## Transaction Architecture

APPROVED

Sprint 15 does not introduce a provider-spanning database transaction.

The external OpenAI HTTP call does not create a distributed transaction with:

- monitoring-run persistence;
- risk persistence;
- recommendation persistence;
- PDF artifact persistence;
- Telegram dispatch persistence.

Recommendation persistence continues through the existing application service
after provider output has been parsed and validated.

Provider failure produces fallback content rather than a partial AI
recommendation record.

The existing per-risk generation and failure-isolation behavior remains
authoritative.

## PDF and Telegram Boundary Preservation

APPROVED

Sprint 15 does not couple OpenAI transport to PDF rendering or Telegram
delivery.

OpenAI communication produces recommendation content only.

The PDF layer reads persisted report data.

The Telegram layer delivers an immutable persisted PDF artifact.

The verified production chain is:

Completed monitoring run
↓
Four persisted risks
↓
Four validated OpenAI recommendations
↓
Persisted recommendation audit metadata
↓
Generated PDF artifact
↓
Automatic Telegram document delivery
↓
Persisted SENT dispatch attempt
↓
Persisted Telegram message ID

Controlled verification confirmed:

- PDF AI recommendation content was present;
- PDF was generated successfully;
- PDF was delivered successfully;
- automatic dispatch count remained one;
- Telegram message ID was persisted;
- no secret was detected in the PDF or observed output.

## Security Review

APPROVED

Sprint 15 preserves the following security properties:

- OpenAI access is disabled by default.
- API key is supplied only through external environment configuration.
- API key is not included in source-controlled properties.
- API key is not included in request JSON.
- API key is not returned in typed results.
- Authorization headers are not logged or persisted.
- Raw OpenAI responses are not persisted.
- Raw OpenAI error bodies are not propagated.
- Provider exception messages are not propagated.
- Refusal text is not propagated.
- Redirect following is disabled.
- Request and output sizes are bounded.
- Only evidence-safe prompt content is sent.
- Strict structured output is required.
- Existing output validation remains mandatory.
- AI output remains advisory.
- Provider failure cannot alter authoritative monitoring results.
- Controlled real verification detected no secret exposure.
- Controlled invalid-key verification detected no secret exposure.

## Operational Review

APPROVED

Sprint 15 supports:

- default-disabled provider operation;
- environment-controlled provider enablement;
- environment-controlled model selection;
- environment-controlled timeouts;
- environment-controlled output size;
- controlled real provider verification;
- provider-disabled fallback;
- provider-failure fallback;
- real AI recommendation persistence;
- real AI content in PDF reports;
- automatic Telegram delivery of AI-enriched reports.

A real controlled monitoring run produced:

- four persisted risks;
- four AI recommendations;
- provider name `OpenAI`;
- model name `gpt-5.6-terra`;
- validation status `VALID`;
- fallback reason `NONE`.

A controlled invalid-key run produced:

- four persisted risks;
- four rule-based fallback recommendations;
- fallback reason `PROVIDER_FAILURE`;
- successful monitoring completion.

## Verification Baseline

- Final compile: SUCCESS
- Final tests: 277 PASSED
- Failures: 0
- Errors: 0
- Latest migration: V18
- Telegram safe property binding: PASSED
- OpenAI safe defaults: PASSED
- OpenAI property binding: PASSED
- Provider readiness rules: PASSED
- Configuration sanitization: PASSED
- Timeout and output bounds: PASSED
- Typed API-result invariants: PASSED
- Strict request-schema generation: PASSED
- Sensitive configuration serialization prevention: PASSED
- Internal metadata serialization prevention: PASSED
- Completed response parsing: PASSED
- Refusal classification: PASSED
- Incomplete-response rejection: PASSED
- Malformed-response rejection: PASSED
- Multiple-output rejection: PASSED
- Responses API endpoint mapping: PASSED
- Bearer authorization transport: PASSED
- HTTP failure classification: PASSED
- Timeout classification: PASSED
- Interruption classification: PASSED
- Interrupt-flag restoration: PASSED
- Network-failure containment: PASSED
- Disabled-provider HTTP prevention: PASSED
- Provider success mapping: PASSED
- Provider-failure containment: PASSED
- Spring application-context wiring: PASSED
- Existing fallback regression: PASSED
- Real OpenAI recommendation: VERIFIED
- Provider-disabled fallback: VERIFIED
- Provider-failure fallback: VERIFIED
- AI recommendation PDF content: VERIFIED
- Automatic Telegram delivery: VERIFIED
- Telegram message ID persistence: VERIFIED
- Secret exposure: NOT DETECTED

## Accepted Sprint 15 Limitations

The following limitations are accepted at Sprint 15 closure:

- Only OpenAI is implemented as a concrete production provider.
- Provider selection uses the existing first-available-provider behavior.
- Explicit provider priority configuration is not implemented.
- Provider failover is not implemented.
- Multi-provider routing is not implemented.
- Provider-specific transport failures collapse to the broad domain status
  `FAILURE`.
- Authentication failure is not separately persisted.
- Rate-limit failure is not separately persisted.
- Timeout failure is not separately persisted.
- Retry-After headers are not processed.
- Automatic OpenAI retry is not implemented.
- Exponential backoff is not implemented.
- Circuit breaker behavior is not implemented.
- Provider health history is not persisted.
- Token usage is not captured.
- Provider cost is not captured.
- Provider response latency is not captured.
- Recommendation-generation duration is not captured.
- Provider request IDs are not persisted.
- Recommendation generation remains synchronous.
- A durable recommendation queue is not implemented.
- Recommendation idempotency is not defined.
- Duplicate recommendation prevention is not implemented.
- Recommendation regeneration is not implemented.
- Recommendation supersession is not implemented.
- Recommendation approval is not implemented.
- Recommendation quality feedback is not implemented.
- Prompt administration is not implemented.
- Prompt experimentation is not implemented.
- External secret-manager integration is not implemented.
- Authentication and role-based authorization remain deferred.
- AI-generated unresolved-risk impact analysis remains deferred.

These limitations do not invalidate the Sprint 15 architecture.

They define later production-hardening and recommendation-lifecycle work.

## Deferred Architecture Items

The following remain deferred:

- second concrete AI provider;
- explicit provider priority;
- provider failover;
- multi-provider routing;
- provider-specific persisted failure classification;
- automatic retry;
- exponential backoff;
- Retry-After processing;
- circuit breaker;
- provider health monitoring;
- provider latency metrics;
- token-usage accounting;
- provider-cost accounting;
- provider request-ID audit;
- asynchronous recommendation generation;
- durable recommendation queue;
- recommendation idempotency policy;
- duplicate recommendation prevention;
- recommendation regeneration;
- recommendation history and supersession;
- recommendation approval workflow;
- recommendation feedback;
- recommendation-quality evaluation;
- prompt administration;
- prompt experimentation;
- external secret management;
- authentication;
- role-based access control;
- AI-generated unresolved-risk impact analysis.

## Architecture Decision

Sprint 15 is architecturally approved.

SiteSentinel now provides a concrete production OpenAI recommendation provider
while retaining the existing provider-neutral recommendation boundary.

The system can:

- build an evidence-safe recommendation context;
- create a versioned provider request;
- call the OpenAI Responses API;
- request strict structured output;
- parse a narrow approved response shape;
- validate generated recommendation content;
- persist AI recommendation audit metadata;
- fall back safely when OpenAI is disabled or fails;
- generate a PDF containing validated AI recommendations;
- automatically deliver the PDF through Telegram;
- preserve an auditable dispatch result.

OpenAI remains an advisory infrastructure dependency.

It does not control the authoritative monitoring, evidence, finding, risk,
assessment, PDF or delivery lifecycles.

Provider failure cannot invalidate a completed monitoring run.

Provider-disabled and provider-failure paths continue to produce one
rule-based remediation recommendation for every persisted risk.

The approved V1 operational chain is now:

Scheduled or Manual Monitoring
↓
Evidence-Based Risk Identification
↓
Validated OpenAI Remediation Recommendations
↓
Rule-Based Fallback When Required
↓
Full Monitoring Run PDF
↓
Automatic Telegram Delivery
↓
Persisted Delivery Audit

# SiteSentinel Architecture Review-16A Closure

## Decision

Sprint 16A is architecturally approved.

SiteSentinel now supports adaptive HTTP response-body storage and streaming
analysis without introducing response truncation or changing the authoritative
monitoring lifecycle.

The memory threshold is approved as a storage spillover boundary only.

It is not approved as a response-size scan cutoff.

## Architectural Outcome

The previous HTTP collection path required response bodies to be materialized
as complete in-memory strings before evidence analysis.

Sprint 16A replaces that unconditional storage assumption with an adaptive
response-processing boundary.

The approved lifecycle is:

```text
HTTP Response
↓
Adaptive Body Handler
↓
Bounded In-Memory Storage
↓
Temporary-File Spillover When Required
↓
Full Streaming Analysis
↓
Existing Collected Evidence
↓
Existing Finding, Risk and Trust Lifecycle
↓
Validated Recommendation
↓
PDF Artifact
↓
Telegram Dispatch
```

This change is internal to HTTP evidence acquisition and analysis.

It does not create a parallel evidence model.

## Adaptive Response Storage Boundary

`AdaptiveResponseBodyCollector.java` controls whether response bytes remain in
memory or spill to temporary storage.

`StoredResponseBody.java` represents the completed storage result.

`CollectedHttpResponse.java` combines HTTP response metadata with ownership of
the stored response body.

The storage decision is based on the configured in-memory threshold.

Crossing that threshold changes the storage medium.

It does not stop response consumption or analysis.

## Streaming Analysis Boundary

`StreamingResponseBodyAnalyzer.java` coordinates response-body analysis without
requiring a complete response string.

The streaming analysis boundary delegates to:

- `StreamingResponseBodyFingerprintCalculator.java`;
- `StreamingHtmlContentExtractor.java`;
- `CountingReader.java`;
- `ScriptAndStyleFilteringReader.java`.

The resulting `ResponseBodyAnalysisResult.java` provides the evidence-compatible
analysis output required by the existing collection engine.

The architecture preserves:

- full body-length calculation;
- full SHA-256 fingerprint calculation;
- bounded snippet extraction;
- visible HTML-text extraction;
- script-content exclusion;
- style-content exclusion;
- non-HTML body analysis.

## Response Lifecycle Ownership

`CollectedHttpResponse.java` is approved as the lifecycle owner for the stored
response body associated with an HTTP response.

The response must be closed when processing finishes.

This ownership rule applies to:

- primary website responses;
- redirected responses;
- robots.txt responses;
- sitemap.xml responses;
- in-memory storage;
- temporary-file storage.

The collection engine must not abandon a temporary-file-backed response without
closing it.

## Temporary-File Architecture

Temporary-file spillover is transient infrastructure behavior.

It is not:

- evidence persistence;
- an artifact model;
- a response archive;
- a reporting input;
- a user-downloadable resource.

Temporary-file content must be removed after the collected response lifecycle
finishes.

The configured temporary directory remains optional.

When no directory is configured, the operating-system temporary directory is
used.

## Evidence Compatibility Decision

Sprint 16A preserves the existing collected-evidence contract.

The adaptive pipeline continues to produce the evidence required for:

- requested URL;
- final URL;
- canonical URL;
- fetch outcome;
- HTTP status;
- response content type;
- security headers;
- response-body length;
- response-body fingerprint;
- bounded body snippet;
- HTML metadata;
- robots.txt;
- sitemap.xml.

No downstream component needs to understand whether a response body was stored
in memory or in a temporary file.

This storage detail remains encapsulated inside the evidence collection
boundary.

## Configuration Architecture

The approved scanner configuration is:

```properties
sitesentinel.scanner.in-memory-body-threshold-bytes=1048576
sitesentinel.scanner.temporary-directory=
```

The threshold property must remain clearly documented as a spillover threshold.

It must not be interpreted as:

- a maximum downloadable response size;
- a maximum analysable response size;
- an evidence truncation boundary;
- a security scan cutoff.

A future maximum-response-size policy, if introduced, must use a separate
property and an explicit product and security decision.

## Monitoring Lifecycle Preservation

Sprint 16A does not change the authoritative lifecycle:


Monitoring Run
↓
Collected Evidence
↓
Normalized Evidence
↓
Finding
↓
Risk
↓
Trust Assessment
↓
Remediation Recommendation
↓
PDF Artifact
↓
Telegram Dispatch

The adaptive storage layer cannot:

- create findings;
- create risks;
- alter risk severity;
- alter trust scores;
- alter recommendation content;
- bypass recommendation validation;
- modify PDF rendering rules;
- modify Telegram dispatch rules.

## Notification Decision

Runtime verification produced an unchanged existing high-risk condition.

The absence of a duplicate notification event was determined to be correct
behavior.

Repeated unchanged risks should not create repeated notification noise.

The existing notification deduplication logic is preserved.

Only the empty-notification explanation on the monitoring run detail page was
clarified.

The UI must not claim that no high-risk assessment was detected when a high-risk
assessment exists but does not qualify for a new notification.

## Security Review

Sprint 16A improves memory behavior for larger HTTP responses, but it does not
claim to provide a complete response-size denial-of-service policy.

The following security properties are preserved:

- private-target restrictions;
- redirect validation;
- configured redirect limits;
- bounded body-snippet persistence;
- no complete response-body persistence;
- no temporary response-body artifact exposure;
- deterministic temporary-file cleanup;
- no secret persistence;
- no raw provider-response persistence.

No authentication or authorization boundary was changed.

No credential-handling behavior was changed.

## Operational Review

Small responses remain in memory.

Responses exceeding the configured threshold spill to temporary storage.

The complete response remains available for streaming analysis after spillover.

Operational correctness depends on:

- writable temporary storage;
- sufficient temporary disk capacity;
- reliable response closure;
- cleanup on supported failure paths;
- suitable threshold configuration.

The current implementation does not add persistent temporary-file inventory or
temporary-disk utilization metrics.

These remain possible future operational-hardening items.

## Dispatch Timestamp Stability

Closure regression identified a timing-dependent dispatch completion failure.

The persisted attempt timestamp and newly generated completion timestamp could
be separated by sub-millisecond precision behavior.

The dispatch service now guarantees:

completionTimestamp = max(currentUtcTimestamp, persistedAttemptTimestamp)

## Runtime Verification

Controlled runtime verification confirmed:

- monitoring completion;
- full evidence collection;
- normalized evidence production;
- finding generation;
- risk generation;
- trust assessment;
- recommendation generation;
- PDF artifact generation;
- successful Telegram PDF delivery.

Observed controlled-run result:

- collected evidence: 43;
- normalized evidence: 27;
- findings: 4;
- risks: 4;
- trust assessments: 1;
- trust status: `HIGH_RISK`;
- trust score: 35;
- recommendation count: 4;
- recommendation failures: 0;
- Telegram dispatch status: `SENT`;
- Telegram delivery attempted: true;
- Telegram delivery successful: true.

## Verification Baseline

- Full regression tests: 333 PASSED
- Failures: 0
- Errors: 0
- Build: SUCCESS
- Template regression tests: PASSED
- Configuration binding tests: PASSED
- Runtime monitoring lifecycle: VERIFIED
- Runtime recommendation generation: VERIFIED
- Runtime PDF generation: VERIFIED
- Runtime Telegram delivery: VERIFIED
- Database migration added: NO
- Latest migration: V18
- Response truncation introduced: NO
- Response scan cutoff introduced: NO
- Existing evidence semantics: PRESERVED
- Notification deduplication behavior: PRESERVED
- Secret exposure detected: NO
- Generated artifacts staged: NO
- Temporary response files staged: NO

## Accepted Sprint 16A Limitations

The following limitations are accepted:

- response analysis remains synchronous;
- temporary-file disk utilization is not measured;
- temporary-file cleanup metrics are not recorded;
- temporary-directory health monitoring is not implemented;
- a distinct maximum-response-size security policy is not implemented;
- response download rate limiting is not implemented;
- per-host response-size history is not recorded;
- spillover frequency metrics are not recorded;
- spillover byte-volume metrics are not recorded;
- operator alerts for temporary-storage pressure are not implemented;
- distributed temporary storage is not implemented;
- authentication and role-based authorization remain deferred.

These limitations do not invalidate the adaptive streaming-response baseline.

## Deferred Architecture Items

The following remain deferred:

- explicit maximum-response-size security policy;
- temporary-storage capacity monitoring;
- spillover metrics;
- cleanup-failure metrics;
- response-processing latency metrics;
- slow-response protection;
- response download rate limits;
- asynchronous evidence analysis;
- operational alerts for temporary-storage pressure;
- configurable cleanup reconciliation;
- authentication;
- role-based authorization.

## Architecture Decision

Sprint 16A is approved as a behavior-preserving HTTP evidence collection
hardening increment.

The implementation removes unconditional full-response in-memory storage while
preserving complete response analysis.

The approved invariant is:

Memory Threshold ≠ Scan Cutoff

A response may move from memory to temporary storage, but it must continue
through the existing full analysis and evidence lifecycle unless a future
explicit response-size security policy is separately approved.

# SiteSentinel Architecture Review-16

## Review Status

- Sprint: Sprint 16
- Scope: Adaptive Response Analysis and Risk Explanation Baseline
- Result: APPROVED
- Final tests: 341 passed
- Latest migration: V18

## Architectural Outcome

Sprint 16 completed two related production-hardening objectives:

1. adaptive processing of HTTP response bodies without response truncation;
2. evidence-bounded risk and potential-impact explanations in remediation
   recommendations and reports.

The implementation preserves the existing monitoring, evidence, risk,
recommendation, reporting and delivery boundaries.

## Adaptive Response Processing Decision

APPROVED

The HTTP evidence collection path no longer depends on
`BodyHandlers.ofString()` for response-body collection.

The adaptive response boundary supports:

- smaller response bodies retained in memory;
- larger response bodies spilled to a temporary file;
- full-body analysis after spillover;
- streaming fingerprint calculation;
- HTML analysis orchestration;
- non-HTML fingerprint and snippet preservation;
- deterministic response-resource cleanup.

No response scan cutoff or truncation boundary was introduced.

The resulting evidence semantics remain compatible with the existing finding
and risk evaluation pipeline.

## Temporary-File Lifecycle Decision

APPROVED

Large-response temporary files are lifecycle-owned resources.

Cleanup is performed for:

- normally processed responses;
- redirect responses;
- optional-resource responses;
- response paths that no longer require retained body content.

Temporary-file spillover does not create a new persistence model and does not
change the monitoring-run transaction boundary.

## Risk Explanation Decision

APPROVED

A persisted risk recommendation now includes a Risk and Potential Impact
Summary explaining:

- what was detected;
- what the risk means;
- why it matters;
- what may happen if it remains unresolved;
- what the evidence confirms;
- what the evidence does not confirm.

The explanation remains advisory and cannot replace or modify the persisted
risk assessment.

## Evidence Authority Decision

APPROVED

The recommendation layer may explain supplied persisted evidence but may not:

- create another risk;
- create findings or evidence;
- alter risk severity or risk score;
- alter confidence or trust scores;
- infer an incident unsupported by persisted evidence;
- present potential consequences as confirmed events.

Persisted evidence, findings and risks remain authoritative.

## Unsupported Incident Claim Decision

APPROVED

The validator now rejects unsupported authoritative incident or exploitation
claims through:

- `UNSUPPORTED_INCIDENT_CLAIM`.

The validator permits conditional impact descriptions using language such as
`may` or `could`.

It also permits explicit evidence-boundary statements explaining that an
attack, compromise, breach, incident or exploitation was not confirmed.

This validation is a safety boundary for recommendation content and is not an
incident-detection subsystem.

## Versioning Decision

APPROVED

Sprint 16B introduced:

- `RiskRemediationPromptVersion.V2`;
- prompt schema `risk-remediation-v2`;
- output schema `risk-remediation-output-v2`;
- `RiskRemediationFallbackRuleVersion.V2`;
- fallback rule `risk-remediation-fallback-v2`.

Historical V1 identifiers remain preserved for existing persisted records and
audit interpretation.

No historical record is rewritten.

## Structured Output Decision

APPROVED

The OpenAI Structured Output schema retains the existing recommendation JSON
shape:

- `schemaVersion`;
- `title`;
- `summary`;
- `remediationSteps`;
- `verificationSteps`;
- `advisory`.

The semantic contract of `summary` is expanded to represent the Risk and
Potential Impact Summary.

No new transport field, persistence field or database column is required.

## Rule-Based Fallback Decision

APPROVED

The rule-based fallback remains the authoritative availability mechanism when:

- the AI provider is disabled;
- the AI provider fails;
- provider output is invalid;
- provider output violates the recommendation contract.

Fallback V2 provides dedicated explanations for the currently supported risk
types and a safe generic explanation for unknown future risk types.

The generic explanation does not invent risk-specific facts.

## Reporting Decision

APPROVED

HTML and PDF reports use the same recommendation structure:

1. Risk Type, Severity and Score
2. Risk and Potential Impact Summary
3. Remediation Steps
4. Verification Steps
5. Recommendation Audit Metadata

Both report paths consume persisted recommendation output.

Neither renderer:

- calls the AI provider;
- regenerates recommendations;
- reruns monitoring;
- recalculates risk or trust output;
- modifies persisted recommendation content.

## Dispatch Decision

APPROVED

Automatic Telegram PDF delivery and manual retry continue to operate on the
generated persisted report artifact.

Recommendation explanation changes do not alter:

- dispatch idempotency;
- delivery-attempt persistence;
- Telegram delivery isolation;
- monitoring-run completion state;
- manual retry semantics.

## Database Decision

APPROVED

Sprint 16 requires no database migration.

Latest migration remains:

- V18

Existing response evidence and recommendation persistence models remain
sufficient.

## Security Decision

APPROVED

Sprint 16 preserves:

- external secret configuration;
- no secret values in recommendation context;
- context sanitization;
- prompt-injection resistance through data-only context handling;
- provider-disabled safe behavior;
- provider-failure isolation;
- advisory-only recommendation output;
- deterministic fallback availability.

Potential consequence language does not establish that an attack or incident
occurred.

## Regression Decision

APPROVED

Automated verification completed successfully:

- Sprint 16A full baseline: 329 tests passed;
- Sprint 16B recommendation/report regression: 96 tests passed;
- Sprint 16 final full regression: 341 tests passed;
- failures: 0;
- errors: 0;
- skipped: 0.

## Accepted Limitations

The following limitations remain accepted:

- recommendations remain advisory and do not apply remediation automatically;
- incident confirmation remains outside the recommendation subsystem;
- the validator targets explicit unsupported incident language and is not a
  general natural-language fact-verification engine;
- only the currently supported risk types have dedicated fallback
  explanations;
- unknown future risk types use a safe generic explanation;
- response-body processing remains bounded by available runtime and temporary
  storage resources;
- authentication and role-based authorization remain deferred;
- artifact retention and cleanup policy remains deferred;
- multi-provider failover remains deferred.

These limitations do not invalidate the Sprint 16 baseline.

## Architecture Decision

Sprint 16 is architecturally approved.

The completed system can process large HTTP response bodies without
truncating the content used by analysis and can communicate the meaning and
potential impact of persisted risks without weakening evidence authority or
claiming unsupported incidents.

Result:

- APPROVED

# SiteSentinel Architecture Review-17

## Review Status

- Sprint: Sprint 17
- Review scope: Recommendation Idempotency and Lifecycle Safety
- Decision: APPROVED
- Database migration: NOT REQUIRED
- Latest migration: V18
- Full regression: 349 PASSED

## Architectural Outcome

Sprint 17 introduces an application-level automatic idempotency boundary for
risk remediation recommendation generation.

Before automatic generation begins for a persisted risk, the run-level
orchestrator checks whether recommendation history already exists for the
specific:

- monitoring run ID;
- risk ID.

If the complete pair already has a persisted recommendation, generation is
skipped.

The AI provider, rule-based fallback generator and recommendation persistence
path are not invoked again for that pair.

## Idempotency Boundary Decision

The idempotency decision is implemented in:

- `RiskRemediationRecommendationRunGenerationService.java`.

The persistence lookup is provided by:

- `RiskRemediationRecommendationRepository.java`;
- `existsByRiskIdAndMonitoringRunId(...)`.

This is the correct boundary because
`RiskRemediationRecommendationRunGenerationService.java` owns automatic
per-run and per-risk recommendation orchestration.

The lower-level
`RiskRemediationRecommendationGenerationService.java` remains responsible for
explicit recommendation generation.

It does not silently decide whether historical recommendation records may be
regenerated, replaced or superseded.

This separation preserves a future explicit regeneration or supersession
policy without weakening the current automatic idempotency guarantee.

## Identity Decision

Automatic recommendation identity is defined by the complete pair:

- `monitoringRunId`;
- `riskId`.

A match on only one identifier is insufficient.

Recommendation history for another risk in the same monitoring run does not
block generation.

Recommendation history associated with another monitoring run does not block
generation.

Repository integration verification confirms exact pair matching.

## Lifecycle Result Decision

`RiskRemediationRecommendationRunGenerationResult.java` now distinguishes:

- generated recommendations;
- skipped recommendations;
- failed recommendations.

The required lifecycle invariant is:

`generatedCount + skippedCount + failedCount = riskCount`

A persisted recommendation produces a skipped result.

Skipped is considered a successful idempotent outcome and does not increment
the failure count.

The existing four-argument constructor remains available with
`skippedCount = 0` to preserve compatibility with existing monitoring and
report-dispatch test fixtures.

## Provider Invocation Decision

For a monitoring-run and risk pair without recommendation history, existing
generation behavior remains unchanged.

The system may continue through:

- provider selection;
- evidence-safe context construction;
- prompt construction;
- OpenAI provider execution;
- provider-output validation;
- rule-based fallback generation;
- recommendation persistence.

For a pair with persisted recommendation history, this chain is not entered.

This guarantees that repeated automatic execution does not call either the AI
provider or fallback generator after persistence is observed.

## Failure Isolation Decision

The recommendation existence query remains inside the existing per-risk
failure-isolation boundary.

A lookup or generation failure affecting one risk:

- is recorded as a failed recommendation outcome;
- does not stop recommendation processing for later risks;
- does not fail the completed monitoring run.

This preserves the monitoring lifecycle isolation established in the existing
recommendation architecture.

## Recommendation History Decision

Sprint 17 does not alter persisted recommendation history.

The implementation does not:

- delete a recommendation;
- replace a recommendation;
- update historical recommendation content;
- mark a recommendation as superseded;
- select a different latest-recommendation algorithm.

The existing history and latest queries remain authoritative.

HTML and PDF reporting continue to consume persisted recommendation history
and display the latest recommendation for each risk.

## Monitoring Execution Decision

`MonitoringExecutionService.java` continues to call
`RiskRemediationRecommendationRunGenerationService.java` after a monitoring
run is completed.

There is no second production automatic path that directly calls
`RiskRemediationRecommendationGenerationService.java`.

The completed production call path remains:

1. Complete monitoring execution.
2. Invoke run-level recommendation orchestration.
3. Check persisted recommendation history per risk.
4. Generate missing recommendations or skip existing recommendations.
5. Continue to automatic report dispatch.
6. Continue to notification generation.

This preserves recommendation-before-report ordering.

## PDF and Telegram Decision

Sprint 17 does not modify the PDF or Telegram boundaries.

The following behavior remains unchanged:

- monitoring-run report construction;
- latest-recommendation resolution;
- PDF rendering;
- PDF artifact persistence;
- PDF artifact reuse;
- automatic Telegram PDF dispatch;
- manual Telegram retry;
- Telegram document delivery;
- report-dispatch idempotency.

A skipped recommendation remains available to reporting through its existing
persisted record.

## Database Decision

Sprint 17 introduces no database migration.

Latest migration remains:

- V18

A database unique constraint was intentionally excluded from the sprint.

The implemented existence check protects sequential and repeated automatic
execution after persisted history becomes visible.

It does not claim to prevent two concurrent transactions from both passing the
existence check before either transaction persists a recommendation.

Database-level concurrency enforcement requires a separate uniqueness,
locking or serialization decision.

## Security Decision

Sprint 17 preserves the existing recommendation security boundaries:

- AI credentials remain externally configured;
- Telegram credentials remain externally configured;
- providers remain disabled by default;
- evidence-safe recommendation context remains enforced;
- recommendation validation remains mandatory;
- recommendations remain advisory;
- recommendations do not create findings or risks;
- recommendations do not apply remediation;
- unsupported incident claims remain rejected.

The idempotency lookup uses persisted identifiers and does not add external
input or provider data to the security boundary.

## Regression Decision

Sprint 17 verification confirms:

- run-generation service tests: 6 passed;
- run-generation result tests: 4 passed;
- recommendation repository tests: 8 passed;
- combined recommendation and fallback regression: 25 passed;
- monitoring execution and report-dispatch safety tests: 5 passed;
- PDF and Telegram regression tests: 34 passed;
- full regression: 349 passed;
- failures: 0;
- errors: 0;
- skipped: 0.

Provider-disabled and provider-failure fallback behavior remains verified.

Recommendation history and latest-recommendation behavior remain verified.

PDF and Telegram behavior remains verified.

## Accepted Limitations

Sprint 17 accepts the following limitations:

- no database unique constraint;
- no concurrent check-and-insert protection;
- no recommendation regeneration UI;
- no recommendation supersession model;
- no human approval workflow;
- no provider retry or backoff;
- no asynchronous recommendation queue;
- no second AI provider;
- no recommendation lifecycle mutation.

These limitations do not invalidate the sequential automatic idempotency
baseline completed in Sprint 17.

## Architecture Decision

Sprint 17 is architecturally approved.

SiteSentinel now prevents repeated automatic recommendation generation when a
persisted recommendation already exists for the same monitoring-run and risk
pair.

The implementation places idempotency at the automatic orchestration boundary,
preserves the explicit generation service, maintains recommendation history,
and leaves reporting, PDF generation and Telegram delivery unchanged.

# SiteSentinel Architecture Review-18

## Sprint

Sprint 18 Closure

## Result

APPROVED

## Product Owner

Approved

## Architecture Status

V1 Single-Operator Authentication and Access Protection Implemented

## Verification Baseline

- Tests run: 366
- Failures: 0
- Errors: 0
- Skipped: 0
- Build: SUCCESS
- Database migration: NOT REQUIRED
- Latest migration: V18
- Controlled runtime authentication verification: PASSED

## Architectural Outcome

Sprint 18 introduces a secure-by-default authentication boundary around the
existing SiteSentinel V1 application.

All application and operational endpoints now require authentication except
for the explicitly permitted:

- login endpoint;
- required static CSS resources;
- application error endpoint.

The implementation uses one environment-configured operator account.

It does not introduce a database-backed identity aggregate, multi-user
authorization model or organization boundary.

## Security Configuration Decision

`SiteSentinelSecurityConfiguration.java` owns the web authentication and
authorization boundary.

The configuration:

- requires authentication for every non-permitted request;
- uses form-based login;
- redirects successful authentication to the dashboard;
- preserves CSRF protection;
- uses POST-based logout;
- invalidates authenticated access after logout;
- exposes no default production credential.

This is the correct V1 boundary because authentication remains an application
access concern and does not alter assessment-domain services or persisted
assessment records.

## Credential Configuration Decision

Operator credentials are supplied through:

- `SITESENTINEL_SECURITY_USERNAME`;
- `SITESENTINEL_SECURITY_PASSWORD`.

`SiteSentinelSecurityProperties.java` rejects blank values.

Missing credentials prevent application startup.

The application therefore does not fall back to:

- anonymous access;
- a generated development password;
- a source-controlled password;
- a database-persisted operator credential.

The runtime password is encoded before being stored in the in-memory
authentication provider.

Raw credentials are not exposed through controllers, templates, logs or
persistence.

## Authentication Model Decision

Sprint 18 deliberately uses a single in-memory operator identity.

The operator receives the application role:

- `OPERATOR`.

This role establishes a stable V1 identity label without claiming that
fine-grained RBAC has been implemented.

Every authenticated V1 operator currently receives access to the complete
SiteSentinel application.

Future multi-user or role-based authorization must be introduced through a
separate architecture decision.

## Login and Logout Decision

`LoginController.java` and `login.html` provide the controlled application
login surface.

The login page:

- accepts username and password;
- displays a generic authentication-failure message;
- does not distinguish an unknown username from an incorrect password;
- displays successful logout status;
- exposes no configured credential data.

The dashboard provides a POST-based logout form with a framework-generated
CSRF token.

After logout, protected resources require authentication again.

## CSRF Decision

CSRF protection remains enabled globally.

Sprint 18 does not add broad CSRF exclusions.

State-changing browser operations continue to require valid CSRF tokens,
including:

- logout;
- website registration;
- monitoring execution;
- schedule changes;
- notification state changes;
- Telegram provider health checks;
- PDF artifact generation;
- manual Telegram report retry.

Security regression tests explicitly verify rejection of representative
critical operations when the CSRF token is absent.

## Protected Resource Decision

Authentication protection applies to the complete existing V1 application,
including:

- monitoring and scheduling;
- evidence, finding and risk traceability;
- trust assessments and comparisons;
- notification operations;
- Telegram delivery administration;
- full monitoring-run reports;
- PDF artifact generation and download;
- manual report-dispatch retry.

Required stylesheet resources remain anonymously accessible so the login
surface can render before authentication.

## Test Configuration Decision

Automated tests receive controlled test-only operator values through Maven
Surefire system properties.

These values:

- exist only inside test JVMs;
- are not runtime defaults;
- are not production secrets;
- do not weaken credentials-required application startup;
- keep existing Spring application-context tests independent from developer
  credentials.

Security-specific tests may provide their own explicit test properties when
verifying authentication behavior.

## Domain Isolation Decision

Authentication is implemented outside the authoritative assessment domain.

Sprint 18 does not change:

- website identity;
- monitoring-run lifecycle;
- HTTP evidence collection;
- finding generation;
- risk evaluation;
- trust assessment;
- assessment comparison;
- recommendation generation;
- recommendation validation;
- recommendation idempotency;
- PDF artifact identity;
- Telegram dispatch identity or audit history.

Authentication failure cannot mutate assessment or recommendation records.

No AI provider or delivery provider participates in authentication.

## Persistence Decision

Sprint 18 introduces no database migration.

Latest migration remains:

- V18

The following are not persisted:

- operator username;
- operator password;
- authentication sessions;
- roles;
- login attempts.

A database-backed identity model requires a separate future decision.

## Runtime Verification

Controlled runtime verification confirmed:

- application startup with explicit credentials;
- no generated security password;
- anonymous redirect to the custom login page;
- invalid-password rejection;
- successful operator authentication;
- authenticated dashboard access;
- CSRF-protected logout;
- rejection of protected access after logout;
- no OpenAI, Telegram or scheduler execution during security verification.

## Regression Decision

Full regression confirms:

- tests run: 366;
- failures: 0;
- errors: 0;
- skipped: 0;
- build: SUCCESS.

The completed monitoring-to-recommendation-to-PDF-to-Telegram chain remains
behavior-compatible.

## Accepted Limitations

Sprint 18 accepts the following V1 limitations:

- one operator account;
- one application-wide role;
- no database-backed users;
- no user administration;
- no password reset;
- no multi-factor authentication;
- no per-website authorization;
- no organization or tenant isolation;
- no OAuth2, OIDC or SSO;
- no API-token authentication;
- no privileged-action audit model.

These limitations do not invalidate the controlled single-operator production
boundary.

## Architecture Decision

Sprint 18 is architecturally approved.

SiteSentinel now requires explicit environment-controlled operator
authentication, protects all non-public application surfaces, preserves CSRF
protection and provides controlled login and logout behavior.

The implementation remains separate from the authoritative assessment,
recommendation, reporting and delivery domains.

---

# Sprint 19 Architecture Review

## Sprint 19 Closure

Sprint 19 title:

Bilingual PDF Reporting

Final verification:

- tests run: 398;
- failures: 0;
- errors: 0;
- skipped: 0;
- build: SUCCESS;
- latest migration: V19.

## Architecture Objective

Sprint 19 extends the existing monitoring-run recommendation, PDF artifact and
Telegram delivery chain with an explicit report-language dimension.

The implementation does not create a second reporting architecture.

English and Turkish reports use the same:

- monitoring-run ownership boundary;
- recommendation validation boundary;
- PDF rendering boundary;
- immutable artifact model;
- SHA-256 integrity validation;
- artifact download authorization;
- Telegram document-delivery boundary;
- dispatch audit model.

## Explicit Language Boundary

`SiteSentinelReportLanguage.java` is the canonical language model.

Supported values are:

- `ENGLISH`;
- `TURKISH`.

Report language participates explicitly in:

- recommendation requests;
- recommendation persistence;
- recommendation lookup;
- recommendation idempotency;
- report read models;
- PDF artifact generation;
- PDF artifact persistence;
- PDF artifact resolution;
- PDF filenames;
- automatic Telegram dispatch.

Language is not inferred from presentation text or filenames.

## Recommendation Identity Decision

Recommendation history remains append-only.

The effective automatic-generation lookup boundary is now:

- monitoring run ID;
- risk ID;
- report language.

This permits English and Turkish recommendation records for the same risk
without weakening Sprint 17 idempotency.

A persisted English recommendation does not prevent required Turkish
recommendation generation.

A persisted Turkish recommendation does not overwrite English recommendation
history.

## PDF Artifact Identity Decision

PDF artifact uniqueness now includes:

- monitoring run ID;
- report version;
- report language.

Separate English and Turkish artifacts therefore have:

- independent persistence IDs;
- independent filenames;
- independent integrity hashes;
- independent resolution results;
- independent download identities.

The language dimension is part of artifact identity rather than mutable
presentation metadata.

## Persistence Decision

Sprint 19 introduces:

- `V19__add_report_language_support.sql`

The migration adds and constrains `report_language` for:

- `risk_remediation_recommendations`;
- `monitoring_run_pdf_artifacts`.

Existing records are conservatively backfilled as `ENGLISH`.

The previous PDF artifact uniqueness boundary is replaced by a
language-aware uniqueness constraint.

Latest migration:

- V19

## Unicode and Font Decision

Turkish PDF output requires deterministic Unicode glyph support.

Sprint 19 packages local Noto Sans regular and bold font resources.

This decision:

- avoids platform-font differences;
- avoids external font retrieval;
- avoids CDN dependencies;
- preserves offline PDF rendering;
- enables verified Turkish characters.

The packaged fonts are presentation resources and do not affect monitoring or
recommendation semantics.

## Localization Boundary

`MonitoringRunPdfTextLocalizer.java` provides deterministic localization of
canonical report labels and supported lifecycle text.

Complete translation of all persisted historical prose is not attempted when
a safe deterministic translation is unavailable.

This preserves an important integrity rule:

Localization must not invent or reinterpret evidence, findings, risks,
provider output or historical persisted statements.

Complete Turkish localization of all persisted lifecycle prose remains a
future follow-up.

## Delivery Decision

The automatic Telegram report-dispatch path resolves both language-specific
artifacts.

The existing safety boundaries remain:

- existing-or-generate resolution;
- integrity verification before dispatch;
- provider readiness;
- append-only dispatch audit;
- delivery failure isolation from monitoring completion.

No new provider, recipient or retry architecture is introduced.

## Preserved Domains

Sprint 19 does not change:

- evidence collection;
- finding generation;
- risk evaluation;
- trust assessment;
- assessment comparison;
- notification generation;
- authentication;
- CSRF protection;
- OpenAI provider selection;
- fallback safety;
- completed monitoring-run status.

## Accepted Limitations

Sprint 19 accepts:

- two explicitly supported report languages;
- no runtime language administration;
- no operator language preference;
- no generalized translation service;
- possible English persisted lifecycle prose in Turkish reports when no safe
  deterministic translation exists;
- no additional delivery destinations;
- no translation-quality workflow.

## Architecture Decision

Sprint 19 is architecturally approved.

The bilingual implementation extends the existing reporting and delivery
architecture through an explicit language identity while preserving
recommendation validation, artifact immutability, integrity validation,
ownership safety and dispatch auditing.

---

# Sprint 20 Architecture Review

## Sprint 20 Closure

Sprint 20 title:

Premium Single-Operator Dashboard Experience

Final verification:

- tests run: 417;
- failures: 0;
- errors: 0;
- skipped: 0;
- build: SUCCESS;
- runtime visual verification: PASSED;
- latest migration: V19.

## Architecture Objective

Sprint 20 improves the authenticated dashboard presentation without changing
application-domain behavior.

The implementation remains server-rendered through Thymeleaf and uses the
existing controller model.

No client-side application architecture is introduced.

## Presentation-Only Decision

Sprint 20 production changes are limited to:

- `dashboard/index.html`;
- `app.css`.

`DashboardController.java` remains unchanged.

The dashboard continues to consume the existing:

- website counts;
- monitoring-run counts;
- latest monitoring runs;
- latest trust assessments;
- latest notification events;
- unread notification count.

No new repository query, entity, projection, persistence model or backend
service was introduced.

## Operational Information Hierarchy

The dashboard is organized through the following hierarchy:

1. Product identity and primary operator actions.
2. Current operational KPI summary.
3. Attention Required notification region.
4. Latest monitoring-run execution.
5. Latest trust posture.
6. Latest notification events.
7. Secondary navigation.

This hierarchy prioritizes operator action without changing the underlying
data model.

## Attention Required Decision

The Attention Required region uses existing notification data.

It displays unread HIGH and CRITICAL event previews from the existing latest
notification collection.

The dashboard does not create:

- a new notification classification;
- a new notification query;
- a new persisted priority;
- a new read/unread lifecycle;
- a new alerting channel.

The region is a presentation of existing operational state.

## Responsive Design Decision

The dashboard uses:

- a bounded maximum-width shell;
- responsive grid layout;
- responsive header actions;
- mobile breakpoints;
- table overflow containment;
- mobile-only horizontal table scrolling when necessary.

Long UUID values use constrained monospace previews and ellipsis.

The complete value remains present in the rendered content and is not
truncated in persistence.

## Desktop Notification Table Decision

The notification table uses controlled desktop column allocation so that the
detail action remains visible without desktop horizontal scrolling.

On smaller viewports, the table retains a controlled minimum width and scrolls
inside its panel rather than expanding the complete page viewport.

This behavior is presentation-only.

## Accessibility Decision

Sprint 20 adds:

- responsive viewport metadata;
- semantic landmarks;
- scoped heading relationships;
- table header scopes;
- visible keyboard focus;
- sufficient button foreground contrast;
- readable status and severity badges;
- accessible notification-region labelling;
- reduced-motion handling.

Status and severity are never communicated by color alone; their textual
values remain visible.

## Security Preservation

The dashboard sign-out control remains:

- a form;
- POST-based;
- protected by the framework-generated CSRF token.

Sprint 20 does not alter:

- security configuration;
- authentication rules;
- operator credentials;
- session handling;
- endpoint authorization;
- CSRF enforcement.

The existing security contract remains passing.

## Dependency Decision

Sprint 20 introduces no:

- JavaScript framework;
- chart library;
- external font;
- external icon;
- CDN resource;
- frontend build pipeline.

The dashboard remains compatible with the existing Spring Boot static-resource
and Thymeleaf deployment model.

## Test Architecture

Sprint 20 adds focused regression contracts through:

- `DashboardPremiumStructureTemplateTests.java`;
- `DashboardPremiumStylesheetTests.java`;
- `DashboardAttentionRequiredTemplateTests.java`;
- `DashboardVisualRefinementStylesheetTests.java`.

The stylesheet test helper requires selectors to start at a CSS rule boundary,
preventing a specific descendant selector from being mistaken for a base
selector.

Existing security integration tests continue to verify rendered dashboard
access, static CSS access, POST logout and CSRF behavior.

## Preserved Domains

Sprint 20 does not change:

- website registration;
- monitoring execution;
- evidence collection;
- findings;
- risks;
- trust assessments;
- comparisons;
- notifications;
- recommendations;
- bilingual PDF generation;
- PDF artifacts;
- Telegram dispatch;
- authentication.

## Accepted Limitations

Sprint 20 accepts:

- one server-rendered dashboard;
- one shared operator layout;
- no customizable widgets;
- no charts;
- no historical trend visualization;
- no dark mode;
- no user-specific preferences;
- no client-side sorting or filtering;
- raw lifecycle timestamps;
- compact UUID previews.

## Architecture Decision

Sprint 20 is architecturally approved.

The dashboard now provides a premium, responsive and operations-focused
single-operator workspace while remaining a presentation-only consumer of
existing application data.

The monitoring, recommendation, bilingual reporting, delivery, authentication
and CSRF boundaries remain unchanged.


---

# Sprint 21 Architecture Review

## Sprint 21 Closure

Sprint 21 title:

Premium Application Shell and Website Management Experience

Final verification:

- targeted UI tests run: 45;
- targeted UI failures: 0;
- targeted UI errors: 0;
- targeted UI skipped: 0;
- full regression tests run: 465;
- full regression failures: 0;
- full regression errors: 0;
- full regression skipped: 0;
- build: SUCCESS;
- latest migration: V19;
- checkpoint commit: `774752c`;
- local and remote branches: synchronized;
- working tree: clean.

## Architecture Objective

Sprint 21 extends the premium authenticated dashboard presentation established
in Sprint 20 into a reusable application-wide presentation foundation and the
website management surfaces.

The implementation remains server-rendered through Thymeleaf.

No client-side application architecture, JavaScript framework or frontend
build pipeline is introduced.

## Shared Application Shell Decision

Sprint 21 introduces a reusable authenticated application header through:

- `templates/fragments/application-header.html`;
- shared application-shell styles in `static/css/app.css`.

The shared header provides:

- product identity;
- primary application navigation;
- active navigation state;
- authenticated POST-based logout;
- responsive horizontal navigation;
- mobile-safe layout behavior;
- visible keyboard focus.

The logout boundary remains protected by the existing Spring Security and CSRF
configuration.

## Reusable Presentation Foundation

Sprint 21 introduces reusable presentation components for:

- application page shells;
- responsive content containers;
- premium panels;
- page headings;
- primary and secondary actions;
- table containers;
- data tables;
- status badges;
- technical values;
- form layouts;
- form controls;
- validation states;
- empty states;
- detail summaries;
- responsive mobile behavior.

These components are presentation-only.

They do not introduce application-domain state or persistence behavior.

## Website Management Surface Decision

Sprint 21 applies the shared presentation foundation to:

- website list;
- website creation;
- website detail.

The existing website controller, service, repository and entity behavior remain
unchanged.

The website pages continue to use the existing:

- website registration flow;
- website validation rules;
- monitoring-run execution actions;
- monitoring schedule controls;
- report and traceability navigation;
- CSRF-protected POST operations.

## Responsive and Accessibility Decision

Sprint 21 adds or preserves:

- responsive viewport metadata;
- semantic main content structure;
- shared navigation labelling;
- visible focus states;
- readable status indicators;
- mobile-safe form layouts;
- contained table overflow;
- accessible form labels;
- validation messages;
- text-based status communication;
- reduced-motion behavior.

Long technical values remain available in the rendered content and are
presented through overflow-safe styling.

## Dependency Decision

Sprint 21 introduces no:

- JavaScript framework;
- CSS framework;
- external font;
- external icon dependency;
- CDN dependency;
- frontend build system;
- client-side routing;
- client-side state store.

The application remains compatible with the existing Spring Boot static
resource and Thymeleaf deployment model.

## Test Architecture

Sprint 21 adds focused presentation regression contracts through:

- `ApplicationPremiumFoundationStylesheetTests.java`;
- `ApplicationHeaderFragmentTemplateTests.java`;
- `ApplicationHeaderStylesheetTests.java`;
- `ApplicationContentComponentsStylesheetTests.java`;
- `ApplicationFormComponentsStylesheetTests.java`;
- `ApplicationDetailComponentsStylesheetTests.java`;
- `WebsiteListPremiumTemplateTests.java`;
- `WebsiteCreatePremiumTemplateTests.java`;
- `WebsiteDetailPremiumShellTemplateTests.java`.

The focused UI suite verifies 45 presentation contracts.

The full regression verifies 465 tests without failure.

## Preserved Domains

Sprint 21 does not change:

- website persistence;
- monitoring execution;
- evidence collection;
- evidence analysis;
- findings;
- risks;
- trust assessments;
- comparison;
- notification generation;
- recommendation generation;
- bilingual PDF generation;
- PDF artifact persistence;
- Telegram dispatch;
- authentication;
- authorization;
- CSRF enforcement;
- database schema.

## Accepted Limitations

Sprint 21 accepts:

- server-rendered application pages;
- one shared operator presentation;
- no dark mode;
- no user-specific theme preferences;
- no client-side table sorting;
- no client-side filtering;
- no customizable navigation;
- progressive application of the shared presentation foundation to remaining
  pages in future work.

## Architecture Decision

Sprint 21 is architecturally approved.

The application now has a reusable premium presentation foundation and a
consistent website management experience while preserving all existing domain,
security, monitoring, reporting and delivery boundaries.


---

# Sprint 22 Architecture Review

## Sprint 22 Opening

Sprint 22 title:

Website Health and Active Problem Detection

Opening baseline:

- branch: `main`;
- commit: `36db857`;
- remote branch: synchronized with `origin/main`;
- working tree: clean;
- tests run: 465;
- failures: 0;
- errors: 0;
- skipped: 0;
- build: SUCCESS;
- latest migration: V19;
- architecture decision: ADR-0005 accepted;
- premium UI checkpoint: `774752c`;
- public IPv4 target classification fix: completed;
- controlled runtime scan of `rbcborealis.com`: successful.

## Architecture Objective

Sprint 22 extends SiteSentinel from homepage-focused assessment into controlled
website health and active problem detection.

The scanner will detect concrete problems that are already present on the
monitored website while preserving the authoritative lifecycle:

Website
↓
MonitoringRun
↓
Evidence Collection
↓
CollectedEvidence
↓
NormalizedEvidence
↓
Finding / Detected Website Problem
↓
Risk
↓
TrustAssessment
↓
Recommendation
↓
Report and Notification

`Finding` remains the canonical domain representation of a detected website
problem.

Sprint 22 does not introduce a separate `WebsiteIssue` entity, repository,
service, table or processing pipeline.

## Approved Detection Scope

The approved initial detection scope includes:

- controlled same-origin crawling;
- broken internal links;
- broken images and selected assets;
- redirect failures and unsafe redirect outcomes;
- mixed-content references;
- robots.txt health and crawl limitations;
- declared sitemap availability and structural health;
- missing and multiple H1 elements;
- images without an alt attribute;
- basic HTTP response and resource performance signals;
- TLS handshake and certificate health signals.

The implementation must remain deterministic and evidence-first.

Artificial Intelligence may explain persisted findings and risks but must not
perform authoritative website problem detection.

## Approved Safety Boundary

The website health scanner must remain:

- same-origin after the accepted homepage origin is established;
- SSRF protected;
- redirect aware;
- request bounded;
- page bounded;
- asset bounded;
- depth bounded;
- duration bounded;
- sequential during the initial baseline;
- limited to safe retrieval methods;
- unable to submit forms;
- unable to issue state-changing HTTP requests;
- unable to expand into unrestricted external crawling.

Reaching a configured scan limit produces a partial scan outcome or scan
limitation evidence.

A scan limitation must not be presented as a confirmed website problem.

## Outcome Classification

Website health observations will be interpreted as:

1. Confirmed problem
2. Suspected problem
3. Scan limitation

A confirmed problem is directly supported by deterministic evidence.

A suspected problem is supported by an incomplete but meaningful technical
signal and must use an appropriately limited confidence score.

A scan limitation records that SiteSentinel could not complete a check because
of access control, rate limiting, robots policy, safety policy or scan budget.

HTTP 401, 403 and 429 outcomes must not automatically be classified as broken
resources.

The absence of robots.txt or an undeclared `/sitemap.xml` resource must not
automatically be classified as a website problem.

An image with `alt=""` must not automatically be classified as a problem
because it may represent a decorative image.

## Sprint 22 Non-Goals

Sprint 22 does not include:

- a `WebsiteIssue` domain model;
- JavaScript-rendered crawling;
- headless browser execution;
- Lighthouse;
- Core Web Vitals;
- authenticated crawling;
- form submission;
- POST, PUT, PATCH or DELETE scanning operations;
- external website crawling;
- vulnerability exploitation;
- directory brute forcing;
- port scanning;
- CAPTCHA bypass;
- unlimited sitemap traversal;
- full WCAG auditing;
- full SEO auditing;
- distributed crawling;
- high-concurrency crawling.


## Approved Controlled Implementation Sequence

Sprint 22 will be implemented through controlled and independently verified
blocks.

Each block must preserve the regression baseline established by the previous
completed block.

### Block 22A — Architecture Foundation

Status: COMPLETED

Scope:

- define the canonical problem representation;
- preserve `Finding` as the domain model;
- reject a duplicate `WebsiteIssue` model;
- define confirmed, suspected and limitation outcomes;
- define the same-origin crawl boundary;
- define SSRF and redirect safety;
- define bounded scan behavior;
- define the initial detection scope.

Architecture artifact:

- `ADR-0005-website-health-and-active-problem-detection.md`.

Completion commit:

- `36db857`.

### Block 22B — Scanner Configuration and Budget Foundation

Scope:

- introduce health-scan enablement;
- introduce page, depth, asset, request and duration limits;
- define validated configuration defaults;
- introduce an execution budget model;
- verify limit accounting and exhaustion behavior;
- ensure budget exhaustion does not become a confirmed website problem.

No network crawling will be introduced in this block.

### Block 22C — URL Normalization and Same-Origin Policy

Scope:

- normalize discovered HTTP and HTTPS URLs;
- resolve relative references;
- normalize default ports;
- remove fragments;
- reject embedded credentials;
- reject unsupported schemes;
- compare normalized origins;
- deduplicate normalized targets;
- preserve the existing configured-target validation behavior.

No crawl queue or resource fetching will be introduced in this block.

### Block 22D — Redirect-Aware Safe HTTP Retrieval

Scope:

- extract reusable safe request behavior from the existing collection boundary;
- preserve explicit redirect handling;
- validate every redirect destination;
- capture redirect-hop evidence;
- preserve adaptive response storage;
- preserve streaming response analysis;
- preserve response-body cleanup;
- prevent unsafe cross-origin crawl expansion.

No broad website crawl will be introduced in this block.

### Block 22E — Controlled Same-Origin HTML Crawl

Scope:

- establish the accepted final homepage origin;
- introduce a sequential crawl queue;
- track page depth;
- track visited URLs;
- enforce page, request and duration budgets;
- respect applicable robots rules;
- record complete and partial scan outcomes;
- collect page-level HTTP and HTML evidence.

Asset verification and problem findings remain outside this block.

### Block 22F — Internal Link and Asset Health Evidence

Scope:

- discover bounded internal link references;
- discover bounded image, stylesheet and script references;
- verify same-origin resource availability;
- distinguish broken, unavailable, restricted and rate-limited outcomes;
- record deterministic resource evidence;
- avoid classifying HTTP 401, 403 or 429 as broken resources.

### Block 22G — Redirect and Mixed-Content Detection

Scope:

- classify redirect loops;
- classify redirect-limit exhaustion;
- classify invalid redirect locations;
- classify broken redirect destinations;
- detect HTTPS-to-HTTP downgrade;
- distinguish active and passive mixed content;
- create evidence-backed findings through the analysis boundary.

### Block 22H — Robots and Sitemap Health

Scope:

- preserve robots.txt as a crawl-policy input;
- distinguish missing robots.txt from a robots failure;
- discover declared sitemap locations;
- inspect bounded sitemap and sitemap-index resources;
- detect malformed declared sitemaps;
- detect unavailable declared sitemaps;
- avoid treating an undeclared missing `/sitemap.xml` as a problem.

### Block 22I — HTML Structure and Image Alternative Text

Scope:

- collect H1 counts;
- detect missing H1;
- detect multiple H1 elements;
- detect images without an alt attribute;
- preserve `alt=""` as a potentially valid decorative-image value;
- detect empty or malformed image source references;
- avoid claiming full accessibility compliance.

### Block 22J — Basic HTTP Performance Signals

Scope:

- collect response-header duration;
- collect total response duration;
- collect response size;
- collect redirect count;
- collect page resource count;
- observe eligible response compression;
- apply configurable thresholds;
- avoid representing HTTP measurements as Core Web Vitals.

### Block 22K — TLS and Certificate Health

Scope:

- capture available TLS session evidence;
- capture certificate validity dates;
- detect expired certificates;
- detect certificates not yet valid;
- detect approaching expiry;
- classify supported handshake and identity failures;
- avoid unsupported certificate conclusions from generic transport errors.

### Block 22L — Finding, Risk and Trust Integration

Scope:

- introduce stable website-health evidence types;
- introduce stable website-problem finding types;
- map supported findings to risks;
- preserve finding confidence separately from risk severity;
- prevent duplicate logical findings and risks;
- ensure scan limitations do not reduce trust as confirmed problems.

### Block 22M — UI, Recommendation and Bilingual Report Integration

Scope:

- present findings as Detected Website Problems;
- show affected page or resource;
- show supporting evidence;
- show confidence;
- show associated risk;
- include remediation and verification guidance;
- preserve English and Turkish report separation;
- preserve PDF artifact generation;
- preserve Telegram report dispatch.

### Block 22N — Runtime Verification and Sprint Closure

Scope:

- run targeted package regressions;
- run the complete regression suite;
- perform controlled runtime scanning;
- verify request and duration limits;
- verify same-origin enforcement;
- verify SSRF rejection;
- verify problem traceability;
- verify bilingual reports;
- verify that external providers are not called unintentionally;
- update architecture and engineering documentation;
- commit and synchronize the completed sprint.

## Controlled Block Exit Rule

A block is complete only when:

- its defined scope is implemented;
- its targeted tests pass;
- the full regression passes when the block changes production behavior;
- no unrelated files are modified;
- `git diff --check` is clean;
- its architecture boundaries remain preserved;
- the result is reviewed before the next block begins.

A later block must not be pulled into an earlier block merely because adjacent
code is being edited.


## Block 22B File-Level Implementation Contract

Block 22B is limited to scanner configuration and an in-memory health-scan
budget model.

It introduces no HTTP requests, crawl queue, URL discovery, evidence records,
findings, risks, migration or UI change.

### Existing Production File to Modify

#### `src/main/java/com/cigabyte/sitesentinel/scanner/ScannerProperties.java`

Purpose:

- own the externally configurable website-health scan limits;
- provide safe defaults;
- prevent invalid zero or negative limits;
- enforce conservative hard maximum values;
- preserve all existing scanner properties and behavior.

Planned properties:

- `healthScanEnabled`;
- `maxCrawlPages`;
- `maxCrawlDepth`;
- `maxAssetChecks`;
- `maxHealthScanRequests`;
- `maxHealthScanDurationSeconds`;
- `maxLinksPerPage`;
- `maxAssetsPerPage`.

Approved defaults:

- health scan enabled: `true`;
- maximum crawl pages: `25`;
- maximum crawl depth: `2`;
- maximum asset checks: `100`;
- maximum health-scan requests: `150`;
- maximum health-scan duration: `60` seconds;
- maximum discovered links per page: `200`;
- maximum discovered assets per page: `200`.

Approved hard maximum values:

- maximum crawl pages: `100`;
- maximum crawl depth: `5`;
- maximum asset checks: `500`;
- maximum health-scan requests: `1000`;
- maximum health-scan duration: `300` seconds;
- maximum discovered links per page: `1000`;
- maximum discovered assets per page: `1000`.

The hard maximum values are safety boundaries.

External configuration may lower the limits but must not silently create
effectively unrestricted crawling.

Existing properties, including timeout, redirect, private-target, adaptive-body
and temporary-directory settings, must remain unchanged.

#### `src/main/resources/application.properties`

Purpose:

- expose the approved website-health scanner defaults;
- keep runtime configuration explicit;
- preserve all existing property values.

Planned entries:

- `sitesentinel.scanner.health-scan-enabled=true`;
- `sitesentinel.scanner.max-crawl-pages=25`;
- `sitesentinel.scanner.max-crawl-depth=2`;
- `sitesentinel.scanner.max-asset-checks=100`;
- `sitesentinel.scanner.max-health-scan-requests=150`;
- `sitesentinel.scanner.max-health-scan-duration-seconds=60`;
- `sitesentinel.scanner.max-links-per-page=200`;
- `sitesentinel.scanner.max-assets-per-page=200`.

These properties do not activate network crawling until a later block wires the
health-scan orchestrator.

### New Production Files

#### `src/main/java/com/cigabyte/sitesentinel/engine/collection/health/WebsiteHealthScanLimitReason.java`

Purpose:

- define stable reasons why a health scan cannot expand further.

Initial values:

- `PAGE_LIMIT_REACHED`;
- `DEPTH_LIMIT_REACHED`;
- `ASSET_LIMIT_REACHED`;
- `REQUEST_LIMIT_REACHED`;
- `DURATION_LIMIT_REACHED`.

The enum represents scanner execution limits.

It does not represent website problems or finding types.

#### `src/main/java/com/cigabyte/sitesentinel/engine/collection/health/WebsiteHealthScanBudget.java`

Purpose:

- enforce per-run page, depth, asset, request and duration limits;
- track acquired capacity in memory;
- expose deterministic exhaustion reasons;
- support partial scan outcomes in later blocks.

The budget must:

- be created separately for each health scan;
- be independent of global application state;
- accept validated limits;
- use an injected `Clock` for deterministic duration testing;
- record its start time;
- reject expansion after the duration limit;
- prevent page acquisition above the page limit;
- prevent page admission above the depth limit;
- prevent asset acquisition above the asset limit;
- prevent request acquisition above the request limit;
- expose the encountered limit reasons;
- avoid negative counters;
- avoid exceeding a limit by one operation;
- remain sequential and non-concurrent in the Sprint 22 baseline.

The budget must not:

- perform HTTP requests;
- normalize URLs;
- create evidence;
- create findings;
- create risks;
- persist state;
- depend on repositories;
- depend on Spring application context;
- fail a complete monitoring run merely because a limit is reached.

Approved acquisition semantics:

- an operation succeeds only when capacity remains;
- a rejected operation does not increment its counter;
- reaching a limit records the corresponding limit reason;
- duration exhaustion prevents additional acquisitions;
- depth rejection does not consume page capacity;
- page, asset and request capacities remain separately accounted.

### New Test Files

#### `src/test/java/com/cigabyte/sitesentinel/scanner/ScannerPropertiesTests.java`

Required coverage:

- approved default values;
- health-scan enablement setter;
- zero and negative values are clamped to safe minimums;
- values above hard maximums are clamped to safety ceilings;
- existing scanner property behavior remains preserved.

#### `src/test/java/com/cigabyte/sitesentinel/engine/collection/health/WebsiteHealthScanBudgetTests.java`

Required coverage:

- valid page acquisition;
- page limit exhaustion;
- depth limit rejection;
- depth rejection does not consume page capacity;
- valid asset acquisition;
- asset limit exhaustion;
- valid request acquisition;
- request limit exhaustion;
- rejected acquisition does not exceed counters;
- duration limit exhaustion using a deterministic clock;
- duration exhaustion prevents later acquisitions;
- encountered limit reasons are exposed;
- budget instances do not share counters;
- invalid constructor limits are rejected.

### Files Explicitly Unchanged in Block 22B

Block 22B must not modify:

- `HttpEvidenceCollectionEngine.java`;
- `MonitoringExecutionService.java`;
- `RuleBasedEvidenceAnalysisEngine.java`;
- `RuleBasedRiskEvaluationEngine.java`;
- `WebsiteTargetValidator.java`;
- evidence entities or repositories;
- finding entities or repositories;
- risk entities or repositories;
- database migrations;
- templates;
- CSS;
- reporting;
- recommendations;
- notifications.

## Block 22B Verification Gate

The initial expected production change set is exactly four files:

- two existing files modified;
- two new files created.

The initial expected test change set is exactly two new files.

Verification order:

1. Run `ScannerPropertiesTests`.
2. Run `WebsiteHealthScanBudgetTests`.
3. Run both Block 22B test classes together.
4. Run the complete regression suite.
5. Run `git diff --check`.
6. Confirm that no network call was introduced.
7. Confirm that no migration was introduced.
8. Review the file list before commit.