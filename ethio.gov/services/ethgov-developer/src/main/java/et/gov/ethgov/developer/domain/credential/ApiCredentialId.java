package et.gov.ethgov.developer.domain.credential;

import java.util.Objects;
import java.util.UUID;

public final class ApiCredentialId {
    private final String value;

    private ApiCredentialId(String value) {
        this.value = value;
    }

    public static ApiCredentialId generate() {
        return new ApiCredentialId("ack_" + UUID.randomUUID());
    }

    public static ApiCredentialId of(String raw) {
        Objects.requireNonNull(raw, "apiCredentialId");
        String n = raw.trim();
        if (!n.startsWith("ack_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid ApiCredentialId: " + raw);
        }
        return new ApiCredentialId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ApiCredentialId that && value.equals(that.value));
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
