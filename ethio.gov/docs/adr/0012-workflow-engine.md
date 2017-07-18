# ADR 0012: Workflow Engine bounded context

## Status

Accepted — Phase 6

## Context

Licensing, permits, and case management need a governed multi-step process runtime: publishable definitions, process instances keyed by business keys, and claimable user tasks. Embedding a full BPMN engine (Camunda/Flowable) in Phase 6 would couple EthGov to an opaque runtime before Case Management exists. A purpose-built DDD engine keeps invariants explicit and reviewable.

## Decision

Implement `services/ethgov-workflow` as an independently deployable Workflow Engine:

- Aggregate `WorkflowDefinition` — tenant-scoped key, ordered `USER_TASK` steps, DRAFT → PUBLISHED → RETIRED
- Aggregate `ProcessInstance` — started from published definition; advances on task completion; cancelable
- Aggregate `WorkflowTask` — OPEN → CLAIMED → COMPLETED; claimer-only completion
- Database `ethgov_workflow`; outbox topic `ethgov.workflow.events`; port `8086`
- Scopes: `workflow.read`, `workflow.write`

Exclusive gateways, timers, and service tasks are deferred until Case Management / licensing phases require them.

## Consequences

- Downstream domains reference `wpi_*` / `wtk_*` / definition keys
- Step graphs are linear user-task sequences in Phase 6
- BPMN import remains a future option behind the same aggregates
