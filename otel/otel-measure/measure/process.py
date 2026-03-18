import sys

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import t

directories = ["plugin", "manual", "reference", "manual-noop"]
files = ["jvm", "js", "native"]

exec = {
    "plugin": {
        "jvm": [],
        "js": [],
        "native": [],
    },
    "manual": {
        "jvm": [],
        "js": [],
        "native": [],
    },
    "reference": {
        "jvm": [],
        "js": [],
        "native": [],
    },
    "manual-noop": {
        "jvm": [],
        "js": [],
        "native": [],
    }
}
flush = {
    "plugin": {
        "jvm": [],
        "js": [],
        "native": [],
    },
    "manual": {
        "jvm": [],
        "js": [],
        "native": [],
    },
}

exec_ci = {
    "plugin": {
        "jvm": (),
        "js": (),
        "native": (),
    },
    "manual": {
        "jvm": (),
        "js": (),
        "native": (),
    },
    "reference": {
        "jvm": (),
        "js": (),
        "native": (),
    },
    "manual-noop": {
        "jvm": [],
        "js": [],
        "native": [],
    }
}
flush_ci = {
    "plugin": {
        "jvm": (),
        "js": (),
        "native": (),
    },
    "manual": {
        "jvm": (),
        "js": (),
        "native": (),
    },
}

alpha = 0.05


def ci(data):
    mean = data.mean()
    n = len(data)
    df = n - 1
    moe = t.ppf(1 - alpha / 2, df) * data.std(ddof=1) / np.sqrt(n)
    lower = mean - moe
    upper = mean + moe
    s = data.std(ddof=1)
    return lower, upper, s

for directory in directories:
    for file in files:
        data = pd.read_csv(f"{directory}/{file}.txt", sep=" ", header=None, names=["exec", "flush"])

        exec[directory][file] = data["exec"]
        if directory != "reference" and directory != "manual-noop":
            flush[directory][file] = data["flush"]

    for file in files:
        edata = exec[directory][file]
        exec_ci[directory][file] = ci(edata)

        if directory != "reference" and directory != "manual-noop":
            fdata = flush[directory][file]
            flush_ci[directory][file] = ci(fdata)

if sys.argv[1] == "project":
    for directory in directories:
        e = exec[directory].values()
        e_ci = exec_ci[directory].values()

        if directory != "reference" and directory != "manual-noop":
            f = flush[directory].values()
            f_ci = flush_ci[directory].values()

        # boxplot (plugin, manual, reference)
        if directory != "reference" and directory != "manual-noop":
            plt.figure(figsize=(10, 5))
            plt.subplot(1, 2, 1)
            plt.boxplot(e, tick_labels=["JVM", "JS", "Native"])
            plt.ylim(bottom=0, top=120000)
            plt.title("Execution Time")
            plt.grid()

            plt.subplot(1, 2, 2)
            plt.boxplot(f, tick_labels=["JVM", "JS", "Native"])
            plt.ylim(bottom=0, top=120000)
            plt.title("Total Time")
            plt.grid()
        else:
            plt.figure(figsize=(5, 5))
            plt.boxplot(e, tick_labels=["JVM", "JS", "Native"])
            plt.ylim(bottom=0, top=50)
            plt.title("Execution Time")
            plt.grid()

        plt.tight_layout()
        plt.savefig(f"{directory}/boxplot")

        # confidence interval
        e_ci_mean = [(a + b) / 2 for a, b, c in e_ci]
        e_ci_error = np.array(e_ci_mean) - [a for a, b, c in e_ci]

        if directory != "reference" and directory != "manual-noop":
            f_ci_mean = [(a + b) / 2 for a, b, c in f_ci]
            f_ci_error = np.array(f_ci_mean) - [a for a, b, c in f_ci]

        plt.figure()

        if directory != "reference" and directory != "manual-noop":
            plt.subplot(1, 2, 1)
            plt.errorbar([0, 1, 2], e_ci_mean, yerr=e_ci_error, capsize=3, linestyle="")
            plt.ylim(bottom=0, top=120000)
            plt.title(r"Execution Time $\mu$")
            plt.xticks([0, 1, 2], ["JVM", "JS", "Native"])
            plt.grid()

            plt.subplot(1, 2, 2)
            plt.errorbar([0, 1, 2], f_ci_mean, yerr=f_ci_error, capsize=3, linestyle="")
            plt.ylim(bottom=0, top=120000)
            plt.title(r"Total Execution Time $\mu$")
            plt.xticks([0, 1, 2], ["JVM", "JS", "Native"])
            plt.grid()
        else:
            plt.errorbar([0, 1, 2], e_ci_mean, yerr=e_ci_error, capsize=3, linestyle="")
            plt.ylim(bottom=0, top=100)
            plt.title(r"Execution Time $\mu$")
            plt.xticks([0, 1, 2], ["JVM", "JS", "Native"])
            plt.grid()

        plt.tight_layout()
        plt.savefig(f"{directory}/ci")

elif sys.argv[1] == "target":
    # boxplot (jvm, js, native)
    for file in files:
        e = []
        f = []
        for directory in directories:
            e.append(exec[directory][file])
            if directory != "reference":
                f.append(flush[directory][file])

        plt.figure()
        plt.subplot(1, 2, 1)
        plt.boxplot(e, tick_labels=["Plugin", "Manual", "Reference"])
        plt.ylim(bottom=0, top=120000)
        plt.title("Execution Time")
        plt.grid()

        plt.subplot(1, 2, 2)
        plt.boxplot([*f, e[-1]], tick_labels=["Plugin", "Manual", "Reference"])
        plt.ylim(bottom=0, top=120000)
        plt.title("Total Execution Time")
        plt.grid()

        plt.tight_layout()
        plt.savefig(f"{file}")

elif sys.argv[1] == "table":
    np.set_printoptions(legacy='1.25')
    just = 30
    # table (confidence interval, standard deviation)
    print(f"\t{"plugin".ljust(just)}\t\t{'manual'.ljust(just)}\t\t{'reference'.ljust(just)}\t\t{'noop'.ljust(just)}")
    for file in files:
        e = []
        f = []
        for directory in directories:
            a, b, c = exec_ci[directory][file]
            e.append([round(a + (b - a) / 2, 2), round((b - a) / 2, 2), round(c, 2)])
            if directory != "reference" and directory != "manual-noop":
                a, b, c = flush_ci[directory][file]
                f.append([round(a + (b - a) / 2, 2), round((b - a) / 2, 2), round(c, 2)])

        print("- - - - " + ("- " * int(((just + 4) * 4) / 2)))
        print(f"{file.ljust(6)}\t{str(e[0]).ljust(just)}\t\t{str(e[1]).ljust(just)}\t\t{str(e[2]).ljust(just)}\t\t{str(e[3]).ljust(just)}")
        print(f"total\t{str(f[0]).ljust(just)}\t\t{str(f[1]).ljust(just)}\t\t{"---".ljust(just)}\t\t{"---".ljust(just)}")
