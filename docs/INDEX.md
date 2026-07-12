# SiteSentinel Documentation Index

This index lists the active documentation set for the SiteSentinel project.

The current active sprint is Sprint 10 — Notification Delivery Operations Baseline.

## Current Implementation Status

Sprint 10 is complete.

No new sprint is currently open.

The latest completed implementation scope is:

Sprint 10 — Notification Delivery Operations Baseline

The current implementation provides a working core assessment lifecycle with an explainable 
traceability review layer, a historical comparison baseline, and a controlled scheduled monitoring baseline:

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

## Implemented Baseline Through Sprint 9

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
- Notification event persistence
- Notification event domain model
- Notification event repository
- Notification event service
- Notification event create-if-absent behavior
- Notification event deduplication key support
- Notification read/unread status model
- Notification event generation rules
- Monitoring run failed notification event generation
- High-risk trust assessment notification event generation
- Trust status changed notification event generation
- Trust score declined notification event generation
- New risk type detected notification event generation
- Lifecycle-safe notification generation after completed monitoring runs
- Lifecycle-safe notification generation after failed monitoring runs
- Dashboard notification event visibility
- Website detail notification event visibility
- Monitoring run detail notification event visibility
- Monitoring run report notification event visibility
- Notification event management list page
- Notification event detail page
- Status-based notification filtering
- Severity-based notification filtering
- Combined status and severity notification filtering
- Website-context notification filtering
- Monitoring-run-context notification filtering
- Mark notification as read action
- Mark notification as unread action
- Dashboard navigation to notification management
- Website detail navigation to website-filtered notification management
- Monitoring run detail navigation to run-filtered notification management
- Monitoring run report navigation to run-filtered notification management
- Notification event detail links from dashboard, website detail, monitoring run detail, 
and monitoring run report
- Notification delivery channel model
- Notification delivery attempt status model
- Notification delivery attempt persistence
- Notification delivery attempt repository
- Notification delivery attempt service
- Manual simulated successful delivery attempt action
- Manual simulated failed delivery attempt action
- Manual skipped delivery attempt action
- Delivery attempt count visibility on notification detail pages
- Delivery attempt history visibility on notification detail pages
- TELEGRAM modeled as a notification delivery channel
- Flyway migration support for notification delivery attempts
- Flyway migration support for TELEGRAM delivery channel constraint
- Telegram delivery configuration properties
- Disabled-by-default Telegram delivery safety switch
- Telegram bot token configuration
- Telegram chat id configuration
- Telegram API base URL configuration
- Telegram delivery provider interface
- Telegram delivery provider result model
- Telegram notification delivery provider implementation
- Real delivery attempt status support
- Controlled real Telegram test delivery service method
- Telegram Bot API sendMessage integration
- Manual Telegram test delivery endpoint
- Manual Telegram test delivery UI action
- Delivery attempt history support for SENT, FAILED, CONFIGURATION_MISSING, and DISABLED results

## Previous Completed Sprint

### Sprint 8 — Notification Delivery Readiness Baseline

Sprint 8 is complete.

Sprint 8 introduced the internal notification delivery readiness model without performing real external delivery.

Sprint 8 added:

- Notification delivery channel enum.
- Notification delivery attempt status enum.
- Notification delivery attempt entity.
- Notification delivery attempt repository.
- Notification delivery attempt service.
- Simulated successful delivery attempt recording.
- Simulated failed delivery attempt recording.
- Skipped delivery attempt recording.
- Delivery attempt history visibility on notification detail pages.
- TELEGRAM as a modeled notification delivery channel.

Sprint 8 did not add:

- Real external notification delivery.
- Automatic notification dispatch.
- Recipient management.
- User notification preferences.
- Notification subscription rules.
- Delivery retry scheduling.
- Escalation policies.
- AI-generated notification messages.

Sprint 8 established the delivery-attempt audit baseline used by Sprint 9.

## Previous Completed Sprint

### Sprint 7 — Notification Management Baseline

Sprint 7 is complete.

Sprint 7 introduced in-application notification management for persisted notification events.

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

Sprint 7 did not add:

- Email notification delivery.
- WhatsApp notification delivery.
- Slack notification delivery.
- Webhook delivery.
- Notification recipient preferences.
- User-specific notification routing.
- Delivery retry tracking.
- AI-generated notification summaries.
- Authentication or user access control.

## Previous Completed Sprint

### Sprint 6 — Notification Event Baseline

Sprint 6 is complete.

