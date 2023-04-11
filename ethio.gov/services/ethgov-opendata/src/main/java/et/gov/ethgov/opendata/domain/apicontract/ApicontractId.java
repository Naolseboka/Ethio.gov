package et.gov.ethgov.opendata.domain.apicontract;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Apicontract. */
public final class ApicontractId {
    private final UUID value;

    private ApicontractId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ApicontractId newId() {
        return new ApicontractId(UUID.randomUUID());
    }

    public static ApicontractId of(UUID value) {
        return new ApicontractId(value);
    }

    public static ApicontractId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ApicontractId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApicontractId other)) {
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
