package jackberg.metric.benchmark;

class OtlpEndpoint {

  final String otlpGrpcEndpoint;
  final String otlpHttpEndpoint;

  OtlpEndpoint(String otlpGrpcEndpoint, String otlpHttpEndpoint) {
    this.otlpGrpcEndpoint = otlpGrpcEndpoint;
    this.otlpHttpEndpoint = otlpHttpEndpoint;
  }
}
