package et.gov.ethgov.search.domain.document;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class SearchLocale {
    private static final Set<String> ALLOWED = Set.of("am", "en", "om", "ti", "so", "sid");
    private final String value;

    private SearchLocale(String value) {
        this.value = value;
    }

    public static SearchLocale of(String raw) {
        Objects.requireNonNull(raw, "locale");
        String n = raw.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(n)) {
            throw new IllegalArgumentException("locale must be one of am/en/om/ti/so/sid");
        }
        return new SearchLocale(n);
    }

    public static SearchLocale english() {
        return new SearchLocale("en");
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof SearchLocale that && value.equals(that.value));
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
