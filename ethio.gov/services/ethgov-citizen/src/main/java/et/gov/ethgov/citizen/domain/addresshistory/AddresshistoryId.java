package et.gov.ethgov.citizen.domain.addresshistory;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Addresshistory. */
public final class AddresshistoryId {
    private final UUID value;

    private AddresshistoryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AddresshistoryId newId() {
        return new AddresshistoryId(UUID.randomUUID());
    }

    public static AddresshistoryId of(UUID value) {
        return new AddresshistoryId(value);
    }

    public static AddresshistoryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AddresshistoryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddresshistoryId other)) {
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
