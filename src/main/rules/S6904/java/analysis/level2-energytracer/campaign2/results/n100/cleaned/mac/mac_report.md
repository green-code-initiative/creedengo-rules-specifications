## Energy Report — `mac` (cleaned)

> 887 samples (with smell) vs 872 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 505.49 ms | 77.68 ms |
| **Average Power** | 4.241 W | 3.619 W |
| **Total Energy** | 1901.70 J | 245.10 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +86.32% | 0.00e+00 | +84.860 | large | ✅ |
| `gpu_mj` | +81.92% | 0.00e+00 | +2.516 | large | ✅ |
| `dram_mj` | +87.77% | 0.00e+00 | +177.673 | large | ✅ |
| `time_s` | +84.39% | 0.00e+00 | +250.581 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 86.3% lower energy (Cohen’s d = +84.860, large)
- **`gpu_mj`**: 81.9% lower energy (Cohen’s d = +2.516, large)
- **`dram_mj`**: 87.8% lower energy (Cohen’s d = +177.673, large)
- **`time_s`**: 84.4% lower time (Cohen’s d = +250.581, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
