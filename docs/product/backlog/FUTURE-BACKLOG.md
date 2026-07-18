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

## AI Remediation Recommendation Production Hardening

Sprint 15 completed the first concrete production AI provider baseline.

The completed foundation includes:

- provider-neutral recommendation orchestration;
- concrete OpenAI provider adapter;
- environment-based OpenAI configuration;
- default-disabled provider behavior;
- OpenAI Responses API HTTP client boundary;
- strict structured-output request generation;
- typed provider response parsing;
- timeout and HTTP failure classification;
- existing recommendation validation;
- provider-disabled rule-based fallback;
- provider-failure rule-based fallback;
- real OpenAI recommendation verification;
- AI recommendation persistence;
- AI-enriched PDF generation;
- automatic Telegram delivery of the AI-enriched PDF;
- secret-safe request, response, and error handling.

Future work must build on the existing provider-neutral boundary and must not
replace the rule-based fallback path.

Remaining production-hardening work:

- Add external secret-manager integration for AI credentials.
- Define production API-key rotation and revocation procedures.
- Add explicit AI provider priority configuration.
- Add a second concrete AI provider only after a documented provider-expansion
  decision.
- Add provider failover.
- Add multi-provider routing.
- Add automatic provider retry policy.
- Classify retryable and non-retryable AI provider failures.
- Add `Retry-After` handling for provider rate limits when available.
- Add exponential backoff.
- Add maximum retry count.
- Add provider circuit breaker.
- Add provider health monitoring.
- Add provider-health freshness rules.
- Add provider request-ID audit when it can be stored without exposing
  sensitive response data.
- Decide whether authentication, rate-limit, timeout, invalid-response and
  provider-unavailable classifications should be persisted separately from the
  current broad `PROVIDER_FAILURE` fallback reason.
- Define safe operational visibility for provider failure classifications.
- Add provider latency metrics.
- Add provider token-usage metrics.
- Add provider cost metrics.
- Add recommendation-generation duration metrics.
- Define model evaluation and promotion criteria.
- Add a repeatable recommendation-quality evaluation dataset.
- Compare configured models using:

  - structured-output success rate;
  - validator acceptance rate;
  - unsupported-claim rate;
  - remediation-step usefulness;
  - verification-step usefulness;
  - latency;
  - token usage;
  - cost.
- Define model-version upgrade and rollback procedures.
- Define behavior when a configured model is retired or unavailable.
- Add recommendation regeneration controls.
- Define recommendation generation idempotency.
- Prevent accidental duplicate recommendation generation.
- Define recommendation supersession rules.
- Define recommendation history retention.
- Add recommendation approval workflow.
- Add recommendation quality feedback.
- Evaluate recommendation quality scoring.
- Add prompt template administration.
- Add prompt experiment management.
- Define prompt-version retirement rules.
- Evaluate asynchronous recommendation generation.
- Evaluate a durable recommendation work queue.
- Optimize recommendation context loading for monitoring runs containing many
  risks.
- Preserve the existing rule-based fallback whenever every configured AI
  provider is disabled, unavailable, invalid, or unsuccessful.

Future implementations must not:

- allow an AI provider to collect evidence;
- allow an AI provider to create findings or risks;
- allow AI output to alter severity or trust assessments;
- bypass `RiskRemediationRecommendationValidator.java`;
- persist raw provider responses;
- persist authorization headers or API keys;
- expose provider exception messages without an approved sanitization
  boundary;
- make recommendation-provider failure fail a completed monitoring run;
- couple provider communication directly to PDF rendering or Telegram
  delivery.

---

## AI Unresolved-Risk Impact Analysis

- Generate potential impact analysis for validated monitoring risks when they remain unresolved.
- Explain what technical, operational, security, availability, customer, financial, compliance, or reputational 
consequences may occur if a risk is not remediated.
- Generate only impact categories that are relevant to the finding and supported by available evidence.
- Link every generated impact statement to the originating risk, finding, evidence, monitoring run, prompt version, 
and recommendation generation record.
- Include an impact category for every generated consequence.
- Include likelihood or urgency when it can be supported safely.
- Include confidence for every generated impact statement.
- Use non-deterministic language such as `may`, `could`, or `is likely to` unless the consequence follows directly 
from verified technical evidence.
- Prevent unsupported claims, exaggerated business consequences, and speculative legal, regulatory, or financial 
conclusions.
- Preserve the rule-based fallback path when AI-generated impact analysis is unavailable or invalid.
- Validate AI-generated impact analysis before persistence or external presentation.
- Store validated impact analysis as structured data rather than generating it inside the PDF renderer.
- Make validated impact analysis reusable by PDF reports, web views, APIs, notification summaries, and future 
executive dashboards.
- Keep unresolved-risk impact analysis separate from risk detection, severity calculation, trust assessment, 
and evidence analysis.
- Do not allow AI-generated impact analysis to modify the authoritative risk severity or monitoring result.
- Define impact-analysis generation idempotency.
- Prevent duplicate impact-analysis generation for the same risk and recommendation lifecycle.
- Define regeneration, supersession, history retention, and approval rules.
- Optimize impact generation for monitoring runs containing many risks.


## Notification and Report Dispatch Follow-Up

- Define risk- and severity-based automatic report-dispatch eligibility.
- Decide whether every completed monitoring run should produce an external
  dispatch.
- Define the approved behavior for completed runs containing no risks.
- Define report-destination ownership.
- Define recipient ownership and recipient authorization boundaries.
- Evaluate whether report dispatch should be correlated with notification
  events without merging their persistence models.
- Evaluate asynchronous post-monitoring report dispatch.
- Add a durable report-dispatch work queue if asynchronous delivery is
  approved.
