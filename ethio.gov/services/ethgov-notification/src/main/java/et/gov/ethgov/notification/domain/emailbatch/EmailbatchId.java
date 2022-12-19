package et.gov.ethgov.notification.domain.emailbatch;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Emailbatch. */
public final class EmailbatchId {
    private final UUID value;

    private EmailbatchId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EmailbatchId newId() {
        return new EmailbatchId(UUID.randomUUID());
    }

    public static EmailbatchId of(UUID value) {
        return new EmailbatchId(value);
    }

    public static EmailbatchId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EmailbatchId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailbatchId other)) {
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
