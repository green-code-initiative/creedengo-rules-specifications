# Interpretation of the measurements — rule "FetchType.LAZY on JPA collections" (S6904)

*Analysis of the campaign run on 2026-08-04. Internal working document.*

---

## Summary

**The rule's impact is measured, robust, and grows faster than collection size.** Over 30
paired phases and 900 runs per variant per size, measured with the hardware counters of a
MacBook Pro M1 Pro, hydrating a collection that is never used costs
**0.31 to 0.92 µJ per child row**. All 30 phases point the same way at all three sizes —
p ≈ 2·10⁻⁹ by a sign test, with no distributional assumption at all.

**The percentage (81% to 91%) should not be the main argument**, for three reasons
developed in § 5: it depends on a bench-calibration parameter, the absolute yearly saving
is modest, and at small sizes the JPA bench — more realistic — shows a much smaller gap
(x1.81 in time at N=10 versus x5.3 in energy).

**The recommended case** rests on three pillars that are harder to contest:

1. **Super-linearity.** Multiplying collection size by 10 multiplies energy per read by 12
   to 13, then by 21 to 23 — the cost grows as N^1.10 to N^1.37.
2. **Latency and memory**: x12.65 in time and x32 in allocation at N=1000, measured on a
   real Hibernate stack.
3. **Zero fix cost**: one annotation attribute.

**Two reservations not to hide.** First, the 900 runs are not independent (inter-variant
correlation per phase of 0.70 to 0.95): the valid statistical unit is the phase, n=30, and
the tool's astronomical p-values are indefensible. Second, part of the super-linearity may
come from the simulated persistence context, which retains 1,024 parents, so its memory
footprint grows with N.

**Recommendation**: present the rule as relevant for collections on the order of a hundred
elements and above, while documenting that the gain is marginal at ten.

---

## 1. What was measured

| | Level 1 — JPA | Level 2 — energy |
|---|---|---|
| Stack | Hibernate 6.4.4 + H2, JDK 21.0.11 | Plain Java, simulated row source |
| Tool | Hibernate Statistics + JDK | EnergyTracer, `mac` profiler (hardware counters) |
| Machine | MacBook Pro M1 Pro (arm64) | MacBookPro18,1 — Apple M1 Pro |
| Volume | **A single run**, 100 to 2,000 reads depending on N | 30 phases × 30 iterations = 900 runs per variant |
| Protocol | — | 60 s cooldown, randomised variant order, machine prepared |

Scenario identical at both levels, and strictly the one the rule targets: a parent entity
owns a collection of children, the application loads the parent and **never accesses**
the collection. Sizes: **10, 100 and 1000 children per parent**.

**Property of the energy bench only**: the number of child rows hydrated per run is held
constant at 5,000,000 there (500,000 × 10, 50,000 × 100, 5,000 × 1000), which makes the
three sizes comparable at equal nominal work. The JPA bench, by contrast, performs
20,000, 20,000 and 100,000 hydrations — its ratios should be read per read, not in
absolute terms.

**Scope of the energy totals**: `ane_mj` is **excluded from every calculation** in this
document (see § 4). Totals therefore equal CPU + GPU + DRAM.

---

## 2. Results

### Level 1 — functional cost (real Hibernate)

| N | Statements / read | Hydrated entities / read | Time | Allocation |
|---:|---:|---:|---:|---:|
| 10 | 1.00 → 1.00 | 11 → 1 | x1.81 | x1.38 |
| 100 | 1.00 → 1.00 | 101 → 1 | x3.73 | x4.37 |
| 1000 | 1.00 → 1.00 | 1001 → 1 | x12.65 | x32.09 |

**The statement count is identical — 1.00 per read in every case.** An important and
counter-intuitive result: Hibernate resolves the eager collection with a join, with no
extra round trip. **The overhead is therefore not an N+1 problem** but a payload and
hydration problem. This confirms the scope decision from the fusion proposal: N+1 on
single-valued associations belongs to a separate rule.

LAZY allocation is flat (≈ 24 KiB per read regardless of N), EAGER allocation grows from
33.6 to 764.1 KiB.

*Caveat: this level rests on a single run, and only 100 reads at N=1000. The ratios are
orders of magnitude, not repeated measurements.*

### Level 2 — energy cost

Means over 900 runs, outliers removed at 3σ, CPU + GPU + DRAM:

| N | EAGER | LAZY | Gap | Relative gap | Ratio |
|---:|---:|---:|---:|---:|---:|
| 10 | 2,047.7 mJ | 388.4 mJ | 1,659.3 mJ | +81.0% | x5.27 |
| 100 | 2,510.6 mJ | 343.5 mJ | 2,167.1 mJ | +86.3% | x7.31 |
| 1000 | 5,041.9 mJ | 468.0 mJ | 4,573.9 mJ | +90.7% | x10.77 |

### The correct statistic: at the phase level

The 900 runs within one phase share the same thermal state and the same system context.
They are not independent: the correlation between a phase's EAGER and LAZY means is
**+0.695 at N=10, +0.851 at N=100, +0.945 at N=1000**. Treating the 900 runs as 900
independent observations — which is what the tool does — artificially inflates
significance. **The valid unit is the phase: n = 30 pairs.**

