# ADR 0002: Hexagonal architecture and DDD

## Status

Accepted — Phase 0

## Context

Government domains (identity, land, licensing, payments) have long-lived invariants and regulatory constraints. Framework coupling in the domain model makes change expensive and testing brittle.

## Decision

Every deployable service uses hexagonal architecture:

- `domain` — aggregates, value objects, domain services, domain events (no Spring/JPA/Kafka)
- `application` — use cases / command & query handlers
- `adapter.in.*` — HTTP, messaging consumers
- `adapter.out.*` — persistence, brokers, external systems

CQRS is applied where read/write models diverge for justified performance or authorization reasons—not by default for every entity.

## Consequences

- ArchUnit rules in `ethgov-platform-testing` enforce dependency directions.
- Slightly more package structure than CRUD apps; lower long-term coupling.
