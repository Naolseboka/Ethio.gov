package et.gov.ethgov.gateway.domain.ratelimit;

import java.time.Instant;

public record RateLimitPolicyRetired(
        RateLimitPolicyId rateLimitPolicyId,
        Instant occurredAt) implements RateLimitPolicyDomainEvent {}
