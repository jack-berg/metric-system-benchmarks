/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

interface RecorderAndCollector {
  default void setup(AttributesHolder attributesHolder, OtlpEndpoint otlpEndpoint) {}

  void record(AttributesHolder attributesHolder, double value, int attributesIndex);

  void collect();
}
