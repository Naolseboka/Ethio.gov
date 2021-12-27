package et.gov.ethgov.identity.domain.session;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Session. */
public final class SessionId {
    private final UUID value;

    private SessionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SessionId newId() {
        return new SessionId(UUID.randomUUID());
    }

    public static SessionId of(UUID value) {
        return new SessionId(value);
    }

    public static SessionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SessionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionId other)) {
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
