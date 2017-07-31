# Architecture overview — Phase 0

## Goals

Provide production-quality shared foundations so Phase 1 (Identity Platform) can start without inventing tenancy, errors, events, or localization.

## Module dependency graph

```mermaid
flowchart TD
  bom[ethgov-bom]
  commons[ethgov-platform-commons]
  web[ethgov-platform-web]
  persistence[ethgov-platform-persistence]
  messaging[ethgov-platform-messaging]
  security[ethgov-platform-security]
  observability[ethgov-platform-observability]
  testing[ethgov-platform-testing]

  commons --> bom
  web --> commons
  persistence --> commons
  messaging --> commons
  messaging --> persistence
  security --> commons
  observability --> commons
  testing --> commons
```

## Quality gates (Phase 0)

- Java 21 toolchain
- `-Werror` on compiler warnings
- JUnit 5 + AssertJ unit tests per module
- Jacoco reports generated on `check`
- SpotBugs plugin available via convention plugin
- ArchUnit rule library published for services
- Testcontainers image contracts fixed for PostGIS / Kafka / Redis

## Explicit non-goals

- No Identity service yet
- No REST business endpoints yet
- No 60-service scaffold
- No fake publishers or dummy repositories in production packages
