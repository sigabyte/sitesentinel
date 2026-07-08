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

### Future Candidate Items

- Add manual re-run controls for specific assessment stages.
- Add scheduled monitoring runs.
- Add report generation for completed monitoring runs.
- Add CSV export for assessment output.
- Add PDF export for completed monitoring reports.
- Add authentication and basic user access control.
- Add advanced HTTP scanner signals.
- Add link extraction baseline.
- Add external resource inventory baseline.
- Add script and third-party domain evidence collection.
- Add suspicious redirect chain analysis.
- Add DNS evidence collection.
- Add TLS evidence collection.
- Add notification hooks for high-risk trust assessments.

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