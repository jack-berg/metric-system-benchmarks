# Java Metric System Benchmarks

Benchmarks to compare the record and collect performance of various popular Java metric systems.

Systems evaluated:

- Micrometer
- OpenTelemetry
- Prometheus

What is benchmarked?

Comparison of record and collect operations for counters, explicit bucket histograms, exponential histograms (if available in the system).

- Record
  - Compare when attributes (or tags or labels) are known ahead of time, and when they need to be computed at time of measurement
  - OpenTelemetry doesn't have bound instruments so knowing attributes ahead of time just means that `Attributes` are cached somewhere
  - Compare recording in a single threaded and multi-threaded (4 threads) environment
- Collect
  - Collect scraps, reads, or gets all metrics, but does not serialize / encode in any particular protocol. The true allocation impact of collecting will depend on the protocol and the implementation of that protocol, but those questions are not currently addressed.
  - For OpenTelemetry, compare both reusable and immutable memory modes

## Run

Run the benchmarks:

```shell
./gradlew -PjmhIncludeSingleClass=CollectBenchmark jmh
./jmh_to_csv jmh-collect.csv

./gradlew -PjmhIncludeSingleClass=RecordBenchmark jmh
./jmh_to_csv jmh-record.csv
```

This will take a while. After it completes, you can view the raw results in a browser by clicking the report link printed to the console.

The `./jmh_to_csv.sh` script is optional, and produces a CSV representation of the results.
