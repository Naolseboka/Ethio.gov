package et.gov.ethgov.opendata.domain.dataset;

import java.util.Objects;
import java.util.UUID;

public final class DatasetId {
    private final String value;

    private DatasetId(String value) {
        this.value = value;
    }

    public static DatasetId generate() {
        return new DatasetId("ods_" + UUID.randomUUID());
    }

    public static DatasetId of(String raw) {
        Objects.requireNonNull(raw, "datasetId");
        String n = raw.trim();
        if (!n.startsWith("ods_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid DatasetId: " + raw);
        }
        return new DatasetId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof DatasetId that && value.equals(that.value));
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
