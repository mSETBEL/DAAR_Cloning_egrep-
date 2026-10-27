import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

# Project paths
ROOT = Path(__file__).resolve().parent.parent
CSV_FILE = ROOT / "graphs" / "benchmark_results.csv"
OUTPUT_FILE = ROOT / "graphs" / "graph2_book_size_vs_time_pizzi.png" 

# Load benchmark results
df = pd.read_csv(CSV_FILE)

# Check required columns
required = {"book", "algorithm", "execution_time_ms"}
missing = required - set(df.columns)
if missing:
    raise ValueError(f"Missing columns in CSV: {missing}")

# Get book sizes from the actual files
data_dir = ROOT / "data"
sizes = {
    file.name: file.stat().st_size / (1024 ** 2)
    for file in data_dir.glob("*.txt")
}

df["book_size_mib"] = df["book"].map(sizes)

if df["book_size_mib"].isna().any():
    missing_books = df.loc[df["book_size_mib"].isna(), "book"].unique()
    raise ValueError(f"Could not find sizes for books: {missing_books}")

# Use the median of repeated runs for each book and algorithm
summary = (
    df.groupby(["book", "algorithm", "book_size_mib"], as_index=False)
      ["execution_time_ms"]
      .median()
)

# Plot
fig, ax = plt.subplots(figsize=(10, 6))

for algorithm, group in summary.groupby("algorithm"):
    group = group.sort_values("book_size_mib")
    ax.plot(
        group["book_size_mib"],
        group["execution_time_ms"],
        marker="o",
        linewidth=2,
        markersize=5,
        label=algorithm,
    )

ax.set_title("Book Size vs. Execution Time")
ax.set_xlabel("Book size (MiB)")
ax.set_ylabel("Execution time (ms)")
ax.legend(title="Algorithm")
ax.grid(True, linestyle="--", alpha=0.5)

fig.tight_layout()
fig.savefig(OUTPUT_FILE, dpi=300, bbox_inches="tight")
print(f"Graph saved to: {OUTPUT_FILE}")

plt.show()