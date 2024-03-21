/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.micrometer.core.instrument.Tag;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import java.util.Collections;
import java.util.List;
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
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Measurement(iterations = 5, batchSize = 100)
@Warmup(iterations = 5, batchSize = 100)
@Fork(1)
public class MetricSystemBenchmark {

  private static final int cardinality = 100;
  private static final int measurementsPerSeries = 1_000;
  static final AttributeKey<String> ATTRIBUTE_KEY = AttributeKey.stringKey("key");

  @State(Scope.Benchmark)
  public static class ThreadState {

    @Param private MetricSystemScenarios scenario;

    private RecorderAndCollector recorderAndCollector;
    private Random random;

    Attributes[] attributesList;
    List<Tag>[] tagsList;
    String[] labelValues;

    @Setup(Level.Trial)
    public void setup() {
      recorderAndCollector = scenario.getRecorderAndCollector();

      random = new Random();
      attributesList = new Attributes[cardinality];
      tagsList = new List[cardinality];
      labelValues = new String[cardinality];
      String last = "aaaaaaaaaaaaaaaaaaaaaaaaaa";
      for (int i = 0; i < cardinality; i++) {
        char[] chars = last.toCharArray();
        chars[random.nextInt(last.length())] = (char) (random.nextInt(26) + 'a');
        last = new String(chars);
        attributesList[i] = Attributes.builder().put(ATTRIBUTE_KEY, last).build();
        tagsList[i] = Collections.singletonList(Tag.of("key", last));
        labelValues[i] = last;
      }

      recorderAndCollector.setup(this);
      // Record as part of setup so collect doesn't have to muddy waters with record
      record(this);
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

  /**
   * Profiles the time to record measurements in a single threaded environment. The most useful
   * benchmark metrics are the time score (i.e. ns/op), and {@code gc.alloc.rate.norm}.
   */
  @Benchmark
  @Threads(1)
  public void recordOneThread(ThreadState threadState) {
    record(threadState);
  }

  /**
   * Profiles the time to record measurements in a multi threaded environment. The most useful
   * benchmark metrics are the time score (i.e. ns/op), and {@code gc.alloc.rate.norm}.
   */
  @Benchmark
  @Threads(4)
  public void recordFourThreads(ThreadState threadState) {
    record(threadState);
  }

  private static void record(ThreadState threadState) {
    for (int j = 0; j < measurementsPerSeries; j++) {
      for (int i = 0; i < threadState.attributesList.length; i++) {
        double value = threadState.random.nextInt(10_000);
        threadState.recorderAndCollector.record(threadState, value, i);
      }
    }
  }
}
