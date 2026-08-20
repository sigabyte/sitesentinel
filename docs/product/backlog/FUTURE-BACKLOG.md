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

Sprint 17 completed application-level automatic recommendation idempotency on
top of the adaptive response-analysis, evidence-bounded risk explanation and
production OpenAI provider baseline.

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
- prompt and output contract V2;
- Risk and Potential Impact Summary;
- conditional potential-consequence language;
- evidence-confirmed and evidence-not-confirmed boundaries;
- unsupported authoritative incident-claim validation;
- fallback rule V2;
- dedicated explanations for 12 supported risk types;
- safe generic explanation for unknown future risk types;
- canonical five-section HTML and PDF recommendation reporting;
- adaptive response-body processing without truncation.

Sprint 17 added the following completed capabilities:

- exact monitoring-run and risk pair recommendation existence checks;
- automatic generation skip when persisted recommendation history exists;
- generated, skipped and failed recommendation lifecycle accounting;
- prevention of repeated automatic AI-provider invocation after persistence;
- prevention of repeated automatic fallback generation after persistence;
- preservation of recommendation history and latest-recommendation behavior.

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
- Decide whether database-level recommendation uniqueness is required for
  concurrent automatic generation.
- Define transaction, locking or serialization behavior for concurrent
  recommendation generation.
- Define how recommendation persistence conflicts should be classified without
  invoking the AI provider or fallback generator again.
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

## Recommendation Explanation Follow-Up

Sprint 16 completed the initial Risk and Potential Impact Summary baseline.

Completed behavior includes:

- explaining what was detected;
- explaining what the persisted risk means;
- explaining why the condition matters;
- explaining what it may cause if unresolved;
- distinguishing what the evidence confirms from what it does not confirm;
- expressing potential consequences conditionally;
- rejecting unsupported authoritative incident and exploitation claims;
- providing rule-based explanations for all currently supported risk types;
- presenting the same explanation structure in HTML and PDF reports.

The completed summary remains part of the existing persisted recommendation
record. It does not create a separate impact-analysis aggregate or persistence
model.

The following advanced capabilities remain deferred and require a separate
future design decision:

- structured impact categories;
- separately persisted impact statements;
- impact-specific likelihood or urgency;
- impact-specific confidence scores;
- impact-specific generation records;
- impact regeneration and supersession;
- impact approval workflow;
- configurable impact visibility by report recipient;
- executive impact dashboards;
- stricter policy controls for financial, legal, regulatory and reputational
  impact categories;
- lifecycle rules for separately versioned impact-analysis records.

Future structured impact work must:

- reuse the persisted risk, finding and evidence traceability chain;
- remain separate from risk detection and severity calculation;
- preserve conditional consequence language;
- reject unsupported or exaggerated conclusions;
- preserve rule-based recommendation availability;
- avoid duplicating the completed Risk and Potential Impact Summary unless a
  separately approved product requirement requires structured impact data.

---

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
- Define an explicit maximum-response-size security policy separately from the
  completed adaptive in-memory spillover threshold.
- Preserve the Sprint 16A invariant that the memory spillover threshold is not a
  response scan cutoff.
- Define response-size rejection, failure classification and audit behavior
  before introducing any maximum-response-size limit.
- Add temporary-storage capacity monitoring.
- Add temporary-storage health checks.
- Add response-body spillover frequency metrics.
- Add response-body spillover byte-volume metrics.
- Add temporary-file cleanup success and failure metrics.
- Add response-processing latency metrics.
- Add operational alerting for temporary-storage pressure.
- Add slow-response protection.
- Add response download rate limiting.
- Add structured logging for scan execution.
- Add scanner failure classification.
- Add retry policy for transient scanner failures.
- Add monitoring run cancellation.
- Add operational metrics for scan duration and failure rates.
- Add advanced scanner signals.
- Add external reputation integrations.
- Evaluate a configurable operational response-body limit only with an explicit
  evidence-completeness and safe-failure policy; do not silently truncate
  analyzed response content.
