/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Measurement(iterations = 5, batchSize = 100)
@Warmup(iterations = 5, batchSize = 100)
@Fork(1)
public class RecordBenchmark {

  static final AttributesHolder attributesHolder = new AttributesHolder();

  @State(Scope.Benchmark)
  public static class ThreadState {

    @Param private Scenarios scenario;

    private RecorderAndCollector recorderAndCollector;

    @Setup(Level.Trial)
    public void setup() {
      recorderAndCollector = scenario.getRecorderAndCollector();

      recorderAndCollector.setup(attributesHolder, null);
    }
  }

  public enum Scenarios {
    OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_COUNTER_REUSABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_COUNTER_REUSABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_COUNTER_IMMUTABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_COUNTER_IMMUTABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES),
    OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES),
    OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES),
    OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES),
    OTEL_COUNTER_REUSABLE_DATA_UNKNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_COUNTER_REUSABLE_DATA_UNKNOWN_ATTRIBUTES),
    OTEL_COUNTER_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_COUNTER_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES),
    MICROMETER_EXPLICIT_HISTOGRAM_UNKNOWN_TAGS(
        ScenarioConstants.MICROMETER_EXPLICIT_HISTOGRAM_UNKNOWN_TAGS),
    MICROMETER_EXPLICIT_HISTOGRAM_KNOWN_TAGS(
        ScenarioConstants.MICROMETER_EXPLICIT_HISTOGRAM_KNOWN_TAGS),
    MICROMETER_COUNTER_UNKNOWN_TAGS(ScenarioConstants.MICROMETER_COUNTER_UNKNOWN_TAGS),
    MICROMETER_COUNTER_KNOWN_TAGS(ScenarioConstants.MICROMETER_COUNTER_KNOWN_TAGS),
    PROMETHEUS_EXPLICIT_HISTOGRAM_UNKNOWN_LABELS(
        ScenarioConstants.PROMETHEUS_EXPLICIT_HISTOGRAM_UNKNOWN_LABELS),
    PROMETHEUS_EXPONENTIAL_HISTOGRAM_UNKNOWN_LABELS(
        ScenarioConstants.PROMETHEUS_EXPONENTIAL_HISTOGRAM_UNKNOWN_LABELS),
    PROMETHEUS_COUNTER_UNKNOWN_LABELS(ScenarioConstants.PROMETHEUS_COUNTER_UNKNOWN_LABELS),
    PROMETHEUS_EXPLICT_HISTOGRAM_KNOWN_LABELS(
        ScenarioConstants.PROMETHEUS_EXPLICT_HISTOGRAM_KNOWN_LABELS),
    PROMETHEUS_EXPONENTIAL_HISTOGRAM_KNOWN_LABELS(
        ScenarioConstants.PROMETHEUS_EXPONENTIAL_HISTOGRAM_KNOWN_LABELS),
    PROMETHEUS_COUNTER_KNOWN_LABELS(ScenarioConstants.PROMETHEUS_COUNTER_KNOWN_LABELS);

    private final RecorderAndCollector recorderAndCollector;

    Scenarios(RecorderAndCollector recorderAndCollector) {
      this.recorderAndCollector = recorderAndCollector;
    }

    RecorderAndCollector getRecorderAndCollector() {
      return recorderAndCollector;
    }
  }

  /**
   * Profiles the time to record measurements in a single threaded environment. The most useful
   * benchmark metrics are the time score (i.e. ns/op), and {@code gc.alloc.rate.norm}.
   */
  @Benchmark
  @Threads(1)
  public void recordOneThread(ThreadState threadState) {
    BenchmarkUtil.record(threadState.recorderAndCollector, attributesHolder);
  }

  /**
   * Profiles the time to record measurements in a multi threaded environment. The most useful
   * benchmark metrics are the time score (i.e. ns/op), and {@code gc.alloc.rate.norm}.
   */
  @Benchmark
  @Threads(4)
  public void recordFourThreads(ThreadState threadState) {
    BenchmarkUtil.record(threadState.recorderAndCollector, attributesHolder);
  }
}
