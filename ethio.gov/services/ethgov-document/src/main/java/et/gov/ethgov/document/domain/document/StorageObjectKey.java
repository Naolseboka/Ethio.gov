package et.gov.ethgov.document.domain.document;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Objects;

/** Object-storage key scoped by tenant and document id. */
public final class StorageObjectKey {

    private final String value;

    private StorageObjectKey(String value) {
        this.value = value;
    }

    public static StorageObjectKey forDocument(TenantId tenantId, DocumentId documentId, int version) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(documentId, "documentId");
        if (version < 1) {
            throw new IllegalArgumentException("version must be >= 1");
        }
        return new StorageObjectKey(tenantId.value() + "/" + documentId.value() + "/v" + version);
    }

    public static StorageObjectKey of(String raw) {
        Objects.requireNonNull(raw, "storageObjectKey");
        String normalized = raw.trim();
        if (normalized.isBlank() || normalized.length() > 512 || normalized.contains("..")) {
            throw new IllegalArgumentException("Invalid storage object key");
        }
        return new StorageObjectKey(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StorageObjectKey that)) {
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
