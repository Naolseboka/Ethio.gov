# ADR 0004: CloudEvents and transactional outbox

## Status

Accepted — Phase 0

## Context

EthGov is event-driven. Publishing Kafka messages inside a DB transaction is not atomic; dual-write failures cause lost or duplicate business signals.

## Decision

- Domain integration events are wrapped as CloudEvents 1.0.
- Required extensions: `tenantid`, `correlationid`, `aggregatetype`, `aggregateid`.
- Producers write to a transactional outbox table in the same DB transaction as state changes.
- A relay publishes pending outbox rows to Kafka.
- Consumers use an inbox table keyed by `(eventId, consumerName)` for idempotent handling.

## Consequences

- Platform libraries provide outbox/inbox primitives in Phase 0.
- First concrete relay implementation ships with the first event-producing service (Phase 1+).
