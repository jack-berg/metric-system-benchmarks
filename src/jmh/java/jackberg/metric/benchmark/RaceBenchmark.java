/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.testcontainers.containers.GenericContainer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(time = 5, timeUnit = TimeUnit.SECONDS)
@Warmup(time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class RaceBenchmark {

  static final AttributesHolder attributesHolder = new AttributesHolder();
  static GenericContainer<?> collector;
  static OtlpEndpoint otlpEndpoint;

  static {
    collector = BenchmarkUtil.collectorContainer();
    collector.start();
    otlpEndpoint = BenchmarkUtil.collectorEndpoint(collector);
  }

  @State(Scope.Benchmark)
  public static class ThreadState {

    @Param private Scenarios scenario;

    private RecorderAndCollector recorderAndCollector;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> future;

    @Setup(Level.Trial)
    public void setup() {
      recorderAndCollector = scenario.getRecorderAndCollector();
      scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

      recorderAndCollector.setup(attributesHolder, otlpEndpoint);
      future = scheduledExecutorService.scheduleAtFixedRate(
          recorderAndCollector::collect, 0, 100, TimeUnit.MILLISECONDS);

      // Record as part of setup so collect doesn't have to muddy waters with record
      BenchmarkUtil.record(recorderAndCollector, attributesHolder);
    }

    @TearDown(Level.Trial)
    public void teardown() {
      future.cancel(true);
      scheduledExecutorService.shutdown();
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
    PROMETHEUS_EXPLICIT_HISTOGRAM_KNOWN_LABELS(
        ScenarioConstants.PROMETHEUS_EXPLICIT_HISTOGRAM_KNOWN_LABELS),
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
  public void record(ThreadState threadState) {
    BenchmarkUtil.record(threadState.recorderAndCollector, attributesHolder);
  }
}