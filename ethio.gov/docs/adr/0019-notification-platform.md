# ADR 0019: Notification Platform bounded context

## Status

Accepted — Phase 13

## Context

Government services need to notify citizens and organizations over SMS, email, and push after licensing, payments, case updates, and similar events. Provider gateways are external; EthGov owns the outbound notification ledger, locale-aware content, and delivery state.

## Decision

Implement `services/ethgov-notification` as an independently deployable Notification Platform:

- Aggregate `OutboundNotification` — tenant-scoped `NTF-YYYY-######` numbers, channel (SMS/EMAIL/PUSH), locale (`am`/`en`/`om`/`ti`/`so`), recipient validated per channel (Ethiopian phone / email / device token), body, optional subject/template/source reference
- Status machine: PENDING → SENT → DELIVERED; PENDING/SENT → FAILED; PENDING → CANCELLED
- Database `ethgov_notification`; outbox topic `ethgov.notification.events`; port `8093`
- Scopes: `notification.read`, `notification.write`

No live SMS/email/push provider SDKs in this phase — provider references are recorded on `markSent`. Template content management remains a thin `templateCode` string for now.

## Consequences

- Upstream services schedule notifications by ID and channel
- Delivery workers / provider adapters are a later hardening concern
- Preference/opt-out management deferred
