# /bin/bash

cat ./build/results/jmh/results.json | jq -r '["Benchmark Scenario","Time (ns/op)","Alloc (bytes/op)"], (.[] | [.benchmark[26:] + "_" + .params.scenario,.primaryMetric.score,.secondaryMetrics."gc.alloc.rate.norm".score]) | @csv' \
 > $1

