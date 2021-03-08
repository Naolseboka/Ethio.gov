package et.gov.ethgov.gateway.domain.ratelimit;

import java.time.Instant;

public interface RateLimitCounterStore {
    /** Atomically increments and returns the new hit count for the window. */
    long incrementAndGet(RateLimitPolicyId policyId, Instant windowStart);
}
