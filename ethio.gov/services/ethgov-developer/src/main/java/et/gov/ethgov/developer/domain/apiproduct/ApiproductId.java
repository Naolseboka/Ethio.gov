package et.gov.ethgov.developer.domain.apiproduct;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Apiproduct. */
public final class ApiproductId {
    private final UUID value;

    private ApiproductId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ApiproductId newId() {
        return new ApiproductId(UUID.randomUUID());
    }

    public static ApiproductId of(UUID value) {
        return new ApiproductId(value);
    }

    public static ApiproductId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ApiproductId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiproductId other)) {
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
