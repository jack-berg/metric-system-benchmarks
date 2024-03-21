pluginManagement {
    plugins {
        id("com.github.johnrengelman.shadow") version "8.1.1"
        id("com.gradle.enterprise") version "3.16.2"
        id("de.undercouch.download") version "5.6.0"
        id("org.jsonschema2pojo") version "1.2.1"
        id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
        id("org.graalvm.buildtools.native") version "0.10.1"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}