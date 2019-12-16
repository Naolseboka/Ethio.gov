package et.gov.ethgov.developer.domain.credential;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class CredentialNumber {
    private static final Pattern PATTERN = Pattern.compile("^ACK-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private CredentialNumber(String value) {
        this.value = value;
    }

    public static CredentialNumber of(String raw) {
        Objects.requireNonNull(raw, "credentialNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("credentialNumber must match ACK-YYYY-######");
        }
        return new CredentialNumber(n);
    }

    public static CredentialNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new CredentialNumber("ACK-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof CredentialNumber that && value.equals(that.value));
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
