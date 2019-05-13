package et.gov.ethgov.casemgmt.domain.casefile;

import java.util.Objects;
import java.util.Optional;

/** Subject of a case — citizen and/or organization reference. */
public final class CaseParty {

    private final String citizenId;
    private final String organizationId;

    private CaseParty(String citizenId, String organizationId) {
        this.citizenId = citizenId;
        this.organizationId = organizationId;
    }

    public static CaseParty of(String citizenId, String organizationId) {
        String cit = normalizeOptionalId(citizenId, "cit_", "citizenId");
        String org = normalizeOptionalId(organizationId, "org_", "organizationId");
        if (cit == null && org == null) {
            throw new IllegalArgumentException("case must reference a citizenId and/or organizationId");
        }
        return new CaseParty(cit, org);
    }

    private static String normalizeOptionalId(String raw, String prefix, String field) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim();
        if (!normalized.startsWith(prefix) || normalized.length() < 40) {
            throw new IllegalArgumentException(field + " must be a valid " + prefix + " identifier");
        }
        return normalized;
    }

    public Optional<String> citizenId() {
        return Optional.ofNullable(citizenId);
    }

    public Optional<String> organizationId() {
        return Optional.ofNullable(organizationId);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CaseParty that)) {
            return false;
        }
        return Objects.equals(citizenId, that.citizenId)
                && Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(citizenId, organizationId);
    }
}
