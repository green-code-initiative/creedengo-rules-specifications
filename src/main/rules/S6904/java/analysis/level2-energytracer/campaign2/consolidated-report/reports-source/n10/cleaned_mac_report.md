## Energy Report — `mac` (cleaned)

> 890 samples (with smell) vs 875 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 473.04 ms | 65.11 ms |
| **Average Power** | 4.361 W | 5.933 W |
| **Total Energy** | 1835.92 J | 337.99 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +79.86% | 0.00e+00 | +5.782 | large | ✅ |
| `gpu_mj` | +90.68% | 4.91e-137 | +1.397 | large | ✅ |
| `ane_mj` | +85.47% | 8.58e-28 | +0.535 | medium | ✅ |
| `dram_mj` | +86.08% | 0.00e+00 | +28.390 | large | ✅ |
| `time_s` | +86.12% | 0.00e+00 | +111.194 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 79.9% lower energy (Cohen’s d = +5.782, large)
- **`gpu_mj`**: 90.7% lower energy (Cohen’s d = +1.397, large)
- **`ane_mj`**: 85.5% lower energy (Cohen’s d = +0.535, medium)
- **`dram_mj`**: 86.1% lower energy (Cohen’s d = +28.390, large)
- **`time_s`**: 86.1% lower time (Cohen’s d = +111.194, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
