> **Superseded.** This draft proposed adding a *new* rule to be identified S6904. It turned
> out SonarQube already ships a built-in rule under that same identifier, so no new rule is
> being added and the four PRs below are being closed as duplicates instead of merged. See
> `../../s6904-pr-description.md` (analysis root) for the closure PR that replaces this
> draft. Kept here for history; the recomputed figures in it are still accurate and were
> reused there.

# PR description — ready to paste

*`results-template.md` filled in with the results of the campaign run on 4 August 2026.*

Three blocks:

- **A** — internal record (keep, do not publish)
- **B** — PR description in English, paste as is
- **C** — annex entries (`RULES.md`, asciidoc)

> Every figure was recomputed from the raw data, independently of the tool's own report.
> `ane_mj` is excluded from every total. The tool's p-values are not reused: they assume
> the independence of 900 runs that are not independent — see
> `fetchtype-measurement-interpretation.md` § 2.
>
> The rule identifier is **S6904**, confirmed by the core team. `<...>` links are still to
> be filled in.

---
---

# A — Internal record

## Run context

| | |
|---|---|
| Date | 2026-08-04 |
| Machine | MacBookPro18,1 — Apple M1 Pro — `M-NGY9VPYVMH` |
| OS | Darwin 25.5.0 (arm64) |
| JDK | 21.0.11 (OpenJDK 64-Bit Server VM) |
| Profiler | EnergyTracer `mac` — Apple Silicon hardware counters (CPU/GPU/ANE/DRAM) |
| Phases / iterations | 30 phases × 30 iterations = 900 runs per variant per size |
| Statistical unit used | **the phase (n = 30 pairs)**, not the individual run |
| Machine preparation | § 4 checklist fully applied |
| Data set | `raw` (filtered once, globally); `ane_mj` excluded |

## Level 1 — functional counters (JPA)

| fetch | N | statements/read | entities/read | µs/read | KiB/read |
|---|---:|---:|---:|---:|---:|
| EAGER | 10 | 1.00 | 11.00 | 121.1 | 33.6 |
| LAZY | 10 | 1.00 | 1.00 | 67.0 | 24.4 |
| EAGER | 100 | 1.00 | 101.00 | 253.9 | 105.1 |
| LAZY | 100 | 1.00 | 1.00 | 68.1 | 24.1 |
| EAGER | 1000 | 1.00 | 1001.00 | 968.5 | 764.1 |
| LAZY | 1000 | 1.00 | 1.00 | 76.6 | 23.8 |

| N | time | allocation | extra rows hydrated |
|---:|---:|---:|---:|
| 10 | x1.81 | x1.38 | +10 |
| 100 | x3.73 | x4.37 | +100 |
| 1000 | x12.65 | x32.09 | +1000 |

*A single run, only 100 reads at N=1000. Orders of magnitude, not repeated measurements.*

## Level 2 — energy (CPU + GPU + DRAM)

Means over 900 runs, outliers removed at 3σ:

| N | EAGER | LAZY | Gap | Relative gap | Ratio |
|---:|---:|---:|---:|---:|---:|
| 10 | 2,047.7 mJ | 388.4 mJ | 1,659.3 mJ | +81.0% | x5.27 |
| 100 | 2,510.6 mJ | 343.5 mJ | 2,167.1 mJ | +86.3% | x7.31 |
| 1000 | 5,041.9 mJ | 468.0 mJ | 4,573.9 mJ | +90.7% | x10.77 |

Phase-level analysis (n = 30 pairs):

| N | Gap / phase | 95% CI | Relative gap | Phases same direction | r(EAGER,LAZY) |
|---:|---:|---:|---:|:---:|---:|
| 10 | 1,674.9 mJ | [1,564.7, 1,785.1] | 80.83% ± 0.76 | 30/30 | +0.695 |
| 100 | 2,166.3 mJ | [2,028.9, 2,303.7] | 86.29% ± 0.52 | 30/30 | +0.851 |
| 1000 | 4,637.2 mJ | [4,545.2, 4,729.2] | 90.61% ± 0.20 | 30/30 | +0.945 |

