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
| **Execution Time** | 665.57 ms | 123.63 ms |
| **Average Power** | 7.826 W | 4.087 W |
| **Total Energy** | 4687.65 J | 454.69 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +90.61% | 0.00e+00 | +12.775 | large | ✅ |
| `gpu_mj` | +85.05% | 1.48e-302 | +2.504 | large | ✅ |
| `ane_mj` | +81.23% | 1.02e-31 | +0.573 | medium | ✅ |
| `dram_mj` | +87.31% | 0.00e+00 | +24.607 | large | ✅ |
| `time_s` | +81.02% | 0.00e+00 | +97.082 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 90.6% lower energy (Cohen’s d = +12.775, large)
- **`gpu_mj`**: 85.0% lower energy (Cohen’s d = +2.504, large)
- **`ane_mj`**: 81.2% lower energy (Cohen’s d = +0.573, medium)
- **`dram_mj`**: 87.3% lower energy (Cohen’s d = +24.607, large)
- **`time_s`**: 81.0% lower time (Cohen’s d = +97.082, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
