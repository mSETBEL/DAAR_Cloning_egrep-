
import csv
import re
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DATA_DIR = ROOT / "data"
OUTPUT = ROOT / "graphs" / "benchmark_results.csv"

PATTERN = "chicago"
REPEATS = 10
WARMUPS = 2

# Change these class names/classpaths to match your project.
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
            f"Command failed: {args}\n{result.stderr}"
        )

    match = re.search(
        r"BENCHMARK_NS=(\d+)", result.stderr
    )

    if not match:
        raise RuntimeError(
            f"No BENCHMARK_NS timing found.\n"
            f"Program: {args}\n"
            f"stderr: {result.stderr}"
        )

    return int(match.group(1)) / 1_000_000


def main():
    books = sorted(DATA_DIR.glob("*.txt"))

    if not books:
        raise FileNotFoundError(f"No .txt files in {DATA_DIR}")

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)

    columns = [
        "book",
        "algorithm",
        "book_size_bytes",
        "book_size_mib",
        "run",
        "execution_time_ms",
    ]

    with OUTPUT.open("w", newline="", encoding="utf-8") as file:
        writer = csv.DictWriter(file, fieldnames=columns)
        writer.writeheader()

        for book in books:
            size = book.stat().st_size

            for algorithm, command in COMMANDS.items():
                print(f"{algorithm}: {book.name}")

                for _ in range(WARMUPS):
                    measure(command, book)

                for run in range(1, REPEATS + 1):
                    elapsed_ms = measure(command, book)

                    writer.writerow({
                        "book": book.name,
                        "algorithm": algorithm,
                        "book_size_bytes": size,
                        "book_size_mib": round(
                            size / (1024 ** 2), 6
                        ),
                        "run": run,
                        "execution_time_ms": round(
                            elapsed_ms, 4
                        ),
                    })

                    file.flush()
                    print(f"  Run {run}: {elapsed_ms:.3f} ms")

    print(f"\nCSV created: {OUTPUT}")


if __name__ == "__main__":
    main()
