# ADR 0018: Payments bounded context

## Status

Accepted — Phase 12

## Context

Revenue bills (Phase 11) record what is owed. Payments capture collection attempts and outcomes against those bills. Gateway providers (Telebirr, bank rails, card) are external; EthGov owns the payment ledger and lifecycle.

## Decision

Implement `services/ethgov-payments` as an independently deployable Payments service:

- Aggregate `Payment` — tenant-scoped `PAY-YYYY-######` numbers, required `rev_*` bill link, ETB amount, method, party, optional idempotency key and provider reference
- Status machine: INITIATED → SUCCEEDED / FAILED / CANCELLED; SUCCEEDED → REFUNDED
- Database `ethgov_payments`; outbox topic `ethgov.payments.events`; port `8092`
- Scopes: `payments.read`, `payments.write`

No live PSP integration in this phase — provider references are opaque strings recorded on success. Revenue `markPaid` remains a separate command (event-driven settlement can wire later).

## Consequences

- Downstream services reference `pay_*` identifiers
- Partial refunds and multi-bill settlements deferred
- Notification of payment events deferred to Phase 13
