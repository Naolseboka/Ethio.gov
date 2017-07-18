# ADR 0010: Government Directory bounded context

## Status

Accepted — Phase 4

## Context

Municipal and federal services need an authoritative hierarchy of government organizational units (ministries, bureaus, offices) and a roster of public employees assigned to those units. Organization Platform (Phase 3) registers legal entities (TIN, companies, NGOs). Identity authenticates accounts. Citizen Registry stores natural persons. None owns the internal government org chart or employee directory.

## Decision

Implement `services/ethgov-directory` as an independently deployable Government Directory:

- Aggregate `OrganizationalUnit` — tenant-scoped hierarchy with unique unit codes, cycle-safe moves, activate/deactivate guards
- Aggregate `PublicEmployee` — tenant-scoped employee number, required `cit_*` link, optional `usr_*` identity link, assignment to an active OU, separation lifecycle
- Database `ethgov_directory` (database-per-service)
- JWT resource server; scopes `directory.read`, `directory.write`
- Outbox events on `ethgov.directory.events`
- Port `8084`

Fine-grained permission/role catalogs remain with Identity ABAC (and future authz refinements). This phase does not implement a full HRIS/payroll system.

## Consequences

- Downstream case/workflow services can route work by `ou_*` and `emp_*` identifiers
- Deactivating a unit requires no active children and no active/on-leave employees
- Legal org (`org_*`) may be optionally linked on a unit but is not required
