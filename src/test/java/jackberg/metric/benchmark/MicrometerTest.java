/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.common.export.MemoryMode;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.internal.SdkMeterProviderUtil;
import io.opentelemetry.sdk.metrics.internal.exemplar.ExemplarFilter;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MicrometerTest {

  private static final AttributeKey<String> ATTRIBUTE_KEY = AttributeKey.stringKey("key");

  private static final int cardinality = 100;
  private static final int measurementsPerSeries = 10_000;

  private final Random random = new Random();
  private MeterRegistry registry;
  private SdkMeterProvider sdkMeterProvider;
  private Attributes[] attributesList;
  private String[] values;
  private List<Tag>[] tagsList;
  private Counter[] counters;
  private DistributionSummary[] distributionSummaries;

  @BeforeEach
  void setup() {
    InMemoryMetricReader reader =
        InMemoryMetricReader.builder()
            .setMemoryMode(MemoryMode.REUSABLE_DATA)
            .setDefaultAggregationSelector(unused -> Aggregation.defaultAggregation())
            .build();
    SdkMeterProviderBuilder builder = SdkMeterProvider.builder();
    SdkMeterProviderUtil.setExemplarFilter(builder, ExemplarFilter.alwaysOff());
    sdkMeterProvider = builder.registerMetricReader(reader).build();

    registry = new SimpleMeterRegistry();
    registry
        .config()
        .meterFilter(
            new MeterFilter() {
              @Override
              public DistributionStatisticConfig configure(
                  Meter.Id id, DistributionStatisticConfig config) {
                return DistributionStatisticConfig.builder()
                    .serviceLevelObjectives(
                        Double.MIN_VALUE,
                        5d,
                        10d,
                        25d,
                        50d,
                        75d,
                        100d,
                        250d,
                        500d,
                        750d,
                        1_000d,
                        2_500d,
                        5_000d,
                        7_500d,
                        10_000d)
                    .build();
              }
            });

    tagsList = new List[cardinality];
    counters = new Counter[cardinality];
    attributesList = new Attributes[cardinality];
    values = new String[cardinality];
    distributionSummaries = new DistributionSummary[cardinality];
    String last = "aaaaaaaaaaaaaaaaaaaaaaaaaa";
    for (int i = 0; i < cardinality; i++) {
      char[] chars = last.toCharArray();
      chars[random.nextInt(last.length())] = (char) (random.nextInt(26) + 'a');
      last = new String(chars);
      attributesList[i] = Attributes.builder().put("key", last).build();
      values[i] = last;
      tagsList[i] = Collections.singletonList(Tag.of("key", last));
      counters[i] = registry.counter("counter", tagsList[i]);
      distributionSummaries[i] = registry.summary("histogram", tagsList[i]);
    }
  }

  @Test
  void attributesKnown() {
    for (int j = 0; j < measurementsPerSeries; j++) {
      for (int i = 0; i < tagsList.length; i++) {
        int value = random.nextInt(10_000);
        distributionSummaries[i].record(value);
        counters[i].increment(value);
      }
    }

    registry
        .getMeters()
        .forEach(
            meter -> {
              System.out.format("%s: %s\n", meter.getId(), meter.measure());
            });
  }

  @Test
  void attributesUnknown() {
    for (int j = 0; j < measurementsPerSeries; j++) {
      for (int i = 0; i < tagsList.length; i++) {
        int value = random.nextInt(10_000);
        // registry.summary("histogram", tagsList[i]).record(value);
        registry.counter("counter", tagsList[i]).increment(value);
      }
    }

    collect();

    //    registry
    //        .getMeters()
    //        .forEach(
    //            meter -> {
    //              System.out.format("%s: %s\n", meter.getId(), meter.measure());
    //            });
  }

  private void collect() {
    for (int i = 0; i < 100; i++) {
      registry
          .getMeters()
          .forEach(
              meter -> {
                meter.measure().forEach(measurement -> {});
              });
    }
  }

  //  @Test
  //  void otelExplicitBucket_MultiThreaded() {
  //    DoubleHistogram histogram =
  // sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
  //    runInMultipleThreads(
  //        4,
  //        () -> {
  //          while (true) {
  //            for (int i = 0; i < tagsList.length; i++) {
  //              int value = random.nextInt(10_000);
  //              // histogram.record(value, attributesList[i], Context.root());
  //              histogram.record(value, Attributes.of(ATTRIBUTE_KEY, values[i]), Context.root());
  //            }
  //            try {
  //              Thread.sleep(10);
  //            } catch (InterruptedException e) {
  //              throw new RuntimeException(e);
  //            }
  //          }
  //        });
  //  }

  //  @Test
  //  void allocateAttributes() {
  //    runInMultipleThreads(
  //        4,
  //        () -> {
  //          while (true) {
  //            for (int i = 0; i < tagsList.length; i++) {
  //              Attributes.of(ATTRIBUTE_KEY, values[i]);
  //            }
  //          }
  //        });
  //  }

  void runInMultipleThreads(int threadCount, Runnable runnable) {
    List<Thread> threads = new ArrayList<>(threadCount);
    for (int i = 0; i < threadCount; i++) {
      Thread thread = new Thread(runnable);
      thread.setDaemon(true);
      thread.setName("test-" + i);
      thread.start();
      threads.add(thread);
    }
    threads.forEach(
        thread -> {
          try {
            thread.join();
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
