package et.gov.ethgov.gateway.domain.ratelimit;

import java.util.Objects;
import java.util.UUID;

public final class RateLimitPolicyId {
    private final String value;

    private RateLimitPolicyId(String value) {
        this.value = value;
    }

    public static RateLimitPolicyId generate() {
        return new RateLimitPolicyId("rlp_" + UUID.randomUUID());
    }

    public static RateLimitPolicyId of(String raw) {
        Objects.requireNonNull(raw, "rateLimitPolicyId");
        String n = raw.trim();
        if (!n.startsWith("rlp_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid RateLimitPolicyId: " + raw);
        }
        return new RateLimitPolicyId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof RateLimitPolicyId that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
