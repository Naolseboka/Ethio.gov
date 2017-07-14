# ADR 0001: Multi-module Gradle monorepo

## Status

Accepted — Phase 0

## Context

EthGov will grow to 55–70 independently deployable services. We need shared libraries, consistent quality gates, and atomic cross-cutting refactors without publishing dozens of versioned JARs on day one.

## Decision

Use a single Git monorepo with Gradle Kotlin DSL and an included `build-logic` project for convention plugins.

Phase 0 contains only platform libraries. Deployable services are added one bounded context at a time starting in Phase 1.

## Consequences

- Shared primitives evolve with the codebase under one review process.
- CI can enforce ArchUnit/SpotBugs/Jacoco consistently.
- Repository size and clone time will grow; we accept this in exchange for coherence.
