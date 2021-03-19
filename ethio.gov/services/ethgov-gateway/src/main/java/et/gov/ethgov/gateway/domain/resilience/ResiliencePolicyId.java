package et.gov.ethgov.gateway.domain.resilience;

import java.util.Objects;
import java.util.UUID;

public final class ResiliencePolicyId {
    private final String value;

    private ResiliencePolicyId(String value) {
        this.value = value;
    }

    public static ResiliencePolicyId generate() {
        return new ResiliencePolicyId("rsp_" + UUID.randomUUID());
    }

    public static ResiliencePolicyId of(String raw) {
        Objects.requireNonNull(raw, "resiliencePolicyId");
        String n = raw.trim();
        if (!n.startsWith("rsp_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid ResiliencePolicyId: " + raw);
        }
        return new ResiliencePolicyId(n);
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ResiliencePolicyId that && value.equals(that.value));
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
