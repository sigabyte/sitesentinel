# SiteSentinel Future Backlog

This document contains ideas and capabilities that are intentionally deferred from the current implementation baseline.

Items listed here are not approved for implementation unless they are explicitly selected during a future sprint opening.

---

## Architecture and Platform

### FB-001 — Presentation Layer Abstraction

Evaluate whether reporting should become part of a broader presentation layer.

### FB-002 — Monitoring Context

Evaluate whether Website should eventually own a Monitoring Context aggregate.

### FB-003 — Reusable Engineering Framework

Generalize the engineering methodology developed during SiteSentinel into a reusable framework.

### FB-004 — Monitoring Lifecycle Diagram

Add a single end-to-end monitoring lifecycle reference diagram to the architecture documentation.

### FB-005 — Architecture Overview Diagram

Add a high-level single-page architecture overview diagram.

### FB-006 — API Versioning Strategy

Define API versioning, backward compatibility, and deprecation rules.

### FB-007 — Workspace-Oriented UI Architecture

Evaluate a workspace-oriented user interface after the MVP UI structure is stable.

### FB-008 — Quality Attribute Metrics

Define measurable targets for response time, uptime, monitoring throughput, and failure rates.

### FB-009 — Asynchronous Post-Monitoring Processing

Evaluate whether automatic recommendation generation, automatic PDF artifact
generation, and report dispatch should use a durable post-monitoring work queue.

---

## AI Remediation Recommendation Follow-Up

- Add a concrete production AI provider adapter.
- Add an AI provider HTTP client boundary.
- Add provider-specific structured response parsing.
- Add environment-based AI provider configuration.
- Add external secret-manager integration for AI credentials.
- Add provider request timeout configuration.
- Add provider retry and backoff policy.
- Add provider rate-limit classification.
- Add provider circuit breaker.
- Add provider failover.
- Add multi-provider routing.
- Add provider latency metrics.
- Add provider token-usage metrics.
- Add provider cost metrics.
- Add recommendation generation duration metrics.
- Add recommendation regeneration controls.
- Define recommendation generation idempotency.
- Prevent accidental duplicate recommendation generation.
- Define recommendation supersession rules.
- Add recommendation approval workflow.
- Add recommendation quality feedback.
- Evaluate recommendation quality scoring.
- Define recommendation history retention.
- Add prompt template administration.
- Add prompt experiment management.
- Define prompt-version retirement rules.
- Evaluate asynchronous recommendation generation.
- Evaluate a durable recommendation work queue.
- Optimize recommendation context loading for monitoring runs containing many risks.
- Define safe operational visibility for validation issue classifications.
- Preserve the rule-based fallback path when a production AI provider is unavailable.

---

## Notification and Report Dispatch

- Design automatic notification and report dispatch rules.
- Define eligible notification event types.
- Define severity-based dispatch rules.
- Define manual and automatic dispatch boundaries.
- Define scheduled monitoring dispatch behavior.
- Add a Telegram document-upload client boundary.
- Keep Telegram document delivery separate from Telegram text-message delivery.
- Automatically dispatch the persisted full monitoring run PDF after successful report generation.
- Require the monitoring run to be completed before automatic report dispatch.
- Require recommendation generation processing to finish before PDF report rendering.
- Do not dispatch when no valid PDF artifact is available.
- Persist report dispatch lifecycle and audit metadata.
- Define report destination ownership.
- Prevent duplicate automatic report dispatch.
- Define report dispatch idempotency requirements.
- Add manual report re-dispatch controls.
- Preserve notification generation when recommendation or report dispatch fails safely.

---

## Recipient Management

- Add notification recipient domain model.
- Add recipient creation and management.
- Add active and inactive recipient status.
- Add channel-specific recipient destinations.
- Add per-website recipient configuration.
- Add organization-level recipient configuration.
- Add recipient validation.
- Add recipient audit history.
- Add multi-recipient routing.

---

## Notification Subscriptions and Preferences

- Add notification subscription rules.
- Add event-type subscriptions.
- Add severity preferences.
- Add website-level subscriptions.
- Add channel preferences.
- Add quiet hours.
- Add user-specific notification preferences.

---

## Delivery Retry and Failure Handling

- Add delivery retry policy.
- Classify retryable and non-retryable failures.
- Add exponential backoff.
- Add maximum retry count.
- Add dead-letter handling.
- Add manual retry action.
- Add retry audit visibility.
- Add delivery queue support.
- Add PDF report dispatch retry classification.
- Add Telegram document-upload retry handling.
- Prevent retry from creating duplicate dispatch records.
- Preserve the original PDF artifact across delivery retries.
- Define recovery for report generation success followed by dispatch failure.

---

## Provider Diagnostics Follow-Up

- Distinguish provider-side rate limiting from generic provider failure.
- Distinguish Telegram or upstream provider unavailability from generic provider failure.
- Evaluate whether additional safe provider error metadata should be stored beyond HTTP status code.
- Define provider health-check freshness and stale-result rules.
- Define provider health-check execution rate limits.

---

## Additional Delivery Providers

- Add Email delivery provider.
- Add WhatsApp delivery provider.
- Add Slack delivery provider.
- Add Webhook delivery provider.
- Add provider-specific configuration readiness checks.
- Add provider-specific health checks.
- Add provider-specific operational settings visibility.

