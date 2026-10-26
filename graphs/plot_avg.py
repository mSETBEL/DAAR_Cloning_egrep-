import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

# Project paths
ROOT = Path(__file__).resolve().parent.parent
CSV_FILE = ROOT / "graphs" / "benchmark_results.csv"
OUTPUT_FILE = ROOT / "graphs" / "graph1_algorithm_comparison.png"

# Load benchmark results
df = pd.read_csv(CSV_FILE)

required = {"book", "algorithm", "execution_time_ms"}
missing = required - set(df.columns)
if missing:
    raise ValueError(f"Missing columns in CSV: {missing}")

# First calculate the median across repeated runs for each book
per_book = (
    df.groupby(["book", "algorithm"], as_index=False)
      ["execution_time_ms"]
      .median()
)

# Then calculate the average of those per-book medians
summary = (
    per_book.groupby("algorithm")["execution_time_ms"]
    .mean()
    .sort_values()
)

# Plot
fig, ax = plt.subplots(figsize=(9, 6))

bars = ax.bar(summary.index, summary.values)

ax.set_title("Average Search Time by Algorithm")
ax.set_xlabel("Algorithm")
ax.set_ylabel("Average execution time (ms)")
ax.grid(axis="y", linestyle="--", alpha=0.5)
ax.set_axisbelow(True)

# Add values above bars
ax.bar_label(bars, fmt="%.3f ms", padding=4)

fig.tight_layout()
fig.savefig(OUTPUT_FILE, dpi=300, bbox_inches="tight")
print(f"Graph saved to: {OUTPUT_FILE}")

plt.show()