import sys
import fileinput
import re
# usage on spark-master
# python3 preprocess.py /home/graphsense/research/cluster_metrics/data/raw/tx_inputs.csv 1> outputFile.csv

#! Only redirect stdout to file as stderr will contain progress information instead
counter = 0
hashes = dict()
i = 0
for line in fileinput.input():
    i += 1
    n = []
    m = re.split("'", line)
    for h in m[1::2]:
        if hashes.get(h) is None:
            hashes[h] = counter
            counter += 1
        e = hashes.get(h)
        n.append(e)
    if i % 10000 == 0:
        print("Line {}, {} addrs so far".format(i, len(hashes)), file=sys.stderr)
    if (len(n) > 1):
        print(",".join(str(index) for index in n))
