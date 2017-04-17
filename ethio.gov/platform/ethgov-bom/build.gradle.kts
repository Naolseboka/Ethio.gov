plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2024.0.0"))
    api(platform("org.springframework.modulith:spring-modulith-bom:1.3.1"))
    api(platform("io.opentelemetry:opentelemetry-bom:1.45.0"))
    api(platform("org.testcontainers:testcontainers-bom:1.21.3"))

    constraints {
        api("io.cloudevents:cloudevents-core:4.0.1")
        api("io.cloudevents:cloudevents-json-jackson:4.0.1")
        api("io.cloudevents:cloudevents-kafka:4.0.1")
        api("org.zalando:problem-spring-web-starter:0.29.1")
        api("com.tngtech.archunit:archunit-junit5:1.3.0")
        api("com.google.guava:guava:33.4.0-jre")
        api("org.openapitools:jackson-databind-nullable:0.2.6")
        api("org.testcontainers:testcontainers:1.21.3")
        api("org.testcontainers:junit-jupiter:1.21.3")
        api("org.testcontainers:postgresql:1.21.3")
        api("org.testcontainers:kafka:1.21.3")
        api("org.testcontainers:jdbc:1.21.3")
        api("software.amazon.awssdk:s3:2.29.51")
        api("software.amazon.awssdk:url-connection-client:2.29.51")
    }
}
