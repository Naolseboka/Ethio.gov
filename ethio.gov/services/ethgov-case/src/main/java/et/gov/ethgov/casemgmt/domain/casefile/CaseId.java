package et.gov.ethgov.casemgmt.domain.casefile;

import java.util.Objects;
import java.util.UUID;

public final class CaseId {

    private final String value;

    private CaseId(String value) {
        this.value = value;
    }

    public static CaseId generate() {
        return new CaseId("cas_" + UUID.randomUUID());
    }

    public static CaseId of(String raw) {
        Objects.requireNonNull(raw, "caseId");
        String normalized = raw.trim();
        if (!normalized.startsWith("cas_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid CaseId: " + raw);
        }
        return new CaseId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CaseId that)) {
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
