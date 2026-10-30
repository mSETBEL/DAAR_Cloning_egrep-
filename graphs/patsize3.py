import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

CSV_FILE = ROOT / "graphs" / "benchmark_pat_size.csv"
OUTPUT_FILE = ROOT / "graphs" / "pat_sizes.png"

df = pd.read_csv(CSV_FILE)

# Group by pattern length and algorithm
summary = (
    df.groupby(["pattern_length", "algorithm"])["execution_time_ms"]
    .median()
    .reset_index()
)

algorithms = ["KMP", "BoyerMoore", "FileSearcher"]

fig, ax = plt.subplots(figsize=(10, 6))

# One line per algorithm
for algorithm in algorithms:
    data = summary[summary["algorithm"] == algorithm]

    ax.plot(
        data["pattern_length"],
        data["execution_time_ms"],
        marker="o",
        linewidth=2,
        label=algorithm
    )

ax.set_title("Search Execution Time vs Pattern Length")
ax.set_xlabel("Pattern length (characters)")
ax.set_ylabel("Execution time (ms)")

ax.legend()
ax.grid(True, linestyle="--", alpha=0.5)

fig.tight_layout()
fig.savefig(OUTPUT_FILE, dpi=300, bbox_inches="tight")

print(f"Graph saved to: {OUTPUT_FILE}")

plt.show()