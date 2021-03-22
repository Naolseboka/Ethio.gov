package et.gov.ethgov.gateway.domain.resilience;

import java.time.Instant;

public record ResiliencePolicyRetired(
        ResiliencePolicyId resiliencePolicyId,
        Instant occurredAt) implements ResiliencePolicyDomainEvent {}
