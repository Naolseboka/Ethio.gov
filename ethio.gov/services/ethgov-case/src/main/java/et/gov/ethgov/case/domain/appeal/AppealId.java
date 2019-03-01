package et.gov.ethgov.case.domain.appeal;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Appeal. */
public final class AppealId {
    private final UUID value;

    private AppealId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AppealId newId() {
        return new AppealId(UUID.randomUUID());
    }

    public static AppealId of(UUID value) {
        return new AppealId(value);
    }

    public static AppealId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AppealId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppealId other)) {
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
