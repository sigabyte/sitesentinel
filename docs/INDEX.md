# SiteSentinel Documentation Index

This index lists the active documentation set for the SiteSentinel project.

Sprint 15 is complete.

## Current Implementation Status

Current implementation status: SPRINT 15
Implemented baseline: UPDATED THROUGH SPRINT 15
Latest completed sprint: SPRINT 15
Latest migration: V18
Final test baseline: 277

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

The V1 monitoring-to-report-to-Telegram chain is complete:

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

Sprint 12 completed the AI remediation recommendation foundation.

Sprint 13 completed manual full monitoring run PDF rendering, immutable
artifact persistence, SHA-256 integrity verification, and ownership-safe
PDF download.

Sprint 14 completed automatic existing-or-generate PDF artifact resolution,
pre-dispatch integrity revalidation, Telegram multipart document upload,
dedicated dispatch persistence, automatic dispatch idempotency, append-only
delivery audit, manual retry, and controlled real Telegram verification.

Sprint 15 completed the first concrete production AI recommendation provider
baseline.

The existing provider-neutral recommendation architecture now includes:

- default-disabled OpenAI configuration;
- environment-based API credentials and model selection;
- OpenAI Responses API communication;
- strict structured-output request generation;
- typed response parsing;
- transport failure classification;
- validated AI recommendation persistence;
- rule-based fallback when OpenAI is disabled or fails;
- controlled real OpenAI recommendation verification;
- AI recommendation inclusion in the full monitoring run PDF;
- automatic Telegram delivery of the AI-enriched PDF.

The completed production recommendation and delivery chain is:

Monitoring Run Completes  
↓  
Evidence-Based Risks Are Persisted  
↓  
Evidence-Safe Recommendation Context Is Built  
↓  
OpenAI Responses API Is Called  
↓  
Strict Structured Output Is Parsed  
↓  
Recommendation Output Is Validated  
↓  
AI Recommendation or Rule-Based Fallback Is Persisted  
↓  
Full Monitoring Run PDF Is Generated  
↓  
PDF Is Automatically Dispatched Through Telegram  
↓  
Dispatch Is Persisted and Auditable

OpenAI remains downstream from authoritative monitoring, evidence, finding,
risk and trust-assessment lifecycles.

OpenAI failure does not fail a completed monitoring run.

Automatic and manual Telegram PDF delivery remain downstream from the
authoritative monitoring, risk, trust, evidence and recommendation
lifecycles.

Telegram delivery failure does not change a completed monitoring run to
FAILED.

