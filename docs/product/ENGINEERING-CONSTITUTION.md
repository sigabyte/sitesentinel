# SiteSentinel Engineering Constitution
> **Engineering Constitution**
>
> This document defines the non-negotiable engineering principles of the SiteSentinel platform.
>
> All architectural, technical, and implementation decisions are expected to align with this constitution.

> **Engineering principles that govern the design, development, and long-term evolution of the SiteSentinel platform.**

---

# Purpose

The Engineering Constitution defines the fundamental principles that guide every architectural, engineering, and product decision within SiteSentinel.

Unlike implementation details, these principles are intended to remain stable throughout the lifetime of the product.

Every contributor is expected to understand and follow this constitution before contributing to the project.

---

# Article 0 — Product Ownership

The Product Owner is the final authority on all product-related decisions.

The Product Owner defines:

- Product vision
- Business direction
- Customer value
- Product priorities
- Scope
- Release strategy
- Commercial positioning

Technical recommendations, architectural guidance, engineering proposals, and implementation strategies exist to support the Product Owner in making informed decisions.

Engineering is expected to challenge assumptions, identify risks, and present alternatives.

However, final product decisions always belong to the Product Owner.

---

# Article 1 — Product First

Every engineering decision must support customer value.

Technology exists to enable the product.

The product does not exist to demonstrate technology.

---

# Article 2 — Architecture Before Code

Major implementation begins only after architectural approval.

Architecture is considered a prerequisite for implementation, not documentation produced afterward.

---

# Article 3 — Single Source of Truth

SiteSentinel maintains three official sources of project knowledge.

Architecture

- SSAS (SiteSentinel Software Architecture Specification)

Architecture Decisions

- ADR (Architecture Decision Records)

Engineering Knowledge

- Engineering Journal

Chat discussions are never considered the permanent source of truth.

---

# Article 4 — Explainability

Every conclusion produced by SiteSentinel must be explainable.

Users should always understand:

- why something was detected
- how the conclusion was reached
- which evidence supports it
- how confidence was calculated

Black-box conclusions are not acceptable.

---

# Article 5 — Deterministic Before AI

Deterministic analysis is the primary source of truth.

Artificial Intelligence assists interpretation.

Artificial Intelligence never replaces deterministic evidence.

AI should explain evidence.

AI should not invent evidence.

---

# Article 6 — Trust Is Earned

SiteSentinel must never exaggerate confidence.

If uncertainty exists, the platform communicates uncertainty clearly.

Example:

❌ Your website is completely safe.

✔ No evidence of malware or SEO spam was detected during the latest scan.

Confidence: High.

Trust is earned through transparency.

---

# Article 7 — Long-Term Maintainability

Short-term convenience must never compromise long-term maintainability.

Temporary solutions should never become permanent architecture.

Technical debt must be intentional, documented, and reviewed.

---

# Article 8 — Product Over Features

Features are not success.

Solving customer problems is success.

Every feature should answer at least one of the following questions:

- Does it improve trust?
- Does it improve usability?
- Does it reduce customer risk?
- Does it strengthen product differentiation?

If not, it should be reconsidered.

---

# Article 9 — Documentation Is Part of Development

Implementation is not complete until documentation has been updated.

Documentation evolves together with the software.

Every major architectural decision must be documented.

---

# Article 10 — Continuous Improvement

Refactoring is encouraged.

However, refactoring should be driven by:

- evolving requirements
- measurable improvements
- architectural simplification

—not by avoidable design shortcuts.

---

# Article 11 — Constructive Challenge

Engineering excellence requires constructive disagreement.

Every contributor is encouraged to question assumptions, designs, and decisions.

Disagreement is considered part of engineering—not conflict.

Ideas compete.

People collaborate.

---

# Article 12 — Commercial Value

Technology is not the product.

Customer value is the product.

Every engineering effort should contribute to at least one of the following:

- measurable customer value
- commercial viability
- operational efficiency
- competitive advantage
- strategic differentiation

---

# Article 13 — Reputation Over Speed

The reputation of SiteSentinel is more valuable than rapid feature delivery.

When quality and speed conflict:

Quality wins.

When certainty and assumptions conflict:

Evidence wins.

When transparency and convenience conflict:

Transparency wins.

---

# Engineering Principles

The engineering culture of SiteSentinel is based on the following principles.

- Simplicity before complexity
- Modularity before coupling
- Explicitness before assumptions
- Reliability before speed
- Explainability before automation
- Prevention before recovery
- Continuous monitoring before reactive investigation
- Product thinking before feature thinking
- Trust before marketing

---

# Working Discipline

Every major feature follows the same lifecycle.

```
Idea
    ↓
Discussion
    ↓
Architecture Review
    ↓
ADR (if required)
    ↓
SSAS Update
    ↓
Implementation
    ↓
Documentation Update
    ↓
Testing
    ↓
Release
```

Implementation should never skip architecture.

---

# Decision Quality

Before approving any major technical decision, the following question should always be asked:

> **Would we still consider this the correct architectural decision three years from today?**

If the answer is uncertain, additional design work is encouraged before implementation.

---

# Amendment Policy

The Engineering Constitution is intended to remain stable.

Changes require explicit discussion and approval by the Product Owner.

Constitutional changes should improve the long-term direction of SiteSentinel rather than solve short-term implementation problems.

---

# Closing Statement

SiteSentinel is built on the belief that trust cannot be assumed.

Trust must be continuously measured, verified, explained, and protected.

Every architectural decision, every line of code, and every product feature should strengthen that mission.

> **Because trust should be monitored, not assumed.**