package et.gov.ethgov.document.domain.document;

import java.util.Objects;
import java.util.UUID;

public final class DocumentId {

    private final String value;

    private DocumentId(String value) {
        this.value = value;
    }

    public static DocumentId generate() {
        return new DocumentId("doc_" + UUID.randomUUID());
    }

    public static DocumentId of(String raw) {
        Objects.requireNonNull(raw, "documentId");
        String normalized = raw.trim();
        if (!normalized.startsWith("doc_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid DocumentId: " + raw);
        }
        return new DocumentId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DocumentId that)) {
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
