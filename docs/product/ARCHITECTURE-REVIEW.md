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