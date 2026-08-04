#!/usr/bin/env bash
# Builds and runs the level 1 JPA/Hibernate benchmark.
#
# Usage:
#   ./run.sh                 # sizes 10,100,1000 with 200 parent rows
#   ./run.sh 10,100,1000 200
#
# Output goes to stdout and to results/jpa-<timestamp>.txt (table + CSV).
set -euo pipefail
cd "$(dirname "$0")"

command -v mvn >/dev/null || { echo "maven not found"; exit 1; }

mvn -q clean package

mkdir -p results
stamp="$(date +%Y%m%d-%H%M%S)"
out="results/jpa-${stamp}.txt"

java -jar target/fetchtype-benchmark.jar "$@" | tee "$out"

echo
echo "Saved to $out"
