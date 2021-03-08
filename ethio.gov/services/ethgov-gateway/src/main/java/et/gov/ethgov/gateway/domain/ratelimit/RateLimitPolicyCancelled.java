package et.gov.ethgov.gateway.domain.ratelimit;

import java.time.Instant;

public record RateLimitPolicyCancelled(
        RateLimitPolicyId rateLimitPolicyId,
        String reason,
        Instant occurredAt) implements RateLimitPolicyDomainEvent {}
