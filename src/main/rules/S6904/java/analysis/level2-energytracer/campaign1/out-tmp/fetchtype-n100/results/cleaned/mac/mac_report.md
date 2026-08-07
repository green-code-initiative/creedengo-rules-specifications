## Energy Report — `mac` (cleaned)

> 891 samples (with smell) vs 875 samples (without smell) — α = 0.05

### Instance Info

* **hostname**: `M-NGY9VPYVMH`
* **model**: `MacBookPro18,1`
* **os**: `Darwin 25.5.0`
* **machine**: `arm64`
* **chip**: `Apple M1 Pro`

### Global Consumption

|  | With smell | Without smell |
|---|---:|---:|
| **Execution Time** | 521.44 ms | 78.82 ms |
| **Average Power** | 4.860 W | 4.414 W |
| **Total Energy** | 2258.08 J | 304.41 J |

> The total energy is the sum of measurements across all iterations, converted to joules (J). If you ran the `./run_experiment.sh` script, this reflects the cumulative energy of all 30 iterations of the process.

### Statistical Analysis

| Metric | Δ mean | p-value | Cohen’s d | Effect | Sig. |
|---|---|---|---|---|---|
| `cpu_mj` | +86.07% | 0.00e+00 | +6.228 | large | ✅ |
| `gpu_mj` | +80.99% | 2.11e-166 | +1.541 | large | ✅ |
| `ane_mj` | +86.38% | 1.05e-47 | +0.729 | medium | ✅ |
| `dram_mj` | +87.09% | 0.00e+00 | +20.546 | large | ✅ |
| `time_s` | +84.69% | 0.00e+00 | +75.359 | large | ✅ |

### Verdict

Removing the code smell leads to measurable energy differences:

- **`cpu_mj`**: 86.1% lower energy (Cohen’s d = +6.228, large)
- **`gpu_mj`**: 81.0% lower energy (Cohen’s d = +1.541, large)
- **`ane_mj`**: 86.4% lower energy (Cohen’s d = +0.729, medium)
- **`dram_mj`**: 87.1% lower energy (Cohen’s d = +20.546, large)
- **`time_s`**: 84.7% lower time (Cohen’s d = +75.359, large)

> Δ mean = (mean\_with − mean\_without) / mean\_with × 100. Positive → the smell consumes more energy.