## Implemented Baseline Through Sprint 15

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
- Monitoring run report read-model support for PDF rendering
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
- Provider-neutral monitoring run PDF renderer contract
- Apache PDFBox full monitoring report renderer
- A4 PDF layout and automatic page breaks
- PDF page-number footer
- Safe PDF text wrapping and character normalization
- Findings, risks, trust and comparison PDF sections
- Advisory recommendation and audit metadata PDF sections
- Immutable monitoring run PDF artifact persistence
- Versioned PDF report contract
- Deterministic PDF filename generation
- SHA-256 PDF artifact fingerprinting
- PDF byte-size integrity validation
- Completed-run-only PDF artifact persistence
- Monitoring-run/report-version duplicate prevention
- Manual PDF artifact generation
- Ownership-safe PDF artifact download
- Secure PDF response headers
- PDF renderer and artifact integration tests
- Browser PDF generation and download verification
- Visual PDF quality assurance
- Telegram document-upload request model
- Defensive copying of Telegram document binary content
- Multipart metadata validation
- Binary-safe Telegram multipart body generation
- Telegram Bot API `sendDocument` integration
- Telegram document response classification
- Successful Telegram message ID extraction
- Dedicated Telegram document-delivery result contract
- Telegram document-delivery disabled-state handling
- Telegram document-delivery missing-configuration handling
- Configured Telegram destination enforcement
- Secret-safe Telegram document-delivery failure handling
- Separate monitoring run report dispatch domain model
- Automatic report dispatch attempt type
- Manual report retry attempt type
- PENDING, SENT and FAILED report-dispatch lifecycle
- Append-only report-dispatch audit history
- Telegram message ID persistence for successful report dispatch
- Monitoring-run-to-PDF-artifact ownership enforcement
- Automatic report-dispatch application-level idempotency
- Automatic report-dispatch database-level idempotency
- Dispatch attempt-number uniqueness
- Manual retry lineage persistence
- Cross-run and cross-artifact retry prevention
- Existing-or-generate V1 PDF artifact resolution
- Existing immutable PDF artifact reuse
- Pre-dispatch PDF binary-size revalidation
- Pre-dispatch SHA-256 revalidation
- Default-disabled automatic Telegram PDF dispatch
- Separate automatic PDF dispatch configuration gate
- Automatic dispatch after recommendation processing
- Recommendation subsystem failure dispatch suppression
- Telegram dispatch failure isolation from monitoring lifecycle
- Telegram network-call separation from dispatch write transactions
- Manual retry of the latest failed dispatch attempt
- Same immutable PDF artifact reuse during manual retry
- PENDING and SENT attempt retry rejection
- Retry-chain branching prevention
- Website-to-monitoring-run ownership validation for retry
- Safe manual retry controller feedback
- Monitoring run report dispatch-history visibility
- Latest dispatch status visibility
- Dispatch attempt-number visibility
- Successful Telegram message ID visibility
- Latest failed-attempt retry control
- Scheduler environment-variable control
- Controlled local Telegram stub end-to-end integration testing
- Controlled real Telegram PDF delivery verification
* Telegram delivery safe-default property binding
* Automatic Telegram PDF dispatch safe-default property binding
* OpenAI recommendation configuration boundary
* Default-disabled OpenAI provider behavior
* Environment-based OpenAI API-key configuration
* Environment-based OpenAI model configuration
* Configurable OpenAI API base URL
* Configurable OpenAI connection timeout
* Configurable OpenAI request timeout
* Configurable OpenAI maximum output-token limit
* OpenAI configuration readiness evaluation
* OpenAI configuration sanitization and minimum bounds
* Provider-neutral OpenAI API client interface
* Typed OpenAI API status classification
* Typed OpenAI API result invariants
* OpenAI Responses API request-body generation
* Strict JSON Schema recommendation output
* Required recommendation output fields
* Recommendation output length and list-size bounds
* OpenAI response-storage disablement
* OpenAI API-key request-body serialization prevention
* Recommendation context-fingerprint serialization prevention
* Internal prompt-version serialization prevention
* Completed OpenAI response parsing
* OpenAI refusal classification
* Incomplete response rejection
* Malformed provider response rejection
* Missing provider output rejection
* Ambiguous multiple-output rejection
* JDK HTTP OpenAI Responses API transport
* Bearer authorization header construction
* UTF-8 JSON request and response transport
* Redirect-following prevention
* OpenAI authentication-failure classification
* OpenAI rate-limit classification
* OpenAI provider-unavailable classification
* OpenAI timeout classification
* OpenAI interruption classification
* Thread interrupt-flag restoration
* OpenAI network-failure containment
* Concrete OpenAI recommendation provider adapter
* Disabled-provider HTTP-call prevention
* Missing-configuration HTTP-call prevention
* OpenAI success mapping into the provider-neutral contract
* OpenAI failure mapping into the provider-neutral contract
* Unexpected OpenAI client-exception containment
* Existing recommendation validator preservation
* Existing rule-based fallback preservation
* Provider-disabled fallback verification
* Provider-failure fallback verification
* Controlled real OpenAI recommendation verification
* OpenAI provider and model audit persistence
* Validated AI recommendation persistence
* Real AI recommendation PDF-content verification
* Real AI PDF automatic Telegram-delivery verification
* OpenAI secret-exposure regression verification

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
- Full monitoring run PDF rendering.
- Immutable and versioned PDF artifact persistence.
- Deterministic PDF filename policy.
- SHA-256 and binary-size artifact integrity.
- Completed-run-only PDF artifact generation.
- Duplicate run/version prevention.
- Ownership-safe manual PDF generation and download.
- PDF visual and integration verification.
- Automated recommendation safety and persistence verification.
- Telegram multipart PDF document transport.
- Telegram `sendDocument` response handling.
- Successful Telegram message ID extraction.
- Dedicated report-dispatch persistence and audit.
- Automatic PDF artifact resolution after completed monitoring runs.
- Pre-dispatch PDF integrity revalidation.
- Default-disabled automatic Telegram PDF dispatch.
- Recommendation-before-PDF dispatch ordering.
- Application- and database-level automatic dispatch idempotency.
- Monitoring lifecycle isolation from Telegram delivery failure.
- Append-only manual retry lineage.
- Same-artifact manual Telegram retry.
- Dispatch history and retry controls in monitoring run reports.
- Controlled end-to-end and real Telegram PDF delivery verification.

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

