package et.gov.ethgov.notification.domain.template;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Template. */
public final class TemplateId {
    private final UUID value;

    private TemplateId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TemplateId newId() {
        return new TemplateId(UUID.randomUUID());
    }

    public static TemplateId of(UUID value) {
        return new TemplateId(value);
    }

    public static TemplateId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TemplateId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateId other)) {
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
