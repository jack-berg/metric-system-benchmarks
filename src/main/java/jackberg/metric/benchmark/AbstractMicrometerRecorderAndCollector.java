/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package jackberg.metric.benchmark;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;

abstract class AbstractMicrometerRecorderAndCollector implements RecorderAndCollector {
  protected MeterRegistry registry;

  @Override
  public void setup(AttributesHolder attributesHolder, OtlpEndpoint otlpEndpoint) {
    if (otlpEndpoint != null) {
      OtlpConfig otlpConfig =
          new OtlpConfig() {
            @Override
            public String get(String key) {
              return null;
            }

            @Override
            public String url() {
              return otlpEndpoint.otlpHttpEndpoint + "/v1/metrics";
            }
          };
      registry = new FlusableOtlpMeterRegistry(otlpConfig, Clock.SYSTEM);
    } else {
      registry = new SimpleMeterRegistry();
    }
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
  }

  @Override
  public void collect() {
    if (registry instanceof FlusableOtlpMeterRegistry) {
      ((FlusableOtlpMeterRegistry) registry).publish();
    } else {
      registry.getMeters().forEach(meter -> meter.measure().forEach(unused -> {}));
    }
  }

  private static class FlusableOtlpMeterRegistry extends OtlpMeterRegistry {
    private FlusableOtlpMeterRegistry(OtlpConfig otlpConfig, Clock clock) {
      super(otlpConfig, clock);
    }

    @Override
    protected void publish() {
      super.publish();
    }
  }
}
