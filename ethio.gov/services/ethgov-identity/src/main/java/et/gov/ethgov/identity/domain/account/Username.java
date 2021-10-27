package et.gov.ethgov.identity.domain.account;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Unique login name for an identity account.
 * Accepts email or Ethiopian phone national/E.164 forms normalized to a canonical lowercase key.
 */
public final class Username {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String raw) {
        Objects.requireNonNull(raw, "username");
        String trimmed = raw.trim();
        if (trimmed.length() < 3 || trimmed.length() > 128) {
            throw new IllegalArgumentException("username length must be between 3 and 128");
        }
        if (EMAIL.matcher(trimmed).matches()) {
            return new Username(trimmed.toLowerCase(Locale.ROOT));
        }
        // Allow opaque usernames for service accounts: svc.<tenant>.<name>
        if (trimmed.startsWith("svc.")) {
            String normalized = trimmed.toLowerCase(Locale.ROOT);
            if (!normalized.matches("^svc\\.[a-z0-9._-]{3,120}$")) {
                throw new IllegalArgumentException("Invalid service account username");
            }
            return new Username(normalized);
        }
        throw new IllegalArgumentException("username must be an email or svc.* service account id");
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Username that)) {
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
