package et.gov.ethgov.document.domain.document;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class ContentType {

    private static final Set<String> ALLOWED = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "text/plain",
            "application/json",
            "application/xml",
            "application/zip",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String value;

    private ContentType(String value) {
        this.value = value;
    }

    public static ContentType of(String raw) {
        Objects.requireNonNull(raw, "contentType");
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported content type: " + raw);
        }
        return new ContentType(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentType that)) {
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
