
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

CSV_FILE = ROOT / "graphs" / "benchmark_pat_size.csv"
OUTPUT_FILE = ROOT / "graphs" / "automate_words.png"

# Read benchmark results
df = pd.read_csv(CSV_FILE)

# Keep only Boyer-Moore
df = df[df["algorithm"] == "FileSearcher"]

# Calculate median time for each word
summary = (
    df.groupby("pattern")[
        ["preprocessing_time_ms", "execution_time_ms"]
    ]
    .median()
)

# Sort words by length
summary = summary.loc[
    sorted(summary.index, key=lambda word: (len(word), word))
]

words = summary.index.tolist()

x = np.arange(len(words))
width = 0.4

fig, ax = plt.subplots(figsize=(max(12, len(words) * 0.6), 7))

# Bars
pre_bars = ax.bar(
    x - width / 2,
    summary["preprocessing_time_ms"],
    width,
    label="Preprocessing"
)

search_bars = ax.bar(
    x + width / 2,
    summary["execution_time_ms"],
    width,
    label="Search execution"
)

# Labels and formatting
ax.set_title("Automata: Preprocessing and Search Time per Pattern")
ax.set_xlabel("Pattern (word)")
ax.set_ylabel("Time (ms)")

ax.set_xticks(x)
ax.set_xticklabels(words, rotation=45, ha="right")

ax.legend()
ax.grid(axis="y", linestyle="--", alpha=0.5)
ax.set_axisbelow(True)

fig.tight_layout()

# Save graph
fig.savefig(OUTPUT_FILE, dpi=300, bbox_inches="tight")

print(f"Graph saved to: {OUTPUT_FILE}")

plt.show()
