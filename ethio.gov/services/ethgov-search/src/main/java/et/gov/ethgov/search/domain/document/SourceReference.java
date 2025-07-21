package et.gov.ethgov.search.domain.document;

import java.util.Locale;
import java.util.Objects;

/** Opaque source entity id with known EthGov prefix conventions. */
public final class SourceReference {
    private final String value;

    private SourceReference(String value) {
        this.value = value;
    }

    public static SourceReference of(String raw) {
        Objects.requireNonNull(raw, "sourceId");
        String n = raw.trim();
        if (n.length() < 8 || n.length() > 64) {
            throw new IllegalArgumentException("sourceId length must be between 8 and 64");
        }
        int underscore = n.indexOf('_');
        if (underscore < 2) {
            throw new IllegalArgumentException("sourceId must use a typed prefix (e.g. cit_...)");
        }
        String prefix = n.substring(0, underscore + 1).toLowerCase(Locale.ROOT);
        if (!n.startsWith(prefix) || n.length() < prefix.length() + 8) {
            throw new IllegalArgumentException("sourceId must be a valid typed identifier");
        }
        return new SourceReference(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof SourceReference that && value.equals(that.value));
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
