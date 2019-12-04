package et.gov.ethgov.developer.domain.app;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class AppSlug {
    private static final Pattern PATTERN = Pattern.compile("^[a-z][a-z0-9-]{2,63}$");
    private final String value;

    private AppSlug(String value) {
        this.value = value;
    }

    public static AppSlug of(String raw) {
        Objects.requireNonNull(raw, "slug");
        String n = raw.trim().toLowerCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("slug must be lowercase kebab-case (3-64 chars)");
        }
        return new AppSlug(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof AppSlug that && value.equals(that.value));
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
