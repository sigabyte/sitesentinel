# ADR-0005

## Website Health and Active Problem Detection Architecture

**Document Type:** Architecture Decision Record

**Document ID:** ADR-0005

**Status:** Accepted

**Version:** 1.0

**Owner:** Product Owner

**Date:** 2026-08-21

---

# Context

SiteSentinel currently collects deterministic website evidence, derives findings from that evidence,
evaluates the risks associated with those findings, and generates remediation recommendations.

The existing scanner primarily identifies security, integrity, configuration, availability, content-quality,
and trust-related risk signals from the monitored website's homepage and selected supporting resources.

The platform must now also detect concrete problems that are already present on the website, including broken
internal links, unavailable images and assets, problematic redirects, mixed content, robots and sitemap failures,
structural HTML problems, basic HTTP performance problems, and TLS or certificate problems.

These detected problems must remain part of the existing evidence-driven architecture.

A detected website problem must be supported by collected evidence, represented by the existing `Finding`
domain model, evaluated for potential risk where applicable, and connected to remediation and verification guidance.

Creating a separate `WebsiteIssue` model would duplicate the responsibility already owned by `Finding`, introduce
competing problem representations, and weaken traceability across the assessment pipeline.

The website health scanner must therefore extend the existing collection and analysis capabilities without
creating a parallel issue-detection pipeline.

The expanded scanner must also remain controlled and safe. Crawling and resource inspection must be same-origin,
bounded by explicit request and duration limits, resistant to Server-Side Request Forgery, and unable to expand
into unrestricted external crawling.

---

# Decision

SiteSentinel will extend its existing deterministic evidence collection and analysis pipeline to support website health and active problem detection.

The canonical processing flow remains:

`Evidence → Finding / Detected Website Problem → Risk → Recommendation`

## Finding as the Problem Representation

The existing `Finding` domain model will represent a detected website problem.

`Finding` remains the canonical domain and persistence term.

User-facing interfaces and reports may present a finding as a:

- Detected Website Problem
- Website Problem
- Problem Detected During the Scan

A separate `WebsiteIssue` entity, repository, service, or processing pipeline will not be introduced.

Each detected problem must:

- Belong to a website and monitoring run
- Use a stable finding type
- Provide a clear title and factual description
- Include a confidence score
- Be traceable to one or more collected evidence records

## Evidence-First Detection

Website problems must be derived from deterministic observations collected by SiteSentinel.

Artificial Intelligence must not perform authoritative problem detection.

AI may consume verified findings and risks to provide:

- Human-readable explanations
- Potential impact summaries
- Remediation guidance
- Verification steps

This preserves the AI boundary established by ADR-0004.

## Outcome Classification

Scanner observations must be classified according to the strength and completeness of the available evidence.

The supported outcome classifications are:

### Confirmed Problem

A concrete problem has been directly demonstrated by deterministic evidence.

Examples include:

- An internal link returns HTTP 404 or 410
- An image or required asset returns HTTP 404 or 410
- A page contains an insecure HTTP script reference
- A certificate is expired
- A declared sitemap is malformed or unavailable

A confirmed problem may produce a `Finding` with high confidence.

### Suspected Problem

Available evidence indicates a likely problem, but the scanner could not establish it conclusively.

Examples include:

- A resource repeatedly times out
- A page or asset connection is terminated before a valid response is received
- A basic performance threshold is exceeded during a single monitoring run

A suspected problem may produce a `Finding`, but its confidence score and description must communicate the uncertainty.

### Scan Limitation

The scanner could not evaluate a target because of a safety, access, policy, or resource constraint.

Examples include:

- HTTP 401 or 403 prevents inspection
- HTTP 429 rate limiting is encountered
- robots.txt disallows crawling a path
- The scan request budget is exhausted
- The scan duration limit is reached
- A discovered URL is outside the accepted origin
- An unsafe or private network target is rejected

A scan limitation must be recorded as evidence or scan outcome metadata.

A scan limitation must not be presented as a confirmed website problem.

## Risk Separation

A `Finding` describes the concrete condition that was detected.

A `Risk` describes the potential consequence or exposure associated with that condition.

