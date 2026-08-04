# Results template — FetchType LAZY measurement (S6904)

Fill in after a campaign, then paste the "For the PR" section into the Pull Request
description. `<...>` placeholders are to be filled in; italic instructions are to be
deleted before publishing.

---

## Internal record

### Run context

| | |
|---|---|
| Date | `<YYYY-MM-DD>` |
| Machine | `<model>` — `<chip>` — `<RAM>` |
| OS | `<version>` |
| JDK | `<version>` |
| EnergyTracer profiler | `<mac \| carbon>` |
| Phases / iterations | `<MEASURE_PHASES> × <MEASURE_N>` = `<total>` samples per variant |
| Sizes covered | `<e.g. 10, 30, 50, 100, 500, 1000>` |
| Campaign archive | `<level2-energytracer/campaign<N>>` |
| Machine preparation | `<README § 4 checklist fully applied: yes / no — if no, explain>` |

### Level 1 — functional counters (JPA)

*Copy the table produced by `level1-jpa/run.sh`.*

| fetch | N | statements/read | entities/read | collections/read | µs/read | KiB/read |
|---|---:|---:|---:|---:|---:|---:|
| EAGER | | | | | | |
| LAZY | | | | | | |

Overhead EAGER / LAZY per read:

| N | time | allocation | extra rows hydrated |
|---:|---:|---:|---:|
| | ×`<...>` | ×`<...>` | +`<...>` |

### Level 2 — energy (EnergyTracer)

*One row per size, taken from `campaign<N>/consolidated-report/REPORT.md` — use the
`raw` data type (filtered once, globally) rather than `cleaned` (filtered per phase on
small samples, then again globally: a weaker pipeline). Prefer the phase-level analysis
over the tool's own per-sample statistics — see the note below.*

| N | Δ `cpu_mj` | Δ `dram_mj` | Δ `time_s` | Sign test (phases) | Cliff's δ | Verdict |
|---:|---:|---:|---:|---:|---:|:---:|
| | | | | | | |

> **Why the sign test, not the tool's p-values.** Runs within one phase share thermal and
> system state, so they are not independent — treating hundreds of runs as independent
> samples (what `ET-analyzer` does) produces p-values that look dramatic but do not survive
> scrutiny. Reanalyse at the phase level (n = number of measurement phases) and report a
> sign test across phases plus Cliff's delta. See `campaign1/fetchtype-measurement-interpretation.md`
> § 2 for the worked example.

Total energy per run:

| N | With smell (EAGER) | Without smell (LAZY) | Gap |
|---:|---:|---:|---:|
| | `<...>` mJ | `<...>` mJ | `<...>` % |

Energy per hydrated row — the transposable figure, independent of harness calibration:

| N | µJ / hydrated row |
|---:|---:|
| | `<...>` |

### Reading

- Size range where the gap becomes materially relevant: `<N = ...>`
- Exponent of the size-vs-energy relationship (see aggregate report scaling table): `<N^...>`
- Dominant component of the gap: `<cpu_mj | dram_mj | ...>`
- Variance / bimodality observed: `<none / some phases in a high regime, unaffecting the relative gap / problematic — if problematic, do not publish>`
- Anomalies, outliers, phases excluded: `<...>`
- If a previous campaign exists: consistency of overlapping sizes with `<campaign<N-1>>`: `<...>`

---

## For the PR — paste into the description

*Everything below is in English, like the rest of the repository.*

```markdown
## Measurement of the rule's impact

None of the four superseded PRs (#122, #125, #155, #325) provided any measurement. This
section fills that gap. The full harness is available at <link to the harness or branch>.

### Scenario

A parent entity owns a collection association. The application loads the parent and **never
accesses the collection** — the exact case this rule targets. Several collection sizes were
tested (<list sizes>), because the effect is proportional to that size.

### Functional cost (Hibernate 6 + H2, per parent read)

| Collection size | Extra rows hydrated | Time | Heap allocation |
|---:|---:|---:|---:|
| <N> | +<...> | x<...> | x<...> |

### Energy cost (EnergyTracer, `<profiler>` profiler, <n> samples per variant)

| Collection size | CPU energy | DRAM energy | Wall time | Sign test across phases |
|---:|---:|---:|---:|:---:|
| <N> | <...>% | <...>% | <...>% | <...> |

<paste the ET-analyzer report for the most representative size here, in a <details> block
 so it does not overwhelm the description>

### Reading

<1 to 3 sentences. State the collection size from which the rule is worth applying, and
 which hardware component drives the gap. Do not over-interpret a gap of a few tenths of a
 percent, even if "statistically significant" by the tool's own test.>

### Limitations

Stated up front, since they matter for how much weight this evidence should carry:

- The energy benchmark runs on a **simulated row source**: no database engine, no network, no
  JDBC driver. It measures the client-side cost only, so it is a **lower bound** — the database
  and network share of the saving is not captured.
- EnergyTracer launches one JVM per iteration, so absolute values include JVM startup. Only the
  relative Java-vs-Java comparison is meaningful, as EnergyTracer itself documents.
- Runs are short, so the JIT never reaches steady state: this is mostly interpreted / C1 code,
  not the C2-compiled code of a long-running production service.
- The functional benchmark uses in-memory H2: **no network latency, no disk I/O**. This is the
  least favourable setup for the demonstration; on a remote RDBMS the gap would widen.
- Runs within a phase are correlated; figures above are reported at the phase level (sign
  test, Cliff's delta), not from the tool's raw per-sample p-values.
- <if `carbon` profiler> The `carbon` profiler is a **software estimate** (CPU TDP x
  utilisation), not a hardware measurement.
- Measurements were taken on a single machine. Independent reproduction is welcome — the
  harness is self-contained.
```

### Line for `RULES.md`, `Reference/Validation` column

```markdown
[energy measurement](<link to the PR or the report>)
```

### `== Resources` section of `S6904.asciidoc`

*The repository's asciidoc format has no measurement section: do not paste the report there,
it would break the convention. A link is enough.*

```asciidoc
=== Benchmarks

- <link to the PR>[Energy and allocation benchmark] - EAGER vs LAZY collection fetching,
  measured with EnergyTracer across several collection sizes
```

---

## Before publishing

- [ ] Published figures come from a full campaign, not an exploratory pass
- [ ] The README § 4 machine-preparation checklist was fully applied
- [ ] The profiler used is named explicitly, and an estimate is never presented as a measurement
- [ ] Figures are reported at the phase level (sign test, Cliff's delta), not from the tool's raw per-sample p-values
- [ ] Limitations are in the PR itself, not only in internal notes
- [ ] No gap under ~1% is presented as an argument, even if "significant"
- [ ] Content intended for GitHub has been reviewed and validated before publishing
