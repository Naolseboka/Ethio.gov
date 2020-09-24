package et.gov.ethgov.document.domain.document;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/** Logical owner of a document outside this service (citizen, org, case, etc.). */
public final class DocumentOwnerRef {

    private static final Set<String> TYPES =
            Set.of("CITIZEN", "ORGANIZATION", "EMPLOYEE", "CASE", "TENANT", "OTHER");

    private final String type;
    private final String id;

    private DocumentOwnerRef(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public static DocumentOwnerRef of(String type, String id) {
        Objects.requireNonNull(type, "ownerType");
        Objects.requireNonNull(id, "ownerId");
        String normalizedType = type.trim().toUpperCase(Locale.ROOT);
        if (!TYPES.contains(normalizedType)) {
            throw new IllegalArgumentException("Unsupported owner type: " + type);
        }
        String normalizedId = id.trim();
        if (normalizedId.length() < 3 || normalizedId.length() > 64) {
            throw new IllegalArgumentException("ownerId length must be between 3 and 64");
        }
        return new DocumentOwnerRef(normalizedType, normalizedId);
    }

    public String type() {
        return type;
    }

    public String id() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DocumentOwnerRef that)) {
            return false;
        }
        return type.equals(that.type) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