Sign test, 30/30 phases: **p ≈ 1.9 × 10⁻⁹**.

Normalised energy:

| N | µJ / hydrated row | Range | µJ / read |
|---:|---:|---:|---:|
| 10 | 0.33 | 0.31 – 0.34 | 3.1 – 3.3 |
| 100 | 0.43 | 0.39 – 0.44 | 39 – 44 |
| 1000 | 0.92 | 0.92 – 0.93 | ≈ 915 |

## Reading

- **Threshold**: marginal at ten, clear at a hundred. Crossover point **not measured** (no
  size tested between 10 and 100).
- **Dominant component**: CPU (76 → 87% of the gap depending on N), then DRAM (24 → 13%).
- **Strictly disjoint distributions** on `cpu_mj`, `dram_mj`, `time_s` at all three sizes.
  Not on `gpu_mj` (overlap at N=10 and N=100; weight < 0.15%).
- **Anomalies**: bimodality on 2 to 8 phases depending on N (+23 to +48%), affecting both
  variants together; `ane_mj` inconsistent across sizes, excluded.

---
---

# B — PR description, paste as is

```markdown
## Measurement of the rule's impact

None of the four superseded PRs (#122, #125, #155, #325) provided any measurement, and that gap
is what stalled the discussion in January 2024. This section fills it.

Full harness, raw data and plots: <link to the branch or repository>

### Scenario

A parent entity owns a collection association. The application loads the parent and **never
accesses the collection** — the exact case this rule targets. Three collection sizes were tested:
10, 100 and 1000 children per parent.

### Functional cost — Hibernate 6.4.4 + H2, JDK 21

| Collection size | Extra rows hydrated | Time | Heap allocation | SQL statements |
|---:|---:|---:|---:|---:|
| 10 | +10 | x1.81 | x1.38 | **unchanged** |
| 100 | +100 | x3.73 | x4.37 | **unchanged** |
| 1000 | +1000 | x12.65 | x32.09 | **unchanged** |

Worth stressing: **the statement count is identical — 1.00 per read in every case**. Hibernate
resolves the eager collection with a join, so there is no extra round trip. This is *not* an N+1
problem; it is a payload and hydration problem. The N+1 case on single-valued associations is a
different rule and is deliberately out of scope here.

LAZY allocation stays flat at ~24 KiB per read regardless of collection size, while EAGER grows
to 764 KiB.

*Caveat: this level is a single run, with only 100 reads at N=1000. Treat the ratios as orders of
magnitude rather than repeated measurements.*

### Energy cost — EnergyTracer, `mac` profiler (Apple M1 Pro hardware counters)

30 phases x 30 iterations = 900 runs per variant per size, 60 s cooldown between phases,
randomised variant order, machine prepared per EnergyTracer's EXPERIMENT_GUIDE. Totals are
CPU + GPU + DRAM; `ane_mj` is excluded (see Limitations).

| Collection size | EAGER | LAZY | Gap | Ratio |
|---:|---:|---:|---:|---:|
| 10 | 2047.7 mJ | 388.4 mJ | 1659.3 mJ | x5.27 |
| 100 | 2510.6 mJ | 343.5 mJ | 2167.1 mJ | x7.31 |
| 1000 | 5041.9 mJ | 468.0 mJ | 4573.9 mJ | x10.77 |

**On statistics.** Runs within one phase share thermal and system state, so they are not
independent — the correlation between a phase's EAGER and LAZY means is +0.70 to +0.95. The valid
unit of analysis is therefore the phase, n = 30 paired observations, not 900 independent samples.
Reported on that basis:

| Collection size | Gap per phase | 95% CI | Relative gap | Phases favouring LAZY |
|---:|---:|---:|---:|:---:|
| 10 | 1674.9 mJ | [1564.7, 1785.1] | 80.83% +/- 0.76 | **30 / 30** |
| 100 | 2166.3 mJ | [2028.9, 2303.7] | 86.29% +/- 0.52 | **30 / 30** |
| 1000 | 4637.2 mJ | [4545.2, 4729.2] | 90.61% +/- 0.20 | **30 / 30** |

All 30 phases point the same way at all three sizes. **Sign test: p = 2 x 0.5^30 ~ 1.9e-9**, with
no assumption of normality, equal variance, or within-phase independence.

Beyond that, on `cpu_mj`, `dram_mj` and `time_s` the two distributions are **strictly disjoint**
at all three sizes — at N=1000 the LAZY maximum is 542.9 mJ against an EAGER minimum of
4265.2 mJ. No statistical test is needed to separate them.

### The number to take away

Percentages depend on how the harness is calibrated. The transposable quantity is the absolute
energy spent per uselessly hydrated row:

| Collection size | Energy per hydrated row | Energy per parent read |
|---:|---:|---:|
| 10 | **0.33 µJ** | 3.1 – 3.3 µJ |
| 100 | **0.43 µJ** | 39 – 44 µJ |
| 1000 | **0.92 µJ** | ~915 µJ |

Two significant figures only: the ranges reflect whether the bimodal high-regime phases are
included (see Limitations).

### The cost grows faster than the collection

| Step | Rows | Energy per read | Exponent |
|---|---:|---:|---:|
| 10 -> 100 | x10 | x12.4 to x13.1 | 1.10 – 1.12 |
| 100 -> 1000 | x10 | x20.9 to x23.5 | 1.32 – 1.37 |

Ten times more children costs 12-13x then 21-23x more energy per read: the cost scales as
**N^1.10 to N^1.37**, not linearly, and this holds under both data treatments. Each individual row
also gets more expensive as the collection grows (0.33 µJ at N=10 versus 0.92 µJ at N=1000).

**What drives it, honestly.** Two mechanisms overlap and this benchmark does not separate them:
degrading cache locality and rising GC pressure on larger collections — the effect the rule
targets — and the harness's simulated persistence context, which retains 1024 parents, so the
live set grows with N by construction (no `-Xmx` was fixed). The second is arguably realistic, as
a real persistence context does retain hydrated collections, but part of the super-linearity may
reflect live-set size rather than a per-row cost. A sensitivity study on those two parameters is
the obvious follow-up.

### Honest reading — what this does *not* show

- **The relative percentage is not a production figure.** In the energy benchmark the LAZY variant
  does almost nothing, so the gap measures how much of *that synthetic program* is hydration —
  which depends on the `PARENT_READS` calibration. In a real service the cost is diluted by SQL,
  network, serialisation and business logic.
- **The absolute yearly saving is modest.** A service handling 1,000 req/s, each loading a parent
  with 100 unused children, wastes roughly **43 mW, about 0.38 kWh per year**. The case for this
  rule does not rest on energy alone: it rests on the combination of 12x latency, 32x allocation,
  super-linear growth, and a **zero-cost fix** (one annotation attribute).
- **The two levels are consistent, not contradictory.** At N=10 the functional benchmark reports
  x1.81 on time while the energy benchmark reports x5.27. A full LAZY read at level 1 costs ~67 µs
  (session open, query, parent hydration, close), which dwarfs the cost of hydrating 10 rows; the
  energy benchmark has a much leaner baseline. The functional figure is the realistic one at small
  sizes. Both converge at N=1000 (x12.65 time versus x10.77 energy).
- **The relevance threshold was not resolved.** No size between 10 and 100 was tested. The gain is
  marginal at ten elements and clear at a hundred; where it crosses over was not measured.

### Limitations, stated up front

- The energy benchmark uses a **simulated row source**: no database engine, no network, no JDBC
  driver — EnergyTracer's Java runner compiles with a bare `javac` and cannot load third-party
  jars. It measures client-side cost only, so these figures are a **lower bound**.
- EnergyTracer launches one JVM per iteration, so absolute values include JVM startup (~100-400
  ms). Only the relative Java-vs-Java comparison is meaningful, as EnergyTracer documents. Runs
  last roughly 0.4-0.7 s, so the JIT may not reach the steady state a long-running service would.
- The functional benchmark uses in-memory H2: **no network latency, no disk I/O**. This is the
  least favourable setup for the demonstration — on a remote RDBMS the gap would widen.
- **A bimodal regime was observed**: 2 to 8 phases out of 30 depending on size sit 23% to 48%
  above the rest. At N=100 the affected phases are 1, 2, 7, 8 and 26-29 — non-contiguous and
  including the first ones, which rules out simple thermal drift and points to core-scheduling or
  intermittent system activity. It affects both variants together, so the relative gap moves by
  less than 0.7 points across the campaign (81.19% to 81.10% at N=10). Energy per row shifts by
  1.4% to 10% when those phases are excluded, which is what the ranges above reflect.
- **`ane_mj` is excluded from every figure.** The Apple Neural Engine counter reports 11.4 mJ at
  N=10, 23.9 mJ at N=100 and 0.0012 mJ at N=1000, with no neural workload involved and no
  plausible mechanism for such variation — a measurement artefact. It accounted for under 1% of
  the total.
- **Average power is not the driver.** At N=10 the LAZY variant draws *more* average power
  (5.17 W versus 4.18 W) but for far less time: the saving comes from duration, not from a lower
  instantaneous draw.
- Measurements come from **a single machine and a single chip model**. Independent reproduction is
  welcome; the harness is self-contained and documented.

### Proposed scope, informed by the measurements

The data supports restricting the rule rather than applying it everywhere:

- Report only **collection associations** (`@OneToMany`, `@ManyToMany`) carrying an **explicit
  `fetch = FetchType.EAGER`**. JPA already defaults these to LAZY, so flagging an absent attribute
  would be a false positive.
- The measured benefit is **marginal at around ten elements** and material **at a hundred and
  above**. Worth documenting in the rule description so users can judge relevance in context.
```

