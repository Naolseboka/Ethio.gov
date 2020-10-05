package et.gov.ethgov.document.domain.ocrjob;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Ocrjob. */
public final class OcrjobId {
    private final UUID value;

    private OcrjobId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OcrjobId newId() {
        return new OcrjobId(UUID.randomUUID());
    }

    public static OcrjobId of(UUID value) {
        return new OcrjobId(value);
    }

    public static OcrjobId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OcrjobId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OcrjobId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
