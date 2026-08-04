#!/usr/bin/env bash
# Runs the full EnergyTracer campaign for FetchType LAZY, one campaign per collection size.
#
# Reimplements the protocol of EnergyTracer's own run_experiment.sh (warm-up phases,
# measurement phases, cooldown, shuffled order) because that script does not expose
# -f1/-f2 and therefore cannot point at our variants.
#
# Usage:
#   ./3-run_campaign.sh <path-to-EnergyTracer-checkout> [profiler] [sizes...]
#
#   profiler : mac (Apple Silicon hardware counters) | carbon (CodeCarbon estimate)
#              default: mac
#   sizes    : any of 10 30 50 100 500 1000      default: 10 30 50 100 500 1000
#
# Example:
#   ./3-run_campaign.sh ~/git_creedengo2/EnergyTracer mac 10 30 50 100 500 1000
#
# Before running, go through the checklist in ../README.md section "Preparing the machine".
#
# Campaign archiving convention
# -----------------------------
# This script always writes to ./out-tmp and ./results. Once a campaign is finished and
# its report has been built, move both directories plus consolidated-report into
# ./campaign<N>/ so the next campaign starts clean. Past campaigns stay analysable in place:
#
#     ./4-aggregate_report.sh <ET dir> -- \
#         --out-tmp campaign1/out-tmp --output campaign1/consolidated-report
#
#
# Where the output goes, and why it is built this way
# ---------------------------------------------------
# Two constraints from EnergyTracer's source dictate the layout below.
#
# 1. `ET -o X` is NOT an output path. src/main.py does:
#        output_directory = Path("output") / args.profiler / args.output_dir
#    so a bare `-o mytag` lands in <cwd>/output/<profiler>/mytag/{raw,cleaned}.
#    Because pathlib drops the left-hand parts when the right-hand one is absolute,
#    passing an ABSOLUTE value to -o overrides the whole prefix and writes exactly
#    there. That is what we do, so nothing is ever written inside the ET checkout.
#
# 2. `ET-analyzer -p D` infers the profiler from the path component that immediately
#    follows D (src/analyzer.py: profiler = parts[parts.index(first_folder) + 1]).
#    So D must be the parent of a directory named after the profiler. Pointing the
#    analyzer one level too deep does not crash - it silently labels the report
#    "raw" instead of "mac", which is worse. Nothing below that point matters to the
#    analyzer, which walks the whole subtree with rglob("*.csv") - hence point 3.
#
# 3. `save_history` (src/utilities/save_csv.py) opens its CSV with mode "w": every
#    call OVERWRITES the previous one at the same path. Upstream's own
#    run_experiment.sh gives every phase a distinct tag (`-o "measure-$i"`) for
#    exactly this reason. A first version of this script reused the same -o value
#    across all measurement phases, so every phase silently erased the previous
#    one and only the LAST phase's samples ever reached the analyzer. Fixed below:
#    each phase gets its own "phase-$i" subdirectory. The analyzer does not care
#    how deep the tree goes past <profiler>, so this does not break point 2.
#
# Hence the layout, all absolute:
#
#   out-tmp/fetchtype-n<size>/<profiler>/phase-<i>/{raw,cleaned}/*.csv  <- one shard per phase, written by ET
#   out-tmp/fetchtype-n<size>/results/<type>/<profiler>/*.md            <- written by ET-analyzer (merges all phases)
#                         ^ analyzer is pointed here, one level above <profiler>
#
# Analysing per size rather than globally is deliberate: pointing the analyzer at the
# common root would merge all three collection sizes into a single meaningless report.
set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"

ET_DIR="${1:-}"
if [ -z "$ET_DIR" ] || [ ! -d "$ET_DIR" ]; then
  echo "usage: $0 <path-to-EnergyTracer-checkout> [mac|carbon] [sizes...]" >&2
  exit 1
fi
ET_DIR="$(cd "$ET_DIR" && pwd)"
shift

PROFILER="${1:-mac}"
case "$PROFILER" in
  mac|carbon) shift || true ;;
  *)          PROFILER="mac" ;;
esac

