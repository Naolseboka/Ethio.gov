# ADR 0011: Document Platform bounded context

## Status

Accepted — Phase 5

## Context

Licensing, cases, land, and civil services need governed document storage with tenant isolation, classification, content integrity (SHA-256), and lifecycle (draft → active → archived/deleted). Binary objects must not live in PostgreSQL; metadata and auditability must.

## Decision

Implement `services/ethgov-document` as an independently deployable Document Platform:

- Aggregate `Document` owns metadata, classification, checksum, owner reference, and status
- Binary content stored in S3-compatible object storage (MinIO locally) behind `ObjectStoragePort`
- Upload flow: initiate (presigned PUT) → client upload → complete (HEAD verify + activate)
- Download via short-lived presigned GET
- Database `ethgov_document`; outbox topic `ethgov.document.events`; port `8085`
- Scopes: `document.read`, `document.write`

Allowed MIME types are allow-listed. Max object size: 50 MiB in Phase 5.

## Consequences

- Downstream services reference `doc_*` identifiers and never store raw blobs in their schemas
- Virus scanning / AV pipeline and multi-version supersede are deferred
- Encryption at rest relies on storage-layer configuration (MinIO/S3)
