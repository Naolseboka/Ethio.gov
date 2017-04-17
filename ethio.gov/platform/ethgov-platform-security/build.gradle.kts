plugins {
    id("ethgov.java-library")
}

dependencies {
    api(project(":platform:ethgov-platform-commons"))
    api("org.springframework.security:spring-security-core")
    api("org.springframework.security:spring-security-oauth2-jose")
    api("org.springframework:spring-context")
}
