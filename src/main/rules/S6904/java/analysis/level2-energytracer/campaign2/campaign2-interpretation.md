# Interpretation of the measurements — rule "FetchType.LAZY on JPA collections" (S6904)

*Campaign 2, run on 5 August 2026. Internal working document. Companion to
`campaign1/campaign1-interpretation.md`, whose conclusions this campaign
was designed to refine, not replace.*

*Context note: SonarQube already ships a built-in rule under the identifier S6904, so the
four historical PRs this harness was built to support (#122, #125, #155, #325) are being
closed as duplicates rather than merged. This document is kept regardless, as independent
evidence that the underlying code smell has a real, measurable cost — useful to justify why
the built-in rule matters and to inform its configuration (severity, default scope), even
though no new rule will be shipped from this work.*

---

## Summary

**Campaign 2 confirms campaign 1's headline finding and adds one genuine complication.**
Six collection sizes were measured this time — 10, 30, 50, 100, 500 and 1000 — instead of
three, specifically to locate the relevance threshold that campaign 1 left open. At every
size, the effect is large, robust, and points the same way across all 30 phases
(sign test p ≈ 1.9 × 10⁻⁹ at each of the six sizes independently).

**What is confirmed.** At the three sizes shared with campaign 1 (10, 100, 1000), the
relative gap reproduces within 0.6 percentage points despite the two campaigns running on
different days: 81.0% → 81.30% at N=10, 86.3% → 86.67% at N=100, 90.7% → 90.13% at N=1000.
Absolute joule levels drift more than that between sessions (up to ±15% at N=100), which
is exactly the reason campaign 1 argued for reporting relative, paired figures rather than
absolute totals — campaign 2 is a direct demonstration of that argument.

**What is new, and not entirely clean.** The extra sizes do not simply fill in a smooth
curve between the three original points. Energy per hydrated row goes 0.33 → 0.36 → 0.47
µJ from N=10 to N=30 to N=50, then **drops to 0.38 µJ at N=100** before resuming its climb
to 0.66 and 0.94 µJ at N=500 and N=1000. Fitted on energy *per read*, a single exponent
across all six points looks excellent (N^1.22, R² = 0.995) simply because per-read values
span two orders of magnitude on the back of N itself — that fit hides the kink. Fitted on
energy *per row* instead, which divides out that trivial N¹ factor, the same six points
give R² = 0.85: the local step from N=50 to N=100 is actually sub-linear (exponent ≈
+0.68), the opposite of what an averaged curve suggests. This is discussed in § 4 and § 6
rather than smoothed over.

**Also new**: the relative percentage gap is already 81% at N=10 and 84% at N=30 — large
from the smallest size tested. What grows with collection size is not "whether the effect
exists" but its **absolute magnitude** and its **share of average power** (EAGER overtakes
LAZY in average power draw somewhere between N=50 and N=100, a crossover campaign 1 could
only bracket, not locate).

**Conclusion, updated from campaign 1**: there is no single clean scaling law. The coarse
trend holds (energy per row roughly triples from N=10 to N=1000, energy per read grows by
two orders of magnitude), but the fine-grained data shows a local anomaly around N=50–100
whose cause is not established.

---

## 1. What was measured

Same scenario, same machine, same protocol as campaign 1 — see that document's § 1 for the
full description. The only change is scope: **six** collection sizes instead of three, each
still measured over 30 phases × 30 iterations = 900 runs per variant, with the same
60-second cooldown and randomised variant order.

| | Campaign 1 | Campaign 2 |
|---|---|---|
| Date | 2026-08-04 | 2026-08-05 |
| Sizes | 10, 100, 1000 | 10, 30, 50, 100, 500, 1000 |
| Runs per variant per size | 900 | 900 |
| Rows hydrated per run (target) | ≈ 5,000,000 | ≈ 5,000,000 (same `CONFIGS`, unchanged) |
| Level 1 (JPA) | single run | **not re-run**; campaign 2 covers level 2 only |

