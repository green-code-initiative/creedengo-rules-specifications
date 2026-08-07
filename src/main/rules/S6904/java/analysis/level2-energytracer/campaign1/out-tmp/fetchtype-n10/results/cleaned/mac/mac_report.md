## Energy Report — `mac` (cleaned)

> 880 samples (with smell) vs 882 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 481.32 ms | 73.56 ms |
| **Average Power** | 4.194 W | 5.170 W |
| **Total Energy** | 1776.26 J | 335.44 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +79.68% | 0.00e+00 | +6.469 | large | ✅ |
| `gpu_mj` | +86.23% | 6.17e-33 | +0.604 | medium | ✅ |
| `ane_mj` | +85.06% | 3.92e-22 | +0.473 | small | ✅ |
| `dram_mj` | +85.42% | 0.00e+00 | +28.839 | large | ✅ |
| `time_s` | +84.91% | 0.00e+00 | +53.990 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 79.7% lower energy (Cohen’s d = +6.469, large)
- **`gpu_mj`**: 86.2% lower energy (Cohen’s d = +0.604, medium)
- **`ane_mj`**: 85.1% lower energy (Cohen’s d = +0.473, small)
- **`dram_mj`**: 85.4% lower energy (Cohen’s d = +28.839, large)
- **`time_s`**: 84.9% lower time (Cohen’s d = +53.990, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
