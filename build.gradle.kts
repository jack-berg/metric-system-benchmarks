plugins {
    id("com.diffplug.spotless") version "6.25.0"
    id("java-library")
    id("me.champeau.jmh") version "0.7.2"
    id("io.morethan.jmhreport") version "0.9.0"
}

val jacksonVersion = "2.16.0"
val jmhVersion = "1.37"
val micrometerVersion = "1.12.4"
val otelVersion = "1.38.0"
val prometheusVersion = "1.2.1"
val testContainers = "1.19.8"

dependencies {
    // jmh
    jmh("org.openjdk.jmh:jmh-core:${jmhVersion}")
    jmh("org.openjdk.jmh:jmh-generator-bytecode:${jmhVersion}")

    // micrometer
    implementation("io.micrometer:micrometer-core:${micrometerVersion}")
    implementation("io.micrometer:micrometer-registry-otlp:${micrometerVersion}")

    // otel
    implementation("io.opentelemetry:opentelemetry-sdk:${otelVersion}")
    implementation("io.opentelemetry:opentelemetry-sdk-testing:${otelVersion}")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:${otelVersion}")

    // prometheus
    implementation("io.prometheus:prometheus-metrics-core:${prometheusVersion}")
    implementation("io.prometheus:prometheus-metrics-exporter-opentelemetry:${prometheusVersion}")

    // other test dependencies
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${jacksonVersion}")
    implementation("org.testcontainers:testcontainers:${testContainers}")

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jmh {
    failOnError.set(true)
    resultFormat.set("JSON")
    // Otherwise an error will happen:
    // Could not expand ZIP 'byte-buddy-agent-1.9.7.jar'.
    includeTests.set(false)
    profilers.add("gc")
    val jmhIncludeSingleClass: String? by project
    if (jmhIncludeSingleClass != null) {
        includes.add(jmhIncludeSingleClass as String)
    }
}

jmhReport {
    val buildDirectory = layout.buildDirectory.asFile.get()
    jmhResultPath = file("$buildDirectory/results/jmh/results.json").absolutePath
    jmhReportOutput = file("$buildDirectory/results/jmh").absolutePath
}

tasks {
    named("jmh") {
        finalizedBy(named("jmhReport"))

        outputs.cacheIf { false }
    }
}

spotless {
    java {
        targetExclude("**/generated/**")
        googleJavaFormat()
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
