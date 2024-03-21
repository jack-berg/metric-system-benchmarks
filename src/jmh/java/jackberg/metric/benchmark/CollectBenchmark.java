/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import java.util.Random;
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
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Measurement(iterations = 5, batchSize = 100)
@Warmup(iterations = 5, batchSize = 100)
@Fork(1)
public class CollectBenchmark {

  private static final int cardinality = 100;
  private static final int measurementsPerSeries = 1_000;
  static final AttributesHolder attributesHolder = new AttributesHolder(cardinality);

  @State(Scope.Benchmark)
  public static class ThreadState {

    @Param private Scenarios scenario;

    private RecorderAndCollector recorderAndCollector;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
      recorderAndCollector = scenario.getRecorderAndCollector();

      random = new Random();
      recorderAndCollector.setup(attributesHolder);
      // Record as part of setup so collect doesn't have to muddy waters with record
      record(this);
    }
  }

  public enum Scenarios {
    OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES),
    OTEL_COUNTER_REUSABLE_DATA_KNOWN_ATTRIBUTES(
        ScenarioConstants.OTEL_COUNTER_REUSABLE_DATA_KNOWN_ATTRIBUTES),
    MICROMETER_EXPLICIT_HISTOGRAM_KNOWN_TAGS(
        ScenarioConstants.MICROMETER_EXPLICIT_HISTOGRAM_KNOWN_TAGS),
    MICROMETER_COUNTER_KNOWN_TAGS(ScenarioConstants.MICROMETER_COUNTER_KNOWN_TAGS),
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
   * Profiles the memory cost of collecting data. The most useful benchmark metrics is {@code
   * gc.alloc.rate.norm}.
   */
  @Benchmark
  public void collect(ThreadState threadState) {
    threadState.recorderAndCollector.collect();
  }

  private static void record(ThreadState threadState) {
    for (int j = 0; j < measurementsPerSeries; j++) {
      for (int i = 0; i < cardinality; i++) {
        double value = threadState.random.nextInt(10_000);
        threadState.recorderAndCollector.record(attributesHolder, value, i);
      }
    }
  }
}
