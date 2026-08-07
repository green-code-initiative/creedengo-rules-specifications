## Energy Report — `mac` (cleaned)

> 891 samples (with smell) vs 890 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 500.03 ms | 61.56 ms |
| **Average Power** | 5.624 W | 6.458 W |
| **Total Energy** | 2505.74 J | 353.85 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +85.38% | 0.00e+00 | +7.249 | large | ✅ |
| `gpu_mj` | +87.81% | 0.00e+00 | +3.995 | large | ✅ |
| `ane_mj` | +87.93% | 1.45e-279 | +2.494 | large | ✅ |
| `dram_mj` | +87.72% | 0.00e+00 | +31.230 | large | ✅ |
| `time_s` | +87.70% | 0.00e+00 | +115.051 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 85.4% lower energy (Cohen’s d = +7.249, large)
- **`gpu_mj`**: 87.8% lower energy (Cohen’s d = +3.995, large)
- **`ane_mj`**: 87.9% lower energy (Cohen’s d = +2.494, large)
- **`dram_mj`**: 87.7% lower energy (Cohen’s d = +31.230, large)
- **`time_s`**: 87.7% lower time (Cohen’s d = +115.051, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
