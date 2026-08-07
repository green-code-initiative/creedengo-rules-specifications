#!/usr/bin/env python3
"""
Builds a single consolidated report directory from a full EnergyTracer campaign.

Why this exists
---------------
EnergyTracer splits its output in a way that is fine for one run but awkward for a
campaign made of many phases:

* `ET` writes CSVs and plots once per phase, under
  `<size>/<profiler>/phase-<i>/{raw,cleaned}/{csv,plots}/`. The plots therefore only
  ever show ONE phase - a small slice of the campaign - which makes them misleading
  to paste into a pull request.
* `ET-analyzer` merges the CSVs and writes a Markdown report, but never re-plots, and
  produces one report per (size, data type) with no cross-size view.

This script closes both gaps: it concatenates every phase, recomputes the statistics on
the whole sample, redraws the plots from the complete data, and assembles everything -
including EnergyTracer's own reports, kept verbatim - into one directory.

Statistics
----------
The mean/Welch/Cohen's d pipeline mirrors `src/analysis/statistical_analysis.py` of
EnergyTracer so the numbers stay comparable with its reports (same 3-sigma z-score
outlier removal, same pooled-std Cohen's d, same alpha, same delta convention).

It adds Mann-Whitney U and Cliff's delta, which EnergyTracer's own EXPERIMENT_GUIDE
recommends for energy data - energy samples are rarely normally distributed, and a
Welch t-test alone is easy to attack in review.

Usage
-----
    ./4-aggregate_report.sh ~/git_creedengo2/EnergyTracer    # recommended wrapper
    python3 4-aggregate_report.py --help                     # direct, needs the deps

Requires pandas, numpy, scipy and matplotlib - all already present in the EnergyTracer
virtualenv, which is what the wrapper script uses.
"""

from __future__ import annotations

import argparse
from dataclasses import dataclass
from pathlib import Path
import platform
import re
import shutil
import sys

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt  # noqa: E402
import numpy as np  # noqa: E402
import pandas as pd  # noqa: E402
from scipy import stats  # noqa: E402

# ── Constants kept aligned with EnergyTracer ──────────────────────────────────

METRICS = ("cpu_mj", "gpu_mj", "ane_mj", "dram_mj", "time_s")
ENERGY_METRICS = ("cpu_mj", "gpu_mj", "dram_mj", "ane_mj")
ALPHA = 0.05
ZSCORE_THRESHOLD = 3
MIN_SAMPLE_SIZE = 25

FIGURE_SIZE = (10, 6)
FIGURE_DPI = 150

LABEL_WITH = "EAGER (with smell)"
LABEL_WITHOUT = "LAZY (without smell)"


# ── Statistics (mirrors src/analysis/statistical_analysis.py) ─────────────────


def remove_outliers_zscore(values: list[float]) -> list[float]:
    """Drop points more than 3 standard deviations from the mean."""
    if len(values) < 2:
        return list(values)
    arr = np.asarray(values, dtype=float)
    std = np.std(arr, ddof=1)
    if std == 0:
        return list(values)
    mean = np.mean(arr)
    return [x for x in values if abs(mean - x) <= ZSCORE_THRESHOLD * std]


def cohens_d(a: list[float], b: list[float]) -> float:
    """Effect size using the pooled standard deviation, as EnergyTracer defines it."""
    x, y = np.asarray(a, dtype=float), np.asarray(b, dtype=float)
    if np.ptp(x) == 0 and np.ptp(y) == 0:
        return 0.0
    pooled = np.sqrt((np.std(x, ddof=1) ** 2 + np.std(y, ddof=1) ** 2) / 2)
    return 0.0 if pooled == 0 else float((np.mean(x) - np.mean(y)) / pooled)


def effect_label(d: float) -> str:
    a = abs(d)
    if a < 0.2:
        return "negligible"
    if a < 0.5:
        return "small"
    if a < 0.8:
        return "medium"
    return "large"


def cliffs_delta(a: list[float], b: list[float]) -> tuple[float, str]:
    """
    Non-parametric effect size, derived from the Mann-Whitney U statistic
    (delta = 2U / (n1 n2) - 1) to stay O(n log n) instead of comparing every pair.

    Thresholds are Romano et al.'s conventional ones.
    """
    n1, n2 = len(a), len(b)
    if n1 == 0 or n2 == 0:
        return float("nan"), "n/a"
    u = stats.mannwhitneyu(a, b, alternative="two-sided").statistic
    delta = float(2 * u / (n1 * n2) - 1)
    m = abs(delta)
    if m < 0.147:
        label = "negligible"
    elif m < 0.33:
        label = "small"
    elif m < 0.474:
        label = "medium"
    else:
        label = "large"
    return delta, label