The platform must not combine the detected condition and its potential impact into a single ambiguous record.

Not every finding is required to produce a risk.

Risk generation remains policy-driven and must preserve traceability to the originating finding.

## Existing Pipeline Preservation

Website health detection will execute within the existing monitoring lifecycle:

1. Collect evidence
2. Normalize and analyze evidence
3. Create findings
4. Evaluate applicable risks
5. Calculate the trust assessment
6. Generate recommendations
7. Generate reports and notifications

A second monitoring lifecycle or parallel issue-processing pipeline will not be introduced.

---

# Controlled Crawl and Scanner Safety

Website health detection will use a controlled same-origin crawl.

The crawler is not a general-purpose web crawler and must not perform unrestricted traversal.

## Accepted Crawl Origin

The accepted crawl origin is determined from the successfully resolved final homepage URL after the permitted homepage redirect sequence completes.

An origin consists of:

- Scheme
- Normalized host
- Effective port

Default ports are normalized so that:

- `https://example.com` and `https://example.com:443` represent the same origin
- `http://example.com` and `http://example.com:80` represent the same origin

Different schemes, hosts, subdomains, or non-default ports represent different origins.

The crawler may inspect and follow only URLs within the accepted crawl origin.

A discovered cross-origin URL may be recorded as an external reference but must not be fetched as part of the controlled crawl.

## Supported Request Schemes

Only HTTP and HTTPS URLs may be considered for scanning.

The crawler must reject or ignore unsupported schemes, including:

- `file`
- `ftp`
- `data`
- `javascript`
- `mailto`
- `tel`
- `jar`
- `gopher`

Unsupported schemes must never reach the HTTP transport layer.

## SSRF Protection

The existing website target validation boundary will remain mandatory and will be extended to every network request performed by the health scanner.

SSRF validation must apply to:

- The configured website host
- The initial homepage request
- Every redirect destination
- Every crawled page
- Every inspected internal link
- Every inspected image or asset
- robots.txt
- Sitemap resources
- Any URL discovered inside a sitemap

Before a request is sent, its target host must be validated and resolved.

Private, loopback, link-local, multicast, local, internal, unspecified, and special-use network targets must be rejected unless an explicitly controlled test configuration permits private targets.

A URL that passed validation earlier must not be assumed to remain safe indefinitely. Redirect destinations and newly discovered targets must be validated immediately before their requests.

DNS resolution or redirect behavior must not allow a public host to be used as a bridge to an unsafe network target.

An unsafe target rejection is a scan limitation and must not be reported as a website defect.

## Redirect Safety

Automatic unrestricted redirect following is prohibited.

Redirect processing must remain explicit and bounded.

For every redirect hop, the scanner must:

1. Verify that the redirect status is supported
2. Read and normalize the `Location` value
3. Resolve relative redirect targets safely
4. Reject unsupported schemes
5. Apply SSRF validation to the destination
6. Enforce the configured redirect limit
7. Record the redirect hop as evidence
8. Release the previous response body safely

During homepage resolution, a safe redirect to a different public origin may be accepted so that common canonical host transitions can complete.

After the accepted crawl origin has been established, a crawled page or asset redirecting outside that origin must not be followed by the controlled crawler.

The cross-origin redirect must be recorded as an observed redirect outcome.

## URL Normalization and Deduplication

Before a discovered URL enters the crawl queue, the scanner must:

- Resolve relative URLs against the containing page
- Normalize the scheme and host
- Normalize the effective port
- Remove the fragment
- Normalize an empty path to `/`
- Reject embedded credentials
- Preserve only a valid HTTP or HTTPS target
- Verify same-origin eligibility
- Deduplicate the normalized URL

Fragment-only differences must not create separate crawl targets.

The crawler must guard against unbounded URL growth caused by query parameter variations, calendar pages, faceted navigation, session identifiers, or dynamically generated links.

## Bounded Execution

Every website health scan must operate within explicit safety limits.

The initial default limits are:

- Maximum crawled HTML pages: 25
- Maximum crawl depth: 2
- Maximum inspected assets: 100
- Maximum total health scan requests: 150
- Maximum discovered links per page: 200
- Maximum discovered assets per page: 200
- Maximum health scan duration: 60 seconds
- Maximum redirect hops: existing configured redirect limit
- Crawl concurrency: one request at a time

