package et.gov.ethgov.identity.domain.account;

import java.util.Objects;
import java.util.UUID;

public final class UserAccountId {

    private final String value;

    private UserAccountId(String value) {
        this.value = value;
    }

    public static UserAccountId generate() {
        return new UserAccountId("usr_" + UUID.randomUUID());
    }

    public static UserAccountId of(String raw) {
        Objects.requireNonNull(raw, "userAccountId");
        String normalized = raw.trim();
        if (!normalized.startsWith("usr_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid UserAccountId: " + raw);
        }
        return new UserAccountId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserAccountId that)) {
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