Telegram delivery currently provides:

- Disabled-by-default Telegram provider enablement.
- Configuration-protected bot token and chat ID.
- Existing manual Telegram text-message delivery.
- Binary-safe Telegram PDF document upload.
- A separately controlled automatic PDF dispatch flag.
- Default-disabled automatic PDF dispatch.
- Automatic PDF dispatch after completed-run recommendation processing.
- Delivery to the configured Telegram destination only.
- Dedicated report-dispatch persistence separate from notification-event
  delivery attempts.
- Application- and database-level automatic dispatch idempotency.
- Persisted SENT and FAILED outcomes.
- Successful Telegram message ID persistence.
- Append-only manual retry history.
- Manual retry using the same immutable PDF artifact.
- PDF integrity revalidation before automatic and manual delivery.
- Monitoring lifecycle isolation from provider failure.
- Provider readiness and health-check visibility.
- Typed and secret-safe provider diagnostics.

The current implementation does not include:

- Concrete production AI provider communication.
- Asynchronous report dispatch.
- Durable report-dispatch queue.
- Automatic report-delivery retry scheduling.
- Exponential retry backoff.
- Telegram `retry_after` handling.
- Dead-letter processing.
- Automated reconciliation of indefinitely PENDING dispatch attempts.
- Multi-recipient Telegram delivery.
- Recipient ownership and destination preferences.
- Additional document-delivery providers.
- Dispatch operational metrics and alerting.
- PDF artifact retention and cleanup automation.
- Authentication and role-based authorization.
- AI-generated unresolved-risk impact analysis.
- Recommendation approval and supersession workflows.

## Current Repository State

Latest completed sprint:

Sprint 14 — Automatic Telegram PDF Dispatch and Audit

Repository baseline:

Stable after Sprint 14 implementation, automated verification, controlled
local Telegram integration, real Telegram PDF delivery verification, report
dispatch persistence verification, UI verification and documentation
closure.

Final verified test baseline:

- Tests run: 228
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven test: BUILD SUCCESS
- Maven compile: BUILD SUCCESS

Latest Flyway migration:

`V18__create_monitoring_run_report_dispatch_attempts_table.sql`

Controlled external verification:

- Telegram provider readiness: READY
- Telegram health check: HEALTHY
- Controlled monitoring run: COMPLETED
- Recommendation-before-dispatch ordering: VERIFIED
- Real PDF generation: VERIFIED
- Real Telegram document delivery: VERIFIED
- Telegram PDF received and opened: VERIFIED
- Automatic dispatch attempt status: SENT
- Telegram message ID persistence: VERIFIED
- Dispatch history UI: VERIFIED
- Secret values in source or logs: NOT DETECTED

## Next Approved Work

No additional sprint has been approved.

Future sprint planning may evaluate:

