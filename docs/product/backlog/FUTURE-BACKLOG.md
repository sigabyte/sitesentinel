# SiteSentinel Future Backlog

Ideas intentionally excluded from the current sprint.

These ideas have not been approved for implementation.

They exist to preserve valuable ideas without disrupting sprint discipline.

---

## FB-001

Presentation Layer abstraction

Description

Evaluate whether Reporting should eventually become part of a broader Presentation Layer.

Reason
Current architecture is sufficient.
No implementation required for Sprint 0.
Review after core architecture is complete.

---

## FB-002

Monitoring Context

Evaluate whether Website should eventually own a Monitoring Context aggregate.

Reason

Current association model is sufficient.

No architectural need exists during Sprint 0.

## FB-003

Engineering Framework

Generalize the engineering methodology developed during SiteSentinel into a
reusable framework.

Reason

Outside current product scope.

Evaluate after SiteSentinel MVP.

# FB-004

Monitoring Lifecycle Diagram

Reason: SSAS sonunda platformun uçtan uca yaşam döngüsünü gösteren tek bir
referans diyagramı eklenebilir. Bu, mevcut mimariyi değiştirmez; yalnızca 
dokümantasyonun okunabilirliğini artırır.

## FB-005

Architecture Overview Diagram

Reason: Sprint 0 tamamlandıktan sonra, SSAS'ın ilk sayfalarına tüm mimariyi tek sayfada gösteren
üst düzey bir referans diyagramı eklenebilir. Bu yalnızca dokümantasyonun okunabilirliğini artırır; 
mimariyi değiştirmez.

## FB-006

API Versioning Strategy

Reason: API versiyonlama, geriye dönük uyumluluk ve deprecations için ayrı bir mimari kararı gerektirebilir. 
Sprint 0 kapsamında gerekli değil; API tasarımı olgunlaştıktan sonra değerlendirilmeli.

## FB-007

Workspace-Oriented UI Information Architecture

Suggested Review: After MVP UI Design

## FB-008

Quality Attribute Metrics

Reason: MVP sonrasında her kalite özelliği için 
ölçülebilir hedefler (ör. response time, uptime, scan throughput) tanımlanabilir.

## Added After Sprint 1

### Completed in Sprint 2

- Added finding detail pages with linked source evidence.
- Added normalized evidence detail pages with source collected evidence and related findings.
- Added collected evidence detail pages with normalized evidence and related findings.
- Added risk detail pages with linked findings and source evidence chain.
- Added trust assessment detail pages with linked risks, findings, and evidence chain.
- Added monitoring run traceability summary dashboard.
- Added traceability QA status and coverage labels.

### Completed Sprint 3 Item

- Add scan history comparison per website.

### Completed Sprint 4 Item

- Add scheduled monitoring runs.

### Completed Sprint 5 Item

- Add browser-based monitoring run reports.

### Completed Sprint 6 Item

- Add notification event baseline.

### Completed Sprint 7 Item

- Added notification management UI.
- Added notification event list page.
- Added notification event detail page.
- Added mark notification as read/unread controls.
- Added notification filtering by severity and status.
- Added website-context notification filtering.
- Added monitoring-run-context notification filtering.
- Added notification management navigation from dashboard, website detail, 
monitoring run detail, and monitoring run report pages.

### Future Candidate Items

- Add notification delivery readiness baseline.
- Add notification delivery channel enum.
- Add notification delivery attempt entity.
- Add notification delivery attempt repository.
- Add notification delivery attempt service.
- Add simulated notification delivery attempt recording.
- Add delivery attempt visibility on notification detail pages.
- Add notification delivery for scheduled monitoring results.
- Add email notification delivery.
- Add WhatsApp notification delivery.
- Add Slack notification delivery.
- Add webhook notification delivery.
- Add user-specific notification preferences.

### Deferred Technical Hardening

- Add database-level uniqueness constraints for idempotent assessment outputs.
- Add service tests for website registration and scanner safety validation.
- Add integration tests for monitoring run execution lifecycle.
- Add test coverage for evidence analysis, risk evaluation, and trust evaluation engines.
- Add request rate limiting.
- Add scan queue controls.
- Add configurable maximum response body size.
- Add structured logging for scan execution.
- Add error classification for scanner failures.
- Add retry policy for transient scanner failures.
- Add monitoring run cancellation support.
- Add operational metrics for scan duration and failure rate.

### Deferred Product Decisions

- Define user-facing trust score explanation format.
- Define report format for business users.
- Define severity language for non-technical users.
- Decide whether trust assessments should be produced when no risks are found.
- Decide whether optional resource failures should affect trust score.
- Decide how historical trend changes should be displayed.
- Decide which scanner signals should be visible by default and which should be advanced-only.

---

## Post-Sprint 8 Backlog — Real Notification Delivery

### Status

Deferred.

### Context

Sprint 8 implemented Notification Delivery Readiness Baseline.

The system can now record simulated delivery attempts for notification events.

Real external delivery remains deferred.

### Candidate Future Sprint

Sprint 9 — Real Notification Delivery Provider Baseline

### Recommended First Provider

TELEGRAM is the recommended first real outbound delivery provider.

Reason:

- Suitable for monitoring alerts.
- Fast to test.
- Does not require email deliverability setup.
- Can be isolated through a provider interface.
- Useful for personal/admin alerting.
- Lower operational complexity than WhatsApp.
- Better real-time behavior than email for critical monitoring events.

### Future Scope Candidates

A future delivery sprint may add:

- Delivery provider interface.
- Provider configuration model.
- TELEGRAM bot configuration.
- Disabled-by-default outbound delivery.
- Safe test delivery action.
- Real delivery attempt status update.
- Provider response capture.
- Provider failure capture.
- Delivery failure visibility.
- External delivery audit trail.
- Delivery enable/disable switch.

### Deferred Provider Options

The following provider options remain deferred:

- Email provider.
- WhatsApp provider.
- Telegram provider.
- Slack provider.
- Webhook provider.

### Provider Boundary Requirement

Real provider integration must be isolated behind a provider boundary.

Controllers and notification services must not call external APIs directly.

Expected future pattern:

NotificationEvent  
↓  
NotificationDeliveryAttemptService  
↓  
NotificationDeliveryProvider  
↓  
Specific Provider Adapter  
↓  
External API

### Safety Boundary

Real delivery must be disabled by default.

A future sprint must not accidentally send outbound messages during normal local testing.

Provider calls should require explicit configuration and explicit user action first.

### Out of Scope Until Explicitly Started

The following remain out of scope until a real delivery sprint starts:

- Production Telegram bot delivery.
- Production email delivery.
- Production WhatsApp delivery.
- Production Slack delivery.
- Production webhook delivery.
- Recipient preference management.
- User subscription rules.
- Retry scheduler.
- Escalation policies.
- AI-generated notification messages.