SIZES=("$@")
[ ${#SIZES[@]} -eq 0 ] && SIZES=(10 30 50 100 500 1000)

# Phase parameters. Deliberately different from EnergyTracer's defaults (500/1000):
# our workload is ~400 ms per JVM run instead of a few ms, so far fewer iterations
# per phase are needed to reach the same number of samples, and the campaign stays
# within a few hours instead of tens of hours.
WARMUP_PHASES="${WARMUP_PHASES:-5}"
WARMUP_N="${WARMUP_N:-5}"
MEASURE_PHASES="${MEASURE_PHASES:-30}"
MEASURE_N="${MEASURE_N:-30}"
COOLDOWN="${COOLDOWN:-60}"

# Scratch area for raw measurements. Absolute, and outside the ET checkout.
OUT_ROOT="${OUT_ROOT:-$HERE/out-tmp}"

command -v uv >/dev/null || { echo "uv not found in PATH" >&2; exit 1; }

# Regenerate if any requested size is missing, not merely if the directory is absent:
# after adding a size to 1-generate.sh, the directory still exists but is incomplete,
# and the campaign would fail several minutes in on the first missing variant.
need_generate=0
for n in "${SIZES[@]}"; do
  [ -f "$HERE/generate/variants/FetchTypeEagerN${n}.java" ] &&
  [ -f "$HERE/generate/variants/FetchTypeLazyN${n}.java" ] || need_generate=1
done
if [ "$need_generate" = 1 ]; then
  echo "Some variants are missing for the requested sizes - regenerating."
  "$HERE/1-generate.sh"
  echo
fi

mkdir -p "$OUT_ROOT"
OUT_ROOT="$(cd "$OUT_ROOT" && pwd)"

echo "EnergyTracer : $ET_DIR"
echo "Profiler     : $PROFILER"
echo "Sizes        : ${SIZES[*]}"
echo "Scratch dir  : $OUT_ROOT"
echo "Warm-up      : $WARMUP_PHASES phases x $WARMUP_N iterations"
echo "Measurement  : $MEASURE_PHASES phases x $MEASURE_N iterations (= $((MEASURE_PHASES * MEASURE_N)) samples per variant)"
echo "Cooldown     : ${COOLDOWN}s between measurement phases"
echo
echo "Do not touch this machine until the campaign is over."
echo

cd "$ET_DIR"

for n in "${SIZES[@]}"; do
  f1="$HERE/generate/variants/FetchTypeEagerN${n}.java"
  f2="$HERE/generate/variants/FetchTypeLazyN${n}.java"
  [ -f "$f1" ] && [ -f "$f2" ] || {
    echo "no variant for size $n - add it to CONFIGS in 1-generate.sh" >&2; exit 1; }

  size_dir="$OUT_ROOT/fetchtype-n${n}"      # analyzer input: parent of <profiler>
  data_dir="$size_dir/$PROFILER"         # ET output: gets /raw and /cleaned appended
  warm_dir="$OUT_ROOT/warmup-n${n}/$PROFILER"

  # Start each size from a clean slate, otherwise CSVs from a previous campaign
  # would be silently merged into this one's report.
  rm -rf "$size_dir" "$OUT_ROOT/warmup-n${n}"
  mkdir -p "$data_dir" "$warm_dir"
  [ -d "$data_dir" ] || { echo "could not create $data_dir" >&2; exit 1; }

  echo "=== size N=${n} : warm-up ==="
  for i in $(seq 1 "$WARMUP_PHASES"); do
    echo "  warm-up $i/$WARMUP_PHASES"
    uv run ET -p "$PROFILER" -n "$WARMUP_N" -f1 "$f1" -f2 "$f2" -o "$warm_dir" --shuffle
  done
  rm -rf "$OUT_ROOT/warmup-n${n}"   # warm-up data is discarded, as upstream does

  echo "=== size N=${n} : measurement ==="
  for i in $(seq 1 "$MEASURE_PHASES"); do
    echo "  phase $i/$MEASURE_PHASES"
    # Each phase writes to its own subdirectory (see point 3 above): reusing
    # $data_dir directly here would make every phase overwrite the last one.
    uv run ET -p "$PROFILER" -n "$MEASURE_N" -f1 "$f1" -f2 "$f2" -o "$data_dir/phase-$i" --shuffle
    if [ "$i" -lt "$MEASURE_PHASES" ]; then
      sleep "$COOLDOWN"
    fi
  done

  # Fail loudly here rather than let the analyzer report an opaque "does not exist".
  csv_count="$(find "$data_dir" -name '*.csv' -type f 2>/dev/null | wc -l | tr -d ' ')"
  if [ "$csv_count" -eq 0 ]; then
    echo "ERROR: no CSV produced under $data_dir" >&2
    echo "Contents of $size_dir:" >&2
    find "$size_dir" -maxdepth 3 2>/dev/null | sed 's|^|  |' >&2
    echo "Check the ET output above: a failed javac or a non-zero exit code aborts a phase." >&2
    exit 1
  fi
  echo "  $csv_count CSV files collected"

  echo "=== size N=${n} : analysis ==="
  uv run ET-analyzer -p "$size_dir" -v

  res="$HERE/results/n${n}"
  rm -rf "$res"
  mkdir -p "$res"
  if [ -d "$size_dir/results" ]; then
    cp -R "$size_dir/results/." "$res"/
    echo "  reports copied to $res"
  else
    echo "  WARNING: analyzer produced no results/ directory under $size_dir" >&2
  fi
  echo
done

echo "Campaign finished."
echo "  reports  : $HERE/results/<size>/"
echo "  raw data : $OUT_ROOT/  (scratch - safe to delete once the reports are saved)"
echo
echo "Fill in ../results-template.md with the numbers, then attach it to the PR."
