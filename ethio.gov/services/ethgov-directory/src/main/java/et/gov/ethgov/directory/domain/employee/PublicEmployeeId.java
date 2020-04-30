package et.gov.ethgov.directory.domain.employee;

import java.util.Objects;
import java.util.UUID;

public final class PublicEmployeeId {

    private final String value;

    private PublicEmployeeId(String value) {
        this.value = value;
    }

    public static PublicEmployeeId generate() {
        return new PublicEmployeeId("emp_" + UUID.randomUUID());
    }

    public static PublicEmployeeId of(String raw) {
        Objects.requireNonNull(raw, "publicEmployeeId");
        String normalized = raw.trim();
        if (!normalized.startsWith("emp_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid PublicEmployeeId: " + raw);
        }
        return new PublicEmployeeId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PublicEmployeeId that)) {
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
