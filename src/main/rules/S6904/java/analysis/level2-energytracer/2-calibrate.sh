#!/usr/bin/env bash
# Times one JVM run of each variant, so PARENT_READS can be tuned for the target machine.
#
# Why this matters: EnergyTracer launches one JVM per iteration, and JVM startup costs
# ~100-400 ms. If a run only lasts 20 ms, 95% of what you measure is JVM startup and the
# EAGER/LAZY difference disappears into the noise. Aim for a run of roughly 300-500 ms.
#
# Usage: ./2-calibrate.sh
set -euo pipefail
cd "$(dirname "$0")"

command -v javac >/dev/null || { echo "javac not found: install a JDK (not just a JRE)"; exit 1; }

[ -d generate/variants ] || ./1-generate.sh

build="$(mktemp -d)"
trap 'rm -rf "$build"' EXIT
javac -d "$build" generate/variants/*.java

echo
printf '%-26s %10s %10s\n' "variant" "run 1" "run 2"
printf '%-26s %10s %10s\n' "--------------------------" "----------" "----------"

for f in generate/variants/*.java; do
  cls="$(basename "$f" .java)"
  times=()
  for _ in 1 2; do
    start=$(python3 -c 'import time;print(int(time.time()*1000))')
    java -cp "$build" "$cls"
    end=$(python3 -c 'import time;print(int(time.time()*1000))')
    times+=("$((end - start)) ms")
  done
  printf '%-26s %10s %10s\n' "$cls" "${times[0]}" "${times[1]}"
done

cat <<'EOF'

Reading the table
-----------------
These timings include JVM startup (~100-400 ms), so subtract roughly that much to get
the actual workload duration.

  workload well under 300 ms -> raise PARENT_READS in 1-generate.sh, then re-run ./1-generate.sh
  workload well over 800 ms  -> lower it, otherwise the campaign takes too long

The EAGER and LAZY variants of the same size MUST keep the same PARENT_READS value.
EOF
