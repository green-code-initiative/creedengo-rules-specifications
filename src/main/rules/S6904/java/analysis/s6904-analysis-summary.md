# PR description — closing #122, #125, #155, #325 in favour of built-in S6904

*Prepared 6 August 2026. Supersedes the earlier draft
`level2-energytracer/campaign1/fetchtype-pr-description.md`, which proposed adding a new
rule under the identifier S6904 before it was established that identifier already belongs
to a SonarQube built-in rule.*

Two blocks:

- **A** — internal record 
- **B** — summary for PR description

---
---

# A — Internal record

## What happened

`#122`, `#125`, `#155` and `#325` each proposed, at different points since January 2024, a
custom rule flagging `FetchType.EAGER` on JPA collection associations. The discussion
stalled every time for lack of measured impact — nobody had shown the smell actually cost
anything. That gap is what the `S6904/java/analysis` harness (this directory) was built to
close: a two-level benchmark (JPA/Hibernate functional cost, then EnergyTracer hardware
energy measurement) run twice, once on 4 August (three sizes) and once on 5 August (six
sizes, refining the first run).

**S6904 already exists as a SonarQube built-in rule** covering all the use case.

## Why keep the analysis

The measurement work answers, with real hardware data, the exact
question that stalled the four PRs for two years: does this pattern actually matter? Yes,
measurably, at every collection size tested (10 to 1000 elements). That evidence is useful
independently of whether a new rule ships:

- it justifies keeping S6904 enabled (and at what severity) in the quality profiles this
  project uses, rather than treating it as noise;
- it settles, for good, the recurring question of whether EAGER-collection flagging is
  worth the false-positive risk on very small collections (answer: gain is present even
  at N=10, but its absolute weight only becomes material from roughly a hundred elements
  up — see the two interpretation documents for the nuance);
- it is the only rigorous, phase-level, hardware-measured analysis of this specific smell
  that exists in this repository's history, and is worth preserving regardless of the
  rule's provenance.

## Where the analysis lives

- `level2-energytracer/campaign1/campaign1-interpretation.md` — first campaign (sizes 10,
  100, 1000; 30 phases × 30 iterations per size).
- `level2-energytracer/campaign2/campaign2-interpretation.md` — second campaign (sizes 10,
  30, 50, 100, 500, 1000; same protocol), run to refine the first campaign's open questions
  and locate the relevance threshold more precisely. Confirms campaign 1 and adds one
  honestly-disclosed complication (a local anomaly around N=50–100 — see that document's
  §4–§5).
- `level1-jpa/`, `level2-energytracer/` — the harness itself (build/run/aggregate scripts),
  documented in `README.md` at the root of this directory.

---
---

# B — Summary for PR description

```markdown
## Closing in favour of the built-in rule

This PR closes #122, #125, #155 and #325 without merging any of them.

All four proposed a custom rule flagging `FetchType.EAGER` on JPA collection associations
that are never read. That discussion stalled repeatedly since January 2024 for lack of
measured impact. To settle it, a two-level measurement harness was built and run twice —
see below.

**S6904 already exists as a SonarQube built-in rule** covering all the use case. Adding a new custom rule under covering the same use case is not viable, so these four PRs are being closed as duplicates rather than merged.

### The measurement work is kept, and here is what it shows

Even though no new rule ships from this work, the two measurement campaigns are a direct,
hardware-measured answer to the question that blocked this discussion for two years: does
`FetchType.EAGER` on an unused collection actually cost anything measurable? Full detail in
the two linked documents; condensed here.

**Functional cost** (Hibernate 6.4.4 + H2, real JPA stack, single run per size):

| Collection size | Extra rows hydrated | Time | Heap allocation | SQL statements |
|---:|---:|---:|---:|---:|
| 10 | +10 | x1.81 | x1.38 | unchanged |
| 100 | +100 | x3.73 | x4.37 | unchanged |
| 1000 | +1000 | x12.65 | x32.09 | unchanged |

The statement count never changes — Hibernate resolves the eager collection with a join, so
this is a payload/hydration cost, not an N+1 problem.

**Energy cost** (EnergyTracer, Apple Silicon hardware counters, 900 runs per variant per
size, two independent campaigns covering six distinct collection sizes from 10 to 1000 —
three of them, 10/100/1000, measured in both campaigns):

| Collection size | Relative energy overhead | Measured in | Sign test across 30 phases |
|---:|---:|---|:---:|
| 10 | 81.0–81.3% | campaigns 1 and 2 | p ≈ 1.9×10⁻⁹ |
| 30 | 83.9% | campaign 2 | p ≈ 1.9×10⁻⁹ |
| 50 | 85.8% | campaign 2 | p ≈ 1.9×10⁻⁹ |
| 100 | 86.3–86.7% | campaigns 1 and 2 | p ≈ 1.9×10⁻⁹ |
| 500 | 87.1% | campaign 2 | p ≈ 1.9×10⁻⁹ |
| 1000 | 90.1–90.7% | campaigns 1 and 2 | p ≈ 1.9×10⁻⁹ |

Every one of the 30 measurement phases at every size points the same way, at both hardware
components (CPU, DRAM) and wall time, with strictly disjoint EAGER/LAZY distributions —
this is not a borderline effect. The relative overhead is already large at the smallest
size tested (10 elements) and climbs to 90%+ at 1000; the second campaign also located a
genuine, honestly-disclosed anomaly in the fine-grained scaling curve around 50–100
elements that does not change this conclusion (see campaign 2's document, §4–§5).

Reproducibility across the two independent measurement sessions (different days) is
strong: the relative overhead never moved by more than 0.6 percentage points at the three
sizes tested in both campaigns, even though absolute joule readings drifted by up to 16%
between sessions — which is why every figure above is a relative, paired comparison rather
than a raw energy total.

### Full analysis

- [First campaign — sizes 10, 100, 1000](<link to
  level2-energytracer/campaign1/campaign1-interpretation.md>)
- [Second campaign — sizes 10, 30, 50, 100, 500, 1000](<link to
  level2-energytracer/campaign2/campaign2-interpretation.md>)
- [Harness and instructions to reproduce](<link to README.md>)

### Suggested follow-up

- Confirm S6904 is enabled (and at an appropriate severity) in this project's active
  quality profile(s) — the measured impact supports treating it as more than cosmetic.
- No action needed on the four closed PRs beyond closing them; this analysis is the
  record of why.
```

---

## Before posting

- [ ] Fill in the `<link to ...>` placeholders once this branch/PR exists
