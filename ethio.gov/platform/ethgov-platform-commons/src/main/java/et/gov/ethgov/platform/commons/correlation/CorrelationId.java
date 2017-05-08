package et.gov.ethgov.platform.commons.correlation;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Correlation identifier propagated across HTTP, Kafka, and logs.
 */
public final class CorrelationId {

    public static final String HEADER_NAME = "X-Correlation-Id";
    private static final Pattern VALUE_PATTERN =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._-]{7,127}$");

    private static final ThreadLocal<CorrelationId> CURRENT = new ThreadLocal<>();

    private final String value;

    private CorrelationId(String value) {
        this.value = value;
    }

    public static CorrelationId generate() {
        return new CorrelationId(UUID.randomUUID().toString());
    }

    public static CorrelationId of(String raw) {
        Objects.requireNonNull(raw, "correlationId");
        String normalized = raw.trim();
        if (!VALUE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid correlation id: " + raw);
        }
        return new CorrelationId(normalized);
    }

    public static void set(CorrelationId correlationId) {
        CURRENT.set(Objects.requireNonNull(correlationId, "correlationId"));
    }

    public static Optional<CorrelationId> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static CorrelationId requireOrGenerate() {
        CorrelationId existing = CURRENT.get();
        if (existing != null) {
            return existing;
        }
        CorrelationId generated = generate();
        set(generated);
        return generated;
    }

    public static void clear() {
        CURRENT.remove();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CorrelationId that)) {
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
