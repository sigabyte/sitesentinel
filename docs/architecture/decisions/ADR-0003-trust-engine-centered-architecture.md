# ADR-0003

## Trust Engine Centered Architecture

**Document Type:** Architecture Decision Record

**Document ID:** ADR-0003

**Status:** Accepted

**Version:** 1.0

**Owner:** Product Owner

**Date:** 2026-07-04

---

# Context

SiteSentinel is expected to evolve beyond external website scanning.

Future versions will incorporate multiple sources of evidence, including website scanning, authenticated agents, server monitoring, reputation services, and third-party integrations.

The platform requires a central architectural component capable of consolidating evidence from multiple sources into a single, explainable assessment.

---

# Decision

SiteSentinel adopts a Trust Engine centered architecture.

All evidence collected throughout the platform will ultimately contribute to a unified trust assessment.

Individual components such as scanners, agents, integrations, and analysis services exist to provide evidence.

The Trust Engine is responsible for evaluating that evidence and producing the platform's overall trust assessment.

---

# Rationale

A scanner alone cannot represent the integrity of an entire website.

Website trust is determined through the combination of multiple independent observations.

By separating evidence collection from trust evaluation, SiteSentinel gains:

- Architectural flexibility
- Technology independence
- Consistent trust evaluation
- Explainable decision making
- Easier future expansion

This approach allows new evidence sources to be introduced without redesigning the platform.

---

# Consequences

## Positive

- Stable long-term architecture
- Clear separation of responsibilities
- Supports future platform growth
- Enables multiple evidence sources
- Consistent trust evaluation

## Trade-offs

- Additional architectural complexity
- Requires a formal trust evaluation model
- Requires clearly defined evidence normalization

These trade-offs are accepted because they support the long-term product vision.

---

# Related Documents

- PRODUCT-VISION.md
- SSAS-v1.0.md
- ADR-0001 Product Positioning