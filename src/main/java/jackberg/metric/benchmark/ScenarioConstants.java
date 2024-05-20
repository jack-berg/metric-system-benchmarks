package jackberg.metric.benchmark;

import io.micrometer.core.instrument.DistributionSummary;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.export.MemoryMode;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.prometheus.metrics.core.datapoints.CounterDataPoint;
import io.prometheus.metrics.core.datapoints.DistributionDataPoint;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;

public class ScenarioConstants {

  /**
   * Otel recording to histogram with explicit bucket boundaries, {@link MemoryMode#REUSABLE_DATA},
   * and known attributes.
   */
  static RecorderAndCollector OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.REUSABLE_DATA) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value, attributeHolder.attributesList[attributesIndex], Context.root());
        }
      };

  /**
   * Otel recording to histogram with explicit bucket boundaries, {@link MemoryMode#IMMUTABLE_DATA},
   * and known attributes.
   */
  static RecorderAndCollector OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.IMMUTABLE_DATA) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value, attributeHolder.attributesList[attributesIndex], Context.root());
        }
      };

  /**
   * Otel recording to histogram with base2 exponential aggregation, {@link
   * MemoryMode#REUSABLE_DATA}, and known attributes.
   */
  static RecorderAndCollector OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_KNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(
          MemoryMode.REUSABLE_DATA, Aggregation.base2ExponentialBucketHistogram()) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value, attributeHolder.attributesList[attributesIndex], Context.root());
        }
      };

  /**
   * Otel recording to histogram with base2 exponential aggregation, {@link
   * MemoryMode#IMMUTABLE_DATA}, and known attributes.
   */
  static RecorderAndCollector OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_KNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(
          MemoryMode.IMMUTABLE_DATA, Aggregation.base2ExponentialBucketHistogram()) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value, attributeHolder.attributesList[attributesIndex], Context.root());
        }
      };

  /** Otel recording to counter, {@link MemoryMode#REUSABLE_DATA}, and known attributes. */
  static RecorderAndCollector OTEL_COUNTER_REUSABLE_DATA_KNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.REUSABLE_DATA) {
        private DoubleCounter doubleCounter;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleCounter =
              sdkMeterProvider.get("meter").counterBuilder("counter").ofDoubles().build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleCounter.add(value, attributeHolder.attributesList[attributesIndex], Context.root());
        }
      };

  /** Otel recording to counter, {@link MemoryMode#REUSABLE_DATA}, and known attributes. */
  static RecorderAndCollector OTEL_COUNTER_IMMUTABLE_DATA_KNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.IMMUTABLE_DATA) {
        private DoubleCounter doubleCounter;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleCounter =
              sdkMeterProvider.get("meter").counterBuilder("counter").ofDoubles().build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleCounter.add(value, attributeHolder.attributesList[attributesIndex], Context.root());
        }
      };

  /**
   * Otel recording to histogram with default explicit bucket boundaries, {@link
   * MemoryMode#REUSABLE_DATA}, and unknown attributes.
   */
  static RecorderAndCollector OTEL_EXPLICIT_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.REUSABLE_DATA) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value,
              Attributes.of(
                  AttributesHolder.ATTRIBUTE_KEY, attributeHolder.labelValues[attributesIndex]),
              Context.root());
        }
      };

  /**
   * Otel recording to histogram with default explicit bucket boundaries, {@link
   * MemoryMode#IMMUTABLE_DATA}, and unknown attributes.
   */
  static RecorderAndCollector OTEL_EXPLICIT_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.IMMUTABLE_DATA) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value,
              Attributes.of(
                  AttributesHolder.ATTRIBUTE_KEY, attributeHolder.labelValues[attributesIndex]),
              Context.root());
        }
      };

  /**
   * Otel recording to histogram with base2 exponential aggregation, {@link
   * MemoryMode#REUSABLE_DATA}, and unknown attributes.
   */
  static RecorderAndCollector OTEL_EXPONENTIAL_HISTOGRAM_REUSABLE_DATA_UNKNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(
          MemoryMode.REUSABLE_DATA, Aggregation.base2ExponentialBucketHistogram()) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value,
              Attributes.of(
                  AttributesHolder.ATTRIBUTE_KEY, attributeHolder.labelValues[attributesIndex]),
              Context.root());
        }
      };

  /**
   * Otel recording to histogram with base2 exponential aggregation, {@link
   * MemoryMode#IMMUTABLE_DATA}, and unknown attributes.
   */
  static RecorderAndCollector OTEL_EXPONENTIAL_HISTOGRAM_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(
          MemoryMode.IMMUTABLE_DATA, Aggregation.base2ExponentialBucketHistogram()) {
        private DoubleHistogram doubleHistogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleHistogram = sdkMeterProvider.get("meter").histogramBuilder("histogram").build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleHistogram.record(
              value,
              Attributes.of(
                  AttributesHolder.ATTRIBUTE_KEY, attributeHolder.labelValues[attributesIndex]),
              Context.root());
        }
      };

  /** Otel recording to counter, {@link MemoryMode#REUSABLE_DATA}, and unknown attributes. */
  static RecorderAndCollector OTEL_COUNTER_REUSABLE_DATA_UNKNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.REUSABLE_DATA) {
        private DoubleCounter doubleCounter;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleCounter =
              sdkMeterProvider.get("meter").counterBuilder("counter").ofDoubles().build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleCounter.add(
              value,
              Attributes.of(
                  AttributesHolder.ATTRIBUTE_KEY, attributeHolder.labelValues[attributesIndex]),
              Context.root());
        }
      };

  /** Otel recording to counter, {@link MemoryMode#REUSABLE_DATA}, and unknown attributes. */
  static RecorderAndCollector OTEL_COUNTER_IMMUTABLE_DATA_UNKNOWN_ATTRIBUTES =
      new AbstractOtelRecorderAndCollector(MemoryMode.IMMUTABLE_DATA) {
        private DoubleCounter doubleCounter;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          doubleCounter =
              sdkMeterProvider.get("meter").counterBuilder("counter").ofDoubles().build();
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          doubleCounter.add(
              value,
              Attributes.of(
                  AttributesHolder.ATTRIBUTE_KEY, attributeHolder.labelValues[attributesIndex]),
              Context.root());
        }
      };

  /**
   * Micrometer recording to summary distribution (i.e. otel histogram) with bucket boundaries
   * reflecting otel default explicit bucket boundaries, assuming tags ARE NOT known ahead of
   * time
   * (i.e. typical http.server.request.duration). See {@link
   * {@link AbstractMicrometerRecorderAndCollector#setup(AttributesHolder, OtlpEndpoint)} for
   * configuration details.
   */
  static RecorderAndCollector MICROMETER_EXPLICIT_HISTOGRAM_UNKNOWN_TAGS =
      new AbstractMicrometerRecorderAndCollector() {
        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          registry.summary("histogram", attributeHolder.tagsList[attributesIndex]).record(value);
        }
      };

  /**
   * Micrometer recording to summary distribution (i.e. otel histogram) with bucket boundaries
   * reflecting otel default explicit bucket boundaries, assuming tags ARE known ahead of time. See
   * {@link AbstractMicrometerRecorderAndCollector#setup(AttributesHolder, OtlpEndpoint)} for
   * configuration details.
   */
  static RecorderAndCollector MICROMETER_EXPLICIT_HISTOGRAM_KNOWN_TAGS =
      new AbstractMicrometerRecorderAndCollector() {
        private DistributionSummary[] summaries;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          summaries = new DistributionSummary[attributeHolder.tagsList.length];
          for (int i = 0; i < attributeHolder.tagsList.length; i++) {
            summaries[i] = registry.summary("histogram", attributeHolder.tagsList[i]);
          }
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          summaries[attributesIndex].record(value);
        }
      };

  /** Micrometer recording to counter, assuming tags ARE NOT known ahead of time. */
  static RecorderAndCollector MICROMETER_COUNTER_UNKNOWN_TAGS =
      new AbstractMicrometerRecorderAndCollector() {
        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          registry.counter("counter", attributeHolder.tagsList[attributesIndex]).increment(value);
        }
      };

  /** Micrometer recording to counter, assuming tags ARE known ahead of time. */
  static RecorderAndCollector MICROMETER_COUNTER_KNOWN_TAGS =
      new AbstractMicrometerRecorderAndCollector() {
        private io.micrometer.core.instrument.Counter[] counters;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          counters = new io.micrometer.core.instrument.Counter[attributeHolder.tagsList.length];
          for (int i = 0; i < attributeHolder.tagsList.length; i++) {
            counters[i] = registry.counter("counter", attributeHolder.tagsList[i]);
          }
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          counters[attributesIndex].increment(value);
        }
      };

  /**
   * Prometheus recording to histogram with explicit bucket boundaries aligned to Otel defaults with
   * unknown labels.
   */
  static RecorderAndCollector PROMETHEUS_EXPLICIT_HISTOGRAM_UNKNOWN_LABELS =
      new AbstractPrometheusRecorderAndCollector() {
        private Histogram histogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          histogram =
              Histogram.builder()
                  .name("histogram")
                  .withoutExemplars()
                  .classicUpperBounds(
                      0d, 5d, 10d, 25d, 50d, 75d, 100d, 250d, 500d, 750d, 1_000d, 2_500d, 5_000d,
                      7_500d, 10_000d)
                  .labelNames("key")
                  .register(prometheusRegistry);
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          histogram.labelValues(attributeHolder.labelValues[attributesIndex]).observe(value);
        }
      };

  /** Prometheus recording to histogram with exponential histogram with unknown labels. */
  static RecorderAndCollector PROMETHEUS_EXPONENTIAL_HISTOGRAM_UNKNOWN_LABELS =
      new AbstractPrometheusRecorderAndCollector() {
        private Histogram histogram;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          histogram =
              Histogram.builder()
                  .name("histogram")
                  .withoutExemplars()
                  .nativeOnly()
                  .labelNames("key")
                  .register(prometheusRegistry);
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          histogram.labelValues(attributeHolder.labelValues[attributesIndex]).observe(value);
        }
      };

  /** Prometheus recording to counter with unknown labels. */
  static RecorderAndCollector PROMETHEUS_COUNTER_UNKNOWN_LABELS =
      new AbstractPrometheusRecorderAndCollector() {
        private Counter counter;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          counter =
              Counter.builder()
                  .name("counter")
                  .withoutExemplars()
                  .labelNames("key")
                  .register(prometheusRegistry);
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          counter.labelValues(attributeHolder.labelValues[attributesIndex]).inc(value);
        }
      };

  /**
   * Prometheus recording to histogram with explicit bucket boundaries aligned to Otel defaults with
   * known labels.
   */
  static RecorderAndCollector PROMETHEUS_EXPLICIT_HISTOGRAM_KNOWN_LABELS =
      new AbstractPrometheusRecorderAndCollector() {
        private DistributionDataPoint[] histogramPoints;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          Histogram histogram =
              Histogram.builder()
                  .name("histogram")
                  .withoutExemplars()
                  .classicUpperBounds(
                      0d, 5d, 10d, 25d, 50d, 75d, 100d, 250d, 500d, 750d, 1_000d, 2_500d, 5_000d,
                      7_500d, 10_000d)
                  .labelNames("key")
                  .register(prometheusRegistry);
          histogramPoints = new DistributionDataPoint[attributeHolder.labelValues.length];
          for (int i = 0; i < attributeHolder.labelValues.length; i++) {
            histogramPoints[i] = histogram.labelValues(attributeHolder.labelValues[i]);
          }
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          histogramPoints[attributesIndex].observe(value);
        }
      };

  /** Prometheus recording to histogram with exponential histogram with known labels. */
  static RecorderAndCollector PROMETHEUS_EXPONENTIAL_HISTOGRAM_KNOWN_LABELS =
      new AbstractPrometheusRecorderAndCollector() {
        private DistributionDataPoint[] histogramPoints;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          Histogram histogram =
              Histogram.builder()
                  .name("histogram")
                  .withoutExemplars()
                  .nativeOnly()
                  .labelNames("key")
                  .register(prometheusRegistry);
          histogramPoints = new DistributionDataPoint[attributeHolder.labelValues.length];
          for (int i = 0; i < attributeHolder.labelValues.length; i++) {
            histogramPoints[i] = histogram.labelValues(attributeHolder.labelValues[i]);
          }
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          histogramPoints[attributesIndex].observe(value);
        }
      };

  /** Prometheus recording to counter with known labels. */
  static RecorderAndCollector PROMETHEUS_COUNTER_KNOWN_LABELS =
      new AbstractPrometheusRecorderAndCollector() {
        private CounterDataPoint[] counterPoints;

        @Override
        public void setup(AttributesHolder attributeHolder, OtlpEndpoint otlpEndpoint) {
          super.setup(attributeHolder, otlpEndpoint);
          Counter counter =
              Counter.builder()
                  .name("counter")
                  .withoutExemplars()
                  .labelNames("key")
                  .register(prometheusRegistry);
          counterPoints = new CounterDataPoint[attributeHolder.labelValues.length];
          for (int i = 0; i < attributeHolder.labelValues.length; i++) {
            counterPoints[i] = counter.labelValues(attributeHolder.labelValues[i]);
          }
        }

        @Override
        public void record(AttributesHolder attributeHolder, double value, int attributesIndex) {
          counterPoints[attributesIndex].inc(value);
        }
      };
}
