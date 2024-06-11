package et.gov.ethgov.permits.domain.permit;

import java.util.Objects;
import java.util.Optional;

public final class PermitApplicant {
    private final String citizenId;
    private final String organizationId;

    private PermitApplicant(String citizenId, String organizationId) {
        this.citizenId = citizenId;
        this.organizationId = organizationId;
    }

    public static PermitApplicant of(String citizenId, String organizationId) {
        String cit = normalize(citizenId, "cit_", "citizenId");
        String org = normalize(organizationId, "org_", "organizationId");
        if (cit == null && org == null) {
            throw new IllegalArgumentException("permit must have citizenId and/or organizationId applicant");
        }
        return new PermitApplicant(cit, org);
    }

    private static String normalize(String raw, String prefix, String field) {
        if (raw == null || raw.isBlank()) return null;
        String n = raw.trim();
        if (!n.startsWith(prefix) || n.length() < 40) {
            throw new IllegalArgumentException(field + " must be a valid " + prefix + " identifier");
        }
        return n;
    }

    public Optional<String> citizenId() { return Optional.ofNullable(citizenId); }
    public Optional<String> organizationId() { return Optional.ofNullable(organizationId); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermitApplicant that)) return false;
        return Objects.equals(citizenId, that.citizenId) && Objects.equals(organizationId, that.organizationId);
    }
    @Override public int hashCode() { return Objects.hash(citizenId, organizationId); }
}
