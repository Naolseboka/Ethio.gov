package et.gov.ethgov.document.domain.document;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** SHA-256 content digest as lowercase hex. */
public final class ContentHash {

    private static final Pattern PATTERN = Pattern.compile("^[a-f0-9]{64}$");

    private final String value;

    private ContentHash(String value) {
        this.value = value;
    }

    public static ContentHash of(String raw) {
        Objects.requireNonNull(raw, "contentHash");
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (!PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("contentHash must be 64-char SHA-256 hex");
        }
        return new ContentHash(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentHash that)) {
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
