# SiteSentinel Documentation Index

This index lists the active documentation set for the SiteSentinel project.

No sprint is currently open.

## Current Implementation Status

Sprint 12 is complete.

The latest completed implementation scope is:

Sprint 12 — AI Remediation Recommendation Baseline

No new sprint has been approved.

The authoritative website assessment lifecycle remains:

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

The scheduled monitoring baseline follows:

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

Sprint 12 added the following post-assessment advisory path:

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
Versioned AI Provider Request Created  
↓  
Structured Output Validated  
↓  
AI Recommendation or Rule-Based Fallback Produced  
↓  
Validated Advisory Recommendation Persisted  
↓  
Risk Detail and Monitoring Run Report Read Models Updated

The recommendation layer is downstream from the authoritative assessment lifecycle.

It does not:

- Create risks.
- Create findings.
- Create evidence.
- Change risk severity.
- Change risk score.
- Change confidence score.
- Change trust score.

The V1 target chain remains:

Monitoring Run Completes  
↓  
Risks Are Identified  
↓  
AI Remediation Recommendations Are Generated  
↓  
Recommendations Are Validated  
↓  
Full Monitoring Run PDF Is Generated  
↓  
PDF Is Automatically Dispatched Through Telegram  
↓  
Dispatch Is Persisted and Auditable

Sprint 12 completed only the AI remediation recommendation foundation.

PDF generation, Telegram document dispatch, automatic dispatch, and dispatch persistence remain deferred.

