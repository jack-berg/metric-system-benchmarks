/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.prometheus.metrics.model.registry.PrometheusRegistry;

abstract class AbstractPrometheusRecorderAndCollector implements RecorderAndCollector {

  protected PrometheusRegistry prometheusRegistry;

  protected AbstractPrometheusRecorderAndCollector() {}

  @Override
  public void setup(AttributesHolder attributesHolder) {
    prometheusRegistry = new PrometheusRegistry();
  }

  @Override
  public void collect() {
    prometheusRegistry
        .scrape()
        .forEach(metricSnapshot -> metricSnapshot.getDataPoints().forEach(dataPointSnapshot -> {}));
  }
}
