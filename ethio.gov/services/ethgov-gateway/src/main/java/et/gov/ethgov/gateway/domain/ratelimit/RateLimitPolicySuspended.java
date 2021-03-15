package et.gov.ethgov.gateway.domain.ratelimit;

import java.time.Instant;

public record RateLimitPolicySuspended(
        RateLimitPolicyId rateLimitPolicyId,
        String reason,
        Instant occurredAt) implements RateLimitPolicyDomainEvent {}