@dataclass
class MetricStats:
    metric: str
    unit: str
    n_with: int
    n_without: int
    mean_with: float
    mean_without: float
    median_with: float
    median_without: float
    std_with: float
    std_without: float
    delta_pct: float
    welch_p: float
    welch_sig: bool
    d: float
    d_label: str
    mw_p: float
    mw_sig: bool
    cliff: float
    cliff_label: str
    normal_with: bool
    normal_without: bool


def shapiro_normal(values: list[float]) -> bool:
    """Shapiro-Wilk normality check, subsampled above 5000 points as scipy advises."""
    arr = np.asarray(values, dtype=float)
    if len(arr) < 3 or np.ptp(arr) == 0:
        return False
    if len(arr) > 5000:
        arr = np.random.default_rng(seed=42).choice(arr, size=5000, replace=False)
    return bool(stats.shapiro(arr).pvalue > ALPHA)


def analyse_metric(
    df_with: pd.DataFrame, df_without: pd.DataFrame, metric: str, unit: str
) -> MetricStats | None:
    if metric not in df_with.columns or metric not in df_without.columns:
        return None

    a = remove_outliers_zscore(df_with[metric].dropna().tolist())
    b = remove_outliers_zscore(df_without[metric].dropna().tolist())
    if len(a) < 2 or len(b) < 2:
        return None

    arr_a, arr_b = np.asarray(a), np.asarray(b)
    mean_a, mean_b = float(np.mean(arr_a)), float(np.mean(arr_b))

    if np.ptp(arr_a) == 0 or np.ptp(arr_b) == 0:
        welch_p, welch_sig = 1.0, False
    else:
        welch_p = float(stats.ttest_ind(a, b, equal_var=False).pvalue)
        welch_sig = welch_p < ALPHA

    mw_p = float(stats.mannwhitneyu(a, b, alternative="two-sided").pvalue)
    cliff, cliff_label = cliffs_delta(a, b)
    d = cohens_d(a, b)

    # Same convention as EnergyTracer: positive means the smell costs more.
    delta = (mean_a - mean_b) / mean_a * 100 if mean_a != 0 else float("nan")

    return MetricStats(
        metric=metric,
        unit=unit,
        n_with=len(a),
        n_without=len(b),
        mean_with=mean_a,
        mean_without=mean_b,
        median_with=float(np.median(arr_a)),
        median_without=float(np.median(arr_b)),
        std_with=float(np.std(arr_a, ddof=1)),
        std_without=float(np.std(arr_b, ddof=1)),
        delta_pct=delta,
        welch_p=welch_p,
        welch_sig=welch_sig,
        d=d,
        d_label=effect_label(d),
        mw_p=mw_p,
        mw_sig=mw_p < ALPHA,
        cliff=cliff,
        cliff_label=cliff_label,
        normal_with=shapiro_normal(a),
        normal_without=shapiro_normal(b),
    )


# ── Discovery ─────────────────────────────────────────────────────────────────


@dataclass
class Shard:
    size: int
    profiler: str
    phase: str
    variant: str  # "with" | "without"
    path: Path


# Size directories are matched on the "-n<digits>" suffix only, not on a fixed
# prefix: campaigns archived under an older naming scheme stay readable, and a
# future rename of the harness will not silently make past data invisible.
SIZE_RE = re.compile(r"-n(\d+)$")
PHASE_RE = re.compile(r"^phase-(\d+)$")

# Filled by discover(): size -> directory that holds it, so the ET-analyzer
# reports can be located later whatever the prefix happens to be.
SIZE_DIRS: dict[int, Path] = {}


def discover(out_tmp: Path, data_type: str) -> list[Shard]:
    """
    Walk out-tmp and index every per-phase CSV.

    Tolerates the older layout that had no `phase-<i>` level, so data collected
    before that fix can still be salvaged (it is reported as phase "single").
    """
    shards: list[Shard] = []
    SIZE_DIRS.clear()
    for size_dir in sorted(out_tmp.glob("*-n*")):
        m = SIZE_RE.search(size_dir.name)
        if not m or not size_dir.is_dir():
            continue
        if size_dir.name.startswith("warmup"):
            continue  # warm-up shards are not measurements
        size = int(m.group(1))
        SIZE_DIRS[size] = size_dir

        for csv_path in size_dir.rglob(f"{data_type}/csv/history_*_smell.csv"):
            stem = csv_path.stem
            variant = "without" if "without_smell" in stem else "with"

            rel = csv_path.relative_to(size_dir).parts
            profiler = rel[0] if rel else "unknown"
            phase = next(
                (p for p in rel if PHASE_RE.match(p)),
                "single",
            )
            shards.append(Shard(size, profiler, phase, variant, csv_path))
    return shards


