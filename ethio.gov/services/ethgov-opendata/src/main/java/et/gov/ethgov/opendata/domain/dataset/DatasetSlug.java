package et.gov.ethgov.opendata.domain.dataset;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Stable public catalog slug, e.g. land-parcels-addis. */
public final class DatasetSlug {
    private static final Pattern PATTERN = Pattern.compile("^[a-z][a-z0-9-]{2,79}$");
    private final String value;

    private DatasetSlug(String value) {
        this.value = value;
    }

    public static DatasetSlug of(String raw) {
        Objects.requireNonNull(raw, "slug");
        String n = raw.trim().toLowerCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("slug must be lowercase kebab-case (3-80 chars)");
        }
        return new DatasetSlug(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof DatasetSlug that && value.equals(that.value));
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
