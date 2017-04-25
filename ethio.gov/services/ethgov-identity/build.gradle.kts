plugins {
    id("ethgov.spring-service")
}

// Docker Engine 29 requires Testcontainers/docker-java newer than Boot 3.4.2's default 1.20.4
extra["testcontainers.version"] = "1.21.3"

tasks.withType<Test>().configureEach {
    // docker-java defaults to API 1.32; Docker Engine 29 requires >= 1.44
    environment("DOCKER_API_VERSION", "1.44")
    systemProperty("api.version", "1.44")
}

dependencies {
    implementation(project(":platform:ethgov-platform-commons"))
    implementation(project(":platform:ethgov-platform-web"))
    implementation(project(":platform:ethgov-platform-persistence"))
    implementation(project(":platform:ethgov-platform-messaging"))
    implementation(project(":platform:ethgov-platform-security"))
    implementation(project(":platform:ethgov-platform-observability"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation(project(":platform:ethgov-platform-testing"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.tngtech.archunit:archunit-junit5")
}