| N | Mean gap per phase | 95% CI | Relative gap | Phases pointing the same way |
|---:|---:|---:|---:|:---:|
| 10 | 1,674.9 mJ | [1,564.7, 1,785.1] | 80.83% ± 0.76 | **30 / 30** |
| 100 | 2,166.3 mJ | [2,028.9, 2,303.7] | 86.29% ± 0.52 | **30 / 30** |
| 1000 | 4,637.2 mJ | [4,545.2, 4,729.2] | 90.61% ± 0.20 | **30 / 30** |

All 30 phases point the same way at all three sizes. **Sign test: p = 2 × 0.5³⁰ ≈
1.9 × 10⁻⁹**, with no assumption of normality, equal variance, or within-phase
independence. This is the figure to cite, and it is more than enough.

Beyond that, on `cpu_mj`, `dram_mj` and `time_s`, the distributions are **strictly
disjoint** at all three sizes: at N=1000 for instance, the LAZY maximum is 542.9 mJ
against an EAGER minimum of 4,265.2 mJ. No statistical test is needed to settle it.

*Nuance: this disjointness does not hold for `gpu_mj`, where the distributions overlap at
N=10 and N=100. The GPU accounts for under 0.15% of the gap anyway and is not an
argument.*

Breakdown of the gap by component:

| N | CPU | DRAM | GPU |
|---:|---:|---:|---:|
| 10 | 76.1% | 23.8% | 0.1% |
| 100 | 79.9% | 20.0% | 0.1% |
| 1000 | 86.9% | 13.0% | 0.0% |

The gap is carried by the CPU (object creation, decoding, collection insert) and by DRAM
(memory pressure). The CPU share grows with size.

---

## 3. The figure to publish: energy per hydrated row

| N | µJ / hydrated row | Range depending on treatment | µJ / parent read |
|---:|---:|---:|---:|
| 10 | 0.33 | 0.31 – 0.34 | 3.1 – 3.3 |
| 100 | 0.43 | 0.39 – 0.44 | 39 – 44 |
| 1000 | 0.92 | 0.92 – 0.93 | ≈ 915 |

The range reflects including or excluding the high-regime phases (§ 4). **Publish two
significant figures**: the third decimal is not supported by the data.

### Super-linearity, the central argument

| Step | Rows | Energy / read | Exponent |
|---|---:|---:|---:|
| N=10 → 100 | x10 | x12.4 to x13.1 | 1.10 – 1.12 |
| N=100 → 1000 | x10 | x20.9 to x23.5 | 1.32 – 1.37 |

Multiplying the collection by 10 multiplies the energy cost per read by 12-13, then by
21-23. The cost grows as **N^1.10 to N^1.37**, not linearly — and the conclusion holds
under both data treatments, which is the important point. Each individual row also gets
more expensive as the collection grows: 0.33 µJ at N=10 versus 0.92 µJ at N=1000.

**Honest reservation about the mechanism.** Two explanations overlap and this
measurement does not separate them:

- degrading cache locality and rising garbage-collector pressure as collections grow —
  the effect the rule targets;
- the **bench's simulated persistence context**, which retains 1,024 parents
  (`CONTEXT_SIZE`): under EAGER, the live set therefore holds 1,024 × N child objects,
  growing mechanically with N. No `-Xmx` was fixed.

This second factor is defensible — a real persistence context does retain hydrated
collections — but it means part of the super-linearity reflects live-set size rather than
a per-row cost. **A sensitivity study on `CONTEXT_SIZE` and `-Xmx` would resolve the
ambiguity**; in its absence, present the super-linearity as observed on this bench, not
as a general law.

---

## 4. Quality control: two regimes, not a drift

The per-phase stability plot initially suggested thermal drift. Examining the affected
phases shows something else: a **binary factor**.

| N | High-regime phases | Gap over the low regime |
|---:|---|---:|
| 10 | 22, 28, 29, 30 (4/30) | +48% |
| 100 | 1, 2, 7, 8, 26, 27, 28, 29 (8/30) | +41% |
| 1000 | 29, 30 (2/30) | +23% |

At N=100, the high phases are **1, 2, 7, 8** then **26 to 29**: non-contiguous, and
including the first ones. Gradual heating does not produce this pattern. The plausible
hypothesis is a scheduling shift (performance cores versus efficiency cores) or
intermittent system activity. The bimodality is visible on the violin plots.

**Why the conclusion holds.** The factor affects both variants together, since they are
measured within the same phase and in randomised order. The relative gap is therefore
insensitive to it:

| N | EAGER drift | LAZY drift | Δ% phases 1-5 → 26-30 |
|---:|---:|---:|---:|
| 10 | +25.2% | +25.1% | 81.19 → 81.10 (−0.09 pt) |
| 100 | +0.8% | −1.8% | 86.16 → 86.01 (−0.16 pt) |
| 1000 | +8.4% | +15.3% | 90.67 → 90.05 (−0.62 pt) |

The relative gap never moves by more than 0.7 points. And energy per row, recomputed
excluding the high phases, varies from −1.4% to −10% — hence the range in § 3.

