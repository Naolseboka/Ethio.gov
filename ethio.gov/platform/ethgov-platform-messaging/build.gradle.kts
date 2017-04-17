plugins {
    id("ethgov.java-library")
}

dependencies {
    api(project(":platform:ethgov-platform-commons"))
    api(project(":platform:ethgov-platform-persistence"))
    api("io.cloudevents:cloudevents-core")
    api("io.cloudevents:cloudevents-json-jackson")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    api("org.springframework:spring-context")
    api("org.springframework:spring-tx")
}
