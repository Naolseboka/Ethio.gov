package et.gov.ethgov.gateway.domain.resilience;

import java.time.Instant;

public record ResiliencePolicyCancelled(
        ResiliencePolicyId resiliencePolicyId,
        String reason, Instant occurredAt) implements ResiliencePolicyDomainEvent {}
