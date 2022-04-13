package et.gov.ethgov.land.domain.parcel;

import java.util.Objects;
import java.util.Optional;

public final class ParcelOwner {
    private final String citizenId;
    private final String organizationId;

    private ParcelOwner(String citizenId, String organizationId) {
        this.citizenId = citizenId;
        this.organizationId = organizationId;
    }

    public static ParcelOwner of(String citizenId, String organizationId) {
        String cit = normalize(citizenId, "cit_", "citizenId");
        String org = normalize(organizationId, "org_", "organizationId");
        if (cit == null && org == null) {
            throw new IllegalArgumentException("parcel must have citizenId and/or organizationId owner");
        }
        return new ParcelOwner(cit, org);
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
        if (!(o instanceof ParcelOwner that)) return false;
        return Objects.equals(citizenId, that.citizenId) && Objects.equals(organizationId, that.organizationId);
    }
    @Override public int hashCode() { return Objects.hash(citizenId, organizationId); }
}
