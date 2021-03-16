package et.gov.ethgov.gateway.domain.ratelimit;

import java.time.Instant;

public record RateLimitPolicyUpdated(
        RateLimitPolicyId rateLimitPolicyId,
        Instant occurredAt) implements RateLimitPolicyDomainEvent {}