## Implemented Baseline Through Sprint 12

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
- Notification delivery provider operational status model
- Notification delivery mode model
- Provider readiness status model
- Telegram configuration readiness evaluation
- Separate provider health-check persistence
- Notification delivery settings page
- Manual Telegram provider health-check action
- Latest provider-check visibility
- Recent provider-check history
- Secret-safe provider configuration visibility
- Telegram Bot API client interface
- JDK Telegram Bot API client implementation
- Separation of Telegram provider behavior from HTTP communication
- Typed Telegram connectivity status model
- Typed Telegram connectivity result model
- Authentication failure classification
- Timeout classification
- DNS and network failure classification
- Invalid provider response classification
- Interrupted request classification
- Generic provider failure classification
- Typed provider health-check status persistence
- Optional provider-check HTTP status persistence
- Provider-check HTTP status visibility
- Structured Telegram JSON response parsing
- Secret-safe connectivity diagnostics
- Secret-safe delivery failure diagnostics
- Raw Telegram response body persistence prevention
- Telegram notification message length hardening
- Unicode-safe Telegram message truncation
- Provider-check diagnostic length hardening
- Provider-check HTTP status validation
- Telegram provider unit-test baseline
- Telegram HTTP request construction tests
- Telegram timeout and interruption tests
- Provider health-check service tests
- Provider-check persistence tests
- Provider-check repository ordering integration tests
- Disabled-by-default readiness tests
- Manual provider diagnostic QA for disabled, missing configuration, healthy, 
authentication failure, timeout, unreachable, and invalid-response states
- Risk remediation recommendation domain vocabulary
- Persisted risk remediation recommendation entity
- Recommendation content contract
- Recommendation source classification
- Recommendation fallback reason classification
- Validated-only recommendation persistence
- Advisory-only recommendation persistence
- Risk-to-monitoring-run recommendation ownership validation
- Recommendation repository
- Monitoring run recommendation history
- Risk recommendation history
- Latest recommendation lookup
- Recommendation count by monitoring run
- Evidence-safe recommendation context
- Persisted risk context loading
- Persisted finding context loading
- Persisted normalized evidence context loading
- Raw collected evidence exclusion from recommendation context
- Evidence source URL exclusion from recommendation context
- Secret-safe context sanitization
- Private key redaction
- Authorization credential redaction
- Telegram bot token redaction
- JWT redaction
- Embedded URL credential redaction
- Sensitive assignment redaction
- UTF-16-safe recommendation context truncation
- Deterministic recommendation context ordering
- SHA-256 recommendation context fingerprinting
- Provider-neutral AI recommendation abstraction
- Typed AI provider status classification
- Structured AI output contract
- Prompt versioning
- Output schema versioning
- Rule-based fallback versioning
- Evidence-safe prompt construction
- Database identifier exclusion from AI prompt payload
- Raw provider response persistence prevention
- Full prompt persistence prevention
- Structured recommendation validation
- Typed recommendation validation issues
- Schema-version validation
- Advisory-output validation
- Recommendation content length validation
- Remediation step validation
- Verification step validation
- Sensitive AI output rejection
- Persistence-ready recommendation content conversion
- Deterministic rule-based remediation fallback
- Severity-aware fallback guidance
- Free-text echo prevention in fallback recommendations
- Single-risk recommendation generation orchestration
- Safe AI provider selection
- Provider metadata safety validation
- AI success recommendation persistence
- Provider-unavailable fallback persistence
- Provider-failure fallback persistence
- Validation-failure fallback persistence
- Recommendation audit metadata persistence
- Completed-run recommendation generation
- Failed-run recommendation generation exclusion
- Per-risk recommendation failure isolation
- Monitoring lifecycle-safe recommendation integration
- Recommendation-before-notification execution ordering
- Latest recommendation visibility on risk detail
- Recommendation audit metadata visibility on risk detail
- Recommendation history visibility on risk detail
- Escaped recommendation content rendering
- Recommendation count in monitoring run reports
- Latest recommendation mapping per persisted risk
- Risk-to-recommendation report traceability
- Advisory remediation recommendation report section
- Monitoring run report read-model readiness for future PDF rendering
- Recommendation context sanitizer unit tests
- Structured recommendation validator unit tests
- Rule-based fallback unit tests
- Prompt and context fingerprint unit tests
- AI recommendation orchestration tests
- Provider failure isolation tests
- Per-risk recommendation generation isolation tests
- Monitoring lifecycle recommendation safety tests
- Recommendation repository ordering integration tests
- Recommendation history and latest-query integration tests
- Recommendation audit persistence integration tests
- Risk-to-monitoring-run persistence boundary integration tests

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
- Browser-based monitoring reports.
- Notification event generation and management.
- Notification delivery attempt audit baseline.
- Controlled Telegram text-message delivery provider.
- Notification delivery operations.
- Typed Telegram provider diagnostics.
- Provider health-check persistence.
- Secret-safe provider operational visibility.
- Automated Telegram provider safety verification.
- Persisted advisory remediation recommendations.
- Evidence-safe recommendation context.
- Provider-neutral AI recommendation abstraction.
- Structured AI recommendation validation.
- Deterministic rule-based fallback.
- Recommendation audit metadata.
- Monitoring lifecycle-safe recommendation generation.
- Risk detail recommendation visibility.
- Monitoring run recommendation report visibility.
- Future PDF report read-model readiness.
- Automated recommendation safety and persistence verification.

The recommendation baseline remains:

- Advisory only.
- Downstream from risk and trust assessment.
- Based on persisted risks, findings, and normalized evidence.
- Isolated from raw collected evidence.
- Isolated from evidence source URLs.
- Isolated from provider credentials.
- Protected by secret-safe context sanitization.
- Protected by structured output validation.
- Protected by deterministic rule-based fallback.
- Operational without a concrete production AI provider.
- Unable to create or modify risks, findings, evidence, or trust output.
- Isolated from monitoring run failure classification.
- Auditable through persisted generation metadata.
- Read-only when displayed through risk detail and monitoring run reports.

Current production recommendation behavior:

- No concrete AI provider adapter is configured.
- Completed monitoring runs generate rule-based fallback recommendations for persisted risks.
- Fallback reason is `PROVIDER_UNAVAILABLE` when no AI provider exists.
- Current prompt version is `risk-remediation-v1`.
- Current output schema version is `risk-remediation-output-v1`.
- Current fallback rule version is `risk-remediation-fallback-v1`.

Telegram delivery remains:

- Disabled by default.
- Configuration-protected.
- Manual only.
- Limited to the existing Telegram text-message delivery path.
- Isolated from monitoring execution.
- Isolated from notification event generation.
- Auditable through notification delivery attempts.
- Operationally observable through separate provider-check records.
- Independent from provider health-check records.
- Protected by typed and secret-safe provider diagnostics.