def load(shards: list[Shard]) -> pd.DataFrame:
    frames = []
    for s in shards:
        try:
            df = pd.read_csv(s.path)
        except Exception as exc:  # noqa: BLE001
            print(f"  skipped {s.path}: {exc}", file=sys.stderr)
            continue
        if df.empty:
            continue
        df["size"] = s.size
        df["profiler"] = s.profiler
        df["phase"] = s.phase
        df["variant"] = s.variant
        frames.append(df)
    if not frames:
        return pd.DataFrame()
    return pd.concat(frames, ignore_index=True)


# ── Plots ─────────────────────────────────────────────────────────────────────


def unit_for(metric: str, profiler: str) -> str:
    if metric == "time_s":
        return "s"
    if metric == "ane_mj" and profiler == "carbon":
        return "mg CO2eq"
    return "mJ"


def display_name(metric: str, profiler: str) -> str:
    if metric == "ane_mj" and profiler == "carbon":
        return "co2_eq"
    return metric


def plot_distributions(
    df_with: pd.DataFrame, df_without: pd.DataFrame, metric: str,
    profiler: str, out_dir: Path, size: int,
) -> list[Path]:
    a = remove_outliers_zscore(df_with[metric].dropna().tolist())
    b = remove_outliers_zscore(df_without[metric].dropna().tolist())
    if len(a) < 2 or len(b) < 2:
        return []

    unit = unit_for(metric, profiler)
    name = display_name(metric, profiler)
    written = []

    fig, ax = plt.subplots(figsize=FIGURE_SIZE)
    ax.boxplot([a, b], tick_labels=[LABEL_WITH, LABEL_WITHOUT], showmeans=True)
    ax.set_ylabel(f"{name} ({unit})")
    ax.set_title(f"{name} - distribution, N={size} (all phases, {len(a)}+{len(b)} samples)")
    ax.grid(True, alpha=0.3)
    p = out_dir / f"{name}_box.png"
    fig.savefig(p, dpi=FIGURE_DPI, bbox_inches="tight")
    plt.close(fig)
    written.append(p)

    fig, ax = plt.subplots(figsize=FIGURE_SIZE)
    parts = ax.violinplot([a, b], positions=[1, 2], showmeans=True, showmedians=True)
    for body in parts["bodies"]:
        body.set_facecolor("lightblue")
        body.set_alpha(0.7)
        body.set_edgecolor("black")
    ax.set_xticks([1, 2])
    ax.set_xticklabels([LABEL_WITH, LABEL_WITHOUT])
    ax.set_ylabel(f"{name} ({unit})")
    ax.set_title(f"{name} - density, N={size} (all phases)")
    ax.grid(True, alpha=0.3)
    p = out_dir / f"{name}_violin.png"
    fig.savefig(p, dpi=FIGURE_DPI, bbox_inches="tight")
    plt.close(fig)
    written.append(p)

    return written


def plot_phase_drift(
    df: pd.DataFrame, metric: str, profiler: str, out_dir: Path, size: int
) -> Path | None:
    """
    Mean per phase. Flat lines mean the campaign was thermally stable; a trend means
    the environment drifted and the campaign should be rerun before publishing.
    """
    if "phase" not in df.columns or df["phase"].nunique() < 2:
        return None

    def phase_key(p: str) -> int:
        m = PHASE_RE.match(p)
        return int(m.group(1)) if m else 0

    phases = sorted(df["phase"].unique(), key=phase_key)
    unit = unit_for(metric, profiler)
    name = display_name(metric, profiler)

    fig, ax = plt.subplots(figsize=FIGURE_SIZE)
    for variant, label in (("with", LABEL_WITH), ("without", LABEL_WITHOUT)):
        means = []
        for ph in phases:
            vals = df[(df["variant"] == variant) & (df["phase"] == ph)][metric].dropna()
            means.append(np.mean(remove_outliers_zscore(vals.tolist())) if len(vals) else np.nan)
        ax.plot(range(1, len(phases) + 1), means, marker="o", label=label)

    # Phases are integers: let matplotlib interpolate ticks and you get "1.25".
    ax.set_xticks(range(1, len(phases) + 1))
    ax.set_xlabel("Measurement phase")
    ax.set_ylabel(f"mean {name} ({unit})")
    ax.set_title(f"{name} - stability across phases, N={size}")
    ax.legend(fontsize="small")
    ax.grid(True, alpha=0.3)
    p = out_dir / f"{name}_per_phase.png"
    fig.savefig(p, dpi=FIGURE_DPI, bbox_inches="tight")
    plt.close(fig)
    return p


