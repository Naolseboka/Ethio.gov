package et.gov.ethgov.search.domain.document;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local search document number, e.g. SDC-2024-000001. */
public final class DocumentNumber {
    private static final Pattern PATTERN = Pattern.compile("^SDC-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private DocumentNumber(String value) {
        this.value = value;
    }

    public static DocumentNumber of(String raw) {
        Objects.requireNonNull(raw, "documentNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("documentNumber must match SDC-YYYY-######");
        }
        return new DocumentNumber(n);
    }

    public static DocumentNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new DocumentNumber("SDC-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof DocumentNumber that && value.equals(that.value));
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
