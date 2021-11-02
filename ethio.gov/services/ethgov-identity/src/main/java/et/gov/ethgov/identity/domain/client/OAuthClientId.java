package et.gov.ethgov.identity.domain.client;

import java.util.Objects;
import java.util.UUID;

public final class OAuthClientId {

    private final String value;

    private OAuthClientId(String value) {
        this.value = value;
    }

    public static OAuthClientId generate() {
        return new OAuthClientId("cli_" + UUID.randomUUID());
    }

    public static OAuthClientId of(String raw) {
        Objects.requireNonNull(raw, "oauthClientId");
        String normalized = raw.trim();
        if (!normalized.startsWith("cli_")) {
            throw new IllegalArgumentException("Invalid OAuthClientId");
        }
        return new OAuthClientId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OAuthClientId that)) {
            return false;
        }
        return value.equals(that.value);
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
