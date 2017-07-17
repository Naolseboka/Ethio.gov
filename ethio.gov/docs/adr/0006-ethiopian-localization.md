# ADR 0006: Ethiopian localization defaults

## Status

Accepted — Phase 0

## Context

EthGov serves Ethiopian public institutions and citizens. Defaults must match national operational reality.

## Decision

| Concern | Default |
|---|---|
| Timezone | `Africa/Addis_Ababa` |
| Currency | ETB only in Phase 0 money type |
| Languages | en, am, om, ti, sid, so |
| Calendars | Ethiopian + Gregorian conversion utilities |
| Addresses | Region → Zone → Woreda → Kebele hierarchy |
| Phone | E.164 `+251` normalization |

Payment rails (Telebirr, CBE Birr, Chapa) and Fayda identity integration are deferred to their owning phases—only extension points/contracts are reserved, not stubbed.

## Consequences

- Commons library owns conversion/validation primitives.
- Services must not invent alternate phone/money/address types.
