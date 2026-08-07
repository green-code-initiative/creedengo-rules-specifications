#!/usr/bin/env bash
# Builds the consolidated campaign report from out-tmp/.
#
# Usage:
#   ./4-aggregate_report.sh [path-to-EnergyTracer-checkout] [-- extra args]
#
# Examples:
#   ./4-aggregate_report.sh ~/git_creedengo2/EnergyTracer
#   ./4-aggregate_report.sh ~/git_creedengo2/EnergyTracer -- --data-type cleaned
#   ./4-aggregate_report.sh                                 # uses the system python3
#
# Output: consolidated-report/ - REPORT.md, aggregated CSVs, regenerated plots, and
# EnergyTracer's own reports kept verbatim. See ../README.md section 6.
#
# To rebuild the report of an archived campaign, point it at that campaign:
#   ./4-aggregate_report.sh ~/git_creedengo2/EnergyTracer -- \
#       --out-tmp campaign1/out-tmp --output campaign1/consolidated-report
#
# Passing the EnergyTracer checkout is the easy path: its virtualenv already has
# pandas, numpy, scipy and matplotlib, so nothing needs installing. Without it,
# the script falls back to python3 and those four packages must be importable.
set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
PY="$HERE/4-aggregate_report.py"

ET_DIR=""
if [ $# -gt 0 ] && [ "${1:-}" != "--" ]; then
  ET_DIR="$1"
  shift
fi
[ "${1:-}" = "--" ] && shift

if [ -n "$ET_DIR" ]; then
  if [ ! -d "$ET_DIR" ]; then
    echo "not a directory: $ET_DIR" >&2
    exit 1
  fi
  ET_DIR="$(cd "$ET_DIR" && pwd)"
  command -v uv >/dev/null || { echo "uv not found in PATH" >&2; exit 1; }
  exec uv run --project "$ET_DIR" python "$PY" "$@"
fi

command -v python3 >/dev/null || { echo "python3 not found" >&2; exit 1; }
if ! python3 -c "import pandas, numpy, scipy, matplotlib" 2>/dev/null; then
  cat >&2 <<'EOF'
Missing Python dependencies (pandas, numpy, scipy, matplotlib).

Easiest fix - reuse the EnergyTracer virtualenv, which already has them:

    ./4-aggregate_report.sh ~/git_creedengo2/EnergyTracer
EOF
  exit 1
fi
exec python3 "$PY" "$@"
