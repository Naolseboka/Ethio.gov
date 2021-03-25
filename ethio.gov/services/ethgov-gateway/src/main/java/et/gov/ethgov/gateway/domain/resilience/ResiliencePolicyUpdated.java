package et.gov.ethgov.gateway.domain.resilience;

import java.time.Instant;

public record ResiliencePolicyUpdated(
        ResiliencePolicyId resiliencePolicyId,
        Instant occurredAt) implements ResiliencePolicyDomainEvent {}
