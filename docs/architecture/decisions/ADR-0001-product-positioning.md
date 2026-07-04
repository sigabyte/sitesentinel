# ADR-0001

## Product Positioning

**Document Type:** Architecture Decision Record

**Document ID:** ADR-0001

**Status:** Accepted

**Version:** 1.0

**Owner:** Product Owner

**Date:** 2026-07-04

---

# Context

The product requires a clear long-term positioning before technical architecture is defined.

Without a stable positioning, future architectural decisions may become inconsistent as the platform evolves.

---

# Decision

SiteSentinel is positioned as a **Website Trust Platform**, not simply a website scanner.

The platform continuously evaluates the integrity and trustworthiness of websites by combining deterministic analysis, rule-based detection, risk assessment, and explainable reporting.

---

# Rationale

Traditional scanners generate isolated technical findings.

SiteSentinel aims to answer a broader business question:

> **Can this website be trusted today?**

This positioning better reflects the long-term vision of the product and supports future expansion without changing the core identity.

---

# Consequences

Positive

- Stable long-term product identity.
- Supports future platform expansion.
- Allows additional engines without changing product positioning.
- Focuses development around customer value rather than technical features.

Trade-offs

- Increased architectural complexity.
- Requires a central Trust Engine.
- Requires consistent trust evaluation methodology.

---

# Related Documents

- PRODUCT-VISION.md
- ENGINEERING-CONSTITUTION.md
- SSAS-v1.0.md