Level 1 figures quoted below are therefore still the single campaign-1 run
(`level1-jpa/results/level1-20260802-120143.txt`); nothing in this document changes them.
`ane_mj` is again **excluded from every total**, for the same reason as campaign 1 (see § 5
here and § 4 of the campaign 1 document).

---

## 2. Results

### Level 2 — energy cost, all six sizes

Means over 900 runs per variant, outliers removed at 3σ, totals = CPU + GPU + DRAM:

| N | EAGER | LAZY | Gap | Relative gap | Ratio |
|---:|---:|---:|---:|---:|---:|
| 10 | 2,049.0 mJ | 383.1 mJ | 1,665.9 mJ | +81.30% | x5.35 |
| 30 | 2,150.3 mJ | 345.6 mJ | 1,804.7 mJ | +83.93% | x6.22 |
| 50 | 2,743.9 mJ | 389.8 mJ | 2,354.0 mJ | +85.79% | x7.04 |
| 100 | 2,175.2 mJ | 290.0 mJ | 1,885.2 mJ | +86.67% | x7.50 |
| 500 | 3,787.8 mJ | 488.5 mJ | 3,299.3 mJ | +87.10% | x7.75 |
| 1000 | 5,192.2 mJ | 512.7 mJ | 4,679.5 mJ | +90.13% | x10.13 |

**The relative gap climbs smoothly and monotonically** from 81.3% to 90.1% across the six
sizes — this part of the curve is clean. **The absolute EAGER mean does not climb
smoothly**: 2,743.9 mJ at N=50 is higher than 2,175.2 mJ at N=100, a reversal that recurs at
the phase level (§ 4) and is not an artefact of a single bad phase.

### The correct statistic: at the phase level

As in campaign 1, the 900 runs within a phase are not independent (they share thermal and
system state), so the valid unit of analysis is the phase, n = 30 pairs:

| N | Gap per phase | 95% CI | Relative gap | Phases same direction | r(EAGER, LAZY) |
|---:|---:|---:|---:|:---:|---:|
| 10 | 1,665.9 mJ | [1,557.4, 1,774.5] | 81.30% | **30 / 30** | +0.885 |
| 30 | 1,831.3 mJ | [1,709.4, 1,953.3] | 84.13% | **30 / 30** | +0.966 |
| 50 | 2,354.0 mJ | [2,223.4, 2,484.6] | 85.79% | **30 / 30** | +0.969 |
| 100 | 1,877.1 mJ | [1,856.6, 1,897.7] | 86.25% | **30 / 30** | **+0.101** |
| 500 | 3,299.3 mJ | [3,141.4, 3,457.2] | 87.10% | **30 / 30** | +0.981 |
| 1000 | 4,675.7 mJ | [4,525.2, 4,826.2] | 90.02% | **30 / 30** | +0.732 |

All 30 phases point the same way at every one of the six sizes. **Sign test at each size:
p = 2 × 0.5³⁰ ≈ 1.9 × 10⁻⁹.** Six independent confirmations of the same result, at six
different sizes, each with the maximum significance a 30-phase design can produce.

**One entry does not fit the pattern**: at N=100, the correlation between a phase's EAGER
mean and its LAZY mean is +0.101, an order of magnitude weaker than at every other size
(+0.73 to +0.97). At every other size, whatever perturbs a phase (thermal state, system
load) perturbs both variants together, which is what produces the strong positive
correlation and is why the relative gap stays stable despite that perturbation (see § 4).
At N=100 this coupling is essentially absent — investigated in § 4.

Distributions remain **strictly disjoint** on `cpu_mj`, `dram_mj` and `time_s` at all six
sizes (checked directly on the pooled 900-sample raw values, not just phase means): the
highest LAZY value is always below the lowest EAGER value.

Breakdown of the gap by component:

| N | CPU | DRAM | GPU |
|---:|---:|---:|---:|
| 10 | 76.4% | 23.5% | 0.1% |
| 30 | 77.3% | 22.6% | 0.1% |
| 50 | 81.5% | 18.4% | 0.1% |
| 100 | 78.0% | 21.9% | 0.1% |
| 500 | 84.2% | 15.7% | 0.1% |
| 1000 | 87.1% | 12.9% | 0.1% |

