package et.gov.ethgov.gateway.domain.resilience;

import java.time.Instant;

public record ResiliencePolicyActivated(
        ResiliencePolicyId resiliencePolicyId,
        Instant occurredAt) implements ResiliencePolicyDomainEvent {}