def plot_scaling(
    per_size: dict[int, list[MetricStats]], out_dir: Path
) -> Path | None:
    """
    Overhead versus collection size. This is the strongest argument available:
    an effect that grows with N establishes the causal mechanism, not just a correlation.
    """
    sizes = sorted(per_size)
    if len(sizes) < 2:
        return None

    metrics = [m for m in ("cpu_mj", "dram_mj", "time_s") if any(
        any(s.metric == m for s in per_size[sz]) for sz in sizes
    )]
    if not metrics:
        return None

    fig, ax = plt.subplots(figsize=FIGURE_SIZE)
    for metric in metrics:
        ys = []
        for sz in sizes:
            st = next((s for s in per_size[sz] if s.metric == metric), None)
            ys.append(st.delta_pct if st else np.nan)
        ax.plot(range(len(sizes)), ys, marker="o", label=metric)

    ax.axhline(0, color="black", linewidth=0.8)
    ax.set_xticks(range(len(sizes)))
    ax.set_xticklabels([str(s) for s in sizes])
    ax.set_xlabel("Collection size (children per parent)")
    ax.set_ylabel("Overhead of EAGER over LAZY (%)")
    ax.set_title("Does the effect grow with collection size?")
    ax.legend(fontsize="small")
    ax.grid(True, alpha=0.3)
    p = out_dir / "scaling_overhead.png"
    fig.savefig(p, dpi=FIGURE_DPI, bbox_inches="tight")
    plt.close(fig)
    return p


# ── Markdown assembly ─────────────────────────────────────────────────────────


def fmt_p(p: float) -> str:
    if np.isnan(p):
        return "n/a"
    return f"{p:.2e}" if p < 0.001 else f"{p:.4f}"


def stats_table(rows: list[MetricStats]) -> list[str]:
    out = [
        "| Metric | Mean EAGER | Mean LAZY | Δ mean | Welch p | Cohen's d | Mann-Whitney p | Cliff's δ | Verdict |",
        "|---|---:|---:|---:|---:|---:|---:|---:|:---:|",
    ]
    for s in rows:
        both = s.welch_sig and s.mw_sig
        if both and abs(s.cliff) >= 0.147:
            verdict = "significant"
        elif both:
            verdict = "significant but negligible"
        elif s.welch_sig or s.mw_sig:
            verdict = "borderline"
        else:
            verdict = "not significant"
        out.append(
            f"| `{s.metric}` | {s.mean_with:.4f} {s.unit} | {s.mean_without:.4f} {s.unit} "
            f"| {s.delta_pct:+.2f}% | {fmt_p(s.welch_p)} | {s.d:+.3f} ({s.d_label}) "
            f"| {fmt_p(s.mw_p)} | {s.cliff:+.3f} ({s.cliff_label}) | {verdict} |"
        )
    return out


