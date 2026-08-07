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
| **Execution Time** | 655.51 ms | 123.73 ms |
| **Average Power** | 7.179 W | 3.544 W |
| **Total Energy** | 4235.28 J | 394.69 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +91.17% | 0.00e+00 | +89.445 | large | ✅ |
| `gpu_mj` | +83.80% | 0.00e+00 | +3.683 | large | ✅ |
| `ane_mj` | +100.00% | 0.00e+00 | +0.272 | small | ✅ |
| `dram_mj` | +87.83% | 0.00e+00 | +117.690 | large | ✅ |
| `time_s` | +81.06% | 0.00e+00 | +134.006 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 91.2% lower energy (Cohen’s d = +89.445, large)
- **`gpu_mj`**: 83.8% lower energy (Cohen’s d = +3.683, large)
- **`ane_mj`**: 100.0% lower energy (Cohen’s d = +0.272, small)
- **`dram_mj`**: 87.8% lower energy (Cohen’s d = +117.690, large)
- **`time_s`**: 81.1% lower time (Cohen’s d = +134.006, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
