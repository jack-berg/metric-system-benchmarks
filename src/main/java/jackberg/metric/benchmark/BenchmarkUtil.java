package jackberg.metric.benchmark;

import java.util.Random;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

public class BenchmarkUtil {

  static final int cardinality = 100;
  static final int COLLECTOR_OTLP_GRPC_PORT = 4317;
  static final int COLLECTOR_OTLP_HTTP_PORT = 4318;
  static final Integer COLLECTOR_HEALTH_CHECK_PORT = 13133;

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

  static GenericContainer<?> collectorContainer() {
    return new GenericContainer<>(
            DockerImageName.parse("ghcr.io/open-telemetry/opentelemetry-java/otel-collector"))
        .withImagePullPolicy(PullPolicy.alwaysPull())
        .withEnv("LOGGING_EXPORTER_VERBOSITY_LEVEL", "normal")
        .withClasspathResourceMapping("otel-config.yaml", "/otel-config.yaml", BindMode.READ_ONLY)
        .withCommand("--config", "/otel-config.yaml")
        .withLogConsumer(
            outputFrame ->
                System.out.println(
                    "Collector log: " + outputFrame.getUtf8StringWithoutLineEnding()))
        .withExposedPorts(
            COLLECTOR_OTLP_GRPC_PORT, COLLECTOR_OTLP_HTTP_PORT, COLLECTOR_HEALTH_CHECK_PORT)
        .waitingFor(Wait.forHttp("/").forPort(COLLECTOR_HEALTH_CHECK_PORT));
  }

  static OtlpEndpoint collectorEndpoint(GenericContainer<?> collectorContainer) {
    return new OtlpEndpoint(
        "http://"
            + collectorContainer.getHost()
            + ":"
            + collectorContainer.getMappedPort(BenchmarkUtil.COLLECTOR_OTLP_GRPC_PORT),
        "http://"
            + collectorContainer.getHost()
            + ":"
            + collectorContainer.getMappedPort(BenchmarkUtil.COLLECTOR_OTLP_HTTP_PORT));
  }
}
