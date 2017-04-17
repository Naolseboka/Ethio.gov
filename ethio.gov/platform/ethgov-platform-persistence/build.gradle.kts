plugins {
    id("ethgov.java-library")
}

dependencies {
    api(project(":platform:ethgov-platform-commons"))
    api("org.springframework.data:spring-data-commons")
    api("org.springframework:spring-tx")
    api("jakarta.persistence:jakarta.persistence-api")
    api("jakarta.validation:jakarta.validation-api")
}
