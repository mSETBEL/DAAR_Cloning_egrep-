import csv
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DATA_DIR = ROOT / "data"
OUTPUT = ROOT / "graphs" / "benchmark_pre_search.csv"

PATTERN = "pizzi"
REPEATS = 10

COMMANDS = {
    "Aho-Ullman": [
        "java", "-cp", ".", "FileSearcher",
        "{pattern}", "{book}",
    ],
    "KMP": [
        "java", "-cp", ".", "KMP",
        "{pattern}", "{book}",
    ],
    "Boyer-Moore": [
        "java", "-cp", ".", "BoyerMoore",
        "{pattern}", "{book}",
    ],
}


def parse_timing(stderr, key):
    prefix = f"{key}="
    for line in stderr.splitlines():
        if line.startswith(prefix):
            return int(line[len(prefix):])

    raise ValueError(
        f"Missing {key} in stderr:\n{stderr}"
    )


def measure(command, book):
    args = [
        item.format(pattern=PATTERN, book=str(book.resolve()))
        for item in command
    ]

    result = subprocess.run(
        args,
        cwd=ROOT,
        capture_output=True,
        text=True,
        timeout=300,
    )

    if result.returncode != 0:
        raise RuntimeError(
            f"Command failed: {args}\n"
            f"stderr: {result.stderr}"
        )

    search_ns = parse_timing(result.stderr, "BENCHMARK_NS")
    preprocess_ns = parse_timing(result.stderr, "PREPROCESS_NS")

    return preprocess_ns / 1_000_000, search_ns / 1_000_000


def main():
    books = sorted(DATA_DIR.glob("*.txt"))

    if not books:
        raise FileNotFoundError(f"No .txt files in {DATA_DIR}")

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)

    columns = [
        "book",
        "algorithm",
        "book_size_bytes",
        "run",
        "preprocessing_time_ms",
        "execution_time_ms",
    ]

    with OUTPUT.open("w", newline="", encoding="utf-8") as file:
        writer = csv.DictWriter(file, fieldnames=columns)
        writer.writeheader()

        for book in books:
            size = book.stat().st_size

            for algorithm, command in COMMANDS.items():
                print(f"{algorithm}: {book.name}")

                for run in range(1, REPEATS + 1):
                    preprocess_ms, search_ms = measure(command, book)

                    writer.writerow({
                        "book": book.name,
                        "algorithm": algorithm,
                        "book_size_bytes": size,
                        "run": run,
                        "preprocessing_time_ms": round(preprocess_ms, 4),
                        "execution_time_ms": round(search_ms, 4),
                    })

                    file.flush()
                    print(
                        f"  Run {run}: "
                        f"preprocessing={preprocess_ms:.4f} ms, "
                        f"search={search_ms:.3f} ms"
                    )

    print(f"\nCSV created: {OUTPUT}")


if __name__ == "__main__":
    main()