# ADR 0017: Revenue Platform bounded context

## Status

Accepted — Phase 11

## Context

Municipal services assess fees and taxes (license fees, permit fees, property tax, fines) before money is collected. Payments (Phase 12) settle obligations; Revenue owns the bill/assessment ledger.

## Decision

Implement `services/ethgov-revenue` as an independently deployable Revenue Platform service:

- Aggregate `RevenueBill` — tenant-scoped `REV-YYYY-######` numbers, revenue type, ETB amount, party (`cit_*`/`org_*`), optional links to `lic_*` / `prm_*` / `par_*` / `cas_*`, period and due dates
- Status machine: DRAFT → ISSUED → OVERDUE/PAID; ISSUED/OVERDUE → WRITTEN_OFF; non-paid → CANCELLED
- `markPaid` accepts an opaque `paymentReference` (Phase 12 will supply `pay_*` IDs)
- Database `ethgov_revenue`; outbox topic `ethgov.revenue.events`; port `8091`
- Scopes: `revenue.read`, `revenue.write`

Payment rails, gateways, and receipts remain Phase 12.

## Consequences

- Licensing/permits/land emit or are linked to bills by ID
- Partial payments and multi-line invoices deferred
- Treasury/accounting consolidation deferred