The limits must be externally configurable through scanner configuration.

Exhausting a page, asset, request, depth, or duration limit must produce a partial scan outcome.

A partial health scan must not automatically fail the complete monitoring run.

## Request Method Safety

The website health scanner may perform only safe retrieval operations.

Permitted methods are:

- `GET`
- `HEAD`, when used conservatively for resource inspection

The scanner must not submit forms or send state-changing methods such as:

- `POST`
- `PUT`
- `PATCH`
- `DELETE`

A failed `HEAD` request must not by itself prove that a resource is broken because some servers do not implement `HEAD` correctly.

Where required, a bounded `GET` request may be used to verify resource availability.

## Robots Policy

SiteSentinel must identify itself through its configured User-Agent.

The crawler must inspect applicable robots.txt rules before expanding the crawl.

Explicitly disallowed paths must not be crawled.

A robots.txt restriction is a scan limitation, not a website problem.

The absence of robots.txt does not by itself represent a website problem.

If robots.txt cannot be evaluated safely because of a server failure or invalid retrieval outcome, the scanner must use a conservative crawl policy and record the limitation.

---

# Website Health Detection Scope

Sprint 22 establishes the first controlled website health detection baseline.

The scanner will evaluate the following problem families.

## Internal Link Health

The scanner will inspect same-origin links discovered on crawled HTML pages.

Confirmed problems include:

- Internal target returns HTTP 404
- Internal target returns HTTP 410
- Internal target returns a persistent HTTP 5xx response
- Internal target uses a malformed URL that cannot be resolved
- Internal target redirects to a broken destination

Potential or inconclusive outcomes include:

- Connection timeout
- Connection reset
- Temporary DNS failure
- HTTP 401
- HTTP 403
- HTTP 429

HTTP 401, 403, and 429 responses must not be classified as broken links.

## Image and Asset Health

The scanner will inspect bounded references to:

- Images
- Stylesheets
- Scripts
- Favicons
- Selected media resources

Confirmed problems include:

- Required resource returns HTTP 404 or 410
- Resource URL is empty or malformed
- Resource redirect resolves to a broken destination
- Stylesheet or script returns a persistent HTTP 5xx response

The scanner must distinguish between a failed resource and a resource that could not be verified because of authorization, rate limiting, safety policy, or scan limits.

## Redirect Health

The scanner will preserve and inspect redirect chains.

Confirmed redirect problems include:

- Redirect loop
- Redirect limit exceeded
- Missing or invalid `Location` header
- Redirect to an unsupported URL scheme
- HTTPS-to-HTTP downgrade
- Redirect to a broken destination

An unnecessarily long but functional redirect chain may produce a lower-confidence or lower-severity finding.

## Mixed Content

HTTPS pages will be inspected for insecure HTTP resource references.

Mixed-content categories include:

- Scripts
- Stylesheets
- Frames
- Images
- Audio
- Video
- Other embedded resources

Active mixed content, such as scripts, stylesheets, and frames, must be treated as more serious than display or passive mixed content.

A plain HTTP hyperlink on an HTTPS page is not automatically mixed content because it is a navigation reference rather than an embedded page resource.

## Robots and Sitemap Health

The scanner will inspect robots.txt and sitemap resources conservatively.

The absence of robots.txt is not a problem by itself.

The absence of `/sitemap.xml` is not a problem by itself unless the sitemap is explicitly declared or otherwise expected from deterministic evidence.

Confirmed or suspected problems include:

- robots.txt returns a persistent server error
- robots.txt contains an unusable sitemap declaration
- A declared sitemap returns HTTP 404 or 410
- A declared sitemap returns a persistent HTTP 5xx response
- A sitemap is malformed
- A sitemap index references an unavailable child sitemap
- A sitemap contains malformed URLs
- A same-origin sitemap URL references a broken page

Sitemap traversal must remain bounded.

## HTML Structure and Accessibility Signals

Crawled HTML pages will be inspected for basic deterministic structure problems.

The initial checks include:

