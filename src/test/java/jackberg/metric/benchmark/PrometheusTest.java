/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.prometheus.metrics.core.datapoints.CounterDataPoint;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.CounterSnapshot;
import io.prometheus.metrics.model.snapshots.HistogramSnapshot;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrometheusTest {
  private static final int cardinality = 100;
  private static final int measurementsPerSeries = 1_000;

  private final PrometheusRegistry prometheusRegistry = new PrometheusRegistry();
  private final Random random = new Random();
  private String[] labelValues;

  @BeforeEach
  void setup() {
    labelValues = new String[cardinality];
    String last = "aaaaaaaaaaaaaaaaaaaaaaaaaa";
    for (int i = 0; i < cardinality; i++) {
      char[] chars = last.toCharArray();
      chars[random.nextInt(last.length())] = (char) (random.nextInt(26) + 'a');
      last = new String(chars);
      labelValues[i] = last;
    }
  }

  @Test
  void attributesKnown() {
    Counter counterWithLabels =
        Counter.builder().name("counter").labelNames("key").register(prometheusRegistry);

    Histogram histogramWithLabels =
        Histogram.builder()
            .name("histogram")
            .classicUpperBounds(
                0d, 5d, 10d, 25d, 50d, 75d, 100d, 250d, 500d, 750d, 1_000d, 2_500d, 5_000d, 7_500d,
                10_000d)
            .labelNames("key")
            .register(prometheusRegistry);

    Histogram nativeHistogramWithLabels =
        Histogram.builder().name("native_histogram").nativeOnly().labelNames("key").register();

    for (int j = 0; j < measurementsPerSeries; j++) {
      for (int i = 0; i < labelValues.length; i++) {
        int value = random.nextInt(10_000);
        histogramWithLabels.labelValues(labelValues[i]).observe(value);
        counterWithLabels.labelValues(labelValues[i]).inc(value);
        nativeHistogramWithLabels.labelValues(labelValues[i]).observe(value);
      }
    }

    scrapeAndPrint();
  }

  @Test
  void attributesUnknown() {
    Counter counterWithLabels =
        Counter.builder()
            .name("counter")
            .labelNames("key")
            .withoutExemplars()
            .register(prometheusRegistry);

    //    Histogram histogramWithLabels =
    //        Histogram.builder()
    //            .name("histogram")
    //            .classicUpperBounds(
    //                0d, 5d, 10d, 25d, 50d, 75d, 100d, 250d, 500d, 750d, 1_000d, 2_500d, 5_000d,
    // 7_500d,
    //                10_000d)
    //            .labelNames("key")
    //            .withExemplars()
    //            .register(prometheusRegistry);
    //
    //    Histogram nativeHistogramWithLabels =
    //        Histogram.builder()
    //            .name("native_histogram")
    //            .nativeOnly()
    //            .withoutExemplars()
    //            .labelNames("key")
    //            .register(prometheusRegistry);

    CounterDataPoint[] counterDataPoints = new CounterDataPoint[labelValues.length];
    //    DistributionDataPoint[] histogramDataPoints = new
    // DistributionDataPoint[labelValues.length];
    //    DistributionDataPoint[] nativeHistogramDataPoints =
    //        new DistributionDataPoint[labelValues.length];
    for (int i = 0; i < labelValues.length; i++) {
      counterDataPoints[i] = counterWithLabels.labelValues(labelValues[i]);
      //      histogramDataPoints[i] = histogramWithLabels.labelValues(labelValues[i]);
      //      nativeHistogramDataPoints[i] = nativeHistogramWithLabels.labelValues(labelValues[i]);
    }

    for (int j = 0; j < measurementsPerSeries; j++) {
      for (int i = 0; i < labelValues.length; i++) {
        int value = random.nextInt(10_000);
        counterDataPoints[i].inc(value);
        //        histogramDataPoints[i].observe(value);
        //        nativeHistogramDataPoints[i].observe(value);
      }
    }

    collect();

    // scrapeAndPrint();
  }

  private void collect() {
    for (int i = 0; i < 100; i++) {
      MetricSnapshots scrape = prometheusRegistry.scrape();
    }
  }

  private void scrapeAndPrint() {
    PrometheusRegistry.defaultRegistry
        .scrape()
        .forEach(
            metricSnapshot -> {
              System.out.format("%s\n", metricSnapshot.getMetadata().getName());
              metricSnapshot
                  .getDataPoints()
                  .forEach(
                      dataPointSnapshot -> {
                        if (dataPointSnapshot instanceof CounterSnapshot.CounterDataPointSnapshot) {
                          System.out.format(
                              "  %s: %s\n",
                              dataPointSnapshot.getLabels(),
                              ((CounterSnapshot.CounterDataPointSnapshot) dataPointSnapshot)
                                  .getValue());
                        }
                        if (dataPointSnapshot
                            instanceof HistogramSnapshot.HistogramDataPointSnapshot) {
                          System.out.format(
                              "  %s: %s\n",
                              dataPointSnapshot.getLabels(),
                              ((HistogramSnapshot.HistogramDataPointSnapshot) dataPointSnapshot)
                                  .getClassicBuckets().stream()
                                      .map(
                                          bucket ->
                                              bucket.getUpperBound() + ": " + bucket.getCount())
                                      .collect(Collectors.joining(", ")));
                        }
                        if (dataPointSnapshot
                            instanceof HistogramSnapshot.HistogramDataPointSnapshot) {
                          System.out.format(
                              "  %s: %s%s\n",
                              dataPointSnapshot.getLabels(),
                              ((HistogramSnapshot.HistogramDataPointSnapshot) dataPointSnapshot)
                                  .getClassicBuckets().stream()
                                      .map(
                                          bucket ->
                                              bucket.getUpperBound() + ": " + bucket.getCount())
                                      .collect(Collectors.joining(", ")),
                              ((HistogramSnapshot.HistogramDataPointSnapshot) dataPointSnapshot)
                                  .getNativeBucketsForPositiveValues().stream()
                                      .map(
                                          bucket ->
                                              bucket.getBucketIndex() + ": " + bucket.getCount())
                                      .collect(Collectors.joining(", ")));
                        }
                      });
              System.out.println();
            });
  }
}
