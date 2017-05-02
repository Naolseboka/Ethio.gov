rootProject.name = "ethgov"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

include(
    "platform:ethgov-bom",
    "platform:ethgov-platform-commons",
    "platform:ethgov-platform-web",
    "platform:ethgov-platform-persistence",
    "platform:ethgov-platform-messaging",
    "platform:ethgov-platform-security",
    "platform:ethgov-platform-observability",
    "platform:ethgov-platform-testing",
    "services:ethgov-identity",
    "services:ethgov-citizen",
    "services:ethgov-organization",
    "services:ethgov-directory",
    "services:ethgov-document",
    "services:ethgov-workflow",
    "services:ethgov-case",
    "services:ethgov-land",
    "services:ethgov-licensing",
    "services:ethgov-permits",
    "services:ethgov-revenue",
    "services:ethgov-payments",
    "services:ethgov-notification",
    "services:ethgov-gis",
    "services:ethgov-reporting",
    "services:ethgov-search",
    "services:ethgov-opendata",
    "services:ethgov-analytics",
    "services:ethgov-developer",
    "services:ethgov-gateway",
)

project(":platform:ethgov-bom").projectDir = file("platform/ethgov-bom")
project(":platform:ethgov-platform-commons").projectDir = file("platform/ethgov-platform-commons")
project(":platform:ethgov-platform-web").projectDir = file("platform/ethgov-platform-web")
project(":platform:ethgov-platform-persistence").projectDir = file("platform/ethgov-platform-persistence")
project(":platform:ethgov-platform-messaging").projectDir = file("platform/ethgov-platform-messaging")
project(":platform:ethgov-platform-security").projectDir = file("platform/ethgov-platform-security")
project(":platform:ethgov-platform-observability").projectDir = file("platform/ethgov-platform-observability")
project(":platform:ethgov-platform-testing").projectDir = file("platform/ethgov-platform-testing")
project(":services:ethgov-identity").projectDir = file("services/ethgov-identity")
project(":services:ethgov-citizen").projectDir = file("services/ethgov-citizen")
project(":services:ethgov-organization").projectDir = file("services/ethgov-organization")
project(":services:ethgov-directory").projectDir = file("services/ethgov-directory")
project(":services:ethgov-document").projectDir = file("services/ethgov-document")
project(":services:ethgov-workflow").projectDir = file("services/ethgov-workflow")
project(":services:ethgov-case").projectDir = file("services/ethgov-case")
project(":services:ethgov-land").projectDir = file("services/ethgov-land")
project(":services:ethgov-licensing").projectDir = file("services/ethgov-licensing")
project(":services:ethgov-permits").projectDir = file("services/ethgov-permits")
project(":services:ethgov-revenue").projectDir = file("services/ethgov-revenue")
project(":services:ethgov-payments").projectDir = file("services/ethgov-payments")
project(":services:ethgov-notification").projectDir = file("services/ethgov-notification")
project(":services:ethgov-gis").projectDir = file("services/ethgov-gis")
project(":services:ethgov-reporting").projectDir = file("services/ethgov-reporting")
project(":services:ethgov-search").projectDir = file("services/ethgov-search")
project(":services:ethgov-opendata").projectDir = file("services/ethgov-opendata")
project(":services:ethgov-analytics").projectDir = file("services/ethgov-analytics")
project(":services:ethgov-developer").projectDir = file("services/ethgov-developer")
project(":services:ethgov-gateway").projectDir = file("services/ethgov-gateway")
