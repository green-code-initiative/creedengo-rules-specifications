## Energy Report — `mac` (cleaned)

> 892 samples (with smell) vs 874 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 650.62 ms | 125.11 ms |
| **Average Power** | 7.226 W | 3.505 W |
| **Total Energy** | 4193.45 J | 383.30 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +91.15% | 0.00e+00 | +95.218 | large | ✅ |
| `gpu_mj` | +83.82% | 0.00e+00 | +3.677 | large | ✅ |
| `ane_mj` | +100.00% | 0.00e+00 | +0.273 | small | ✅ |
| `dram_mj` | +87.81% | 0.00e+00 | +129.181 | large | ✅ |
| `time_s` | +81.05% | 0.00e+00 | +145.073 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 91.1% lower energy (Cohen’s d = +95.218, large)
- **`gpu_mj`**: 83.8% lower energy (Cohen’s d = +3.677, large)
- **`ane_mj`**: 100.0% lower energy (Cohen’s d = +0.273, small)
- **`dram_mj`**: 87.8% lower energy (Cohen’s d = +129.181, large)
- **`time_s`**: 81.0% lower time (Cohen’s d = +145.073, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
