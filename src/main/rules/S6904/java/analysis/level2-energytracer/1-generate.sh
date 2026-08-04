#!/usr/bin/env bash
# Generates the Java variants (one pair per collection size: EAGER + LAZY) from the two
# templates. Regenerate after editing a template so the compliant and noncompliant
# variants never drift apart.
#
# Usage: ./1-generate.sh
#
# Output: generate/variants/FetchType{Eager,Lazy}N<size>.java
set -euo pipefail

cd "$(dirname "$0")"
mkdir -p generate/variants

# size:parent_reads
#
# parent_reads is calibrated on two constraints:
#
#  1. One JVM run of the EAGER variant should last ~300-500 ms, so that JVM startup
#     (~100-400 ms, paid once per iteration) does not drown the signal.
#     Re-run ./2-calibrate.sh on the target machine and adjust if needed.
#
#  2. parent_reads x size is kept at ~5,000,000 hydrated child rows for every size.
#     This is what makes the sizes comparable at equal nominal work, and it is the
#     basis of the "energy per hydrated row" figure. Do not break it casually.
#     N=30 is the only inexact one (166,667 x 30 = 5,000,010, off by 10 rows).
#
# The values for 10, 100 and 1000 are identical to those of campaign1: keeping them
# unchanged is what allows a later campaign to be compared with the first one.
CONFIGS=(
  "10:500000"
  "30:166667"
  "50:100000"
  "100:50000"
  "500:10000"
  "1000:5000"
)

for cfg in "${CONFIGS[@]}"; do
  n="${cfg%%:*}"
  reads="${cfg##*:}"

  for variant in Eager Lazy; do
    class="FetchType${variant}N${n}"
    out="generate/variants/${class}.java"
    sed -e "s/__CLASS__/${class}/g" \
        -e "s/__CHILD_COUNT__/${n}/g" \
        -e "s/__PARENT_READS__/${reads}/g" \
        "generate/template/${variant}.java.tpl" > "$out"
    printf 'generated %-46s (CHILD_COUNT=%-4s PARENT_READS=%-7s rows=%s)\n' \
      "$out" "$n" "$reads" "$((n * reads))"
  done
done

echo
echo "Sanity check: compiling every variant."
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
for f in generate/variants/*.java; do
  javac -d "$tmp" "$f"
done
echo "All variants compile."