---

## Advanced Delivery Operations

- Add delivery analytics.
- Add provider success-rate reporting.
- Add provider latency reporting.
- Add failure trend reporting.
- Add provider rate limiting.
- Add provider circuit breaker.
- Add provider failover.
- Add multi-provider routing.
- Add escalation policies.

---

## Secret Management

- Evaluate external secret manager integration.
- Define production secret rotation.
- Define provider credential revocation procedures.
- Preserve environment-based configuration until a dedicated secret management design is approved.
- Prevent application UI from exposing or editing raw provider secrets.

---

## Monitoring and Scanner Hardening

- Add request rate limiting.
- Add scan queue controls.
- Add configurable maximum response body size.
- Add structured logging for scan execution.
- Add scanner failure classification.
- Add retry policy for transient scanner failures.
- Add monitoring run cancellation.
- Add operational metrics for scan duration and failure rates.
- Add advanced scanner signals.
- Add external reputation integrations.

---

## Data Integrity and Idempotency

- Add database-level uniqueness constraints for idempotent assessment outputs.
- Define recommendation generation idempotency.
- Define duplicate recommendation prevention rules.
- Define recommendation supersession integrity rules.
- Define notification dispatch idempotency.
- Define report dispatch idempotency.
- Define delivery attempt uniqueness rules.
- Define duplicate Telegram document dispatch prevention.
- Define duplicate provider check handling.

---

## Testing

- Add service tests for website registration.
- Add scanner safety validation tests.
- Add evidence analysis tests.
- Add risk evaluation tests.
- Add trust evaluation tests.
- Add notification event generation tests.
- Add notification delivery settings controller and template integration tests.
- Add concrete AI provider contract tests.
- Add AI provider HTTP integration tests using controlled stub responses.
- Add provider timeout, rate-limit, and failure-classification tests.
- Add recommendation generation idempotency tests.
- Add recommendation supersession tests.
- Add Telegram document-upload safety tests.
- Add automatic report dispatch ordering tests.
- Add report dispatch idempotency tests.
- Add duplicate document-dispatch prevention tests.
- Add report delivery retry and recovery tests.
- Add end-to-end completed-run-to-Telegram-report tests.

---

## Reporting, PDF Artifacts and Export

- Define PDF retention and deletion policy.
- Add CSV export.
- Define business-user report format.
- Add report approval workflow.
- Evaluate AI-assisted report summaries as a separate validated output contract.

---

## Security and Access Control

- Add authentication.
- Add user accounts.
- Add role-based access control.
- Add organization-level access boundaries.
- Add audit logging for privileged actions.
- Define provider secret access rules.

---

## UI Maintenance

- Clean redundant nested section elements in the notification detail template.
- Standardize notification operations navigation.
- Evaluate shared Thymeleaf fragments for repeated navigation.
- Evaluate shared summary-card components.
- Define non-technical severity language.
- Define user-facing trust score explanations.

---

## Product Decisions

- Decide whether trust assessments should be produced when no risks are found.
- Decide whether optional resource failures should affect trust score.
- Decide how historical trend changes should be displayed.
- Decide which scanner signals should be visible by default.
- Decide whether AI may ever participate in risk, finding, or evidence analysis beyond the approved advisory 
remediation recommendation boundary.
- Decide whether AI-generated recommendations require human approval before external report dispatch.
- Decide whether repeated recommendations replace, supersede, or remain alongside previous recommendation history.
- Decide whether report dispatch should include all completed runs or only runs meeting configured risk and severity 
rules.
- Decide whether the full PDF report or a short notification should be sent when a completed run contains no risks.

## Automatic Monitoring Report Dispatch Chain

### Automatic PDF generation

Generate the existing versioned full monitoring run PDF artifact
automatically after a monitoring run completes.

The future implementation must reuse the Sprint 13 renderer, generation and
artifact persistence boundaries. It must not introduce a parallel PDF model.

### Telegram PDF document dispatch

Upload and dispatch the persisted full monitoring run PDF through the
controlled Telegram delivery provider.

The Telegram message may include a concise summary, but the persisted PDF
artifact remains the authoritative full monitoring report.

### Dispatch persistence and audit

Persist the relationship between:

- monitoring run;
- PDF artifact;
- notification event;
- Telegram delivery attempt;
- provider response;
- delivery status;
- failure category;
- timestamps.

### Recommendation and dispatch idempotency

Prevent duplicate recommendation generation, duplicate PDF generation and
duplicate Telegram document dispatch for the same monitoring lifecycle event.

Sprint 12 recommendation persistence and lifecycle-isolation boundaries,
together with Sprint 13 PDF run/version uniqueness, must be reused rather
than bypassed.

Recommendation generation idempotency remains future work and must be
defined before automatic report dispatch is approved.

### Retry and failure isolation

Telegram upload or dispatch failure must not:

- roll back a completed monitoring run;
- delete or corrupt the PDF artifact;
- regenerate recommendations;
- create duplicate PDF artifacts;
- create uncontrolled repeated dispatch attempts.

Retry, backoff and queue behavior require a separate approved scope.