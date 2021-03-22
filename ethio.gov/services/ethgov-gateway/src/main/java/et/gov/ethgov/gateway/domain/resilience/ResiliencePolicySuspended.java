package et.gov.ethgov.gateway.domain.resilience;

import java.time.Instant;

public record ResiliencePolicySuspended(
        ResiliencePolicyId resiliencePolicyId,
        String reason, Instant occurredAt) implements ResiliencePolicyDomainEvent {}
