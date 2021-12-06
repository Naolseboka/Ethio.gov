package et.gov.ethgov.identity.domain.oauthclient;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Oauthclient. */
public final class OauthclientId {
    private final UUID value;

    private OauthclientId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OauthclientId newId() {
        return new OauthclientId(UUID.randomUUID());
    }

    public static OauthclientId of(UUID value) {
        return new OauthclientId(value);
    }

    public static OauthclientId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OauthclientId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OauthclientId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
