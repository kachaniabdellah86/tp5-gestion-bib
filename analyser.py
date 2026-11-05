"""Synthese des sorties CKJM et jdeps (Python 3, bibliotheque standard)."""
from pathlib import Path
import json
import math
import re

root = Path(__file__).resolve().parent
out = root / 'resultats'
keys = ['WMC', 'DIT', 'NOC', 'CBO', 'RFC', 'LCOM', 'Ca', 'NPM']
summary = {}
for version in ['avant', 'apres']:
    metrics = {}
    for line in (out / ('metrics_' + version + '.txt')).read_text(encoding='utf-8-sig').splitlines():
        parts = line.split()
        metrics[parts[0]] = dict(zip(keys, map(int, parts[1:])))
    edges = set()
    current = None
    for line in (out / ('dependances_' + version + '.txt')).read_text(encoding='utf-8-sig').splitlines():
        source = re.match(r'^\s+(library\.[\w.]+) \(bin\)', line)
        if source:
            current = source.group(1)
        target = re.match(r'^\s+-> (library\.[\w.]+)\s', line)
        if target and current:
            edges.add((current, target.group(1)))
    packages = {}
    for package in ['library.model', 'library.service', 'library.app']:
        incoming = sorted({s for s, t in edges if t.rsplit('.', 1)[0] == package and s.rsplit('.', 1)[0] != package})
        outgoing = sorted({t for s, t in edges if s.rsplit('.', 1)[0] == package and t.rsplit('.', 1)[0] != package})
        ca, ce = len(incoming), len(outgoing)
        packages[package] = {'Ca': ca, 'Ce': ce, 'I': ce/(ca+ce) if ca+ce else None, 'clients': incoming, 'dependances': outgoing}
    x = [m['WMC'] for m in metrics.values()]
    y = [m['RFC'] for m in metrics.values()]
    mx, my = sum(x)/len(x), sum(y)/len(y)
    correlation = sum((a-mx)*(b-my) for a,b in zip(x,y))/math.sqrt(sum((a-mx)**2 for a in x)*sum((b-my)**2 for b in y))
    summary[version] = {'classes': metrics, 'packages': packages, 'dependances': sorted(edges), 'correlation_WMC_RFC': correlation}
(out/'synthese.json').write_text(json.dumps(summary, ensure_ascii=False, indent=2), encoding='utf-8')
print('Synthese enregistree : resultats/synthese.json')