The current implementation does not include:

- Concrete production AI provider communication.
- PDF report generation.
- PDF artifact persistence.
- Telegram document upload.
- Automatic Telegram PDF dispatch.
- Report dispatch persistence.
- Report dispatch idempotency.
- Report delivery retry and recovery.

## Current Repository State

Latest completed sprint:

Sprint 12 — AI Remediation Recommendation Baseline

Repository baseline:

Stable after Sprint 12 implementation, automated verification, final regression, and documentation closure.

Final verified test baseline:

- Tests run: 110
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven test: BUILD SUCCESS
- Maven compile: BUILD SUCCESS

Latest Flyway migration:

`V16__create_risk_remediation_recommendations_table.sql`

## Next Approved Work

No additional sprint has been approved.

Future sprint planning may evaluate:

- Concrete production AI provider adapter.
- AI provider HTTP client boundary.
- AI provider environment configuration.
- AI credential secret management.
- Provider timeout and failure classification.
- Recommendation generation idempotency.
- Recommendation supersession policy.
- PDF generation from the existing monitoring run report read model.
- PDF artifact persistence and versioning.
- PDF artifact integrity fingerprinting.
- Telegram document-upload client boundary.
- Automatic Telegram PDF report dispatch.
- Report dispatch persistence and audit.
- Report dispatch idempotency.
- Duplicate report dispatch prevention.
- Report delivery retry and recovery.
- Report destination and recipient ownership.

The approved future report-delivery direction is:

Persisted Monitoring Run Report View  
↓  
PDF Renderer  
↓  
Versioned PDF Artifact  
↓  
Telegram Document Delivery  
↓  
Persisted Dispatch Audit

A future PDF renderer must consume existing persisted report data.

It must not:

- Re-run monitoring.
- Recalculate risks.
- Recalculate trust.
- Regenerate recommendations during report rendering.
- Call an AI provider while rendering an already completed report.

Automatic Telegram report dispatch must not be implemented until PDF artifact ownership,
dispatch persistence, idempotency, duplicate prevention, retry, destination ownership, and
security boundaries are explicitly documented and approved.

## Next Phase

Sprint 12 is complete.

The next sprint scope has not yet been approved.

The AI remediation recommendation foundation is complete.

The next major V1 chain remains:

Persisted Recommendations  
↓  
Full Monitoring Run PDF  
↓  
Automatic Telegram Document Dispatch  
↓  
Persisted and Auditable Dispatch

Concrete AI provider integration, PDF generation, Telegram document delivery, and automatic
report dispatch remain outside the approved implementation boundary.

---

## Latest Completed Sprint

### Sprint 12 — AI Remediation Recommendation Baseline

Sprint 12 is complete.

Sprint 12 introduced a persisted, evidence-grounded, validated, advisory remediation recommendation layer.

The completed recommendation path is:

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
Provider Result Classified  
↓  
Structured AI Output Validated  
↓  
AI Recommendation or Rule-Based Fallback Produced  
↓  
Validated Recommendation Persisted  
↓  
Risk Detail and Monitoring Run Report Read Models Updated

Sprint 12 introduced:

- Risk remediation recommendation domain model.
- Recommendation persistence.
- Recommendation repository and service.
- Risk-to-monitoring-run persistence validation.
- Evidence-safe recommendation context.
- Raw collected evidence exclusion.
- Source URL exclusion.
- Secret-safe text sanitization.
- Deterministic context ordering.
- SHA-256 context fingerprinting.
- Provider-neutral AI abstraction.
- Typed provider result classification.
- Structured AI output contract.
- Prompt and output schema versioning.
- Structured recommendation validation.
- Sensitive AI output rejection.
- Deterministic rule-based fallback.
- Severity-aware advisory guidance.
- Recommendation generation orchestration.
- Provider failure isolation.
- Recommendation audit metadata.
- Completed-run lifecycle integration.
- Per-risk generation failure isolation.
- Risk detail recommendation visibility.
- Recommendation history visibility.
- Monitoring run recommendation report visibility.
- Future PDF report read-model readiness.
- Automated recommendation safety tests.
- Recommendation persistence integration tests.

Current version identifiers:

- Prompt version: `risk-remediation-v1`
- Output schema version: `risk-remediation-output-v1`
- Fallback rule version: `risk-remediation-fallback-v1`

Sprint 12 added the following Flyway migration:

- `V16__create_risk_remediation_recommendations_table.sql`

Final verification:

- Tests run: 110
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven test: BUILD SUCCESS
- Maven compile: BUILD SUCCESS

Sprint 12 preserved:

- Persisted risk authority.
- Persisted finding authority.
- Persisted evidence authority.
- Persisted trust-assessment authority.
- Raw collected evidence isolation.
- Secret-safe provider boundaries.
- Monitoring lifecycle safety.
- Notification event isolation.
- Notification delivery isolation.
- Manual-only Telegram text-message delivery.
- Disabled-by-default Telegram delivery configuration.

Sprint 12 did not add:

- A concrete production AI provider adapter.
- AI provider HTTP communication.
- AI credential management.
- Recommendation approval workflow.
- Recommendation regeneration controls.
- Recommendation idempotency.
- PDF generation.
- PDF artifact persistence.
- Telegram document delivery.
- Automatic Telegram PDF dispatch.
- Report dispatch persistence.
- Dispatch retry or recovery.

## Previous Completed Sprint

### Sprint 11 — Notification Provider Diagnostics and Safety Verification Baseline

Sprint 11 is complete.

Sprint 11 replaced the boolean Telegram connectivity baseline with a typed, testable,
secret-safe provider diagnostic model.

Sprint 11 introduced:

- `TelegramBotApiClient` HTTP communication boundary.
- `JdkTelegramBotApiClient` implementation.
- Typed `TelegramConnectivityStatus`.
- Typed `TelegramConnectivityResult`.
- Authentication failure classification.
- Timeout classification.
- DNS and network failure classification.
- Invalid provider response classification.
- Interrupted request classification.
- Generic provider failure classification.
- Typed provider-check persistence.
- Nullable provider-check HTTP status.
- Provider-check HTTP status visibility.
- Structured Telegram JSON response parsing.
- Secret-safe provider diagnostics.
- Secret-safe delivery failure diagnostics.
- Telegram message length hardening.
- Unicode-safe message truncation.
- Provider-check diagnostic normalization.
- Provider-check repository ordering integration tests.
- Automated Telegram provider safety tests.

The completed provider connectivity path is:

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

Supported connectivity statuses:

- HEALTHY
- AUTHENTICATION_FAILED
- TIMEOUT
- UNREACHABLE
- INVALID_RESPONSE
- INTERRUPTED
- FAILED

Supported provider-check statuses:

- HEALTHY
- DISABLED
- CONFIGURATION_MISSING
- AUTHENTICATION_FAILED
- TIMEOUT
- UNREACHABLE
- INVALID_RESPONSE
- INTERRUPTED
- FAILED

Sprint 11 added the following Flyway migrations:

- `V14__expand_notification_delivery_provider_check_statuses.sql`
- `V15__add_http_status_code_to_notification_delivery_provider_checks.sql`

Final verification:

- Tests run: 74
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven test: BUILD SUCCESS
- Maven compile: BUILD SUCCESS

Manual QA verified:

- DISABLED
- CONFIGURATION_MISSING
- HEALTHY
- AUTHENTICATION_FAILED
- TIMEOUT
- UNREACHABLE
- INVALID_RESPONSE

Sprint 11 preserved:

- Disabled-by-default Telegram configuration.
- Manual-only Telegram delivery.
- Environment-based secret configuration.
- Notification event isolation.
- Notification delivery attempt isolation.
- Provider health-check isolation.
- Monitoring lifecycle isolation.
- Scheduled monitoring isolation.
- Secret-safe operational visibility.

Sprint 11 did not add:

- Automatic notification dispatch.
- Recipient management.
- Notification subscriptions.
- Retry scheduling.
- Delivery queue.
- Additional real delivery providers.
- UI-based secret editing.
- AI-generated notification messages.

## Previous Completed Sprint

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

Sprint 10 originally used a boolean Telegram connectivity verification boundary.

Sprint 11 replaced that boundary with the typed provider health-check path documented
in the latest completed sprint section above.

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