## Energy Report — `mac` (cleaned)

> 898 samples (with smell) vs 881 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 490.02 ms | 75.55 ms |
| **Average Power** | 4.325 W | 4.612 W |
| **Total Energy** | 1903.20 J | 307.00 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +83.33% | 0.00e+00 | +6.279 | large | ✅ |
| `gpu_mj` | +83.08% | 8.41e-224 | +1.932 | large | ✅ |
| `ane_mj` | +86.82% | 6.94e-29 | +0.545 | medium | ✅ |
| `dram_mj` | +85.80% | 0.00e+00 | +28.191 | large | ✅ |
| `time_s` | +84.45% | 0.00e+00 | +112.566 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 83.3% lower energy (Cohen’s d = +6.279, large)
- **`gpu_mj`**: 83.1% lower energy (Cohen’s d = +1.932, large)
- **`ane_mj`**: 86.8% lower energy (Cohen’s d = +0.545, medium)
- **`dram_mj`**: 85.8% lower energy (Cohen’s d = +28.191, large)
- **`time_s`**: 84.5% lower time (Cohen’s d = +112.566, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