def build_markdown(
    per_size: dict[int, list[MetricStats]],
    sample_counts: dict[int, tuple[int, int, int]],
    profiler: str,
    data_type: str,
    source_reports: dict[int, list[Path]],
    scaling_plot: Path | None,
    plots_by_size: dict[int, list[Path]],
) -> str:
    L: list[str] = []
    L.append("# FetchType LAZY on JPA collections - consolidated energy report")
    L.append("")
    L.append(
        "Aggregated across every measurement phase of the campaign. "
        "Regenerated by `4-aggregate_report.py`; EnergyTracer's own per-size reports are "
        "reproduced verbatim at the end."
    )
    L.append("")
    L.append("| | |")
    L.append("|---|---|")
    L.append(f"| Profiler | `{profiler}` |")
    L.append(f"| Data type | `{data_type}` |")
    L.append(f"| Collection sizes | {', '.join(str(s) for s in sorted(per_size))} |")
    L.append(f"| Significance level | α = {ALPHA} |")
    L.append(f"| Host | `{platform.node()}` - {platform.system()} {platform.release()} - {platform.machine()} |")
    L.append("")

    L.append("## Sample sizes")
    L.append("")
    L.append("| Collection size | Phases | Samples EAGER | Samples LAZY |")
    L.append("|---:|---:|---:|---:|")
    for sz in sorted(sample_counts):
        ph, nw, nwo = sample_counts[sz]
        L.append(f"| {sz} | {ph} | {nw} | {nwo} |")
    L.append("")
    thin = [sz for sz, (_, nw, nwo) in sample_counts.items()
            if min(nw, nwo) < MIN_SAMPLE_SIZE]
    if thin:
        L.append(
            f"> ⚠ Sizes {', '.join(map(str, sorted(thin)))} have fewer than "
            f"{MIN_SAMPLE_SIZE} samples per variant, below the minimum EnergyTracer's "
            "own methodology considers acceptable. Treat these rows as exploratory."
        )
        L.append("")

    if scaling_plot is not None:
        L.append("## Effect versus collection size")
        L.append("")
        L.append(
            "An overhead that grows with the number of children is the core argument "
            "for the rule: it shows the cost is caused by hydrating the collection, "
            "not by incidental noise."
        )
        L.append("")
        L.append(f"![scaling](plots/{scaling_plot.name})")
        L.append("")
        L.append("| Collection size | Δ cpu_mj | Δ dram_mj | Δ time_s |")
        L.append("|---:|---:|---:|---:|")
        for sz in sorted(per_size):
            def d(m: str) -> str:
                st = next((s for s in per_size[sz] if s.metric == m), None)
                return f"{st.delta_pct:+.2f}%" if st else "n/a"
            L.append(f"| {sz} | {d('cpu_mj')} | {d('dram_mj')} | {d('time_s')} |")
        L.append("")

    for sz in sorted(per_size):
        L.append(f"## Collection size N = {sz}")
        L.append("")
        L.extend(stats_table(per_size[sz]))
        L.append("")
        L.append(
            "> Δ mean = (EAGER − LAZY) / EAGER × 100, EnergyTracer's convention: "
            "positive means the smell costs more."
        )
        L.append("")
        non_normal = [s.metric for s in per_size[sz]
                      if not (s.normal_with and s.normal_without)]
        if non_normal:
            L.append(
                f"> Shapiro-Wilk rejects normality for {', '.join(f'`{m}`' for m in non_normal)}. "
                "For those metrics the Mann-Whitney result and Cliff's δ carry more weight "
                "than the Welch t-test and Cohen's d."
            )
            L.append("")
        for p in plots_by_size.get(sz, []):
            L.append(f"![{p.stem}](plots/n{sz}/{p.name})")
        L.append("")

    if any(source_reports.values()):
        L.append("## EnergyTracer reports, verbatim")
        L.append("")
        L.append(
            "Unmodified output of `ET-analyzer`, kept so every number above can be "
            "traced back to the tool itself."
        )
        L.append("")
        for sz in sorted(source_reports):
            for rep in source_reports[sz]:
                L.append(f"<details><summary>N = {sz} - <code>{rep.name}</code></summary>")
                L.append("")
                try:
                    L.append(rep.read_text(encoding="utf-8"))
                except Exception as exc:  # noqa: BLE001
                    L.append(f"*(unreadable: {exc})*")
                L.append("")
                L.append("</details>")
                L.append("")

    L.append("## How to read this, and what not to claim")
    L.append("")
    L.append(
        "- A result is worth quoting only when Welch **and** Mann-Whitney agree and "
        "Cliff's δ is at least small. Large sample sizes make tiny differences "
        "\"significant\" without making them relevant."
    )
    L.append(
        "- Check the per-phase plots before trusting anything: a visible trend means "
        "the machine drifted during the campaign and the run should be repeated."
    )
    L.append(
        "- These figures measure the client side only - no database engine, no network. "
        "They are a lower bound on the real saving."
    )
    L.append("")
    return "\n".join(L)


# ── Main ──────────────────────────────────────────────────────────────────────