Same reading as campaign 1: CPU dominates and its share grows with size, GPU is
negligible. The N=100 row is again slightly out of trend (78.0% sits below both its
neighbours, 81.5% at N=50 and 84.2% at N=500) — consistent with the same anomaly.

---

## 3. Consistency with campaign 1

The three sizes tested in both campaigns give a direct check on reproducibility, run on two
different days:

| N | Relative gap, campaign 1 | Relative gap, campaign 2 | Δ | EAGER mean drift | LAZY mean drift |
|---:|---:|---:|---:|---:|---:|
| 10 | 81.0% | 81.30% | +0.3 pt | +0.1% | −1.4% |
| 100 | 86.3% | 86.67% | +0.4 pt | −13.4% | −15.6% |
| 1000 | 90.7% | 90.13% | −0.6 pt | +3.0% | +9.6% |

**The relative gap is remarkably stable** — never moving by more than 0.6 percentage
points between two independent measurement sessions. **The absolute joule levels are not**:
at N=100 both variants' absolute means shifted by 13-16% between campaigns (same machine,
same code, different day — almost certainly ambient temperature or background system load).
This is precisely why campaign 1 warned against quoting absolute joule figures as if they
were portable constants, and why every headline number in both documents is a relative
comparison or a per-row normalisation, never a bare joule total.

Energy per hydrated row at the shared sizes: 0.33 → 0.33 µJ (N=10), 0.43 → 0.38 µJ (N=100),
0.92 → 0.94 µJ (N=1000). N=10 and N=1000 reproduce closely; N=100 is the one point where
the two campaigns disagree by more than rounding — consistent with it being the same
anomalous size in both the correlation breakdown above and the scaling analysis below.

---

## 4. The number to publish, and the complication

### Energy per hydrated row and per parent read

| N | µJ / hydrated row | Range (incl. / excl. high-regime phases) | µJ / parent read |
|---:|---:|---:|---:|
| 10 | 0.33 | 0.30 – 0.33 | 3.3 |
| 30 | 0.36 | 0.34 – 0.36 | 10.8 |
| 50 | 0.47 | 0.47 (no high-regime phases at this size) | 23.5 |
| 100 | 0.38 | 0.38 (no meaningful spread) | 37.7 |
| 500 | 0.66 | 0.61 – 0.66 | 329.9 |
| 1000 | 0.94 | 0.90 – 0.94 | 935.9 |

**Energy per row is not monotonic in N.** It rises from N=10 through N=50, dips at N=100,
then resumes rising through N=500 and N=1000. This is not a rounding effect nor the product
of a single outlier phase: excluding every phase flagged as high-regime at N=100 changes
the figure by under 1%, and the EAGER-only mean (not just the EAGER-minus-LAZY gap) is
itself lower at N=100 (2,175.2 mJ) than at N=50 (2,743.9 mJ). Whatever is happening at
N=100 affects the EAGER variant's own absolute cost, not only its difference from LAZY.

### Scaling exponent — coarse view versus fine view

Campaign 1, with only three points, fitted **N^1.10–1.12** between 10 and 100 and
**N^1.32–1.37** between 100 and 1000, and reported an overall picture of smooth
super-linear growth. Campaign 2's coarse view (using only the shared points, 10 → 100 →
1000) is close: **N^1.05** and **N^1.40** — a reasonable, if not exact, reproduction, well
within what a single-day measurement session can be expected to drift.

The fine view, using all six points, breaks the smooth picture:

| Step | Energy per read | Exponent |
|---|---:|---:|
| N=10 → 30 | x3.25 | +1.07 |
| N=30 → 50 | x2.17 | +1.52 |
| **N=50 → 100** | **x1.60** | **+0.68** |
| N=100 → 500 | x8.75 | +1.35 |
| N=500 → 1000 | x2.84 | +1.50 |

