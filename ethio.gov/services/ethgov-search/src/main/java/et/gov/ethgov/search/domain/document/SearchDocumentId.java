package et.gov.ethgov.search.domain.document;

import java.util.Objects;
import java.util.UUID;

public final class SearchDocumentId {
    private final String value;

    private SearchDocumentId(String value) {
        this.value = value;
    }

    public static SearchDocumentId generate() {
        return new SearchDocumentId("sdc_" + UUID.randomUUID());
    }

    public static SearchDocumentId of(String raw) {
        Objects.requireNonNull(raw, "searchDocumentId");
        String n = raw.trim();
        if (!n.startsWith("sdc_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid SearchDocumentId: " + raw);
        }
        return new SearchDocumentId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof SearchDocumentId that && value.equals(that.value));
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
