plugins {
    id("ethgov.java-library")
}

dependencies {
    api(project(":platform:ethgov-platform-commons"))
    api("io.opentelemetry:opentelemetry-api")
    api("io.micrometer:micrometer-core")
    api("org.slf4j:slf4j-api")

    testRuntimeOnly("ch.qos.logback:logback-classic")
}
