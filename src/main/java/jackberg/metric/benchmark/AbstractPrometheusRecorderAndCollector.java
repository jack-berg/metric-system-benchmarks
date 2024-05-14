/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.prometheus.metrics.exporter.opentelemetry.OpenTelemetryExporter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.shaded.io_opentelemetry_1_36_0.sdk.metrics.export.PeriodicMetricReader;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

abstract class AbstractPrometheusRecorderAndCollector implements RecorderAndCollector {

  protected PrometheusRegistry prometheusRegistry;
  private PeriodicMetricReader periodicMetricReader;

  protected AbstractPrometheusRecorderAndCollector() {}

  @Override
  public void setup(AttributesHolder attributesHolder, OtlpEndpoint otlpEndpoint) {
    prometheusRegistry = new PrometheusRegistry();
    if (otlpEndpoint != null) {
      OpenTelemetryExporter exporter =
          OpenTelemetryExporter.builder()
              .endpoint(otlpEndpoint.otlpGrpcEndpoint)
              .intervalSeconds(Integer.MAX_VALUE)
              .registry(prometheusRegistry)
              .buildAndStart();
      // Reflectively access underlying periodic metric reader so we can manually flush it
      try {
        Field reader = OpenTelemetryExporter.class.getDeclaredField("reader");
        reader.setAccessible(true);
        periodicMetricReader = (PeriodicMetricReader) reader.get(exporter);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void collect() {
    if (periodicMetricReader != null) {
      periodicMetricReader.forceFlush().join(10, TimeUnit.SECONDS);
    }
    prometheusRegistry
        .scrape()
        .forEach(metricSnapshot -> metricSnapshot.getDataPoints().forEach(dataPointSnapshot -> {}));
  }
}
