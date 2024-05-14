# Java Metric System Benchmarks

Benchmarks to compare the record and collect performance of various popular Java metric systems.

Systems evaluated:

- Micrometer
- OpenTelemetry
- Prometheus

What is benchmarked?

Comparison of record and collect operations for counters, explicit bucket histograms, exponential histograms (if available in the system).

- Record (`RecordBenchmark`)
  - Compare when attributes (or tags or labels) are known ahead of time, and when they need to be computed at time of measurement
  - OpenTelemetry doesn't have bound instruments so knowing attributes ahead of time just means that `Attributes` are cached somewhere
  - Compare recording in a single threaded and multi-threaded (4 threads) environment
- Collect (`CollectBenchmark`)
  - Collect scrapes, reads, or gets all metrics, but does not serialize / encode in any particular protocol. See Collect and Export benchmark below for allocation impact when export is considered.
  - For OpenTelemetry, compare both reusable and immutable memory modes.
- Collect (`CollectAndExportBenchmark`)
  - Collect scrapes, reads, or gets all metrics, and exports to a noop OTLP receiver using the OTLP export pattern documented for the library. The true allocation impact is a function of protocol and the implementation of that protocol. OTLP was chosen because it was available in all the systems and is becoming quite standard.
  - For OpenTelemetry, compare both reusable and immutable memory modes.

## Run

Run the benchmarks:

```shell
./gradlew -PjmhIncludeSingleClass=RecordBenchmark jmh
./jmh_to_csv.sh jmh-record.csv

./gradlew -PjmhIncludeSingleClass=CollectBenchmark jmh
./jmh_to_csv.sh jmh-collect.csv

./gradlew -PjmhIncludeSingleClass=CollectAndExportBenchmark jmh
./jmh_to_csv.sh jmh-collect-export.csv
```

The `./jmh_to_csv.sh` script is optional, and produces a CSV representation of the results.

This will take a while. After it completes, you can view the raw results in a browser by clicking the report link printed to the console.

