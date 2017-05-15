package et.gov.ethgov.platform.commons.tenant;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Stable identifier for a government tenant (municipality, regional government, or federal agency).
 * Format: {@code tnt_<uuid>} to keep opaque IDs distinguishable in logs and traces.
 */
public final class TenantId {

    private static final Pattern VALUE_PATTERN =
            Pattern.compile("^tnt_[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    private final String value;

    private TenantId(String value) {
        this.value = value;
    }

    public static TenantId generate() {
        return new TenantId("tnt_" + UUID.randomUUID());
    }

    public static TenantId of(String raw) {
        Objects.requireNonNull(raw, "tenantId must not be null");
        String normalized = raw.trim().toLowerCase();
        if (!VALUE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid TenantId format: " + raw);
        }
        return new TenantId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TenantId that)) {
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
