package jackberg.metric.benchmark;

import java.util.Random;

public class BenchmarkUtil {

  static final int cardinality = 100;
  private static final int measurementsPerSeries = 1_000;
  private static final Random random = new Random();

  static void record(RecorderAndCollector recorderAndCollector, AttributesHolder attributesHolder) {
    for (int j = 0; j < measurementsPerSeries; j++) {
      for (int i = 0; i < cardinality; i++) {
        double value = random.nextInt(100);
        recorderAndCollector.record(attributesHolder, value, i);
      }
    }
  }
}