- Define recovery and reconciliation for indefinitely PENDING dispatch
  attempts.
- Add report-dispatch operational metrics.
- Add report-dispatch latency and failure-rate reporting.
- Add operational alerting for repeated report-dispatch failures.
- Add a dedicated dispatch administration and audit page.
- Evaluate additional document-delivery providers.

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

- Add automatic delivery retry policy.
- Classify retryable and non-retryable provider failures.
- Add Telegram rate-limit classification.
- Add Telegram `retry_after` handling when available.
- Add exponential backoff.
- Add maximum automatic retry count.
- Add durable retry scheduling.
- Add dead-letter handling.
- Add delivery queue support.
- Define recovery for indefinitely PENDING dispatch attempts.
- Add dispatch reconciliation after application or infrastructure failure.
- Prevent concurrent automatic retry workers from creating duplicate
  attempts.
- Define operational escalation after retry exhaustion.
- Preserve the existing immutable PDF artifact across all future automatic
  retries.
- Keep manual retry history append-only.

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

- Add database-level uniqueness constraints for any remaining assessment
  outputs that require idempotent persistence.
- Define recommendation generation idempotency.
- Define duplicate recommendation prevention rules.
- Define recommendation supersession integrity rules.
- Define notification-event dispatch idempotency.
- Define duplicate provider-check handling.
- Define idempotency for future asynchronous dispatch jobs.
- Define concurrency control for automatic retry workers.
- Define reconciliation rules for PENDING attempts whose provider outcome is
  unknown.

---

## Testing

- Add service tests for website registration.
- Add scanner safety validation tests.
- Add evidence analysis tests.
- Add risk evaluation tests.
- Add trust evaluation tests.
- Add notification event generation tests.
- Add notification delivery settings controller and template integration tests.
- Add unresolved-risk impact generation service tests.
- Add impact-analysis structured output validation tests.
- Add risk, finding, evidence, and impact traceability tests.
- Add unsupported and speculative impact rejection tests.
- Add non-deterministic wording validation tests.
- Add impact category, likelihood, and confidence validation tests.
- Add impact-analysis idempotency and duplicate-prevention tests.
- Add impact-analysis regeneration and supersession tests.
- Add PDF rendering tests for validated unresolved-risk impacts.
- Verify that the PDF renderer does not independently generate impact statements.
- Add recommendation generation idempotency tests.
- Add recommendation supersession tests.
- Add repeatable AI recommendation quality-evaluation tests across approved
  monitoring-risk fixtures.
- Add model-comparison tests for validator acceptance, unsupported claims,
  latency, token usage, and estimated cost.
- Add provider model-retirement and configuration-rollback tests.
- Add automatic AI provider retry-policy tests when retry behavior is
  implemented.
- Add AI provider `Retry-After` and exponential-backoff tests when implemented.
- Add provider circuit-breaker tests when implemented.
- Add multi-provider priority and failover tests when a second provider is
  approved.
- Add provider-specific persisted failure-classification tests if detailed
  provider failure audit is approved.
- Add automatic report-dispatch retry-policy tests.
- Add provider rate-limit and `retry_after` handling tests.
- Add exponential-backoff scheduling tests.
- Add dispatch queue recovery tests.
- Add indefinitely PENDING attempt reconciliation tests.
- Add concurrent retry-worker idempotency tests.
- Add dead-letter transition tests.
- Add multi-recipient dispatch isolation tests when recipient management is
  implemented.
- Add authentication and authorization tests for report retry operations
  when access control is implemented.

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
- Decide whether unresolved-risk impact analysis should be generated for every risk or only for configured severity levels.
- Decide which impact categories may be displayed to external report recipients.
- Decide whether AI-generated impact analysis requires human approval before external report dispatch.
- Decide whether likelihood should use descriptive levels or a numeric probability.
- Decide whether financial, legal, regulatory, and reputational impacts require stricter validation or exclusion 
  from the initial implementation.
- Decide how regenerated impact analysis supersedes or remains alongside previous validated versions.
- Decide whether reports should clearly distinguish verified technical consequences from contextual potential impacts.
- Decide whether repeated recommendations replace, supersede, or remain alongside previous recommendation history.
- Decide whether report dispatch should include all completed runs or only runs meeting configured risk and severity 
  rules.
- Decide whether the full PDF report or a short notification should be sent when a completed run contains no risks.
- Decide whether synchronous post-monitoring Telegram PDF dispatch remains
  acceptable for production-scale monitoring.
- Decide whether future automatic retries should occur without human
  intervention or require an operational approval boundary.

## Report Dispatch Production Hardening

Sprint 14 completed the synchronous V1 monitoring-to-PDF-to-Telegram
dispatch chain.

Future production hardening must build on the existing:

- immutable and versioned PDF artifact model;
- SHA-256 integrity validation;
- dedicated report-dispatch attempt model;
- application- and database-level automatic idempotency;
- append-only manual retry lineage;
- configured Telegram destination boundary;
- completed-run lifecycle isolation.

Future work may include:

- asynchronous dispatch execution;
- durable dispatch queue;
- automatic retry scheduling;
- exponential backoff;
- provider rate-limit handling;
- `retry_after` support;
- dead-letter processing;
- PENDING-attempt recovery;
- provider-outcome reconciliation;
- dispatch latency and success-rate metrics;
- operational failure alerting;
- dedicated dispatch administration;
- recipient ownership;
- multi-recipient routing;
- notification subscriptions;
- additional document-delivery providers.

Future implementations must not:

- replace the existing PDF artifact model;
- overwrite historical dispatch attempts;
- regenerate the PDF during delivery retry;
- reopen or modify a completed monitoring run;
- merge report-dispatch persistence into the notification-event attempt
  model;
- persist provider credentials or raw secret-bearing responses.