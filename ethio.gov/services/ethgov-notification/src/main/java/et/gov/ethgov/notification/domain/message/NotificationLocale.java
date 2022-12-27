package et.gov.ethgov.notification.domain.message;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class NotificationLocale {
    private static final Set<String> SUPPORTED = Set.of("am", "en", "om", "ti", "so");
    private final String value;

    private NotificationLocale(String value) { this.value = value; }

    public static NotificationLocale of(String raw) {
        Objects.requireNonNull(raw, "locale");
        String n = raw.trim().toLowerCase(Locale.ROOT);
        if (!SUPPORTED.contains(n)) {
            throw new IllegalArgumentException("locale must be one of am, en, om, ti, so");
        }
        return new NotificationLocale(n);
    }

    public String value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof NotificationLocale that && value.equals(that.value));
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
