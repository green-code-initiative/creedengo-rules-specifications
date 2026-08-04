# Measurement campaign — FetchType LAZY on JPA collections (S6904)

Measurement harness for the rule "force FetchType.LAZY on JPA collections".
Produces the impact evidence missing from the four superseded PRs
(#122, #125, #155, #325), which is what is blocking its acceptance by the core team.

**Status: campaign1 executed and analysed.** Run on 2026-08-04 on a MacBook Pro M1 Pro,
30 phases × 30 iterations, sizes 10 / 100 / 1000. Results archived under
`level2-energytracer/campaign1/`; interpretation and PR description alongside them
(`fetchtype-measurement-interpretation.md`, `fetchtype-pr-description.md`).

**campaign2 in preparation**: sizes 10 / 30 / 50 / 100 / 500 / 1000, to locate the
relevance threshold between 10 and 100 — the question campaign1 leaves open.

---

## 1. Summary

Two levels, numbered in their execution order — simplest and lightest first.

| | Level 1 — JPA | Level 2 — energy |
|---|---|---|
| **Question** | How much useless work actually happens? | How much energy does that useless work cost? |
| **Measures** | SQL statements, hydrated rows, time, allocations | Joules (CPU / DRAM / GPU / ANE) |
| **Tool** | Hibernate Statistics + JDK | EnergyTracer, `mac` profiler |
| **Stack** | Real Hibernate 6 + H2 | Plain Java, simulated row source |
| **Duration** | a few minutes | ~45 min per size |
| **Machine** | any | Apple Silicon Mac, prepared and left undisturbed |
| **Role in review** | The functional proof | The energy proof |

Both are needed, and they answer each other. Level 1 establishes *that* waste happens
and quantifies it in units any reviewer can check. Level 2 establishes *what that waste
costs* in energy. Neither is sufficient alone: level 1 measures no energy, and level 2
runs neither SQL nor an ORM.

### Why this split rather than a single measurement

EnergyTracer's Java runner compiles with a bare `javac <file>` and runs with
`java -cp <tmpdir> <Class>`. **No third-party library is reachable**: not Hibernate, not
H2, not even a JDBC driver. A JPA benchmark therefore cannot be measured directly by
EnergyTracer, and modifying the tool to make that possible is out of scope here — hence
two separate benches, each measuring what it can measure correctly.

---

## 2. Layout

```
./
├── README.md                          this file: protocol and how-to
├── results-template.md                results template to fill in and paste into the PR
├── level1-jpa/
│   ├── pom.xml                        Hibernate 6.4 + H2 2.2, Java 17
│   ├── run.sh                         build + run
│   ├── results/                       generated: jpa-<timestamp>.txt
│   └── src/main/java/org/greencodeinitiative/bench/
│       ├── FetchTypeBenchmark.java       driver and instrumentation
│       ├── eager/{Order,OrderItem}.java   FetchType.EAGER mapping
│       └── lazy/{Order,OrderItem}.java    FetchType.LAZY mapping
│
└── level2-energytracer/
    ├── 1-generate.sh                  generates the variants + checks they compile
    ├── 2-calibrate.sh                 (optional) tunes PARENT_READS for the machine
    ├── 3-run_campaign.sh              the campaign
    ├── 4-aggregate_report.sh          consolidates the campaign into a single report
    ├── 4-aggregate_report.py          (the actual work: stats, plots, markdown)
    │
    ├── generate/
    │   ├── template/Eager.java.tpl    noncompliant variant  (source of truth)
    │   ├── template/Lazy.java.tpl     compliant variant      (source of truth)
    │   └── variants/                  generated: FetchType{Eager,Lazy}N<size>.java
    │
    ├── out-tmp/                       generated: raw per-phase measurements (disposable)
    ├── results/n<size>/                generated: raw ET-analyzer reports
    ├── consolidated-report/           generated: THE deliverable to use
    │
    └── campaign<N>/                   archived campaigns (see § 5)
```

Level 2's scripts are **prefixed by their launch order**. `2-calibrate.sh` is optional:
it only checks or tunes the bench's sizing on a new machine. `1-generate.sh` does not
need to be run by hand: `3-run_campaign.sh` triggers it automatically whenever a variant
is missing.

The two JPA mappings are **identical except for one attribute** (`fetch = EAGER` vs
`LAZY`) and point at the same tables. Same at level 2: the two variants differ only in
the body of `loadOrder`. That is the condition for the comparison to mean anything.

---

## 3. The scenario measured

The one the rule targets, and only that one:

> A parent entity owns a collection of children. The application loads the parent and
> **never accesses the collection**.

- Under `EAGER`, the provider hydrates the whole collection anyway.
- Under `LAZY`, it only allocates the collection wrapper.

Several sizes are swept, because the effect grows with collection size. A single figure
would say nothing; a sweep documents from which size the rule becomes relevant, which is
exactly what a demanding reviewer will ask for.

| Campaign | Sizes | Purpose |
|---|---|---|
| campaign1 | 10, 100, 1000 | Establish that the effect exists, and its order of magnitude |
| campaign2 | 10, 30, 50, 100, 500, 1000 | Locate the relevance threshold between 10 and 100 |

`1-generate.sh` maintains an **important invariant**: `PARENT_READS × N ≈ 5,000,000`
hydrated child rows per run, regardless of size. That is what makes the sizes comparable
at equal nominal work, and it is the basis of the "energy per hydrated row" figure, the
only quantity that is genuinely transposable. The `PARENT_READS` values for 10, 100 and
1000 are **identical to campaign1's**, so the two campaigns stay comparable on those
points.

*Note: N=30 is the only inexact value (166,667 × 30 = 5,000,010, 10 rows off out of five
million — negligible). No integer divides 5,000,000 exactly by 30.*

### Conservative choices, to own in review

Three decisions consistently **underestimate** the gap. Worth stating before anyone
objects:

1. Level 2 allocates an empty `HashSet` on the LAZY side (modelling the Hibernate
   wrapper) rather than `Collections.emptySet()`: the compliant variant is not free.
2. Both level-2 variants allocate the same child-row buffer, even though LAZY never reads
   it: identical starting memory footprint.
3. The owning-side `@ManyToOne` is `LAZY` in both level-1 mappings, so no difference
   sneaks in there.

---

## 4. Preparing the machine

This section applies **only to level 2**. Level 1 runs on any machine, no special care
needed.

Taken from EnergyTracer's `EXPERIMENT_GUIDE.md`, itself based on Luís Cruz's guide.
**This step is not optional**: without it, background noise dwarfs the effect being
measured and the result is worthless.

- [ ] Close **all** non-essential applications, including menu-bar utilities
- [ ] Disconnect unnecessary peripherals (external drives, printers…)
- [ ] Wired network, or no network at all
- [ ] Fix brightness, volume, power mode; disable sleep and screen saver
- [ ] Turn on "Do Not Disturb"
- [ ] **Plug in the power adapter** (a campaign runs for several hours)
- [ ] Stable ambient temperature, out of direct sunlight
- [ ] **Do not touch the machine** once the campaign has started

Software prerequisites:

- For level 1: Maven and a JDK 17+.
- For level 2: a **JDK** (not just a JRE — `javac` is required),
  [`uv`](https://github.com/astral-sh/uv), and a clone of
  `https://github.com/green-code-initiative/EnergyTracer` initialised with `./init.sh`.

---

## 5. Running it

The levels are numbered in execution order: increasing complexity and duration.

### Level 1 — JPA (a few minutes)

Fast, no requirement on the machine, and it tells you immediately whether the effect
exists. No point tying up a Mac for hours on an energy campaign if level 1 shows no gap.

```bash
cd level1-jpa
./run.sh
```

Expected output: one table per size with, per parent read, the number of JDBC
statements, hydrated entities, loaded collections, time and allocated bytes — then the
EAGER/LAZY ratios, plus a reusable CSV block.

### Level 2 — energy (budget ~45 min per size)

Scripts are numbered in launch order.

```bash
cd level2-energytracer

./2-calibrate.sh                             # OPTIONAL: checks the bench's sizing
./3-run_campaign.sh ~/src/EnergyTracer mac   # the campaign — calls 1-generate.sh if needed
./4-aggregate_report.sh ~/src/EnergyTracer   # the consolidated report
```

`1-generate.sh` does not need to be run by hand: `3-run_campaign.sh` triggers it
automatically as soon as a variant is missing for one of the requested sizes.

Sizes: default **10, 30, 50, 100, 500, 1000**. To restrict:

```bash
./3-run_campaign.sh ~/src/EnergyTracer mac 10 100 1000
```

**Duration.** About 45 minutes per size at default settings, half of it cooldown. Six
sizes therefore add up to **close to 5 hours** — best started at the end of the day.

`2-calibrate.sh` is not decorative, even though it is optional. EnergyTracer launches
**one JVM per iteration**, and JVM startup costs 100 to 400 ms. If a run only lasts 20 ms,
95% of what is measured is JVM startup and the EAGER/LAZY gap disappears into the noise.
Aim for 300 to 500 ms per run. Rerun it on any new machine, or after changing
`PARENT_READS`.

### Archiving a campaign

`3-run_campaign.sh` always writes to `out-tmp/` and `results/`. Once the consolidated
report has been built, move the three directories into `campaign<N>/` so the next
campaign starts clean:

```bash
mkdir -p campaign2 && mv out-tmp results consolidated-report campaign2/
```

An archived campaign stays analysable in place:

```bash
./4-aggregate_report.sh ~/src/EnergyTracer -- \
    --out-tmp campaign1/out-tmp --output campaign1/consolidated-report
```

**Do not merge two campaigns** into one report: they run under different thermal and
system conditions. Comparing them size by size is legitimate and informative;
statistically pooling them is not.

Campaign parameters, overridable via environment variable:

| Variable | Default | Role |
|---|---|---|
| `WARMUP_PHASES` / `WARMUP_N` | 5 / 5 | Reach thermal steady state |
| `MEASURE_PHASES` / `MEASURE_N` | 30 / 30 | 900 samples per variant |
| `COOLDOWN` | 60 | Seconds between measurement phases |

These values deliberately depart from EnergyTracer's own defaults (500 / 1000). Its
examples measure a few-millisecond arithmetic loop, entirely dominated by JVM startup,
hence the need for tens of thousands of samples. Here each run does ~400 ms of real work:
900 samples are ample, and the campaign fits in a few hours instead of several days.

For a first exploratory pass: `MEASURE_PHASES=10 MEASURE_N=10 ./3-run_campaign.sh ...`
(~25 min). Do not publish those figures — they only serve to check the pipeline works.

---

## 6. Interpreting and publishing

`ET-analyzer` produces a Markdown report per size, under
`level2-energytracer/results/n<size>/`, directly pastable into a PR: comparison table,
Welch's t-test (α = 0.05), Cohen's d, and a verdict.

Raw measurements land in `level2-energytracer/out-tmp/`, outside the EnergyTracer clone,
which stays untouched. Once a campaign is archived under `campaign<N>/`, this directory
is kept: it lets the report be regenerated, and it is the only record of the individual
measurements.

Two pitfalls in the tool, both handled by `3-run_campaign.sh` — worth knowing if you
adapt the script:

- **`-o` is not an output path**, it is a tag: `src/main.py` computes
  `Path("output") / <profiler> / <value of -o>`. A relative value therefore lands inside
  the EnergyTracer clone. An **absolute** value overrides the whole prefix (`pathlib`
  behaviour) and writes exactly where requested.
- **`ET-analyzer -p D` infers the profiler from the path component right after `D`**
  (`profiler = parts[parts.index(first_folder) + 1]`). Pointing it one level too deep
  does not error out: it produces a report labelled `raw` instead of `mac`. And pointing
  it at the common root would merge every size into one meaningless report. Hence one
  analysis per size, `-p out-tmp/fetchtype-n<size>`.

### Consolidating the campaign

`ET-analyzer` produces one report per size, with no cross-size view and no plots.
`4-aggregate_report.sh` fills both gaps:

```bash
cd level2-energytracer
./4-aggregate_report.sh ~/git_creedengo2/EnergyTracer
```

Passing the EnergyTracer clone avoids any installation: its virtual environment already
has pandas, numpy, scipy and matplotlib. Output under `consolidated-report/`:

| | |
|---|---|
| `REPORT.md` | the consolidated document, ready to use for the PR |
| `data/` | every phase concatenated, with `size`, `phase`, `variant` columns added |
| `plots/n<size>/` | box, violin, and **per-phase drift**, redrawn on the full sample |
| `plots/scaling_overhead.png` | the gap as a function of collection size |
| `reports-source/` | `ET-analyzer`'s own reports, unmodified, for traceability |

Three additions over the tool's native output:

- **Plots finally cover the whole campaign.** `ET`'s own plots are generated per phase,
  so they only ever show one slice — pasting them into a PR would be misleading.
- **A per-phase drift plot.** If the curves climb across phases, the machine heated up or
  was disturbed and the campaign should be rerun. Check this before anything else.
- **Mann-Whitney U and Cliff's delta**, alongside Welch's test. EnergyTracer's own guide
  recommends them, because energy data is rarely normal. The report flags metrics where
  Shapiro-Wilk rejects normality, and only concludes when both tests agree.

The `--data-type` option defaults to `raw`, and that is the right choice: `raw` is
filtered once, globally, on the full aggregate, whereas `cleaned` has already been
filtered per phase on small samples — a weaker, doubly-filtered pipeline.

The script also picks up data produced by the earlier version of `3-run_campaign.sh`
(without the `phase-<i>` level), reported as a single phase.

Then transcribe the figures into `results-template.md`, which pre-structures the
write-up and already lists the limitations to state.

### What to look at, in order

1. **The direction of the gap.** A positive `Δ mean` means the smelly variant consumes
   more. A negative or null gap on a given size is useful information, not a failure: it
   documents the threshold below which the rule is not worth applying.
2. **The dominant component.** If the gap is carried by `dram_mj`, that is memory
   allocation; by `cpu_mj`, that is hydration. This says *where* the energy goes.
3. **The variance.** Wide box plots signal a poorly controlled environment — redo the
   machine preparation before interpreting anything.
4. **The trend with N.** This is the strongest argument: if the gap grows with collection
   size, a causal mechanism is established, not just a correlation.

### The trap to avoid

With 900 samples per variant, a 0.4% gap comes out "statistically significant" with a
flattering Cohen's d. **Significant does not mean relevant.** Report the relative gap
next to the p-value, and do not defend the rule on a gain of a few tenths of a percent —
that would hand a careful reviewer an easy stick to beat it with.

---

## 7. Limitations to state in the PR

Hiding them would be both dishonest and counterproductive: a project reviewer will find
them anyway. Stating them upfront strengthens the measurement's credibility.

**Level 1 — JPA**

- In-memory H2: **no network latency, no disk I/O**. This is the least favourable setup
  for the demonstration — on a remote RDBMS the gap would be markedly wider. Worth
  saying, since it turns an apparent weakness into an argument.
- One session per read, so no first-level cache between reads. This is deliberate:
  without it, the second read would be free and the measurement pointless.
- The second-level cache is disabled.
- Time and allocations are measured on a single thread, with no contention.

**Level 2 — energy**

- The row source is **simulated**: no database engine, no network, no JDBC driver. What
  is measured is the client-side share (deserialisation, allocation, collection insert).
  The database-server cost and the network transfer — likely the largest share of the
  real saving — **are not measured**. The figure is therefore a **lower bound**.
- **The relative gap does not transpose to production.** Both variants do almost nothing
  but hydration: the LAZY variant does almost nothing at all. An 80% gap means "hydrating
  the collection is 80% of this synthetic program", not "a real application would consume
  80% less". In production this cost is diluted by SQL, network and business logic.
  Publishing the **absolute energy per operation** is far more defensible than the
  percentage.
- Each iteration launches a JVM. Absolute values include that startup; only the
  **relative Java-vs-Java comparison** is valid, as EnergyTracer itself documents.
- Runs are short, so the JIT never reaches steady state: this is mostly interpreted or
  C1-compiled code, not the hot C2 code of a long-running production service.
- The `mac` profiler reads Apple Silicon hardware counters. On another machine, only the
  `carbon` profiler is available — an **estimate** (TDP × CPU utilisation), never to be
  presented as a hardware measurement.
- `ET-analyzer` applies a Welch t-test, which assumes near-normality. EnergyTracer's own
  guide recommends complementing it with Mann-Whitney U, Cliff's delta and 95% confidence
  intervals on the medians. For evidence meant to settle a core-team decision, do both.

---

## 8. Harness validation status

**Proven by campaign1**

- Both levels run end to end: the Maven project builds, the variants compile, the
  campaign and the aggregation produce their reports.
- The contract of EnergyTracer's Java runner, read from its source (`java_runner.py`,
  `main.py`, `parser.py`, `analyzer.py`): classpath-free compilation, class-name detection
  by regex, one JVM per iteration, checked exit code, no argument passed through, `-o`
  read as a tag rather than a path, profiler inferred from the path component right after
  the one passed to `-p`.
- The bench's sizing: `PARENT_READS` does place EAGER runs in the targeted window
  (0.49 s at N=10 up to 0.67 s at N=1000).

**Fixed along the way, worth knowing if you are rereading old reports**

- An early version of `3-run_campaign.sh` reused the same output directory on every
  phase. Since EnergyTracer writes its CSVs in overwrite mode, **only the last phase was
  ever analysed**. Every phase now writes under its own `phase-<i>/`. Any report predating
  this fix covers a much smaller sample than it claims.

**Unresolved, and worth a dedicated run**

- **How much of the super-linearity is attributable to the simulated persistence
  context.** `CONTEXT_SIZE` retains 1,024 parents, so the live set grows mechanically
  with N, and no `-Xmx` is fixed. This is the most likely angle of attack in review on the
  dossier's strongest argument. A sensitivity study on those two parameters would resolve
  the ambiguity.
- **Repeating level 1**: its headline ratios (x12.65 in time, x32 in allocation) come from
  a single run, with only 100 reads at N=1000, even though these are the figures put
  forward.
- **The bimodality observed** over 2 to 8 phases depending on size (+23% to +48%), not
  contiguous. It does not affect the relative gap, but its cause remains unknown.

---

## 9. Feeding the result back into the specification

Once figures are in hand, two places to update:

1. **The PR description** — the level-1 table, plus the consolidated energy report. This
   is where the evidence is discussed.
2. **`src/main/rules/S6904/java/S6904.asciidoc`**, `== Resources` section — a link to the
   PR or to this harness. Rule files in this repository carry no measurement section: do
   not paste the report there, it would break the convention. `results-template.md`
   proposes compact wording for that section.
3. **`RULES.md`**, `Reference/Validation` column, currently empty on every row of the
   matrix — filling it in is a welcome gesture.

Reminder: the project's `starter-pack.md` **does not require** an energy measurement. The
Definition of Done is purely software-based (unit tests, integration tests, CHANGELOG);
the only evidentiary requirement is "give references proving the benefit of the rule". An
EnergyTracer measurement answers that well beyond the project's norm — an asset to
highlight, not a box to tick.

---

*Internal working document. Content intended for public publication on GitHub: review and
validate before releasing.*
