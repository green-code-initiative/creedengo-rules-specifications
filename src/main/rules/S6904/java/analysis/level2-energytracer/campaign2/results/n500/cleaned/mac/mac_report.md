## Energy Report — `mac` (cleaned)

> 875 samples (with smell) vs 877 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 613.25 ms | 108.37 ms |
| **Average Power** | 6.227 W | 4.551 W |
| **Total Energy** | 3341.35 J | 432.52 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +87.24% | 0.00e+00 | +8.245 | large | ✅ |
| `gpu_mj` | +84.73% | 1.53e-265 | +2.269 | large | ✅ |
| `ane_mj` | +81.57% | 7.15e-43 | +0.691 | medium | ✅ |
| `dram_mj` | +86.54% | 0.00e+00 | +19.272 | large | ✅ |
| `time_s` | +82.21% | 0.00e+00 | +92.465 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 87.2% lower energy (Cohen’s d = +8.245, large)
- **`gpu_mj`**: 84.7% lower energy (Cohen’s d = +2.269, large)
- **`ane_mj`**: 81.6% lower energy (Cohen’s d = +0.691, medium)
- **`dram_mj`**: 86.5% lower energy (Cohen’s d = +19.272, large)
- **`time_s`**: 82.2% lower time (Cohen’s d = +92.465, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
