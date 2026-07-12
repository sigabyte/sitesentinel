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

---

## Notification Dispatch

- Design automatic notification dispatch rules.
- Define eligible notification event types.
- Define severity-based dispatch rules.
- Define manual and automatic dispatch boundaries.
- Define scheduled monitoring dispatch behavior.
- Prevent duplicate automatic dispatch.
- Define delivery idempotency requirements.
- Add automatic Telegram delivery after monitoring completion.

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

---

## Provider Connectivity Improvements

- Replace boolean Telegram connectivity verification with a typed result.
- Distinguish authentication failure.
- Distinguish connection timeout.
- Distinguish DNS or network failure.
- Distinguish Telegram API unavailability.
- Distinguish unexpected provider responses.
- Capture safe provider response metadata without exposing secrets.

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
- Define notification dispatch idempotency.
- Define delivery attempt uniqueness rules.
- Define duplicate provider check handling.

---

## Testing

- Add service tests for website registration.
- Add scanner safety validation tests.
- Add monitoring lifecycle integration tests.
- Add evidence analysis tests.
- Add risk evaluation tests.
- Add trust evaluation tests.
- Add notification event generation tests.
- Add notification delivery provider tests.
- Add Telegram readiness and health-check tests.
- Add notification delivery operations integration tests.

---

## Reporting and Export

- Add PDF report export.
- Add CSV export.
- Define business-user report format.
- Add report versioning.
- Add report approval workflow.
- Evaluate AI-assisted report summaries.

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
- Define future AI-assisted analysis boundaries.