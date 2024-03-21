/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

interface RecorderAndCollector {
  default void setup(MetricSystemBenchmark.ThreadState threadState) {}

  void record(MetricSystemBenchmark.ThreadState threadState, double value, int attributesIndex);

  void collect();
}