Sprint 6 introduced persisted in-application notification events for important monitoring outcomes.

Sprint 6 added:

- Notification event persistence.
- Notification event domain model.
- Notification event repository.
- Notification event service.
- Notification event create-if-absent behavior.
- Notification event deduplication key support.
- Notification read/unread status model.
- Notification event generation rules.
- Monitoring run failed notification event generation.
- High-risk trust assessment notification event generation.
- Trust status changed notification event generation.
- Trust score declined notification event generation.
- New risk type detected notification event generation.
- Lifecycle-safe notification generation after completed monitoring runs.
- Lifecycle-safe notification generation after failed monitoring runs.
- Dashboard notification event visibility.
- Website detail notification event visibility.
- Monitoring run detail notification event visibility.
- Monitoring run report notification event visibility.

Sprint 6 did not add:

- Email notification delivery.
- WhatsApp notification delivery.
- Slack notification delivery.
- Webhook delivery.
- Notification management page.
- Notification detail page.
- Mark as read/unread UI controls.
- User-specific notification preferences.
- Delivery retry tracking.
- AI-generated notification summaries.
- Authentication or user access control.

The recommended next sprint is Sprint 7 — Notification Management Baseline.

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

Sprint 4 introduced controlled scheduled monitoring while preserving the existing lifecycle,
traceability, and comparison boundaries.

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

Sprint 3 added a read-only comparison layer that compares a completed monitoring run against 
the previous completed monitoring run for the same website.

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
- `docs/architecture/decisions/ADR-0003-trust-engine-centered-architecture.md` — Trust-engine-centered 
architecture decision.
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

Implemented platform capabilities:

- Website assessment lifecycle.
- Evidence traceability.
- Historical assessment comparison.
- Scheduled monitoring.
- Monitoring reports.
- Notification event management.
- Notification delivery attempts.
- Controlled Telegram delivery provider.
- Notification delivery operations.

## Current Repository State

Latest completed sprint:

Sprint 10 — Notification Delivery Operations Baseline

Repository baseline:

Stable

## Next Approved Work

No additional sprint has been approved.


The implemented notification delivery path is:

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

Real Telegram delivery remains:

- Explicitly configuration-protected.
- Disabled by default.
- Manual only.
- Isolated from monitoring execution.
- Isolated from notification event generation.
- Auditable through notification delivery attempts.

Automatic notification dispatch has not been implemented.

## Next Phase

Sprint 10 is complete.

The next sprint scope has not yet been approved.

Recommended next-sprint planning should evaluate:

- Notification recipient modeling.
- Delivery destination ownership.
- Recipient activation and deactivation.
- Per-website notification routing.
- Dispatch rule boundaries.
- Duplicate delivery prevention.
- Manual and automatic delivery governance.
- Retry and failure classification requirements.

Automatic notification dispatch must not be implemented until recipient, routing, 
idempotency, retry, and security decisions are documented and approved.

---

## Latest Completed Sprint

### Sprint 10 — Notification Delivery Operations Baseline

Sprint 10 is complete.

Sprint 10 introduced:

- Notification delivery mode modeling.
- Provider readiness status modeling.
- Provider operational status modeling.
- Telegram configuration readiness evaluation.
- Separate provider health check persistence.
- Controlled Telegram Bot API health check.
- Notification delivery settings page.
- Manual provider health check action.
- Latest provider check visibility.
- Recent provider check history.
- Secret-safe configuration state visibility.
- Notification delivery operations navigation.

The completed provider readiness path is:

TelegramDeliveryProperties  
↓  
TelegramDeliveryReadinessService  
↓  
NotificationDeliveryProviderStatus  
↓  
NotificationDeliverySettingsController  
↓  
Notification Delivery Settings UI

The completed provider health check path is:

Notification Delivery Settings UI  
↓  
TelegramProviderHealthCheckService  
↓  
TelegramNotificationDeliveryProvider.verifyConnection()  
↓  
Telegram Bot API getMe  
↓  
NotificationDeliveryProviderCheck

Sprint 10 preserved:

- Manual-only real Telegram delivery.
- Disabled-by-default Telegram configuration.
- Notification event isolation.
- Notification delivery attempt isolation.
- Monitoring lifecycle isolation.
- Secret-safe UI visibility.

Sprint 10 did not add:

- Automatic notification dispatch.
- Recipient management.
- Retry scheduling.
- Escalation rules.
- Additional real delivery providers.
- UI-based secret management.