import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from pathlib import Path

# Project paths
ROOT = Path(__file__).resolve().parent.parent
CSV_FILE = ROOT / "graphs" / "benchmark_pre_search.csv"
OUTPUT_FILE = ROOT / "graphs" / "graph1_algorithm_pre_comparison.png"

# Load benchmark results
df = pd.read_csv(CSV_FILE)

required = {
    "book",
    "algorithm",
    "preprocessing_time_ms",
    "execution_time_ms",
}
missing = required - set(df.columns)
if missing:
    raise ValueError(f"Missing columns in CSV: {missing}")

# Median across repeated runs for each book and algorithm
per_book = (
    df.groupby(["book", "algorithm"], as_index=False)[
        ["preprocessing_time_ms", "execution_time_ms"]
    ]
    .median()
)

# Average the per-book medians for each algorithm
summary = (
    per_book.groupby("algorithm")[
        ["preprocessing_time_ms", "execution_time_ms"]
    ]
    .mean()
    .sort_values("execution_time_ms")
)

# Grouped bar chart
algorithms = summary.index.tolist()
x = np.arange(len(algorithms))
width = 0.36

fig, ax = plt.subplots(figsize=(10, 6))

pre_bars = ax.bar(
    x - width / 2,
    summary["preprocessing_time_ms"],
    width,
    label="Preprocessing",
)

search_bars = ax.bar(
    x + width / 2,
    summary["execution_time_ms"],
    width,
    label="Search",
)

ax.set_title("Average Preprocessing and Search Time by Algorithm")
ax.set_xlabel("Algorithm")
ax.set_ylabel("Average time (ms)")
ax.set_xticks(x)
ax.set_xticklabels(algorithms)
ax.legend()
ax.grid(axis="y", linestyle="--", alpha=0.5)
ax.set_axisbelow(True)

# Add values above bars
ax.bar_label(pre_bars, fmt="%.3f", padding=3, fontsize=8)
ax.bar_label(search_bars, fmt="%.3f", padding=3, fontsize=8)

fig.tight_layout()
fig.savefig(OUTPUT_FILE, dpi=300, bbox_inches="tight")
print(f"Graph saved to: {OUTPUT_FILE}")

plt.show()