This is the strength of the paired, randomised design: it absorbs perturbations that hit
both variants at once. Worth stating in the PR; a reviewer will open the plots.

### Anomaly excluded: `ane_mj`

The ANE (Apple Neural Engine) counter reports 11.4 mJ at N=10, 23.9 mJ at N=100 and
0.0012 mJ at N=1000 — with no neural workload in the benchmark, and no plausible
mechanism for such variation across sizes. A measurement artefact, or crosstalk with
system activity.

Its Cliff's δ is negligible everywhere (+0.050, +0.121, +0.036) and it accounts for under
1% of the total. **It is excluded from every figure in this document.** This lowers
totals by roughly 0.6% at N=10 and N=100, without changing any conclusion.

### Average power

At N=10, the LAZY variant shows a *higher* average power draw (5.17 W versus 4.18 W): it
works in short bursts. **The saving comes from duration, not from a lower instantaneous
draw.** At N=1000, EAGER reaches 7.18 W against 3.54 W — sustained memory pressure. EAGER
power grows from 4.18 to 7.18 W with size, consistent with the super-linear reading.

---

## 5. What not to claim

### "EAGER consumes 91% more energy"

Wrong out of context. At level 2, the LAZY variant does almost nothing: the gap measures
the share of hydration **within this synthetic program**, a share that depends on the
`PARENT_READS` parameter chosen to calibrate the bench. In production this cost is
diluted by SQL, network, serialisation and business logic.

Correct wording: "hydrating an unused collection costs 0.33 to 0.92 µJ per row, and that
cost grows faster than the number of rows".

### The tool's p-values

`ET-analyzer` reports p-values below 10⁻²⁷⁰, computed on 900 samples treated as
independent. They are not (§ 2). **Do not cite them**: a statistically literate reviewer
will have no trouble dismantling the reasoning, and that would be a shame since the
result does not need it. Cite the sign test over 30 phases (p ≈ 2·10⁻⁹) and the
disjointness of the distributions instead.

### The two levels do not contradict each other

At N=10, level 1 reports x1.81 in time while level 2 reports x5.27 in energy. This is not
an inconsistency: at level 1, a full LAZY read costs ≈ 67 µs (session open, query, parent
hydration, close), which dwarfs the cost of hydrating 10 rows. Level 2 has a much leaner
baseline.

**Level 1 is the realistic figure at small sizes.** The two converge at N=1000: x12.65 in
time versus x10.77 in energy.

### The absolute saving is modest

A service handling 1,000 requests per second, each loading a parent with 100 unused
children, wastes:

> 1,000 × 100 × 0.43 µJ ≈ **43 mW**, roughly **0.38 kWh per year**

That is little — a few hours of laptop use. And it is a lower bound: the database-server
cost and the network transfer are not measured.

**Defending the rule on energy savings alone would be fragile.** It is defended by the
combination: latency divided by 12, allocation by 32, super-linear growth, free fix. The
energy figure confirms the mechanism; it is not the knockout argument.

### The relevance threshold is not resolved

No size between 10 and 100 was tested. Saying "from about a hundred" is an
interpolation, not a measurement. Honest wording: the gain is marginal at ten, clear at a
hundred, and the crossover point was not sought.

---

## 6. Recommendations for the PR

1. **Open on the functional cost** (level 1): uselessly hydrated rows, time, memory.
   Verifiable without instrumentation.
2. **Follow with super-linearity**, along with its `CONTEXT_SIZE` reservation.
3. **State energy in absolute terms** (µJ per row, two significant figures), not as a
   percentage.
4. **Cite the sign test**, not the tool's p-values.
5. **State the bimodality** and explain why the paired design absorbs it.
6. **Exclude `ane_mj`** explicitly and say why.
7. **Assume the yearly saving is modest.**
8. **Propose a scope**: collections with an explicit `fetch = EAGER` only, marginal gain
   at ten elements.

### What remains to be produced

- **An independent reproduction**: a single machine, a single chip model.
- **A sensitivity study** on `CONTEXT_SIZE` and `-Xmx`, to isolate the live-set share of
  the super-linearity. This is the most likely angle of attack in review.
- **A repeated level 1**: the x12.65 and x32.09 ratios come from a single run even though
  they are the figures put forward.
- **An intermediate size** (30, 50) to locate the threshold.
- **The core team's scope decision**, which remains the real blocking point.

---

## Sources

- Consolidated report: `level2-energytracer/campaign1/consolidated-report/REPORT.md`
- Consolidated data: `.../consolidated-report/data/` — 5,400 measurements (3 sizes × 2 variants × 900)
- Plots: `.../consolidated-report/plots/`
- Raw EnergyTracer reports: `.../consolidated-report/reports-source/`
- Level 1: `level1-jpa/results/level1-20260802-120143.txt` — file predates both the
  harness rename and the S6904 assignment; its header still reads the original rule
  identifier used before this repository location was settled.

Every figure in this document was recomputed from the raw data, independently of the
tool's own report.

---

*Content intended for public publication on GitHub: review and validate before
releasing.*
