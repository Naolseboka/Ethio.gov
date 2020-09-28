package et.gov.ethgov.document.domain.document;

import java.util.Locale;
import java.util.Objects;

public final class FileName {

    private final String value;

    private FileName(String value) {
        this.value = value;
    }

    public static FileName of(String raw) {
        Objects.requireNonNull(raw, "fileName");
        String normalized = raw.trim().replaceAll("[\\\\/]+", "_");
        if (normalized.isBlank() || normalized.length() > 255) {
            throw new IllegalArgumentException("fileName length must be between 1 and 255");
        }
        if (normalized.contains("..")) {
            throw new IllegalArgumentException("fileName must not contain path traversal");
        }
        return new FileName(normalized);
    }

    public String value() {
        return value;
    }

    public String extension() {
        int idx = value.lastIndexOf('.');
        if (idx < 0 || idx == value.length() - 1) {
            return "";
        }
        return value.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FileName that)) {
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
