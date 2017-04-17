plugins {
    id("ethgov.java-library")
}

dependencies {
    api(project(":platform:ethgov-platform-commons"))
    api("com.tngtech.archunit:archunit-junit5")
    api("org.testcontainers:junit-jupiter")
    api("org.testcontainers:postgresql")
    api("org.testcontainers:kafka")
    api("org.junit.jupiter:junit-jupiter")
    api("org.assertj:assertj-core")
}
