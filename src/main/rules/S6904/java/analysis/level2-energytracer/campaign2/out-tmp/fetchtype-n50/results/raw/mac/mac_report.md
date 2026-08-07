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
| **Execution Time** | 499.59 ms | 61.33 ms |
| **Average Power** | 5.623 W | 6.479 W |
| **Total Energy** | 2528.48 J | 357.61 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +85.36% | 0.00e+00 | +7.194 | large | ✅ |
| `gpu_mj` | +87.85% | 0.00e+00 | +3.974 | large | ✅ |
| `ane_mj` | +87.88% | 7.64e-279 | +2.465 | large | ✅ |
| `dram_mj` | +87.74% | 0.00e+00 | +31.039 | large | ✅ |
| `time_s` | +87.71% | 0.00e+00 | +113.485 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 85.4% lower energy (Cohen’s d = +7.194, large)
- **`gpu_mj`**: 87.8% lower energy (Cohen’s d = +3.974, large)
- **`ane_mj`**: 87.9% lower energy (Cohen’s d = +2.465, large)
- **`dram_mj`**: 87.7% lower energy (Cohen’s d = +31.039, large)
- **`time_s`**: 87.7% lower time (Cohen’s d = +113.485, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
