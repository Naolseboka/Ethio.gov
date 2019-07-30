package et.gov.ethgov.citizen.domain.citizen;

import java.util.Objects;
import java.util.UUID;

public final class CitizenId {

    private final String value;

    private CitizenId(String value) {
        this.value = value;
    }

    public static CitizenId generate() {
        return new CitizenId("cit_" + UUID.randomUUID());
    }

    public static CitizenId of(String raw) {
        Objects.requireNonNull(raw, "citizenId");
        String normalized = raw.trim();
        if (!normalized.startsWith("cit_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid CitizenId: " + raw);
        }
        return new CitizenId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CitizenId that)) {
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
