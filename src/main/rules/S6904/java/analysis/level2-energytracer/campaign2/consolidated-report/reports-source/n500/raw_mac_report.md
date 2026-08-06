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
| **Execution Time** | 611.35 ms | 106.11 ms |
| **Average Power** | 6.242 W | 4.652 W |
| **Total Energy** | 3434.22 J | 444.25 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +87.22% | 0.00e+00 | +8.263 | large | ✅ |
| `gpu_mj` | +84.75% | 3.81e-273 | +2.269 | large | ✅ |
| `ane_mj` | +81.59% | 2.97e-43 | +0.684 | medium | ✅ |
| `dram_mj` | +86.52% | 0.00e+00 | +19.310 | large | ✅ |
| `time_s` | +82.21% | 0.00e+00 | +92.123 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 87.2% lower energy (Cohen’s d = +8.263, large)
- **`gpu_mj`**: 84.7% lower energy (Cohen’s d = +2.269, large)
- **`ane_mj`**: 81.6% lower energy (Cohen’s d = +0.684, medium)
- **`dram_mj`**: 86.5% lower energy (Cohen’s d = +19.310, large)
- **`time_s`**: 82.2% lower time (Cohen’s d = +92.123, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