def main() -> int:
    here = Path(__file__).resolve().parent
    ap = argparse.ArgumentParser(
        description="Aggregate a full EnergyTracer campaign into one report directory."
    )
    ap.add_argument("--out-tmp", type=Path, default=here / "out-tmp",
                    help="campaign scratch directory (default: ./out-tmp)")
    ap.add_argument("--output", type=Path, default=here / "consolidated-report",
                    help="report directory to create (default: ./consolidated-report)")
    ap.add_argument("--data-type", choices=["raw", "cleaned"], default="raw",
                    help="which EnergyTracer data set to aggregate. 'raw' is "
                         "recommended: it is filtered once, globally, whereas "
                         "'cleaned' was already filtered per phase on small samples "
                         "(default: raw)")
    args = ap.parse_args()

    out_tmp: Path = args.out_tmp.resolve()
    output: Path = args.output.resolve()

    if not out_tmp.is_dir():
        print(f"error: {out_tmp} does not exist - run a campaign first", file=sys.stderr)
        return 1

    print(f"Scanning {out_tmp} for '{args.data_type}' data…")
    shards = discover(out_tmp, args.data_type)
    if not shards:
        print(f"error: no '{args.data_type}' CSV found under {out_tmp}", file=sys.stderr)
        print("Expected .../<prefix>-n<size>/<profiler>/phase-<i>/"
              f"{args.data_type}/csv/history_*_smell.csv", file=sys.stderr)
        return 1

    df = load(shards)
    if df.empty:
        print("error: every CSV was empty or unreadable", file=sys.stderr)
        return 1

    profilers = sorted(df["profiler"].unique())
    if len(profilers) > 1:
        print(f"warning: several profilers present ({', '.join(profilers)}); "
              "reporting them together is not meaningful", file=sys.stderr)
    profiler = profilers[0]

    if output.exists():
        shutil.rmtree(output)
    (output / "data").mkdir(parents=True)
    (output / "plots").mkdir(parents=True)
    (output / "reports-source").mkdir(parents=True)

    per_size: dict[int, list[MetricStats]] = {}
    sample_counts: dict[int, tuple[int, int, int]] = {}
    plots_by_size: dict[int, list[Path]] = {}
    source_reports: dict[int, list[Path]] = {}

    for size in sorted(df["size"].unique()):
        sub = df[df["size"] == size]
        d_with = sub[sub["variant"] == "with"]
        d_without = sub[sub["variant"] == "without"]
        n_phases = sub["phase"].nunique()
        sample_counts[int(size)] = (n_phases, len(d_with), len(d_without))
        print(f"  N={size}: {n_phases} phases, "
              f"{len(d_with)} + {len(d_without)} samples")

        size_data = output / "data" / f"n{size}"
        size_data.mkdir(parents=True, exist_ok=True)
        d_with.to_csv(size_data / "with_smell.csv", index=False)
        d_without.to_csv(size_data / "without_smell.csv", index=False)

        rows = []
        for metric in METRICS:
            st = analyse_metric(d_with, d_without, metric, unit_for(metric, profiler))
            if st:
                rows.append(st)
        per_size[int(size)] = rows

        size_plots = output / "plots" / f"n{size}"
        size_plots.mkdir(parents=True, exist_ok=True)
        written: list[Path] = []
        for metric in METRICS:
            written += plot_distributions(
                d_with, d_without, metric, profiler, size_plots, int(size)
            )
            drift = plot_phase_drift(sub, metric, profiler, size_plots, int(size))
            if drift:
                written.append(drift)
        plots_by_size[int(size)] = written

        # EnergyTracer's own reports, copied so nothing is lost.
        found: list[Path] = []
        results_dir = SIZE_DIRS[int(size)] / "results"
        if results_dir.is_dir():
            dest = output / "reports-source" / f"n{size}"
            dest.mkdir(parents=True, exist_ok=True)
            for md in sorted(results_dir.rglob("*_report.md")):
                target = dest / f"{md.parent.parent.name}_{md.name}"
                shutil.copy2(md, target)
                found.append(target)
        source_reports[int(size)] = found

    df.to_csv(output / "data" / "all_measurements.csv", index=False)
    scaling = plot_scaling(per_size, output / "plots")

    (output / "REPORT.md").write_text(
        build_markdown(per_size, sample_counts, profiler, args.data_type,
                       source_reports, scaling, plots_by_size),
        encoding="utf-8",
    )

    n_plots = sum(len(v) for v in plots_by_size.values()) + (1 if scaling else 0)
    print()
    print(f"Report written to {output}")
    print(f"  REPORT.md           consolidated document")
    print(f"  data/               {len(df)} measurements, per size + combined")
    print(f"  plots/              {n_plots} figures regenerated from the full sample")
    print(f"  reports-source/     {sum(len(v) for v in source_reports.values())} "
          "EnergyTracer reports, unmodified")
    return 0


if __name__ == "__main__":
    sys.exit(main())
