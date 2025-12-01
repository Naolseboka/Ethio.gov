package et.gov.ethgov.workflow.domain.definition;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class DefinitionKey {

    private static final Pattern PATTERN = Pattern.compile("^[a-z][a-z0-9-]{1,63}$");

    private final String value;

    private DefinitionKey(String value) {
        this.value = value;
    }

    public static DefinitionKey of(String raw) {
        Objects.requireNonNull(raw, "definitionKey");
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (!PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "definitionKey must be 2–64 lowercase letters, digits, or hyphens");
        }
        return new DefinitionKey(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefinitionKey that)) {
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