Four of the five steps are super-linear, consistent with campaign 1's reading. **One step,
N=50 → 100, is sub-linear** (exponent below 1, meaning energy per read grows slower than
collection size there — the opposite of the rule's expected direction, though the smell is
still far more expensive than LAZY in absolute terms at both sizes). A single exponent
fitted across all six points on **energy per read** gives **N^1.22** with R² = 0.995 — an
excellent-looking fit that is, on its own, misleading: energy per read spans almost three
orders of magnitude (3.3 µJ to 936 µJ) driven mostly by the trivial fact that N itself
grows by a factor of 100, so a log-log fit on that quantity looks clean even with the kink
present, because the kink is a small wobble relative to the dominant N¹ trend.

Fitting the **same six points on energy per hydrated row** instead — which divides out
that trivial N¹ factor and isolates exactly the part of the cost that is not simple
proportionality — gives exponent ≈ **N^0.22** (i.e. total exponent on energy-per-read of
1 + 0.22 ≈ 1.22, the same headline number) but with **R² = 0.85**, visibly below 1. That
weaker fit is the honest signal that no single power law actually describes the residual,
size-dependent part of the cost: the curve has a genuine kink around N=50–100. Quoting only
the R² = 0.995 energy-per-read fit would hide exactly the anomaly this campaign was
designed to surface; both numbers belong together wherever the exponent is cited.

**What this does not do**: it does not overturn the case for the rule. Even at its lowest
point (N=100), energy per row (0.38 µJ) is still higher than at N=10 (0.33 µJ) and the
relative gap keeps climbing smoothly through the same range (86.67% at N=100, up from
85.79% at N=50). The kink is in the second-order shape of the curve, not in the sign or the
overall order of magnitude of the effect.

---

## 5. Quality control

### The N=100 anomaly

Three independent signals converge on N=100 as an outlier among the six sizes:

1. Phase-level correlation between EAGER and LAZY collapses to +0.101, versus +0.73 to
   +0.97 everywhere else.
2. EAGER's own per-phase `total_mj` mean is unusually flat at N=100 (coefficient of
   variation 1.25%, versus 9–17% at the other five sizes) — every other size shows
   noticeably more phase-to-phase spread on the EAGER side.
3. Energy per row dips at N=100 rather than continuing to climb from N=50.

A plausible reading is that whatever produces the shared, correlated phase-to-phase
variation at other sizes (ambient temperature, background load, core scheduling) happened,
by chance, to leave the EAGER runs at N=100 largely undisturbed while still perturbing
LAZY — which would simultaneously explain the low correlation, the flat EAGER phases, and
the depressed gap. **This is a hypothesis, not a finding**: nothing in the collected data
identifies a mechanism, and it was not reproduced by re-running N=100 within this campaign.
It should be treated as an open question, not folded into the headline scaling narrative.

### Bimodality, the other five sizes

As in campaign 1, several sizes show a subset of phases sitting well above the rest, and
in every one of those cases **both variants are affected together**:

| N | High-regime phases (of 30) | Affects |
|---:|---|---|
| 10 | 5, 7-11, 13, 16 | EAGER and LAZY together |
| 30 | 26-30 | EAGER and LAZY together |
| 50 | none detected | — |
| 500 | 3-10 | EAGER and LAZY together |
| 1000 | 6-11, 15, 24 | EAGER and LAZY together |

This is the same paired-cancellation pattern documented in campaign 1 § 4: because both
variants are measured within the same phase in randomised order, a shared perturbation
moves both means together and the *relative* gap barely shifts (see the stability of the
percentages in § 3). N=100 is the one size where this cancellation mechanism does not
appear to operate, which is consistent with — and adds weight to — treating it as the
outlier.

### `ane_mj`, again excluded

Excluded from every total in this document, as in campaign 1. At N=100 specifically,
`ane_mj` was measured as exactly zero for every one of the 1,800 samples (both variants) —
not just small, but identically zero, which is itself consistent with `ane_mj` being an
unreliable counter on this hardware rather than a real physical measurement (see campaign 1
§ 4 for the same conclusion at other sizes, where `ane_mj` varied by two orders of magnitude
across sizes with no plausible mechanism).