---
---

# C — Annex entries

## `RULES.md`, `Reference/Validation` column

```markdown
[energy & allocation measurement](<link to the PR>)
```

## `== Resources` section of `S6904.asciidoc`

*The repository's asciidoc format has no measurement section: do not paste the report there.
A link is enough.*

```asciidoc
=== Benchmarks

- <link to the PR>[Energy and allocation benchmark] - EAGER vs LAZY collection fetching,
  measured with EnergyTracer on Apple Silicon hardware counters across three collection sizes,
  30 paired phases per size
```

## `== Exceptions to this rule` section

```asciidoc
== Exceptions to this rule

This rule only reports collection associations declared with an explicit
`fetch = FetchType.EAGER`. Associations without a `fetch` attribute are already LAZY by default
in JPA and are not reported.

The measured cost grows faster than the collection size and is marginal on collections of around
ten elements. On very small, always-consumed collections, eager fetching may legitimately be
preferred.
```

---

## Before publishing

- [x] Rule identifier confirmed: **S6904**
- [ ] Fill in the `<...>` links
- [x] Full campaign (30 × 30), figures recomputed from the raw data
- [x] Machine-preparation checklist applied
- [x] Hardware measurement, not a software estimate
- [x] Statistics at the right unit (the phase), tool p-values discarded
- [x] `ane_mj` genuinely excluded from every total
- [x] Bimodality stated and explained
- [x] Possible confound on non-linearity (`CONTEXT_SIZE`) stated
- [x] Modest yearly saving assumed
- [x] Two significant figures, ranges given
- [ ] **Review and validate before public release**

*Content intended for public publication on GitHub: review and validate before releasing.*
