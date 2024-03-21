plugins {
    id("com.diffplug.spotless") version "6.25.0"
    id("java-library")
    id("me.champeau.jmh") version "0.7.2"
    id("io.morethan.jmhreport") version "0.9.0"
}

val jacksonVersion = "2.16.0"
val jmhVersion = "1.37"
val micrometerVersion = "1.12.4"
val otelVersion = "1.35.0"
val prometheusVersion = "1.0.0"

dependencies {
    // jmh
    jmh("org.openjdk.jmh:jmh-core:${jmhVersion}")
    jmh("org.openjdk.jmh:jmh-generator-bytecode:${jmhVersion}")

    // micrometer
    jmh("io.micrometer:micrometer-core:${micrometerVersion}")
    testImplementation("io.micrometer:micrometer-core:${micrometerVersion}")

    // otel
    jmh("io.opentelemetry:opentelemetry-sdk:${otelVersion}")
    jmh("io.opentelemetry:opentelemetry-sdk-testing:${otelVersion}")
    testImplementation("io.opentelemetry:opentelemetry-sdk:${otelVersion}")
    testImplementation("io.opentelemetry:opentelemetry-sdk-testing:${otelVersion}")

    // prometheus
    jmh("io.prometheus:prometheus-metrics-core:${prometheusVersion}")
    testImplementation("io.prometheus:prometheus-metrics-core:${prometheusVersion}")

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // other test dependencies
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${jacksonVersion}")
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
