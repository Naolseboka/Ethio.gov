package et.gov.ethgov.gateway.domain.audit;

import java.util.Objects;
import java.util.UUID;

public final class AuditEntryId {
    private final String value;

    private AuditEntryId(String value) { this.value = value; }

    public static AuditEntryId generate() {
        return new AuditEntryId("aud_" + UUID.randomUUID());
    }

    public static AuditEntryId of(String raw) {
        Objects.requireNonNull(raw, "auditEntryId");
        String n = raw.trim();
        if (!n.startsWith("aud_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid AuditEntryId: " + raw);
        }
        return new AuditEntryId(n);
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof AuditEntryId that && value.equals(that.value));
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
