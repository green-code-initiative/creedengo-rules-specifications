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
| **Execution Time** | 482.89 ms | 73.43 ms |
| **Average Power** | 4.177 W | 5.169 W |
| **Total Energy** | 1815.47 J | 341.56 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +79.74% | 0.00e+00 | +6.500 | large | ✅ |
| `gpu_mj` | +86.51% | 1.12e-33 | +0.605 | medium | ✅ |
| `ane_mj` | +85.56% | 9.85e-23 | +0.475 | small | ✅ |
| `dram_mj` | +85.45% | 0.00e+00 | +28.600 | large | ✅ |
| `time_s` | +84.95% | 0.00e+00 | +52.041 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 79.7% lower energy (Cohen’s d = +6.500, large)
- **`gpu_mj`**: 86.5% lower energy (Cohen’s d = +0.605, medium)
- **`ane_mj`**: 85.6% lower energy (Cohen’s d = +0.475, small)
- **`dram_mj`**: 85.5% lower energy (Cohen’s d = +28.600, large)
- **`time_s`**: 84.9% lower time (Cohen’s d = +52.041, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
