/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.opentelemetry.sdk.common.export.MemoryMode;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.internal.SdkMeterProviderUtil;
import io.opentelemetry.sdk.metrics.internal.exemplar.ExemplarFilter;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;

abstract class AbstractOtelRecorderAndCollector implements RecorderAndCollector {
  private final MemoryMode memoryMode;
  private final Aggregation aggregation;

  private InMemoryMetricReader reader;
  protected SdkMeterProvider sdkMeterProvider;

  protected AbstractOtelRecorderAndCollector(MemoryMode memoryMode, Aggregation aggregation) {
    this.memoryMode = memoryMode;
    this.aggregation = aggregation;
  }

  protected AbstractOtelRecorderAndCollector(MemoryMode memoryMode) {
    this(memoryMode, Aggregation.defaultAggregation());
  }

  @Override
  public void setup(AttributesHolder attributesHolder) {
    reader =
        InMemoryMetricReader.builder()
            .setMemoryMode(memoryMode)
            .setDefaultAggregationSelector(unused -> aggregation)
            .build();
    SdkMeterProviderBuilder builder = SdkMeterProvider.builder();
    builder.registerMetricReader(reader);
    SdkMeterProviderUtil.setExemplarFilter(builder, ExemplarFilter.alwaysOff());
    sdkMeterProvider = builder.build();
  }

  @Override
  public void collect() {
    reader
        .collectAllMetrics()
        .forEach(metricData -> metricData.getData().getPoints().forEach(point -> {}));
  }
}
