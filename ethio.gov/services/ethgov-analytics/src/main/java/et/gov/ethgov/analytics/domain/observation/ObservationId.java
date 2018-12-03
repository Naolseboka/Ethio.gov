package et.gov.ethgov.analytics.domain.observation;

import java.util.Objects;
import java.util.UUID;

public final class ObservationId {
    private final String value;

    private ObservationId(String value) {
        this.value = value;
    }

    public static ObservationId generate() {
        return new ObservationId("mob_" + UUID.randomUUID());
    }

    public static ObservationId of(String raw) {
        Objects.requireNonNull(raw, "observationId");
        String n = raw.trim();
        if (!n.startsWith("mob_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid ObservationId: " + raw);
        }
        return new ObservationId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ObservationId that && value.equals(that.value));
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
