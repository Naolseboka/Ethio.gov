package et.gov.ethgov.gateway.domain.ratelimit;

import java.time.Instant;

public sealed interface RateLimitPolicyDomainEvent
        permits RateLimitPolicyCreated,
                RateLimitPolicyUpdated,
                RateLimitPolicyActivated,
                RateLimitPolicySuspended,
                RateLimitPolicyRetired,
                RateLimitPolicyCancelled {
    RateLimitPolicyId rateLimitPolicyId();

    Instant occurredAt();
}
