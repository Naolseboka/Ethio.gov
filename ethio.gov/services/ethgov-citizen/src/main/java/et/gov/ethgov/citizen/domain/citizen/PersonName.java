package et.gov.ethgov.citizen.domain.citizen;

import java.util.Locale;
import java.util.Objects;

/**
 * Ethiopian-style personal name: given name, father's name, grandfather's name.
 */
public final class PersonName {

    private final String givenName;
    private final String fatherName;
    private final String grandfatherName;

    private PersonName(String givenName, String fatherName, String grandfatherName) {
        this.givenName = givenName;
        this.fatherName = fatherName;
        this.grandfatherName = grandfatherName;
    }

    public static PersonName of(String givenName, String fatherName, String grandfatherName) {
        return new PersonName(requireName(givenName, "givenName"), requireName(fatherName, "fatherName"),
                blankToNull(grandfatherName));
    }

    private static String requireName(String value, String field) {
        Objects.requireNonNull(value, field);
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 80) {
            throw new IllegalArgumentException(field + " length must be between 2 and 80");
        }
        return normalized;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return requireName(value, "grandfatherName");
    }

    public String givenName() {
        return givenName;
    }

    public String fatherName() {
        return fatherName;
    }

    public String grandfatherName() {
        return grandfatherName;
    }

    public String fullName() {
        if (grandfatherName == null) {
            return givenName + " " + fatherName;
        }
        return givenName + " " + fatherName + " " + grandfatherName;
    }

    public String searchKey() {
        return fullName().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PersonName that)) {
            return false;
        }
        return givenName.equalsIgnoreCase(that.givenName)
                && fatherName.equalsIgnoreCase(that.fatherName)
                && Objects.equals(
                        grandfatherName == null ? null : grandfatherName.toLowerCase(Locale.ROOT),
                        that.grandfatherName == null ? null : that.grandfatherName.toLowerCase(Locale.ROOT));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                givenName.toLowerCase(Locale.ROOT),
                fatherName.toLowerCase(Locale.ROOT),
                grandfatherName == null ? null : grandfatherName.toLowerCase(Locale.ROOT));
    }
}
