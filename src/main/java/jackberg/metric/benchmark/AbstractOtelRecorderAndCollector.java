/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporterBuilder;
import io.opentelemetry.sdk.common.export.MemoryMode;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.metrics.internal.SdkMeterProviderUtil;
import io.opentelemetry.sdk.metrics.internal.exemplar.ExemplarFilter;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

abstract class AbstractOtelRecorderAndCollector implements RecorderAndCollector {
  private final MemoryMode memoryMode;
  private final Aggregation aggregation;

  private InMemoryMetricReader inMemoryReader;
  private PeriodicMetricReader periodicOtlpReader;
  protected SdkMeterProvider sdkMeterProvider;

  protected AbstractOtelRecorderAndCollector(MemoryMode memoryMode, Aggregation aggregation) {
    this.memoryMode = memoryMode;
    this.aggregation = aggregation;
  }

  protected AbstractOtelRecorderAndCollector(MemoryMode memoryMode) {
    this(memoryMode, Aggregation.defaultAggregation());
  }

  @Override
  public void setup(AttributesHolder attributesHolder, OtlpEndpoint endpoint) {
    SdkMeterProviderBuilder builder = SdkMeterProvider.builder();

    if (endpoint == null) {
      inMemoryReader =
          InMemoryMetricReader.builder()
              .setMemoryMode(memoryMode)
              .setDefaultAggregationSelector(unused -> aggregation)
              .build();
      builder.registerMetricReader(inMemoryReader);
    } else {
      OtlpGrpcMetricExporterBuilder exporterBuilder =
          OtlpGrpcMetricExporter.builder().setEndpoint(endpoint.otlpGrpcEndpoint);
      OtlpConfigUtil.setMemoryModeOnOtlpExporterBuilder(exporterBuilder, memoryMode);
      periodicOtlpReader =
          PeriodicMetricReader.builder(exporterBuilder.build())
              .setInterval(Duration.ofMillis(Integer.MAX_VALUE))
              .build();
      builder.registerMetricReader(periodicOtlpReader);
    }

    SdkMeterProviderUtil.setExemplarFilter(builder, ExemplarFilter.alwaysOff());
    sdkMeterProvider = builder.build();
  }

  @Override
  public void collect() {
    if (inMemoryReader != null) {
      inMemoryReader
          .collectAllMetrics()
          .forEach(metricData -> metricData.getData().getPoints().forEach(point -> {}));
    } else {
      periodicOtlpReader.forceFlush().join(10, TimeUnit.SECONDS);
    }
  }
}
