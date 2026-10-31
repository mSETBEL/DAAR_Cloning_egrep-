# patlen.py  --  usage : python3 patlen.py data/un_livre.txt
import subprocess, re, random, statistics, sys
import matplotlib.pyplot as plt

BOOK = sys.argv[1]
LENS = [3, 5, 8, 12, 20]
RUNS = 6          # la 1re exécution (cache disque) est ignorée
random.seed(42)

lines = open(BOOK, encoding="utf-8", errors="ignore").read().splitlines()

def random_pattern(n):
    for _ in range(20000):
        l = random.choice(lines)
        if len(l) >= n:
            s = random.randint(0, len(l) - n)
            p = l[s:s + n]
            if re.fullmatch(r"[A-Za-z]([A-Za-z ]*[A-Za-z])?", p):
                return p
    raise SystemExit(f"Pas de motif de longueur {n} trouvé")

def run(cmd, pat):
    r = subprocess.run(["java", cmd, pat, BOOK], capture_output=True, text=True)
    err = r.stderr
    return (int(re.search(r"BENCHMARK_NS=(\d+)", err).group(1)) / 1e6,
            int(re.search(r"PREPROCESS_NS=(\d+)", err).group(1)) / 1e6)

algos = {"KMP": "KMP", "Boyer-Moore": "BoyerMoore", "Automate": "FileSearcher"}
res = {a: [] for a in algos}
for n in LENS:
    pat = random_pattern(n)
    print(f"longueur {n} : motif = {pat!r}")
    for name, cmd in algos.items():
        t = [run(cmd, pat)[0] for _ in range(RUNS)][1:]
        res[name].append((statistics.mean(t), statistics.stdev(t)))

fig, ax = plt.subplots()
for name, vals in res.items():
    ax.errorbar(LENS, [v[0] for v in vals], yerr=[v[1] for v in vals],
                marker="o", capsize=3, label=name)
ax.set_xlabel("Longueur du motif (caractères)")
ax.set_ylabel("Temps de recherche (ms)")
ax.legend()
fig.savefig("graphs/fig_patlen.png", dpi=200, bbox_inches="tight")
for name, vals in res.items():
    print(name, [f"{m:.1f}±{s:.1f}" for m, s in vals])