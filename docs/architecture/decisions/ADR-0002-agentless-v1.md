# ADR-0002

## V1 Agentless Architecture

**Document Type:** Architecture Decision Record

**Document ID:** ADR-0002

**Status:** Accepted

**Version:** 1.0

**Owner:** Product Owner

**Date:** 2026-07-04

---

# Context

SiteSentinel requires a practical and low-friction entry point for users.

Requiring server credentials, WordPress administrator access, or hosting integration during initial adoption would significantly increase onboarding complexity and reduce accessibility.

The first version of the platform should maximize ease of adoption while still delivering meaningful value.

---

# Decision

Version 1 of SiteSentinel will operate as an **Agentless External Monitoring Platform**.

Users will only be required to provide a website domain.

No server credentials, hosting credentials, WordPress administrator credentials, or software installation will be required.

---

# Rationale

An agentless approach provides:

- Simple onboarding
- Lower adoption barrier
- Faster deployment
- Reduced security concerns
- Platform independence

This approach allows SiteSentinel to deliver immediate value while establishing a strong foundation for future authenticated monitoring capabilities.

---

# Consequences

## Positive

- Extremely simple setup
- Supports virtually any public website
- No software installation required
- Lower customer trust barrier
- Faster MVP delivery

## Trade-offs

- Limited visibility into internal website components
- Cannot inspect protected server resources
- Cannot analyze databases or private files
- Certain classes of threats remain outside the scope of V1

These limitations are accepted as part of the Version 1 product strategy.

---

# Related Documents

- PRODUCT-VISION.md
- SSAS-v1.0.md