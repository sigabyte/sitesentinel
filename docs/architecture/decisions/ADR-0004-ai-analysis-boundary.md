# ADR-0004

## AI Analysis Boundary

**Document Type:** Architecture Decision Record

**Document ID:** ADR-0004

**Status:** Accepted

**Version:** 1.0

**Owner:** Product Owner

**Date:** 2026-07-04

---

# Context

Artificial Intelligence provides significant value in interpreting complex technical findings and assisting users in understanding security and integrity issues.

However, AI-generated conclusions are probabilistic and should not replace deterministic evidence when evaluating website integrity.

SiteSentinel requires a clear architectural boundary between deterministic detection and AI-assisted interpretation.

---

# Decision

Artificial Intelligence is positioned as an analysis and explanation layer.

AI does not perform authoritative detection.

AI does not determine trust.

AI does not generate evidence.

Instead, AI consumes verified evidence produced by the platform and provides:

- Interpretation
- Context
- Root cause analysis
- Human-readable explanations
- Recommended actions

---

# Rationale

Deterministic systems produce repeatable and verifiable results.

AI enhances usability by helping users understand those results.

Separating detection from interpretation improves:

- Trustworthiness
- Explainability
- Predictability
- Auditability
- Regulatory compliance

This separation also reduces the risk of unsupported or hallucinated conclusions.

---

# Consequences

## Positive

- Clear architectural responsibility
- Explainable findings
- Improved user understanding
- Reduced false confidence
- Easier auditing

## Trade-offs

- Additional processing layer
- Increased architectural complexity
- Requires well-defined evidence models

These trade-offs are accepted because they reinforce the long-term trust objectives of the platform.

---

# Related Documents

- ENGINEERING-CONSTITUTION.md
- PRODUCT-VISION.md
- SSAS-v1.0.md