* AI provider production hardening;
* external secret-manager integration;
* explicit AI provider priority;
* second-provider evaluation;
* provider failover and multi-provider routing;
* retryable and non-retryable provider failure policy;
* OpenAI rate-limit and `Retry-After` handling;
* automatic retry and exponential backoff;
* provider circuit breaker;
* provider health monitoring;
* token-usage, latency and cost metrics;
* repeatable recommendation-quality evaluation;
* model upgrade and rollback procedures;
* recommendation generation idempotency;
* duplicate recommendation prevention;
* recommendation regeneration and supersession;
* recommendation approval and feedback;
* prompt administration and experimentation;
* asynchronous recommendation generation;
* durable recommendation work queue;
* AI-generated unresolved-risk impact analysis;
* asynchronous report dispatch;
* report-delivery retry and reconciliation;
* recipient management and multi-recipient routing;
* authentication and role-based authorization;
* artifact retention and cleanup.

Future AI-provider work must build on the completed Sprint 15 baseline:

Completed Monitoring Run
↓
Persisted Evidence-Based Risk
↓
Evidence-Safe Recommendation Context
↓
Provider-Neutral AI Boundary
↓
OpenAI or Future Approved Provider
↓
Strict Structured Output
↓
Existing Recommendation Validation
↓
AI Recommendation or Rule-Based Fallback
↓
Existing Recommendation Persistence

Future implementations must not:

* allow an AI provider to collect evidence;
* allow an AI provider to create findings or risks;
* allow AI output to modify severity, confidence, trust or monitoring status;
* bypass `RiskRemediationRecommendationValidator.java`;
* remove the rule-based fallback path;
* persist API credentials or authorization headers;
* persist raw provider responses;
* expose unsanitized provider exception messages;
* make provider failure fail a completed monitoring run;
* couple AI provider communication directly to PDF rendering;
* couple AI provider communication directly to Telegram delivery;
* introduce a parallel recommendation persistence model.

Future report-delivery work must continue to build on the completed Sprint 14
dispatch baseline and reuse immutable persisted PDF artifacts.

## Next Phase

Sprint 15 is complete.

No Sprint 16 scope has yet been approved.

The V1 monitoring-to-AI-recommendation-to-report-to-Telegram chain is complete:

Scheduled or Manual Monitoring
↓
Evidence Collection and Normalization
↓
Finding, Risk and Trust Assessment
↓
Evidence-Safe Recommendation Context
↓
Validated OpenAI Remediation Recommendations
↓
Rule-Based Fallback When Required
↓
Immutable and Versioned Full Monitoring Run PDF
↓
Automatic Telegram Document Dispatch
↓
Persisted and Auditable Delivery Outcome

Controlled production verification confirmed:

* four persisted risks;
* four validated OpenAI recommendations;
* provider name `OpenAI`;
* model name `gpt-5.6-terra`;
* AI recommendation content in the generated PDF;
* successful automatic Telegram PDF delivery;
* persisted Telegram message ID;
* one automatic dispatch attempt;
* no detected secret exposure.

Controlled resilience verification confirmed:

* provider disabled → `PROVIDER_UNAVAILABLE` fallback;
* invalid provider credentials → `PROVIDER_FAILURE` fallback;
* four risks → four fallback recommendations;
* monitoring run remained `COMPLETED`;
* provider failure did not alter authoritative monitoring results.

The next phase should focus on an explicitly approved product objective,
recommendation-lifecycle capability or production-hardening requirement rather
than extending Sprint 15 implicitly.

Potential future directions include:

* provider production hardening;
* recommendation quality evaluation;
* recommendation idempotency and lifecycle;
* unresolved-risk impact analysis;
* asynchronous recommendation or report processing;
* automatic retry and reconciliation;
* recipient management;
* authentication and authorization;
* operational metrics and alerting;
* artifact retention and cleanup.

---

## Latest Completed Sprint

### Sprint 15 — Production OpenAI Recommendation Provider Baseline

Sprint 15 completed the first concrete production AI recommendation provider
and closed the V1 real-AI recommendation chain.

Implemented capabilities include:

