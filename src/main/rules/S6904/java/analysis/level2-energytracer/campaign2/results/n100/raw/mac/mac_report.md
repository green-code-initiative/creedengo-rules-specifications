## Energy Report — `mac` (raw)

> 900 samples (with smell) vs 900 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 504.53 ms | 78.18 ms |
| **Average Power** | 4.258 W | 3.585 W |
| **Total Energy** | 1933.48 J | 252.27 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +86.36% | 0.00e+00 | +81.417 | large | ✅ |
| `gpu_mj` | +81.96% | 0.00e+00 | +2.519 | large | ✅ |
| `dram_mj` | +87.79% | 0.00e+00 | +170.568 | large | ✅ |
| `time_s` | +84.43% | 0.00e+00 | +221.489 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 86.4% lower energy (Cohen’s d = +81.417, large)
- **`gpu_mj`**: 82.0% lower energy (Cohen’s d = +2.519, large)
- **`dram_mj`**: 87.8% lower energy (Cohen’s d = +170.568, large)
- **`time_s`**: 84.4% lower time (Cohen’s d = +221.489, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
