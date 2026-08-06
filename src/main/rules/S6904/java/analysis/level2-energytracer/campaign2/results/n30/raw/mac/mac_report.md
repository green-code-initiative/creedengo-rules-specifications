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
| **Execution Time** | 489.50 ms | 75.33 ms |
| **Average Power** | 4.329 W | 4.613 W |
| **Total Energy** | 1907.29 J | 312.72 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +83.37% | 0.00e+00 | +6.288 | large | ✅ |
| `gpu_mj` | +83.10% | 1.39e-224 | +1.934 | large | ✅ |
| `ane_mj` | +86.98% | 5.53e-29 | +0.545 | medium | ✅ |
| `dram_mj` | +85.83% | 0.00e+00 | +28.185 | large | ✅ |
| `time_s` | +84.47% | 0.00e+00 | +111.097 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 83.4% lower energy (Cohen’s d = +6.288, large)
- **`gpu_mj`**: 83.1% lower energy (Cohen’s d = +1.934, large)
- **`ane_mj`**: 87.0% lower energy (Cohen’s d = +0.545, medium)
- **`dram_mj`**: 85.8% lower energy (Cohen’s d = +28.185, large)
- **`time_s`**: 84.5% lower time (Cohen’s d = +111.097, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
