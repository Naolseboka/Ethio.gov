package et.gov.ethgov.platform.commons.idempotency;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Client-supplied idempotency key for mutating HTTP operations.
 */
public final class IdempotencyKey {

    public static final String HEADER_NAME = "Idempotency-Key";
    private static final Pattern VALUE_PATTERN =
            Pattern.compile("^[A-Za-z0-9._:\\-]{8,128}$");

    private final String value;

    private IdempotencyKey(String value) {
        this.value = value;
    }

    public static IdempotencyKey of(String raw) {
        Objects.requireNonNull(raw, "idempotencyKey");
        String normalized = raw.trim();
        if (!VALUE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Idempotency-Key must be 8-128 chars of [A-Za-z0-9._:-]");
        }
        return new IdempotencyKey(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IdempotencyKey that)) {
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
