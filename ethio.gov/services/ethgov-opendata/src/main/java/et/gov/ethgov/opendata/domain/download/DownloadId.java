package et.gov.ethgov.opendata.domain.download;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Download. */
public final class DownloadId {
    private final UUID value;

    private DownloadId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DownloadId newId() {
        return new DownloadId(UUID.randomUUID());
    }

    public static DownloadId of(UUID value) {
        return new DownloadId(value);
    }

    public static DownloadId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DownloadId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DownloadId other)) {
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
