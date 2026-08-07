# PR #487 description — corrected against the actual current file changes

*Reworked 6 August 2026. The previous version of this file corrected the "closing as
duplicate" framing but still reused the **Context**/**What this PR contains** text as
published at the top of [PR #487](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/487)'s
conversation. That text describes the PR's *first* commit (`d111698`, "GCI116 - first init
from other PRs") — it was written before three more commits pivoted the approach. Checked
against the actual current file contents at the latest commit (`d92dbf7`) rather than the
stale top-of-page description:*

- `CHANGELOG.md` (raw, current): adds — under `[Unreleased]` / `Added` — *"[#487] Add
  complete analysis for FetchType issue for Java (sonarqube built-in rule S6904)"*.
- `RULES.md` (raw, current): the row for this rule still uses the **legacy key `CRJVM205`**
  and now reads *"The rule already exists inside SonarQube : built-in rule 'java:S6904'"*,
  with its Reference/Validation link pointing at PR #487.
- `src/main/rules/S6904/java/S6904.asciidoc` (new file, 106 lines, fully present in this
  PR): opens with *"== Why is this rule isn't a 'GCIxxx' rule ? Because the rule already
  exists as built-in rule inside SonarQube with number S6904. Some works were done by
  different people. To not loose this work, we created this documentation to keep it."*

**No `GCI116.json` or `GCI116.asciidoc` file exists in the current changeset.** That plan
was abandoned after the first commit. **"contenu" (What this PR contains) in the previous
draft was wrong** — it listed the GCI116 files from the abandoned plan. Fixed below.

Two blocks:

- **A** — internal record of everything changed in this pass (What this PR contains,
  Context, and the removal of Implementation and Open question)
- **B** — the full corrected PR description, paste as is over the current one on #487

---
---

# A — Internal record

## What is fixed here

- **"What this PR contains"** — rewritten to match the real current file list (`RULES.md`,
  `CHANGELOG.md`, `S6904.asciidoc`, and the analysis harness/campaigns), dropping the
  `GCI116.json` / `GCI116.asciidoc` bullets that no longer correspond to anything in the PR.
- **"Context"** — last sentence rewritten: no longer claims the PR "consolidates them into
  a single specification... under the key `GCI116`." Now states the rule already exists as
  the built-in `java:S6904`, that no new specification is added, and that `RULES.md`'s
  `CRJVM205` entry is updated to point at the built-in rule instead — matching what
  `RULES.md` and `CHANGELOG.md` actually say.
- **"Implementation"** — removed. It assumed a new rule was being implemented in
  `creedengo-java`; that doesn't apply once the rule is recognised as already built into
  SonarQube's engine.
- **"Open question"** — removed. It framed the 2024-01-01 core-team question as resolved by
  *narrowing the scope*; the real resolution is that the rule already exists as a built-in,
  which made the scope question moot rather than answered.
- The **"Measured impact"** section's forward reference to "see Open question below" was
  removed along with that section, and reworded to stand on its own.

---
---

# B — Corrected PR description, paste as is

```markdown
Supersedes PRs #122, #125, #155, #325 and resolves #98, #116, #118

### Context

Four PRs have proposed this rule since April 2023 (#122, #125, #155, #325). None could be merged:
three of them target the old `java-plugin/` layout that no longer exists in this repository, and
they use four different rule keys (`EC80`, `CRJVM205`, `EC_CRJVM205`, `EC205`). On review, the rule
already exists inside SonarQube as the built-in rule `java:S6904`. This PR does not add a new
specification: it keeps the `CRJVM205` entry in `RULES.md`, updates it to point at the built-in
rule, and preserves the work already done as supporting analysis rather than letting it go to
waste — see **Measured impact** below.

### What this PR contains

- `RULES.md` — updates the `CRJVM205` row: instead of a new rule entry, it now states the rule
  already exists inside SonarQube as the built-in rule `java:S6904`, with the Reference/Validation
  link pointing at this PR.
- `CHANGELOG.md` — entry under `[Unreleased]` / `Added`: "Add complete analysis for FetchType issue
  for Java (sonarqube built-in rule S6904)".
- `src/main/rules/S6904/java/S6904.asciidoc` — documentation explaining why this isn't a new
  `GCIxxx` rule, plus the standard "why is this an issue," compliant/noncompliant examples,
  exceptions and resources sections, and a pointer to the full measurement analysis.
- `src/main/rules/S6904/java/analysis/` — the two-level measurement harness (JPA/Hibernate
  functional cost, then EnergyTracer hardware energy measurement) and two independent measurement
  campaigns, added to answer the impact question the four superseded PRs left open. See
  **Measured impact** below.

### Scope decision

The rule targets **collection associations only** (`@OneToMany`, `@ManyToMany`) and **only when
`fetch = FetchType.EAGER` is set explicitly**. Omitting `fetch` on those annotations is compliant,
since `LAZY` is already the JPA default — PR #155 reported those cases, which would have produced
false positives on the most common mapping in the domain.

Single-valued associations (`@ManyToOne`, `@OneToOne`) are explicitly out of scope: their cost
profile is different and their real impact belongs to the N+1 selects problem (`CRJVM206`).

### Measured impact

None of the four superseded PRs provided any measurement of the actual cost — that gap is what
stalled the core-team discussion since April 2023. Two independent campaigns close
it, using a real Hibernate stack for functional cost and EnergyTracer's hardware counters
(Apple Silicon CPU/GPU/DRAM) for energy, 900 runs per variant per size across 30 measurement
phases.

**Functional cost** (Hibernate 6.4.4 + H2, single run per size):

| Children collection size | Extra rows hydrated | Time | Heap allocation | SQL statements |
|---:|---:|---:|---:|---:|
| 10 | +10 | x1.81 | x1.38 | unchanged |
| 100 | +100 | x3.73 | x4.37 | unchanged |
| 1000 | +1000 | x12.65 | x32.09 | unchanged |

The statement count never changes — Hibernate resolves the eager collection with a join, so this
is a payload/hydration cost, confirming the N+1 exclusion in the scope decision above rather than
overlapping with it.

**Energy cost** (two campaigns, six distinct collection sizes from 10 to 1000; three of them,
10/100/1000, measured independently in both):

| Children collection size | Relative energy overhead (with smell) | Measured in | Sign test across 30 phases |
|---:|---:|---|:---:|
| 10 | 81.0–81.3% | campaigns 1 and 2 | p ≈ 1.9×10⁻⁹ |
| 30 | 83.9% | campaign 2 | p ≈ 1.9×10⁻⁹ |
| 50 | 85.8% | campaign 2 | p ≈ 1.9×10⁻⁹ |
| 100 | 86.3–86.7% | campaigns 1 and 2 | p ≈ 1.9×10⁻⁹ |
| 500 | 87.1% | campaign 2 | p ≈ 1.9×10⁻⁹ |
| 1000 | 90.1–90.7% | campaigns 1 and 2 | p ≈ 1.9×10⁻⁹ |

Every one of the 30 measurement phases at every size points the same way, at both hardware
components (CPU, DRAM) and wall time, with strictly disjoint EAGER/LAZY distributions. The
relative overhead is already large at the smallest size tested (10 elements) and climbs to 90%+
at 1000. The second campaign also located a genuine, honestly-disclosed anomaly in the
fine-grained scaling curve around 50–100 elements, which does not change this conclusion (see
campaign 2's document, §4–§5).

Reproducibility across the two independent measurement sessions (different days) is strong: the
relative overhead never moved by more than 0.6 percentage points at the three sizes tested in
both campaigns, even though absolute joule readings drifted by up to 16% between sessions —
which is why every figure above is a relative, paired comparison rather than a raw energy total.

Full detail, including the honest caveats (bimodal phases, the `CONTEXT_SIZE` confound, why the
tool's own p-values are not cited, the modest absolute yearly saving): `campaign1-interpretation.md`
and `campaign2-interpretation.md` under `src/main/rules/S6904/java/analysis/level2-energytracer/`.

### Credits

- [@AntoineMeheut](https://github.com/AntoineMeheut) and [@dirdr](https://github.com/dirdr) (Crjvm205 #122): ecological rationale, JPA default fetch types, test matrix
- [@kerimboukadida](https://github.com/kerimboukadida) (CRJVM205 - Forcer l'utilisation du FetchType LAZY sur les collections… #155): `@ManyToMany(EAGER)` example, real-project validation
- [@AndreBraga1](https://github.com/AndreBraga1) ([205][Java] Force usage of FetchType LAZY for collections on JPA entities #325): repository structure, CHANGELOG and RULES.md entries
- [@misterdz-mh](https://github.com/misterdz-mh) (CRJVM 205 : Forcer l'utilisation du FetchType LAZY sur les collections dans les Entity JPA #125): `orm` tag
- [@dedece35](https://github.com/dedece35) : complete Analysis (JPA impact / EnergyTracer measurements)
```

---

## Before posting

- [ ] Confirm the analysis-harness bullet under **What this PR contains** matches how you
      want `S6904/java/analysis/` described
- [ ] **Review and validate before posting** — this replaces the live description on #487
