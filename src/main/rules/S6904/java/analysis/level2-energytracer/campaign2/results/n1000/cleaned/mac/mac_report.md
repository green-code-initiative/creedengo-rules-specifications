## Energy Report — `mac` (cleaned)

> 883 samples (with smell) vs 876 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 667.61 ms | 125.92 ms |
| **Average Power** | 7.811 W | 4.028 W |
| **Total Energy** | 4604.63 J | 444.29 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +90.58% | 0.00e+00 | +12.693 | large | ✅ |
| `gpu_mj` | +85.03% | 2.06e-296 | +2.501 | large | ✅ |
| `ane_mj` | +81.14% | 2.51e-31 | +0.575 | medium | ✅ |
| `dram_mj` | +87.28% | 0.00e+00 | +24.469 | large | ✅ |
| `time_s` | +81.01% | 0.00e+00 | +98.450 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 90.6% lower energy (Cohen’s d = +12.693, large)
- **`gpu_mj`**: 85.0% lower energy (Cohen’s d = +2.501, large)
- **`ane_mj`**: 81.1% lower energy (Cohen’s d = +0.575, medium)
- **`dram_mj`**: 87.3% lower energy (Cohen’s d = +24.469, large)
- **`time_s`**: 81.0% lower time (Cohen’s d = +98.450, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