- Add temporary-file storage capacity monitoring and cleanup metrics for
  adaptive response spillover.

---

## Data Integrity and Idempotency

Sprint 17 completed sequential application-level automatic recommendation
idempotency for persisted monitoring-run and risk pairs.

Remaining work:

- Decide whether recommendation persistence requires a database unique
  constraint for the monitoring-run and risk pair.
- Define concurrency control for simultaneous recommendation generation.
- Define transaction, locking or serialization behavior for recommendation
  check-and-insert races.
- Define safe handling when concurrent generation has already called an
  external provider before a persistence conflict is detected.
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
- Add broader recommendation-language fixtures for conditional potential-impact
  validation.
- Add validator fixtures for additional unsupported authoritative incident
  phrasings when observed.
- Add risk-specific explanation regression coverage whenever a new persisted
  risk type is introduced.
  - Add structured impact-analysis tests only if a separate structured impact
    model is approved in a future sprint.
- Add concurrent recommendation-generation tests if database-level
  idempotency is approved.
- Add persistence-conflict tests if a recommendation unique constraint is
  introduced.
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
- Add role-specific authorization tests for report retry operations when
  fine-grained RBAC is implemented.

---

## Reporting, PDF Artifacts and Export

- Define PDF retention and deletion policy.
- Add CSV export.
- Define business-user report format.
- Add report approval workflow.
- Evaluate AI-assisted report summaries as a separate validated output contract.

---

## Authentication and Access Control Follow-Up

Sprint 18 completed the secure-by-default V1 single-operator authentication
and access-protection baseline.

Completed behavior includes:

- environment-controlled operator username and password;
- blank-credential startup rejection;
- removal of generated development-password behavior;
- application-wide authentication requirements;
- custom form-based login;
- generic invalid-credential feedback;
- in-memory operator authentication;
- the `OPERATOR` role;
- POST-based logout;
- dashboard sign-out control;
- CSRF-protected logout;
- CSRF enforcement for state-changing browser operations;
- protected monitoring, evidence, risk, report, PDF and Telegram
  administration surfaces;
- test-only credential configuration separated from runtime configuration;
- controlled runtime authentication verification.

The completed V1 model intentionally supports one application-wide operator.

Remaining authentication and authorization work:

- Define whether SiteSentinel requires database-backed user accounts.
- Add multi-user support only after an explicit identity-model decision.
- Define user creation, activation, suspension and deletion.
- Define password change and password reset workflows.
- Define production operator-credential rotation procedures.
- Define session idle timeout and absolute session lifetime.
- Define concurrent-session policy.
- Add authentication failure rate limiting.
- Add brute-force protection.
- Define account lockout and recovery behavior if persistent accounts are
  introduced.
- Define privileged-action audit requirements.
- Define login-success, login-failure and logout audit requirements.
- Define whether authentication events may safely include source-network
  metadata.
- Add role-based access control only after an approved role matrix.
- Define permissions for monitoring execution.
- Define permissions for schedule administration.
- Define permissions for report and PDF access.
- Define permissions for manual report-dispatch retry.
- Define permissions for Telegram provider health checks.
- Define permissions for security and provider configuration visibility.
- Add per-website authorization only after an ownership model is approved.
- Add organization or tenant access boundaries only after a corresponding
  domain model is approved.
- Evaluate multi-factor authentication.
- Evaluate OAuth2, OIDC or SSO.
- Evaluate API-token authentication for future non-browser clients.
- Define provider secret access rules.

Future authentication work must:

- preserve secure-by-default startup;
- preserve externally configured credentials until a replacement identity
  model is approved;
- avoid source-controlled production credentials;
- keep raw passwords and provider secrets out of logs;
- preserve CSRF protection for browser operations;
- deny access by default;
- avoid coupling authentication directly to monitoring, recommendation, PDF
  or Telegram provider services.

Future implementations must not:

- reintroduce anonymous access to application data;
- introduce a default production password;
- expose whether a username exists through authentication failure messages;
- persist raw passwords;
- disable CSRF globally to simplify controller integration;
- use the `OPERATOR` role as evidence that fine-grained RBAC already exists.

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
- Decide whether a future structured impact model is required beyond the
  completed Risk and Potential Impact Summary.
- Decide which structured impact categories may be supported if separately
  persisted impact analysis is approved.
- Decide whether structured financial, legal, regulatory or reputational
  impacts should remain excluded unless supported by a stricter validation
  policy.
- Decide whether separately persisted structured impact analysis would require
  approval, regeneration, supersession and retention rules.
- Decide whether explicitly regenerated recommendations replace, supersede or
  remain alongside previous recommendation history.
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

---

## Bilingual Reporting Follow-Up

Sprint 19 completed the bilingual monitoring-run PDF reporting baseline.

Completed behavior includes:

- explicit English and Turkish report languages;
- language-aware recommendation generation;
- language-aware recommendation persistence and lookup;
- preservation of recommendation idempotency per report language;
- separate English and Turkish PDF artifact identities;
- language-aware PDF artifact persistence and resolution;
- separate English and Turkish filenames;
- separate English and Turkish download controls;
- local Unicode-capable PDF fonts;
- verified Turkish character rendering;
- automatic Telegram dispatch of both language-specific PDF artifacts;
- migration V19 language constraints and indexes.

The following bilingual-reporting work remains deferred:

- complete deterministic Turkish localization of all persisted historical
  lifecycle prose;
- additional language support beyond English and Turkish;
- operator-selectable report-language preferences;
- per-website language configuration;
- language administration;
- generalized translation-service integration;
- translation quality review and approval;
- persisted translation provenance;
- translation versioning;
- localized date and number formatting;
- locale-specific terminology administration;
- multilingual notification-message templates;
- multilingual Telegram captions;
- automated bilingual report comparison;
- broader Turkish localization regression fixtures.

Future localization must not:

- invent evidence;
- reinterpret findings or risks;
- alter severity or trust assessments;
- translate unsupported provider claims into authoritative statements;
- change persisted source records;
- weaken recommendation validation;
- make translation failure fail a completed monitoring run;
- merge or overwrite language-specific artifact identities.

---

## Dashboard Experience Follow-Up

Sprint 20 completed the premium single-operator dashboard baseline.

Completed behavior includes:

- premium SiteSentinel product identity;
- responsive maximum-width dashboard shell;
- operational KPI cards based on existing controller data;
- Attention Required notification region;
- unread HIGH and CRITICAL event previews;
- readable monitoring-run tables;
- readable trust-assessment tables;
- notification severity and status badges;
- monitoring and trust status badges;
- long UUID layout containment;
- controlled desktop notification-table layout;
- responsive table scrolling;
- visible keyboard focus;
- accessible action-button contrast;
- reduced-motion support;
- preserved POST logout and CSRF protection.

The following dashboard work remains deferred:

- charts and historical trend visualization;
- operator-configurable widgets;
- dashboard sorting and filtering;
- server-side pagination;
- dashboard search;
- customizable dashboard layouts;
- dark mode;
- user-specific display preferences;
- persisted dashboard preferences;
- localized dashboard interface;
- relative or localized timestamp presentation;
- dashboard operational metrics;
- dashboard performance instrumentation;
- notification acknowledgement directly from the dashboard;
- bulk notification actions;
- richer monitoring-run detail previews;
- client-side live updates;
- WebSocket or server-sent event updates;
- multi-user dashboard experiences;
- role-specific dashboards;
- organization or tenant-specific dashboards.

Future dashboard work must preserve:

- the server-authoritative monitoring lifecycle;
- existing notification status semantics;
- authenticated access;
- POST and CSRF requirements for state-changing actions;
- backend ownership boundaries;
- long-identifier containment;
- keyboard accessibility;
- sufficient contrast;
- responsive behavior;
- the no-external-dependency decision unless separately approved.

Charts, live updates, new backend data and user-specific configuration require
separate future sprint approval.