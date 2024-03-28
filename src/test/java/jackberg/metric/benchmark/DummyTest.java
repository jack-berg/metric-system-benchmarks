/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import static jackberg.metric.benchmark.ScenarioConstants.*;

import org.junit.jupiter.api.Test;

class DummyTest {

  private static final AttributesHolder attributesHolder = new AttributesHolder();

  //  private final RecorderAndCollector scenario =
  // OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario =
  // OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario =
  // OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario =
  // OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario = OTEL_COUNTER_REUSABLE_DATA_KNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario = OTEL_COUNTER_IMMUTABLE_DATA_KNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario =
  // OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario =
  // OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario =
  // OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario =
  // OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario = OTEL_COUNTER_REUSABLE_DATA_UNKNOWN_ATTRIBUTES;
  //  private final RecorderAndCollector scenario = OTEL_COUNTER_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES;
//  private final RecorderAndCollector scenario = MICROMETER_EXPLICIT_HISTOGRAM_UNKNOWN_TAGS;

    private final RecorderAndCollector scenario = MICROMETER_EXPLICIT_HISTOGRAM_KNOWN_TAGS;
  //  private final RecorderAndCollector scenario = MICROMETER_COUNTER_UNKNOWN_TAGS;
  //  private final RecorderAndCollector scenario = MICROMETER_COUNTER_KNOWN_TAGS;
  //  private final RecorderAndCollector scenario = PROMETHEUS_EXPLICIT_HISTOGRAM_UNKNOWN_LABELS;
  //  private final RecorderAndCollector scenario = PROMETHEUS_EXPONENTIAL_HISTOGRAM_UNKNOWN_LABELS;
  //  private final RecorderAndCollector scenario = PROMETHEUS_COUNTER_UNKNOWN_LABELS;
  //  private final RecorderAndCollector scenario = PROMETHEUS_EXPLICT_HISTOGRAM_KNOWN_LABELS;
  //  private final RecorderAndCollector scenario = PROMETHEUS_EXPONENTIAL_HISTOGRAM_KNOWN_LABELS;
  //  private final RecorderAndCollector scenario = PROMETHEUS_COUNTER_KNOWN_LABELS;

  @Test
  void record() {
    scenario.setup(attributesHolder);

    for (int i = 0; i < 100; i++) {
      BenchmarkUtil.record(scenario, attributesHolder);
    }
  }
}