- Missing H1
- Multiple H1 elements
- Image without an `alt` attribute
- Image with an empty or malformed `src`
- Missing page title
- Duplicate page titles across crawled pages

An image with `alt=""` must not automatically be classified as a problem because an empty alt value may correctly identify a decorative image.

The scanner will not claim full accessibility compliance.

These checks are limited structural signals and are not a substitute for a WCAG audit.

## Basic HTTP Performance Signals

The scanner will collect basic transport and response measurements.

The initial measurements may include:

- Time until response headers are received
- Total response retrieval duration
- Response body size
- HTML document size
- Resource count
- Redirect count
- Response compression presence

Threshold-based findings may include:

- Slow server response
- Excessively large HTML document
- Excessively large asset
- Excessive page resource count
- Excessive redirect overhead
- Missing compression for an eligible large text response

Performance thresholds must be configurable.

A single HTTP observation must not be presented as a Core Web Vitals result or a complete user-experience assessment.

Performance findings should generally use lower confidence than deterministic HTTP status, mixed-content, or certificate findings.

## TLS and Certificate Health

HTTPS connections will be inspected for available TLS and certificate evidence.

The initial checks include:

- TLS handshake failure
- Certificate hostname mismatch
- Expired certificate
- Certificate not yet valid
- Certificate approaching expiry
- Untrusted certificate chain
- HTTPS unavailable
- Negotiated TLS protocol
- Available certificate validity dates

Certificate expiry findings will use configurable thresholds.

The initial default interpretation is:

- Already expired: high or critical impact
- Expires within 7 days: high
- Expires within 30 days: medium
- Expires within 60 days: low or informational

TLS inspection must record factual certificate and connection evidence where available.

A generic transport exception must not be converted into a precise certificate claim unless the underlying cause supports that conclusion.

## Out of Scope

The following capabilities are outside the Sprint 22 baseline:

- JavaScript-rendered crawling
- Headless browser execution
- Lighthouse execution
- Core Web Vitals measurement
- Authenticated crawling
- Form submission
- State-changing HTTP requests
- External website crawling
- Vulnerability exploitation
- Directory brute forcing
- Port scanning
- CAPTCHA bypass
- Unlimited sitemap traversal
- Full WCAG compliance auditing
- Full SEO auditing
- Distributed or highly concurrent crawling

---

# Component Responsibilities and Integration

Website health detection will extend the current modular monolith without introducing a separate application or processing service.

## Monitoring Lifecycle

`MonitoringExecutionService` will preserve the existing lifecycle order:

1. Create and start the monitoring run
2. Collect evidence
3. Analyze evidence and create findings
4. Evaluate risks
5. Calculate the trust assessment
6. Complete the monitoring run
7. Generate recommendations
8. Generate and dispatch reports
9. Generate notifications

Website health collection must complete before evidence analysis begins.

The monitoring lifecycle must not contain a second problem-detection branch outside the existing evidence analysis stage.

## Evidence Collection Boundary

`HttpEvidenceCollectionEngine` remains the top-level HTTP evidence collection implementation used by the monitoring lifecycle.

It may coordinate:

- Homepage collection
- Optional robots.txt collection
- Optional sitemap collection
- Website health scan orchestration

It must not accumulate every crawl, resource, redirect, performance, and TLS responsibility inside one class.

Website health responsibilities will be separated into focused collaborators under the evidence collection boundary.

The intended responsibility groups are:

### Website Health Scan Orchestration

Responsible for:

- Starting the health scan from the accepted homepage
- Maintaining the scan scope
- Applying scan budgets
- Coordinating page and resource inspection
- Producing a complete or partial scan outcome

### Safe HTTP Resource Retrieval

Responsible for:

- Building safe HTTP requests
- Applying timeouts and the configured User-Agent
- Enforcing supported schemes
- Applying SSRF validation
- Processing redirects explicitly
- Preserving redirect trace information
- Safely releasing response bodies

Existing adaptive response storage and streaming analysis capabilities must be reused where applicable.

The health scanner must not reintroduce unbounded in-memory response collection or silent response truncation.

### Controlled Same-Origin Crawling

Responsible for:

- Maintaining the crawl queue
- Tracking crawl depth
- Normalizing discovered URLs
- Deduplicating visited URLs
- Enforcing the accepted origin
- Respecting robots rules
- Stopping when scan limits are reached

### HTML Resource Discovery

Responsible for extracting bounded references to:

- Internal links
- Images
- Scripts
- Stylesheets
- Frames
- Selected media
- H1 elements
- Image alt attributes
- Sitemap declarations where applicable

HTML extraction must not execute scripts.

### TLS and Certificate Inspection

Responsible for:

- Reading available TLS session information
- Extracting certificate validity and identity evidence
- Classifying only supported TLS outcomes
- Avoiding unsupported conclusions from generic connection failures

### Evidence Recording

Website health components must write observations through the existing evidence service boundary.

They must not write findings or risks directly.

Collection components produce evidence.

Analysis components produce findings.

Risk evaluation components produce risks.

## Evidence Analysis Boundary

`RuleBasedEvidenceAnalysisEngine` will remain the evidence analysis entry point.

Website health analysis rules must be separated from the existing homepage analysis rules when their growth would make the current class difficult to maintain.

A dedicated website health evidence analyzer may be introduced to:

- Read website health evidence for a monitoring run
- Correlate related evidence records
- Classify confirmed, suspected, and limited outcomes
- Create findings through `FindingService`
- Link findings to supporting evidence

The analyzer must not call an AI provider.

## Risk Evaluation Boundary

`RuleBasedRiskEvaluationEngine` remains the risk evaluation entry point.

Website health finding-to-risk mappings may be maintained in a dedicated rule catalog or policy component.

The risk evaluator must:

- Use stable finding types
- Produce a risk only where a meaningful potential impact exists
- Preserve the finding-to-risk relationship
- Avoid producing duplicate risks for the same logical problem
- Keep risk severity distinct from finding confidence

A high-confidence finding does not automatically require a high-severity risk.

## Trust Assessment Boundary

Existing trust assessment behavior will remain in place.

New website health risks may contribute to the trust assessment through the existing risk-based evaluation process.

Changes to trust penalties must be explicit, tested, and conservative.

Scan limitations must not reduce the trust score as though they were confirmed website problems.

## Recommendation and Reporting Boundary

Recommendations will continue to be generated from persisted risks.

Recommendation context may include the originating detected problem and its evidence.

Reports and UI views may display `Finding` records under the user-facing label `Detected Website Problems`.

The underlying entity and traceability relationships remain unchanged.

## Persistence Strategy

Sprint 22 will reuse the existing tables where their current responsibilities are sufficient:

- `collected_evidence`
- `normalized_evidence`
- `findings`
- `finding_evidence`
- `risks`
- `risk_findings`

A database migration will be introduced only when a required capability cannot be represented safely by the existing schema.

The architecture does not require a `website_issues` table.

## Stable Type Vocabulary

New evidence, finding, and risk type values must use stable uppercase identifiers.

Examples include:

- `INTERNAL_LINK_HTTP_STATUS`
- `ASSET_HTTP_STATUS`
- `REDIRECT_HOP`
- `MIXED_CONTENT_REFERENCE`
- `TLS_CERTIFICATE_EXPIRY`
- `BROKEN_INTERNAL_LINK`
- `BROKEN_IMAGE_ASSET`
- `HTTPS_TO_HTTP_REDIRECT`
- `EXPIRED_TLS_CERTIFICATE`
- `WEBSITE_NAVIGATION_RISK`
- `RESOURCE_AVAILABILITY_RISK`
- `TRANSPORT_SECURITY_RISK`

User-facing text must not be used as the stored type identifier.

Type identifiers must remain stable even when UI wording or report localization changes.

---

# Rationale

Reusing `Finding` as the canonical representation of a detected website problem preserves the existing evidence-driven architecture and avoids duplicate domain concepts.

The distinction between a concrete finding and its potential risk improves clarity:

- Evidence records what was observed
- A finding records the problem demonstrated by that evidence
- A risk records what the problem may cause
- A recommendation explains how to remediate and verify the condition

A controlled same-origin crawl expands detection coverage while keeping scanner behavior predictable, auditable, and safe.

Explicit limits protect SiteSentinel and monitored websites from:

