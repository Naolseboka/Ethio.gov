package et.gov.ethgov.directory.domain.employee;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local employee number (HR badge / payroll id). */
public final class EmployeeNumber {

    private static final Pattern PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9/-]{1,31}$");

    private final String value;

    private EmployeeNumber(String value) {
        this.value = value;
    }

    public static EmployeeNumber of(String raw) {
        Objects.requireNonNull(raw, "employeeNumber");
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "employeeNumber must be 2–32 chars of A-Z, 0-9, '/', or '-'");
        }
        return new EmployeeNumber(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmployeeNumber that)) {
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
