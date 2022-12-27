package et.gov.ethgov.notification.domain.message;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local notification number, e.g. NTF-2024-000001. */
public final class NotificationNumber {
    private static final Pattern PATTERN = Pattern.compile("^NTF-[0-9]{4}-[0-9]{6}$");
    private final String value;
    private NotificationNumber(String value) { this.value = value; }
    public static NotificationNumber of(String raw) {
        Objects.requireNonNull(raw, "notificationNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("notificationNumber must match NTF-YYYY-######");
        }
        return new NotificationNumber(n);
    }
    public static NotificationNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) throw new IllegalArgumentException("invalid year");
        if (sequence < 1 || sequence > 999_999) throw new IllegalArgumentException("sequence out of range");
        return new NotificationNumber("NTF-%d-%06d".formatted(year, sequence));
    }
    public String value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof NotificationNumber that && value.equals(that.value));
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
