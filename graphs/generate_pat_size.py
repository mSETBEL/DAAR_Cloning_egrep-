import subprocess
import csv
import re
from pathlib import Path

# Paths
ROOT = Path(__file__).resolve().parent.parent
TEXT_FILE = ROOT / "data" / "04.txt"
OUTPUT_FILE = ROOT / "graphs" / "benchmark_pat_size.csv"

#mots courts et moyens qui sont dans le text
#mots courts et moyens qui ne sont pas dans le text
#phrases courtes et moyennes qui sont dans le text
#phrases courtes et moyennes qui ne sont dans le text
words = [
    	"the", "people", "dressing", "bye", "enough",
        "bip", "burp", "fananhanta", "sufficienm", "oncredulity",
        "ut there was no great difficulty", "so that they may meet any little expenses of their journey", "there seems to me to be only one possible solution", "Hence those vows of fidelity exacted upon a Testament", "Suddenly there was the momentary gleam of a light up in the direction of the ventilator",
		"people have the right to protest don't they", "because i can't imagine a small window on that wall", "ringhanapanioiidp mwxoxujakjhhat hanmpmondaynight", "percentage ifhuhdjpappa haugs hdn haapeppenn happen", "open the shuttte hahen", "wmpwpskjahofnamncioandjhstbdkp"
		]

ALGORITHMS = ["FileSearcher", "KMP", "BoyerMoore"]
REPEATS = 5

COMMANDS = {
    "FileSearcher": ["java", "-cp", ".", "FileSearcher", "{pattern}", "{book}"],
    "KMP": ["java", "-cp", ".", "KMP", "{pattern}", "{book}"],
    "BoyerMoore": ["java", "-cp", ".", "BoyerMoore", "{pattern}", "{book}"],
}


def parse_timing(stderr, key):
    match = re.search(rf"{key}=(\d+)", stderr)

    if not match:
        raise ValueError(f"Could not find {key} in Java output:\n{stderr}")

    return int(match.group(1))


def measure(algorithm, pattern):
    command = [
        arg.format(pattern=pattern, book=str(TEXT_FILE))
        for arg in COMMANDS[algorithm]
    ]

    result = subprocess.run(
        command,
        cwd=ROOT,
        capture_output=True,
        text=True,
        timeout=300
    )

    if result.returncode != 0:
        raise RuntimeError(
            f"{algorithm} failed for pattern '{pattern}':\n{result.stderr}"
        )

    preprocessing_ns = parse_timing(result.stderr, "PREPROCESS_NS")
    execution_ns = parse_timing(result.stderr, "BENCHMARK_NS")

    return preprocessing_ns / 1_000_000, execution_ns / 1_000_000


def main():
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)

    with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)

        writer.writerow([
            "text",
            "pattern",
            "pattern_length",
            "algorithm",
            "run",
            "preprocessing_time_ms",
            "execution_time_ms"
        ])

        for pattern in words:
            print(f"Testing pattern: {pattern}")

            for algorithm in ALGORITHMS:
                for run in range(1, REPEATS + 1):

                    preprocessing, execution = measure(
                        algorithm,
                        pattern
                    )

                    writer.writerow([
                        TEXT_FILE.name,
                        pattern,
                        len(pattern),
                        algorithm,
                        run,
                        preprocessing,
                        execution
                    ])

                    file.flush()

    print(f"\nBenchmark saved to: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