### Average power: locating the crossover

Campaign 1 observed that LAZY draws *more* average power than EAGER at N=10 (a short,
intense burst) but *less* at N=1000 (sustained EAGER memory pressure), and could not say
where the crossover sits. Campaign 2's intermediate sizes locate it, using EnergyTracer's
own reported average power (raw data type):

| N | EAGER | LAZY | Higher average power |
|---:|---:|---:|:---:|
| 10 | 4.373 W | 5.938 W | LAZY |
| 30 | 4.329 W | 4.613 W | LAZY |
| 50 | 5.623 W | 6.479 W | LAZY |
| 100 | 4.258 W | 3.585 W | **EAGER** |
| 500 | 6.242 W | 4.652 W | EAGER |
| 1000 | 7.826 W | 4.087 W | EAGER |

**The crossover sits between N=50 and N=100** — the same interval where the energy-per-row
anomaly and the correlation collapse occur. That three unrelated statistics all shift
character in the same narrow interval is worth flagging to a reviewer even without a
confirmed mechanism: something about the system's behaviour changes around a collection of
roughly fifty to a hundred elements, on top of the average trend the rule is concerned
with.

---

## 6. What not to claim

Everything in campaign 1's § 5 still holds (the percentage is not a production figure, the
tool's p-values are not reusable, the absolute yearly saving is modest, level 1 and level 2
are consistent despite different-looking ratios). Two additions specific to campaign 2:

### Do not present a single clean exponent as if the curve were smooth

**N^1.22 is a legitimate summary statistic for energy per read**, and on that metric alone
the fit looks excellent (R² = 0.995) — but that is because per-read values are dominated by
the trivial N¹ scaling. The same exponent, measured on energy *per row* (R² = 0.85), shows
the fit is visibly imperfect once the trivial factor is removed. Quoting N^1.22 with only
the per-read R² would overstate how well-behaved the relationship is; a reviewer who reruns
the six points and fits their own curve on a per-row basis will find the same kink — better
to have already named it.

### The relevance threshold is not "N ≈ 100"

Campaign 1 tentatively suggested the rule becomes clearly relevant "around a hundred
elements". With six points, that framing does not survive: the **relative** gap is already
81% at N=10, the smallest size tested — there is no size threshold below which the smell
stops mattering in relative terms. What actually grows with N is the **absolute** energy
wasted per parent read (3.3 µJ at N=10 versus 936 µJ at N=1000, roughly 280 times more) and,
independently, the crossover in average power (between N=50 and N=100). The more accurate
statement is that the relative overhead is present at every size tested; its absolute
weight becomes material as the collection and the read rate grow — a statement about
magnitude, not a threshold.

### The N=100 finding should be disclosed, not smoothed away

It would be tempting to quietly drop N=100 from a plot or fit and present a cleaner curve
through the remaining five points. That would hide a real observation, and it is disclosed
throughout this document (§ 4, § 5) rather than omitted.

---

## Sources

- Consolidated report: `level2-energytracer/campaign2/consolidated-report/REPORT.md`
- Consolidated data: `level2-energytracer/campaign2/consolidated-report/data/all_measurements.csv` — 10,800
  measurements (6 sizes × 2 variants × 900)
- Plots: `level2-energytracer/campaign2/consolidated-report/plots/` (including `scaling_overhead.png`)
- Raw EnergyTracer reports: `level2-energytracer/campaign2/consolidated-report/reports-source/`
- Companion document: `level2-energytracer/campaign1/campaign1-interpretation.md`
- Level 1 (unchanged, not re-run for campaign 2): `level1-jpa/results/level1-20260802-120143.txt`

Every figure in this document was recomputed independently from the raw
`all_measurements.csv`, using 3σ outlier removal and phase-level aggregation, rather than
taken from `ET-analyzer`'s own per-sample statistics — for the same reason given in
campaign 1 § 2 (900 runs within a phase are not independent samples).

---

*Internal analysis, kept as evidence that the code smell targeted by S6904 has a real,
measurable cost.*
