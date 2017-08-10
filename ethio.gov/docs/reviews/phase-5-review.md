# Phase 5 Engineering Review — Document Platform

**Project:** EthGov  
**Phase:** 5 — Document Platform  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 6)  
**Prior phases:** Phase 0–4 APPROVED

## Independent review method

This review evaluates Phase 5 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers (PostGIS + MinIO) API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-document`

| Capability | Implementation |
|---|---|
| Document aggregate | Metadata, classification, MIME allow-list, SHA-256, owner ref, lifecycle |
| Object storage | S3-compatible port; MinIO adapter with presigned PUT/GET |
| Upload flow | Initiate → client PUT → complete (HEAD verify) → ACTIVE |
| Download | Short-lived presigned GET for ACTIVE/ARCHIVED |
| AuthZ | JWT; `document.read` / `document.write` |
| Events | Outbox → `ethgov.document.events` |
| Schema | Flyway V1–V2; DB `ethgov_document`; Compose init + MinIO |
| Tests | Domain + ArchUnit + PostGIS/MinIO integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; storage behind port; ArchUnit green |
| DDD | 9.3 | Clear draft/active/archive/delete invariants; integrity hash |
| Security | 9.3 | Scoped JWT; allow-listed MIME; path-safe file names; no public blob API |
| Performance | 9.2 | Blobs off DB; unique storage key; 50 MiB cap |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8085; MinIO in Compose; bucket auto-create |
| Developer Experience | 9.3 | Presigned upload/download matches real gov platforms |
| Testing | 9.3 | Real MinIO + PostGIS end-to-end upload/download |
| Documentation | 9.3 | ADR 0011 + this review |
| Coverage (service line) | **77.8%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage/AV remediation before Phase 6 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Document service Jacoco line coverage 77.8% < 90%.** Archive/delete/metadata paths need additional tests before Phase 6 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Virus/malware scanning pipeline deferred (ADR 0011).
4. Client-supplied SHA-256 is trusted after storage HEAD size/type checks; server-side hash of object bytes is a future hardening step.

### Low
1. Multi-version supersede not implemented (single version `v1` per document).
2. MinIO Compose healthcheck omitted intentionally (image lacks ready `mc` alias).

## Cross-phase consistency

- Uses platform commons/web/persistence/messaging
- Does not embed case/workflow/licensing logic
- Does not scaffold Workflow Engine / Phase 6+ services

## Approval gate

**APPROVED** — Phase 6 (Workflow Engine) started.
