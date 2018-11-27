package et.gov.ethgov.analytics.domain.metric;

import java.util.Objects;
import java.util.UUID;

public final class MetricDefinitionId {
    private final String value;

    private MetricDefinitionId(String value) {
        this.value = value;
    }

    public static MetricDefinitionId generate() {
        return new MetricDefinitionId("met_" + UUID.randomUUID());
    }

    public static MetricDefinitionId of(String raw) {
        Objects.requireNonNull(raw, "metricDefinitionId");
        String n = raw.trim();
        if (!n.startsWith("met_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid MetricDefinitionId: " + raw);
        }
        return new MetricDefinitionId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof MetricDefinitionId that && value.equals(that.value));
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