- Unbounded traversal
- Excessive request volume
- Dynamically generated URL explosions
- Redirect loops
- Resource exhaustion
- Accidental external crawling
- Unsafe internal network access

Separating health scan responsibilities into focused collection and analysis components prevents the existing HTTP collection engine and rule-based analysis engine from becoming unmaintainable.

Maintaining deterministic detection also preserves the AI analysis boundary and ensures that every authoritative problem can be traced to collected evidence.

---

# Consequences

## Positive

- Concrete website problems can be detected and explained
- Existing evidence-to-finding traceability is preserved
- No duplicate issue model is introduced
- Risk remains separate from the detected condition
- False positives are reduced through explicit outcome classification
- Crawl behavior remains bounded and auditable
- SSRF protection applies throughout the expanded scan surface
- Existing adaptive response streaming can be reused
- Existing recommendation, reporting, and notification flows remain applicable
- Future health checks can be added without redesigning the monitoring lifecycle

## Trade-offs

- Evidence collection will become more complex
- Monitoring runs will perform more network requests
- Scan duration may increase
- New scan limits and configuration properties will require testing
- Redirect, robots, sitemap, and URL normalization behavior require careful policy design
- Some conditions will remain inconclusive because safe scanning takes priority over exhaustive scanning
- Sequential crawling will be slower than concurrent crawling
- Basic HTTP measurements cannot represent full browser performance
- TLS evidence may vary according to the transport information exposed by the runtime

These trade-offs are accepted because they preserve safety, traceability, and deterministic behavior.

## Operational Consequences

Website owners may observe multiple safe GET or HEAD requests from the configured SiteSentinel User-Agent during a monitoring run.

The scanner must stop expanding its crawl when configured budgets are reached.

A partial health scan may still allow the monitoring run to complete successfully.

Reports must distinguish between:

- Confirmed problems
- Suspected problems
- Scan limitations
- Checks not performed

Monitoring and diagnostic logs must not expose secrets, credentials, private certificate material, or unsafe raw exception details.

## Migration Consequences

No `WebsiteIssue` table will be created.

Existing evidence, finding, risk, and relationship tables will be reused where possible.

Any future migration must be justified by a specific persistence requirement and must not duplicate the responsibility of `Finding`.

---

# Alternatives Considered

## Separate WebsiteIssue Domain Model

A separate `WebsiteIssue` entity and table were considered.

This alternative was rejected because it would:

- Duplicate the role of `Finding`
- Create competing problem representations
- Require parallel repositories and services
- Complicate reports and recommendations
- Weaken evidence-to-risk traceability
- Require unnecessary migration and lifecycle rules

## Direct Problem Creation During Crawling

Creating findings directly from crawler components was considered.

This alternative was rejected because it would bypass the evidence analysis boundary and combine collection with interpretation.

Collection components must record observations first.

## AI-Based Problem Detection

Using AI to inspect website content and authoritatively determine problems was considered.

This alternative was rejected because AI output is probabilistic and cannot replace deterministic evidence.

AI remains an explanation and recommendation layer.

## Unrestricted Site Crawling

Crawling every discovered internal and external URL was considered.

This alternative was rejected because it would create uncontrolled execution time, request volume, safety risks, and unpredictable monitoring behavior.

## Headless Browser Baseline

Using a headless browser or Lighthouse as the initial health detection foundation was considered.

This alternative was deferred because it would significantly increase runtime complexity, resource consumption, execution variability, and operational dependencies.

A browser-based scanner may be evaluated in a future sprint as a separate controlled capability.

## Concurrent Crawl Baseline

Parallel resource inspection was considered.

This alternative was deferred for the initial implementation.

Sequential crawling provides simpler budgeting, deterministic tests, lower load on monitored websites, and clearer failure analysis.

---

# Related Documents

- ADR-0002 Agentless V1
- ADR-0003 Trust Engine Centered Architecture
- ADR-0004 AI Analysis Boundary
- SSAS-v1.0.md
- ENGINEERING-CONSTITUTION.md
- ENGINEERING-OS.md
- PRODUCT-VISION.md
- ARCHITECTURE-REVIEW.md
- FUTURE-BACKLOG.md