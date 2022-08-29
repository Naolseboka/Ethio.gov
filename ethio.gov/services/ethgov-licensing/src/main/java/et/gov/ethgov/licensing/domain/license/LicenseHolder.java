package et.gov.ethgov.licensing.domain.license;

import java.util.Objects;
import java.util.Optional;

/** Business license holder — organization required; citizen optional (sole proprietor). */
public final class LicenseHolder {
    private final String organizationId;
    private final String citizenId;

    private LicenseHolder(String organizationId, String citizenId) {
        this.organizationId = organizationId;
        this.citizenId = citizenId;
    }

    public static LicenseHolder of(String organizationId, String citizenId) {
        String org = normalize(organizationId, "org_", "organizationId");
        if (org == null) {
            throw new IllegalArgumentException("organizationId is required for a business license");
        }
        String cit = normalize(citizenId, "cit_", "citizenId");
        return new LicenseHolder(org, cit);
    }

    private static String normalize(String raw, String prefix, String field) {
        if (raw == null || raw.isBlank()) return null;
        String n = raw.trim();
        if (!n.startsWith(prefix) || n.length() < 40) {
            throw new IllegalArgumentException(field + " must be a valid " + prefix + " identifier");
        }
        return n;
    }

    public String organizationId() { return organizationId; }
    public Optional<String> citizenId() { return Optional.ofNullable(citizenId); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LicenseHolder that)) return false;
        return Objects.equals(organizationId, that.organizationId)
                && Objects.equals(citizenId, that.citizenId);
    }
    @Override public int hashCode() { return Objects.hash(organizationId, citizenId); }
}