* Telegram and automatic PDF dispatch safe-default correction;
* OpenAI configuration boundary;
* default-disabled provider operation;
* environment-based API-key configuration;
* environment-based model selection;
* configurable API base URL;
* configurable connection and request timeouts;
* configurable maximum output-token limit;
* provider readiness evaluation;
* provider-neutral OpenAI API client contract;
* typed provider result classification;
* strict Responses API request generation;
* strict JSON Schema recommendation output;
* bounded structured recommendation content;
* provider response-storage disablement;
* typed completed-response parsing;
* refusal handling;
* incomplete and malformed response rejection;
* Java `HttpClient` OpenAI transport;
* Bearer authentication;
* redirect-following prevention;
* authentication, rate-limit, timeout and provider-unavailable classification;
* interruption handling and interrupt-flag restoration;
* network-failure containment;
* concrete OpenAI provider adapter;
* disabled-provider HTTP-call prevention;
* provider failure containment;
* existing output-validator preservation;
* existing rule-based fallback preservation;
* real OpenAI recommendation persistence;
* OpenAI provider and model audit metadata;
* controlled provider-disabled fallback verification;
* controlled provider-failure fallback verification;
* AI recommendation PDF-content verification;
* automatic Telegram delivery of the AI-enriched PDF;
* secret-exposure regression verification.

The completed production AI path is:

Completed Monitoring Run
↓
Persisted Risks Loaded
↓
Evidence-Safe Recommendation Context Built
↓
Versioned Prompt Created
↓
OpenAI Provider Selected
↓
OpenAI Responses API Called
↓
Strict Structured Output Parsed
↓
Existing Recommendation Validator Applied
↓
Validated AI Recommendation Persisted
↓
Full Monitoring Run PDF Generated
↓
PDF Automatically Dispatched Through Telegram
↓
Dispatch Audit Persisted

The preserved provider-disabled path is:

OpenAI Disabled or Incompletely Configured
↓
Provider Marked Unavailable
↓
OpenAI HTTP Client Not Called
↓
Rule-Based Fallback Generated
↓
Fallback Reason `PROVIDER_UNAVAILABLE`
↓
Recommendation Persisted

The preserved provider-failure path is:

OpenAI Request Fails
↓
Typed Provider Failure Returned
↓
Rule-Based Fallback Generated
↓
Fallback Reason `PROVIDER_FAILURE`
↓
Recommendation Persisted
↓
Monitoring Run Remains `COMPLETED`

Verification baseline:

* Compile: SUCCESS
* Tests: 277 PASSED
* Failures: 0
* Errors: 0
* Latest migration: V18
* Database migration added: NO
* Real OpenAI recommendation: VERIFIED
* Provider-disabled fallback: VERIFIED
* Provider-failure fallback: VERIFIED
* Real AI recommendation count: 4
* AI recommendation PDF content: VERIFIED
* Automatic Telegram PDF dispatch: SENT
* Telegram message ID persistence: VERIFIED
* Automatic dispatch count: 1
* Secret exposure: NOT DETECTED

Sprint 15 preserved:

* provider-neutral recommendation orchestration;
* authoritative monitoring results;
* evidence, finding, risk and trust authority;
* evidence-safe AI context;
* structured recommendation validation;
* mandatory rule-based fallback;
* immutable PDF artifact history;
* Telegram delivery isolation;
* append-only dispatch audit;
* external secret configuration;
* monitoring lifecycle isolation from provider failure.

Sprint 15 did not add:

* a second concrete AI provider;
* provider failover;
* automatic AI retry;
* exponential backoff;
* circuit breaker;
* token, cost or latency metrics;
* provider-health persistence;
* recommendation idempotency;
* recommendation regeneration;
* recommendation supersession;
* recommendation approval or feedback;
* prompt administration;
* asynchronous recommendation processing;
* durable recommendation queue;
* AI-generated unresolved-risk impact analysis;
* authentication or role-based authorization;
* external secret-manager integration.

---

## Latest Completed Sprint

