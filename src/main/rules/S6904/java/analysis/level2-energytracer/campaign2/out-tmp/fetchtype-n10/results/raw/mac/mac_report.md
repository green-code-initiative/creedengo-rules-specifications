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
| **Execution Time** | 471.54 ms | 64.80 ms |
| **Average Power** | 4.373 W | 5.938 W |
| **Total Energy** | 1856.01 J | 346.36 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +79.93% | 0.00e+00 | +5.793 | large | ✅ |
| `gpu_mj` | +90.91% | 1.99e-139 | +1.403 | large | ✅ |
| `ane_mj` | +85.73% | 4.05e-28 | +0.536 | medium | ✅ |
| `dram_mj` | +86.12% | 0.00e+00 | +28.295 | large | ✅ |
| `time_s` | +86.15% | 0.00e+00 | +109.558 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 79.9% lower energy (Cohen’s d = +5.793, large)
- **`gpu_mj`**: 90.9% lower energy (Cohen’s d = +1.403, large)
- **`ane_mj`**: 85.7% lower energy (Cohen’s d = +0.536, medium)
- **`dram_mj`**: 86.1% lower energy (Cohen’s d = +28.295, large)
- **`time_s`**: 86.1% lower time (Cohen’s d = +109.558, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
