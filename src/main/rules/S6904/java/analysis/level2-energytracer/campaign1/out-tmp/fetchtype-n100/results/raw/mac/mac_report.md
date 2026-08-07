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
| **Execution Time** | 520.34 ms | 78.33 ms |
| **Average Power** | 4.871 W | 4.426 W |
| **Total Energy** | 2281.01 J | 312.04 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +86.12% | 0.00e+00 | +6.230 | large | ✅ |
| `gpu_mj` | +81.23% | 4.98e-169 | +1.546 | large | ✅ |
| `ane_mj` | +86.55% | 2.91e-48 | +0.729 | medium | ✅ |
| `dram_mj` | +87.13% | 0.00e+00 | +20.514 | large | ✅ |
| `time_s` | +84.73% | 0.00e+00 | +74.836 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 86.1% lower energy (Cohen’s d = +6.230, large)
- **`gpu_mj`**: 81.2% lower energy (Cohen’s d = +1.546, large)
- **`ane_mj`**: 86.6% lower energy (Cohen’s d = +0.729, medium)
- **`dram_mj`**: 87.1% lower energy (Cohen’s d = +20.514, large)
- **`time_s`**: 84.7% lower time (Cohen’s d = +74.836, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