### Sprint 14 — Automatic Telegram PDF Dispatch and Audit

Sprint 14 completed the V1 monitoring-to-report-to-Telegram delivery chain.

Implemented capabilities include:

- binary-safe Telegram multipart PDF upload;
- Telegram Bot API `sendDocument` integration;
- Telegram message ID extraction;
- dedicated document-delivery result classification;
- default-disabled automatic PDF dispatch configuration;
- existing-or-generate V1 PDF artifact resolution;
- PDF ownership, binary-size and SHA-256 revalidation;
- dedicated report-dispatch persistence;
- automatic and manual-retry dispatch types;
- PENDING, SENT and FAILED lifecycle states;
- application- and database-level automatic dispatch idempotency;
- monitoring-run-to-artifact ownership enforcement;
- attempt-number and retry-lineage constraints;
- recommendation-before-PDF dispatch ordering;
- monitoring lifecycle isolation from Telegram failure;
- append-only manual retry;
- same immutable PDF reuse during retry;
- run-scoped retry ownership validation;
- dispatch history and retry controls in monitoring run reports;
- scheduler environment-variable control;
- controlled local Telegram stub integration;
- controlled real Telegram PDF delivery verification.

The completed automatic path is:

Monitoring Run COMPLETED  
↓  
Recommendation Generation  
↓  
Existing-or-Generate V1 PDF Artifact Resolution  
↓  
PDF Integrity Revalidation  
↓  
PENDING Automatic Dispatch Attempt  
↓  
Telegram Document Upload  
↓  
SENT or FAILED Dispatch Persistence  
↓  
Notification Event Generation

The completed manual retry path is:

Latest FAILED Dispatch Attempt  
↓  
Existing Immutable PDF Artifact  
↓  
PDF Integrity Revalidation  
↓  
New PENDING Manual-Retry Attempt  
↓  
Telegram Document Upload  
↓  
SENT or FAILED Dispatch Persistence

Verification baseline:

- Compile: SUCCESS
- Tests: 228 PASSED
- Latest migration: V18
- Controlled end-to-end integration: PASSED
- Real Telegram document delivery: VERIFIED
- Telegram PDF received and opened: VERIFIED
- Automatic dispatch attempt: SENT
- Telegram message ID persistence: VERIFIED
- Dispatch history UI: VERIFIED
- Secret values in source or logs: NOT DETECTED

Sprint 14 preserved:

- authoritative monitoring run results;
- persisted risk and trust authority;
- advisory recommendation boundaries;
- immutable PDF artifact history;
- notification-event delivery isolation;
- environment-based Telegram secrets;
- disabled-by-default automatic PDF dispatch;
- append-only delivery audit.

Sprint 14 did not add:

- asynchronous dispatch;
- automatic retry scheduling;
- durable delivery queue;
- exponential backoff;
- dead-letter processing;
- multi-recipient routing;
- additional document-delivery providers;
- authentication or role-based authorization;
- artifact cleanup automation;
- AI-generated unresolved-risk impact analysis.

### Sprint 13 — Full Monitoring Run PDF Artifact Baseline

Sprint 13 established manual generation, persistence and download of a
versioned full monitoring run PDF artifact.

Implemented capabilities include:

- PDFBox-based full monitoring report rendering;
- findings, risks, trust and comparison output;
- advisory AI remediation recommendation content and audit metadata;
- immutable PDF artifact persistence;
- deterministic versioned filenames;
- SHA-256 and binary-size integrity validation;
- completed-run-only generation;
- duplicate run/version prevention;
- ownership-safe manual PDF download;
- browser generation and download controls;
- end-to-end integration and visual PDF verification.

Verification baseline:

- Compile: SUCCESS
- Tests: 143 PASSED
- Latest migration: V17
- Visual PDF QA: PASSED
- Downloaded SHA-256: MATCHED
- Duplicate artifact check: PASSED

Automatic PDF generation and Telegram PDF dispatch were outside the
Sprint 13 boundary and were completed in Sprint 14.

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